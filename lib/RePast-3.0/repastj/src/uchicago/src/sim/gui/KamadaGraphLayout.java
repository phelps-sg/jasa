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
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import uchicago.src.sim.network.NetUtilities;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * Positions network nodes according to the Kamada-Kawai algorithm.
 * The Kamada-Kawai graph layout class attempts to position nodes on
 * the space so that the geometric (Euclidean) distance between them
 * is as close as possible to the graph-theoretic (path) distance
 * between them.  the x and y coordinates of the nodes will be
 * modified by the layout.<p>
 *
 * The KamadaGraphLayout implements the ActionListener interface to
 * interrupt the layout. This breaks out of the algorithm
 * implementation as soon as possible, but will rescale the display if
 * appropriate. You can have the KamadaGraphLayout listener for RePast
 * toolbar button presses by including something like the following
 * code inside your model class.
 *
 * <code><pre>
 * graphLayout = new KamadaGraphLayout(...);
 * Controller c = (Controller)getController();
 * c.addStopListener(graphLayout);
 * c.addPauseListener(graphLayout);
 * c.addExitListener(graphLayout);
 * </pre></code>
 *
 * This will cause the KamadaGraphLayout graphLayout to interrupt its layout
 * whenever stop, pause, or exit is pressed.<p>
 *
 * <b>Note</b> The KamadaGraphLayout is quite slow. It is not meant
 * as a "true" visualization tool, but rather is intended only to
 * provide the modeler with "sense" of the network. Real analysis and
 * visualization should be done in a tool like Pajek.
 *
 * @version $Revision$ $Date$
 * @author Skye Bender-deMoll e-mail skyebend@santafe.edu
 */
public class KamadaGraphLayout extends LayoutWithDisplay implements
        ActionListener {
  //kamada-kawai algorithm vars
  private double springConst = 1;       //K in KK paper (avg. i,j distance?)
  private double minEpsilon = 1;  //target deltaM goal
  private int maxPasses = 5000;   //maximum number of inner loops
  private boolean circleLayout = false; //start each layout from circle?
  private boolean rescaleLayout = true; //wheter to resize and recenter
  private boolean animate = true;       //whether to animate the transitions
  private int pad = 20;
  private int updates = 0;            //how often to update the screen during layout
  private boolean stop = false;    //flag to break layout
  //private String status = "";      //status string for reporting breaks
  private boolean firstLayout = true;
  //private boolean breakOut = false;

  /**
   * Creates a KamadaGraphLayout with specified width and height.
   * setDisplay must be called before calling updateLayout().
   *
   * @param width the width (in pixels) of the displaySurface
   * @param height the height (in pixels) of the DisplaySurface
   */
  public KamadaGraphLayout(int width, int height) {
    super(width, height);
  }

  /**
   * Creates a KamadaGraphLayout with specified width and height, and
   * list of nodes. setDisplay must be called before calling
   * updateLayout().
   *
   * @param nodes the list nodes to layout with this KamadaGraphLayout
   * @param width the width (in pixels) of the DisplaySurface
   * @param height the height (in pixels) of the DisplaySurface
   */
  public KamadaGraphLayout(List nodes, int width, int height) {
    super(nodes, width, height);
  }

  /**
   * If the KamadaGraphLayout is passed a display, and a value for
   * updateEveryN, it will call the display's updateDisplay() method
   * on every Nth pass during the layout to provide visual feedback.
   * Note: this update occurs within the current tick and is intended
   * to provide visual feedback on the algorithms convergence and the
   * network structure The additional updating will slow the layout
   * down dramatically when displaying large networks.
   *
   * @param nodes the network to layout.
   * @param width the width (pixels) of the display surface
   * @param height the height (pixels) of the display surface
   * @param surface the displaysurface to update
   * @param updateEveryN determines if/ when layout is redrawn during
   * algorithm
   */
  public KamadaGraphLayout(List nodes, int width, int height,
                           DisplaySurface surface, int updateEveryN) {
    super(nodes, width, height, surface);
    updates = updateEveryN;
  }

  /**
   * Sets the number of pixels to shrink the effective window by. Java
   * draws object from top left hand corner and this allows objects
   * drawn on the far right to be visible.
   *
   * @param p the number of pixels to inset by
   */
  public void setPad(int p) {
    pad = p;
  }

  /**
   * Sets how frequently the layout will be redrawn during algorithm
   * convergence.  If updateEveryN is greater than 0,the layout will
   * update the display after every Nth pass through the algorithm.
   * Additional updates make the layout take much longer, especially
   * if there are a large number of nodes to draw.
   *
   * @param updateEveryN the N for determining wether to update on the
   * Nth pass
   */
  public void setUpdateEveryN(int updateEveryN) {
    updates = updateEveryN;
  }

  /**
   * Sets the minimum "spring" energy which the layout attempts to
   * achieve.  Small values mean greater accuracy, and an unknown (but
   * large) amount of additional run time. The algorithm will start
   * with an initially high epsilon value, and keep decreasing it
   * until the layout drops below epsilon, the layout stops improving,
   * or maxPasses is exceeded.  Default is 1, so setting to a higher
   * value will speed up layouts.
   *
   * @param energy the value for the minimum epsilon
   */
  public void setMinEpsilon(double energy) {
    minEpsilon = energy;
  }

  /**
   * Sets the "springiness" of the imaginary springs connecting the
   * nodes.  Impact on layout is not well understood, seems to control
   * how far nodes are moved each time.  Default is 1.
   *
   * @param spring the value for the spring constant in the algorithm
   */
  public void setSpringConst(double spring) {
    springConst = spring;
  }


  /**
   * Sets the maximum number of passes the inner loop of the KK
   * algorithm will execute.  Lower values mean that the layout is
   * more likely to end before arriving at a minima, but it will break
   * more quickly when stuck in a cycle.  The number of loops needed
   * to a achieve a layout is roughly proportional to the number of
   * nodes (but not in all cases!). Default is 5000
   *
   * @param passes the maximum number of time the inner loop will
   * execute.
   */
  public void setMaxPasses(int passes) {
    maxPasses = passes;
  }

  /**
   * Sets whether circleLayout will be called to arrange nodes before
   * starting each layout.  Should be called before first layout to
   * insure repeatability.  May make layouts take slightly longer.
   * Default is false, but will still circle the first layout unless
   * explicitly set to false.
   *
   * @param eachTime true = always call circleLayout
   */
  public void setCircleLayout(boolean eachTime) {
    circleLayout = eachTime;
    firstLayout = false;
  }

  /**
   * Sets whether the completed layout will be resized to exactly fill
   * the display window.  Setting rescale to false may mean that
   * individual nodes or the entire network may drift off the screen,
   * but it will insure maximum visual continuity between layouts, and
   * minimum layout time.  default is true.
   *
   * @param rescale whether to rescale the layout
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
   * @param animateTrans whether to animate transitions between layouts
   */
  public void setAnimateTransitions(boolean animateTrans) {
    animate = animateTrans;
  }

  /**
   * Positions nodes on layout in a circle for repeatability.  Can be
   * called internally before each layout by setting circleLayout to
   * true.  Useful to insure that nodes have starting coordinates.
   */
  public void circleLayout() {
    int nNodes = nodeList.size();
    // calculate the radius of the circle
    int originX = (int) (width / 2);
    int originY = (int) (height / 2);
    int radius;
    //calc radius
    if (height > width)
      radius = (width / 2) - (pad * 2);
    else
      radius = (height / 2) - (pad * 2);

    for (int i = 0; i < nNodes; i++) {
      DrawableNonGridNode node = (DrawableNonGridNode) nodeList.get(i);
      node.setX(radius * Math.cos(2 * Math.PI * i / nNodes) + originX);
      node.setY(radius * Math.sin(2 * Math.PI * i / nNodes) + originY);
    }
  }

  //set up matrix of spring forces between pairs using K/(d[i][j]^2)
  private DenseDoubleMatrix2D calcKMatrix(DenseDoubleMatrix2D distMatrix,
                                          double spring) {
    int nNodes = distMatrix.rows();
    DenseDoubleMatrix2D kMatrix = new DenseDoubleMatrix2D(nNodes, nNodes);
    for (int i = 0; i < nNodes; i++) {
      for (int j = 0; j < nNodes; j++) {
        double distMVal = distMatrix.getQuick(i, j);
        kMatrix.setQuick(i, j, (spring / (distMVal * distMVal)));
      }
    }
    return kMatrix;
  }

  //set up matrix of desired edge lengths using L*d[i][j]
  private DenseDoubleMatrix2D calcLMatrix(DenseDoubleMatrix2D distMatrix,
                                          double optDist) {
    int nNodes = distMatrix.rows();
    DenseDoubleMatrix2D lMatrix = new DenseDoubleMatrix2D(nNodes, nNodes);
    for (int i = 0; i < nNodes; i++) {
      for (int j = 0; j < nNodes; j++) {
        lMatrix.setQuick(i, j, (optDist * distMatrix.getQuick(i, j)));
      }
    }
    return lMatrix;
  }

  //calculate the diameter of the graph (longest shortest path)
  //requires that path lengths are calc'd first
  private int getDiam(DenseDoubleMatrix2D distMatrix) {
    int nNodes = distMatrix.rows();
    double graphDiam = 0;
    for (int i = 0; i < nNodes; i++) {
      for (int j = 0; j < nNodes; j++) {
        graphDiam = Math.max(graphDiam, distMatrix.getQuick(i, j));
      }
    }
    return (int) graphDiam;
  }


  /**
   * Positions the nodes on the layout according to the results of
   * numerous iterations of the Kamada-Kawai spring-embedding
   * algorithm.  Essentially, the network is modeled as a collection
   * of nodes connected by springs with resting lengths proportional
   * to the length of the shortest path distance between each node
   * pair.  Nodes are normally positioned in a circle, and then each
   * node in sequence is repositioned until the "energy" of all of its
   * springs are minimized to a parameter value epsilon.  The location
   * of the local minima for each node is estimated with iterations of
   * a Newtown-Raphson steepest descent method.  Repositioning ceases
   * when all nodes have energy below epsilon.  In this
   * implementation, epsilon is initialized at a high value, and than
   * decreased as in simulated annealing.  the layout SHOULD stop when
   * a low value (epsilon < 1) is reached or when energies of nodes
   * can now longer be decreased.<p>
   *
   * Note: In the current implementation the layout may not always
   * converge!  however, the maxPasses parameter can be set lower to
   * interrupt cycling layouts.  Also has not been tested/ implemented
   * on weighted graphs. The Kamada-Kawai algorithm was not intended
   * to run on disconnected graphs (graphs with multiple components.
   * The kludgy solution implemented here is to run the algorithm
   * independently on each of the components (of size > 1).  This is
   * somewhat unsatisfactory as the components will often overlap.<p>
   *
   * The KK algorithm is relatively slow, especially on the first
   * round.  However, it often discovers layouts of regularly
   * structured graphs which are "better" and more repeatable than the
   * Fruchmen-Reingold technique.  Implementation of the numerics of
   * the Newton-Raphson method follows Shawn Lorae Stutzman, Auburn
   * University, 12/12/96 <A
   * href="http://mathcs.mta.ca/research/rosebrugh/gdct/javasource.htm">
   * http://mathcs.mta.ca/research/rosebrugh/gdct/javasource.htm</A>
   * <p> Kamada, Tomihisa and Satoru Kawai (1989) "An Algorithm for
   * Drawing Undirected Graphs" <CITE> Information Processing
   * Letters</CITE> 31:7-15
   */
  public void updateLayout() {
    //check that layout should be drawn
    if (update) {
      isEventThread = SwingUtilities.isEventDispatchThread();
      stop = false;
      if (circleLayout) {
        //give nodes circular initial coord to begin with
        circleLayout();
      }

      if (firstLayout) {
        firstLayout = false;
        circleLayout();
      }

      //runs kk algorithm on each component individualy
      ArrayList components = NetUtilities.getComponents(nodeList);
      Iterator compIter = components.iterator();

      while (compIter.hasNext() && !stop) {
        ArrayList comp = (ArrayList) compIter.next();
        if (comp.size() > 1) runKamadaOn(comp);

      }

      //rescale node positions to fit in window
      if (rescaleLayout) rescalePositions(nodeList);

    }
  }

  //RENORM COORDS TO 0-1 range before running ?
  private void runKamadaOn(ArrayList componentNodes) {
    int nNodes = componentNodes.size();
    //sets up the matrix of path distances
    DenseDoubleMatrix2D distMatrix =
            NetUtilities.getAllShortPathMatrix(componentNodes);
    //sets up kmatrix of forces
    DenseDoubleMatrix2D kMatrix = calcKMatrix(distMatrix, springConst);
    //calc desired distance between nodes
    double optDist = Math.min(width, height) /
            Math.max(getDiam(distMatrix), 1);
    //sets up lMatrix of distance between nodes pairs
    DenseDoubleMatrix2D lMatrix = calcLMatrix(distMatrix, optDist);
    //arrays for quick acess to node coords
    double[] xPos = new double[nNodes];
    double[] yPos = new double[nNodes];

    int numEdges = 0;

    for (int i = 0; i < nNodes; i++) {
      DrawableNonGridNode workNode =
              (DrawableNonGridNode) componentNodes.get(i);
      xPos[i] = workNode.getX();
      yPos[i] = workNode.getY();
      numEdges += ((ArrayList) workNode.getOutEdges()).size();
    }

    //calc value to start minimization from (should be based on previous?)
    //epsilon = (nNodes * numEdges)/2;
    //figure out the initial stat to compare to at the end
    double initialEnergy = getEnergy(lMatrix, kMatrix, xPos, yPos);
    double epsilon = initialEnergy / nNodes;
    //figure out which node to start moving first
    double deltaM;
    int maxDeltaMIndex = 0;
    double maxDeltaM = getDeltaM(0, lMatrix, kMatrix, xPos, yPos);
    for (int i = 1; i < nNodes; i++) {
      deltaM = getDeltaM(i, lMatrix, kMatrix, xPos, yPos);
      if (deltaM > maxDeltaM) {
        maxDeltaM = deltaM;
        maxDeltaMIndex = i;
      }
    }

    int passes = 0;
    int subPasses = 0;
    //epsilon minimizing loop
    while ((epsilon > minEpsilon) && !stop) {
      double previousMaxDeltaM = maxDeltaM + 1;
      // KAMADA-KAWAI LOOP: while the deltaM of the node with
      // the largest deltaM  > epsilon..
      while ((maxDeltaM > epsilon) && ((previousMaxDeltaM - maxDeltaM) > 0.1)
              && !stop) {

        double[] deltas;
        double moveNodeDeltaM = maxDeltaM;
        //double previousDeltaM = moveNodeDeltaM + 1;

        //KK INNER LOOP while the node with the largest energy > epsilon...
        while ((moveNodeDeltaM > epsilon) && !stop) {

          //get the deltas which will move node towards the local minima
          deltas = getDeltas(maxDeltaMIndex, lMatrix, kMatrix,
                             xPos, yPos);
          //set coords of node to old coords + changes
          xPos[maxDeltaMIndex] += deltas[0];
          yPos[maxDeltaMIndex] += deltas[1];
          //previousDeltaM = moveNodeDeltaM;
          //recalculate the deltaM of the node w/ new vals
          moveNodeDeltaM = getDeltaM(maxDeltaMIndex, lMatrix, kMatrix,
                                     xPos, yPos);
          subPasses++;
          if (subPasses > maxPasses) stop = true;
        }
        //previousDeltaM = maxDeltaM;
        //recalculate deltaMs and find node with max
        maxDeltaMIndex = 0;
        maxDeltaM = getDeltaM(0, lMatrix, kMatrix, xPos, yPos);
        for (int i = 1; i < nNodes; i++) {
          deltaM = getDeltaM(i, lMatrix, kMatrix, xPos, yPos);
          if (deltaM > maxDeltaM) {
            maxDeltaM = deltaM;
            maxDeltaMIndex = i;
          }
        }

        //if set to update display, update on every nth pass
        if (updates > 0) {
          if ((passes % updates) == 0) {
            for (int i = 0; i < nNodes; i++) {
              DrawableNonGridNode node =
                      (DrawableNonGridNode) componentNodes.get(i);
              node.setX(xPos[i]);
              node.setY(yPos[i]);
            }
            updateDisplay();
          }
        }

        passes++;
      }

      epsilon -= epsilon / 4;
    }

    if (animate)
      animateTransition(8, componentNodes, xPos, yPos);
    else {
      //set positions of nodes to coord array vals
      for (int i = 0; i < nNodes; i++) {
        DrawableNonGridNode node =
                (DrawableNonGridNode) componentNodes.get(i);
        node.setX(xPos[i]);
        node.setY(yPos[i]);
      }
    }
  }

  //the bulk of the KK inner loop, estimates location of local minima
  private double[] getDeltas(int i, DenseDoubleMatrix2D lMatrix,
                             DenseDoubleMatrix2D kMatrix, double[] xPos,
                             double[] yPos) {
    //solve deltaM partial eqns to figure out new position for node of index i
    // where deltaM is close to 0 (or less then epsilon)
    int nNodes = lMatrix.rows();
    double[] deltas = new double[2];  //holds x and y coords to return
    double dx, dy, dd;
    //double deltaX, deltaY;
    double xPartial = 0;
    double yPartial = 0;
    double xxPartial = 0;
    double xyPartial = 0;
    double yxPartial = 0;
    double yyPartial = 0;
    for (int j = 0; j < nNodes; j++) {
      if (i != j) {
        dx = xPos[i] - xPos[j];
        dy = yPos[i] - yPos[j];
        dd = Math.sqrt(dx * dx + dy * dy);

        double kMatrixVal = kMatrix.getQuick(i, j);
        double lMatrixVal = lMatrix.getQuick(i, j);
        double ddCubed = dd * dd * dd;


        xPartial += kMatrixVal * (dx - lMatrixVal * dx / dd);
        yPartial += kMatrixVal * (dy - lMatrixVal * dy / dd);
        xxPartial += kMatrixVal * (1 - lMatrixVal * dy * dy / ddCubed);
        xyPartial += kMatrixVal * (lMatrixVal * dx * dy / ddCubed);
        yxPartial += kMatrixVal * (lMatrixVal * dy * dx / ddCubed);
        yyPartial += kMatrixVal * (1 - lMatrixVal * dx * dx / ddCubed);
      }
    }

    //calculate x and y position difference using partials
    deltas[0] = ((-xPartial) * yyPartial - xyPartial * (-yPartial)) /
            (xxPartial * yyPartial - xyPartial * yxPartial);
    deltas[1] = (xxPartial * (-yPartial) - (-xPartial) * yxPartial) /
            (xxPartial * yyPartial - xyPartial * yxPartial);

    return deltas;
  }

  //returns the energy of i (looping over all other nodes)
  private double getDeltaM(int i, DenseDoubleMatrix2D lMatrix,
                           DenseDoubleMatrix2D kMatrix, double[] xPos,
                           double[] yPos) {
    int nNodes = lMatrix.rows();
    double deltaM = 0;
    double xPartial = 0;
    double yPartial = 0;
    double dx, dy, dd;
    for (int j = 0; j < nNodes; j++) {
      if (i != j) {
        dx = xPos[i] - xPos[j];
        dy = yPos[i] - yPos[j];
        dd = Math.sqrt(dx * dx + dy * dy);
        double kMatrixVal = kMatrix.getQuick(i, j);
        double lMatrixVal = lMatrix.getQuick(i, j);
        xPartial += kMatrixVal * (dx - lMatrixVal * dx / dd);
        yPartial += kMatrixVal * (dy - lMatrixVal * dy / dd);
      }
    }
    //deltaM = sqrt(xPartial^2+yPartial^2)
    deltaM = Math.sqrt(xPartial * xPartial + yPartial * yPartial);
    return deltaM;
  }

  /**
   * Rescales the x and y coordinates of each node so that the network
   * will maximally fill the display. Will result in some distortion.
   * Called internally if rescale is set to true, will rescale
   * smoothly if animateTransitions is true.
   *
   * @param nodes the nodes to rescale.
   */
  private void rescalePositions(ArrayList nodes) {

    int nNodes = nodes.size();
    double[] xPos = new double[nNodes];
    double[] yPos = new double[nNodes];

    // HACK (cjw)
    if (nNodes == 0) { return; }

    for (int i = 0; i < nNodes; i++) {
      DrawableNonGridNode workNode = (DrawableNonGridNode) nodes.get(i);
      xPos[i] = (double) workNode.getX();
      yPos[i] = (double) workNode.getY();
    }
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
    for (int i = 0; i < nNodes; i++) {
      xPos[i] = ((xPos[i] - xMin) / (xMax - xMin)) * (width - pad);
      yPos[i] = ((yPos[i] - yMin) / (yMax - yMin)) * (height - pad);
    }
    //animate the transiton
    int numSteps = 5;
    if (animate)
      animateTransition(numSteps, nodes, xPos, yPos);
    else {
      //set positions of nodes to coord array vals
      for (int i = 0; i < nNodes; i++) {
        DrawableNonGridNode node = (DrawableNonGridNode) nodes.get(i);
        node.setX(xPos[i]);
        node.setY(yPos[i]);
      }
      // HACK (cjw): don't update if display is null
      // (requires access on display be changed to protected)
      if (display != null) updateDisplay();
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
        node.setX(node.getX() + ((targetX[i] - node.getX()) *
                                 n / steps));
        node.setY(node.getY() + ((targetY[i] - node.getY()) *
                                 n / steps));
      }
    }
  }

  private double getEnergy(DenseDoubleMatrix2D lMatrix,
                           DenseDoubleMatrix2D kMatrix, double[] xPos,
                           double[] yPos) {
    int nNodes = lMatrix.rows();
    double energy = 0;
    double dx, dy,lij;
    int limit = nNodes - 1;
    //for all pairs..
    for (int i = 0; i < limit; i++) {
      for (int j = i + 1; j < nNodes; j++) {
        dx = xPos[i] - xPos[j];
        dy = yPos[i] - yPos[j];
        lij = lMatrix.getQuick(i, j);
        energy += 0.5 * kMatrix.getQuick(i, j) * (dx * dx + dy * dy +
                lij * lij - 2 * lij *
                Math.sqrt(dx * dx + dy * dy));
      }
    }
    return energy;
  }

  /**
   * Implements the ActionListener interface. Whenever this is called the
   * layout will be interrupted as soon as possible.
   */
  public void actionPerformed(ActionEvent evt) {
    stop = true;
  }
}
