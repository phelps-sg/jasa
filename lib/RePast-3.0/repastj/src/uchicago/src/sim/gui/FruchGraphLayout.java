/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Chicago nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.sim.gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import uchicago.src.sim.network.Edge;
import uchicago.src.sim.network.NetUtilities;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;

/**
 * Positions nodes in layout according to iterations of an
 * implementation of the Fruchmen-Reingold graph layout
 * algorithm. See the docs to <code>updateLayout</code> for the details.<p>
 *
 * The FruchGraphLayout implements the ActionListener interface to
 * interrupt the layout. This breaks out of the algorithm
 * implementation as soon as possible, but will rescale the display if
 * appropriate. You can have the FruchGraphLayout listener for RePast
 * toolbar button presses by including something like the following
 * code inside your model class.<p>
 *
 * <code><pre>
 * graphLayout = new FruchGraphLayout(...);
 * Controller c = (Controller)getController();
 * c.addStopListener(graphLayout);
 * c.addPauseListener(graphLayout);
 * c.addExitListener(graphLayout);
 * </pre></code>
 *
 * This will cause the FruchGraphLayout graphLayout to interrupt its layout
 * whenever stop, pause, or exit is pressed.<p>
 *
 * <b>Note</b> The FruchGraphLayout is not particularly fast, although
 * it is faster than the KamadaGraphLayout. It is not meant as a
 * "true" visualization tool, but rather is intended only to provide
 * the modeler with "sense" of the network. Real analysis and visualization
 * should be done in a tool like Pajek.
 *
 * @version $Revision$ $Date$
 * @author Skye Bender-deMoll email:skyebend@santafe.edu
 */
public class FruchGraphLayout extends LayoutWithDisplay {


  private int pad = 20;
  private int initialIter = 30;  //number of loops before cooling starts
  private int maxPasses = 500;     //maximum number of loops through the Fruch layout procedure
  private double optDist = 10;    //optimal distance for nodes, gets reset later in code
  private int updates = 0;
  private boolean rescaleLayout = true; //wether to resize and recenter
  private boolean animate = true;       //whether to animate the transitions
  private boolean firstLayout = true;
  private boolean noBreak = true;
  private int seed;
  private boolean isSeedSet = false;

  /**
   * Constructs new FruchGraphLayout.  Set Display must be called to
   * pass in a display before update display is called
   *
   * @param width the width of the display in pixels
   * @param height the height of the display in pixels
   */
  public FruchGraphLayout(int width, int height) {
    super(width, height);
  }

  /**
   * Constructs new FruchGraphLayout.
   * Set Display must be called to pass in a display before update
   * display is called
   *
   * @param nodes the list of nodes to be optimised
   * @param width the width of the display in pixels
   * @param height the height of the display in pixels
   */
  public FruchGraphLayout(List nodes, int width, int height) {
    super(nodes, width, height);
  }

  /**
   * Constructs new FruchGraphLayout.  If the FruchGraphLayout is
   * passed a display, and a value for updateEveryN, it will call the
   * display's updateDisplay() method updateEveryN on every Nth pass
   * of the layout algorithm. This updating occurs within the current
   * tick and is intended to provide visual feedback on the
   * algorithm's convergence. However, this additional updating will
   * slow the layout down dramatically when displaying large networks.
   *
   * @param surface the display surface to update
   * @param updateEveryN how often to update the display
   * @param nodes the list of nodes to be optimised
   * @param width the width of the display in pixels
   * @param height the height of the display in pixels
   */
  public FruchGraphLayout(List nodes, int width, int height,
                          DisplaySurface surface, int updateEveryN) {
    super(nodes, width, height, surface);
    updates = updateEveryN;
  }


  /**
   * Sets the number of pixels to shrink radius by. Java draws object
   * from top left hand corner and this allows objects drawn on the
   * far right to be visible.
   *
   * @param p the number of pixels to shrink by
   */
  public void setPad(int p) {
    pad = p;
  }

  /**
   * If the layout has been passed a display to update, and
   * updateEveryN is greater than 0, the layout will update the
   * display after every Nth pass through the algorithm.
   *
   * @param updateEveryN how often to update the display
   */
  public void setUpdateEveryN(int updateEveryN) {
    updates = updateEveryN;
  }

  /**
   * Sets whether the completed layout will be resized to exactly fill
   * the display window.  Setting rescale to false may mean that
   * individual nodes or the entire network may drift off the screen,
   * but it will insure maximum visual continuity between layouts, and
   * minimum layout time.  default is true.
   *
   * @param rescale sets if layout will be rescaled
   */
  public void setRescaleLayout(boolean rescale) {
    rescaleLayout = rescale;
  }

  /**
   * Sets whether the completed layout (and each component as it is
   * finished) will smoothly transition to its new location.  Provides
   * visually pleasing results and makes it easy to track structural
   * changes in the network, especially if updateEveryN is set to
   * zero. In this case, no visual feedback will be provided on layout
   * algorithm convergence, but the comparison of the structure of the
   * network at successive time steps is maximized.  The additional
   * redraws required to do the animation will slow down the
   * simulation, especially on large networks. Default is true.
   *
   * @param animateTrans sets if transitions will be animated
   */
  public void setAnimateTransitions(boolean animateTrans) {
    animate = animateTrans;
  }

  private double calcAttraction(double dist) {
    return dist * dist / optDist;
  }

  private double calcRepulsion(double dist) {
    return optDist * optDist / dist;
  }

  private double coolTemp(double val) {
    return val / 1.1;
  }

  /**
   * Sets the Random seed used in the intial random placement of the
   * nodes. If the seed is not set, the current timestamp is used as
   * the seed.
   *
   * @param seed the random seed for the initial random placement of the nodes
   */
  public void setRandomSeed(int seed) {
    this.seed = seed;
    isSeedSet = true;
  }

  /**
   * Randomly positions nodes on layout.  Called internally before
   * update layout is called for the first time to insure that nodes
   * have starting coordinates. This uses a random generator stream
   * separate from the default RePast random stream.  You can set the seed
   * for this stream using the setRandomSeed method.
   */
  public void randomizeLayout() {
    MersenneTwister mt = null;
    if (isSeedSet) mt = new MersenneTwister(seed);
    else mt = new MersenneTwister(new Date());
    Uniform uni = new Uniform(mt);

    int n = nodeList.size();
    int xLimit = width - pad;
    int yLimit = height - pad;
    for (int i = 0; i < n; i++) {
      DrawableNonGridNode node = (DrawableNonGridNode) nodeList.get(i);
      node.setX(uni.nextIntFromTo(0, xLimit));
      node.setY(uni.nextIntFromTo(0, yLimit));
    }
  }

  /**
   * Positions nodes in layout according to a modified implementation
   * of the Fruchterman-Reingold graph layout algorithm. Nodes are
   * positioned according to an iterative algorithm that assumes that
   * nodes repel each other when close, but are attracted to connected
   * nodes. Convergence is obtained by using a "simulated
   * annealing"-style technique with an arbitrary cooling
   * function. Acts on existing node positions, so randomizeLayout()
   * will be called before first update. The default random seed for this
   * initial layout is the current timestamp. The seed can, however, be set with
   * the <code>setRandomSeed</code> method. The random stream for this initial
   * layout is separate from RePast's default random stream.<p>
   *
   * See, Fruchterman, T.M.J and Reingold, E.M. (1991) <CITE>
   * "Graph Drawing by Force-directed Placement" in Software-Practice and
   * Experience, </CITE>Vol 21(11), 1129-1164
   * Modified for code optimization. (Skye Bender-deMoll)
   *
   * <b> Note at this point, this implementation does not take into account
   * edge strengths</b><p>
   *
   * <code><pre>
   * pseudo code of implementation of Furchterman-Reingold Algorithm
   * -----------------------------------------
   *
   * As implemented in Pajek, the algorithm makes initialIter (10) through
   * the algorithm before starting the cooling function.  If this is the
   * first layout, each nodes is given a random initial position.
   *
   * while temp > 0.5 and passes < maxIterations (500)
   *  //calculate repulsive forces between each node
   *  for v = 0 to numberOfNodes
   *    for u = v+1 to numberOfNodes
   *      calculate the distance vector between the positions of v and u
   *      calculate a displacement displaceVec = (distVec/|distVec|)
   *                                               * repulsion(|distVec|)
   *      add displaceVec vector to v's displacement vector
   *      subtract displaceVec from u's displacement vector
   *    end
   *  end
   *
   *  //calculate attractive forces
   *  for e = 0 to numberOfEdges
   *    get the nodes attached to the edge (v and u)
   *    calculate the distance vector between the positions of v and u
   *    calculate a displacement displaceVec = (distVec/|distVec|) * attraction(|distVec|)
   *    subtract displaceVec vector from v's displacement vector
   *    add displaceVec to u's displacement vector
   *  end
   *
   *  calculate each nodes's displacement, but limit max displacement to temp
   *
   *  //decrease temperature parameter
   *  coolTemp()
   *
   *  if this is an Nth pass, update the layout on screen
   *
   *
   *  at the end, go over all the nodes to find the max and min of the coords,
   *  rescale all coords so that network will fill the display
   * end while
   *
   *  //repulsion function
   *  repulsion(distance) = (distance^2)/optimalDistance

   * //attraction function attraction(distance) =
   * (optimalDistance^2)/distance^2 (formula used in pajek)
   *
   *  //cooling function
   *  coolTemp(temp) = unchanged for initialIter iterations, temp/1.1
   *
   * //optimal distance optimalDistance = 0.46*Math.sqrt(((width *
   * height) / (nodeList.size()+1)))
   * </pre></code>
   *
   * <p>
   * Additional comments: Because the original algorithm repositions the
   * nodes in a deterministic order, highly structured / regular networks
   * may exhibit rotations drift during the layout.
   *
   */
  public void updateLayout() {
    if (update) {
      isEventThread = SwingUtilities.isEventDispatchThread();
      noBreak = true;

      // Why do this?
      //update display to show new links
      //updateDisplay();
      // calc constants
      optDist = 0.46 * Math.sqrt(((width * height) / (nodeList.size() + 1)));
      double temp = width / 10;
      int passes = 0;
      int nNodes = nodeList.size();
      double xDelta = 0;
      double yDelta = 0;
      double deltaLength = 0;
      double force = 0;
      ArrayList edges = new ArrayList();
      HashMap nodeIndexer = new HashMap();

      if (firstLayout) {
        //make sure nodes have random initial coord to begin with
        randomizeLayout();
        firstLayout = false;
      }

      //make arrays corresponding to the coords of each node
      double[] xPos = new double[nNodes];
      double[] yPos = new double[nNodes];
      for (int i = 0; i < nNodes; i++) {
        DrawableNonGridNode workNode = (DrawableNonGridNode) nodeList.get(i);
        xPos[i] = workNode.getX();
        yPos[i] = workNode.getY();

        edges.addAll(workNode.getOutEdges());
        nodeIndexer.put(workNode, new Integer(i));
      }

      //remove check for self loops and remove them
      if (NetUtilities.hasSelfLoops(nodeList)) edges = removeLoops(edges);


      //make arrays corresponding to the displacement vector for each node
      double[] xDisp = new double[nNodes];
      double[] yDisp = new double[nNodes];

      // keep passing through the layout loop until the temp is low
      // initialIter + time for cooling schedule
      while ((temp > 1) && (passes < maxPasses) && noBreak) {
        //calculate repulsive forces between each pair of nodes (set both)
        int limit = nNodes - 1;
        for (int v = 0; v < limit; v++) {
          xDisp[v] = 0;
          yDisp[v] = 0;
          // can skip many loops by assuming that uv = -vu and looping in factorial
          for (int u = v + 1; u < nNodes; u++) {
            //get difference of position vectors
            xDelta = xPos[v] - xPos[u];
            yDelta = yPos[v] - yPos[u];

            //trap condition where nodes have same position
            if ((xDelta == 0) && (yDelta == 0)) {
              //don't do anything in hopes that someone else
              //will kick them apart

              // Do we continue or break here? Waiting for Skye's
              // answer.
              continue;
              //break;
            }
            //set vu disp vector
            deltaLength = Math.sqrt((xDelta * xDelta) + (yDelta * yDelta));
            force = calcRepulsion(deltaLength);
            xDisp[v] += (xDelta / deltaLength) * force;
            yDisp[v] += (yDelta / deltaLength) * force;
            //set uv disp vector (-vu)
            xDisp[u] -= (xDelta / deltaLength) * force;
            yDisp[u] -= (yDelta / deltaLength) * force;
          }
        }

        //calculate attractive forces between nodes connected by an edge
        Iterator iter = edges.iterator();
        while (iter.hasNext() && noBreak) {
          Edge edge = (Edge) iter.next();
          int vIndex = ((Integer) nodeIndexer.get(edge.getFrom())).intValue();
          int uIndex = ((Integer) nodeIndexer.get(edge.getTo())).intValue();

          //get difference of position vectors
          xDelta = xPos[vIndex] - xPos[uIndex];
          yDelta = yPos[vIndex] - yPos[uIndex];
          //set vu disp vector
          deltaLength = Math.sqrt((xDelta * xDelta) + (yDelta * yDelta));
          // get div by 0 "errors" if deltaLength is 0.
          // BUT WHAT SHOULD deltaLength BE IN THESE CASES?
          if (deltaLength == 0) deltaLength = 0.001;
          force = calcAttraction(deltaLength);
          xDisp[vIndex] -= (xDelta / deltaLength) * force;
          yDisp[vIndex] -= (yDelta / deltaLength) * force;
          //set uv disp vector to (-vu) because nodes may not be mutually
          xDisp[uIndex] += (xDelta / deltaLength) * force;
          yDisp[uIndex] += (yDelta / deltaLength) * force;

          /*
            if (Double.isNaN(xDisp[uIndex]) && Double.isNaN(yDisp[uIndex]))
            {
            System.out.println();
            System.out.println("xDisp[uIndex] = " + xDisp[uIndex]);
            System.out.println("yDisp[uIndex] = " + yDisp[uIndex]);
            System.out.println("xDelta: " + xDelta);
            System.out.println("yDelta: " + yDelta);
            System.out.println("deltaLength: " + deltaLength);
            System.out.println("force: " + force);
            }
          */
        }

        //caculate displacement, but limit max displacement to temp
        for (int v = 0; v < nNodes; v++) {
          double xDispVal = xDisp[v];
          double yDispVal = yDisp[v];
          deltaLength = Math.sqrt((xDispVal * xDispVal) +
                                  (yDispVal * yDispVal));
          if (deltaLength > temp) {
            xPos[v] += xDisp[v] / (deltaLength / temp);
            yPos[v] += yDisp[v] / (deltaLength / temp);
          } else {
            xPos[v] += xDisp[v];
            yPos[v] += yDisp[v];
          }

        }

        //cool temp
        if (passes > initialIter) temp = coolTemp(temp);

        //if set to update display, update on every nth pass
        if (updates > 0 && (passes % updates == 0)) {

          for (int i = 0; i < nNodes; i++) {
            DrawableNonGridNode node = (DrawableNonGridNode) nodeList.get(i);
            node.setX(xPos[i]);
            node.setY(yPos[i]);
          }
          updateDisplay();
        }

        passes++;
      }

      if (rescaleLayout)
        rescalePositions(nodeList, xPos, yPos);
      else {
        if (animate)
          animateTransition(10, nodeList, xPos, yPos);
        else {
          for (int i = 0; i < nNodes; i++) {
            DrawableNonGridNode node = (DrawableNonGridNode) nodeList.get(i);
            node.setX(xPos[i]);
            node.setY(yPos[i]);
          }
          updateDisplay();
        }
      }
    }
  }

  /**
   * Rescales the x and y coordinates of each node so that the network
   * will maximally fill the display. Will result in some distortion.
   * Called internally if rescale is set to true, will rescale
   * smoothly if animateTransitions is true.
   *
   * @param nodes the nodes to rescale.
   */
  private void rescalePositions(ArrayList nodes, double[] xPos, double[] yPos) {
    int nNodes = nodes.size();
    //find largest coords
    double xMax = xPos[0];
    double yMax = yPos[0];
    double xMin = xPos[0];
    double yMin = yPos[0];
    for (int i = 1; i < nNodes; i++) {
      xMax = Math.max(xMax, xPos[i]);
      yMax = Math.max(yMax, yPos[i]);
      xMin = Math.min(xMin, xPos[i]);
      yMin = Math.min(yMin, yPos[i]);
    }

    // FIX (cjw): If the graph is a single node, or multiple nodes
    // with the same position, xMax - xMin and yMax - yMin are zero,
    // which would result in the node(s) being assigned a position of
    // NaN, NaN.  This code puts it/them in the center instead.
    if (xMax == xMin) { xMin = 0; xMax = xMax * 2; }
    if (yMax == yMin) { yMin = 0; yMax = yMax * 2; }

    //rescale coords of nodes to fit inside frame
    double xDiff = xMax - xMin;
    double yDiff = yMax - yMin;
    int xPadVal = width - pad;
    int yPadVal = height - pad;
    for (int i = 0; i < nNodes; i++) {
      xPos[i] = ((xPos[i] - xMin) / xDiff) * xPadVal;
      yPos[i] = ((yPos[i] - yMin) / yDiff) * yPadVal;
    }
    //animate the transiton
    int numSteps = 10;
    if (animate)
      animateTransition(numSteps, nodes, xPos, yPos);
    else {
      //set positions of nodes to coord array vals
      for (int i = 0; i < nNodes; i++) {
        DrawableNonGridNode node = (DrawableNonGridNode) nodes.get(i);
        node.setX(xPos[i]);
        node.setY(yPos[i]);
      }
      updateDisplay();
    }
  }

  //moves nodes smoothly from current position to target position
  private void animateTransition(int steps, ArrayList nodes, double[] targetX,
                                 double[] targetY) {
    int nNodes = nodes.size();
    for (int n = 1; n <= steps; n++) {
      updateDisplay();
      for (int i = 0; i < nNodes; i++) {
        DrawableNonGridNode node = (DrawableNonGridNode) nodes.get(i);
        node.setX(node.getX() + ((targetX[i] - node.getX())
                                 * n / steps));
        node.setY(node.getY() + ((targetY[i] - node.getY())
                                 * n / steps));
      }
    }
  }

  private ArrayList removeLoops(ArrayList edges) {
    ArrayList returnList = new ArrayList();
    Iterator edgeIter = edges.iterator();
    while (edgeIter.hasNext()) {
      Edge edge = (Edge) edgeIter.next();
      if (!(edge.getFrom()).equals(edge.getTo())) returnList.add(edge);
    }

    return returnList;
  }

  /**
   * Implements the ActionListener interface. Whenever this is called the
   * layout will be interrupted as soon as possible.
   */
  public void actionPerformed(ActionEvent evt) {
    noBreak = false;
  }
}
