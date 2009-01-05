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
package uchicago.src.sim.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.RepastException;
import uchicago.src.sim.util.SimUtilities;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;


/**
 * Performs various common operations on passed networks and returns them,
 * and/or returns a statistic on a passed network and returns the result.
 * Please note that these may be fairly "naive" algorithm implementations,
 * and no guarantees are made about the accuracy of the statistics.  The
 * intention is that these may be used for "on the fly" qualitative evaluation
 * of a model, but real network statistics should be done with more serious
 * software such as UCINET or Pajek.<p>
 *
 * ALL THE METHODS CAN BE CONSIDERED BETA AND SHOULD ONLY BE USED FOR
 * "ON THE FLY" CALCULATIONS. ACTUAL NETWORK STATISTICS SHOULD BE DONE
 * WITH DEDICATED NETWORK ANALYSIS SOFTWARE, SUCH AS PAJEK OR UCINET.
 *
 * @author Skye Bender-deMoll
 * @version $Revision$ $Date$
 */
public class NetUtilities {
  //private ArrayList nodeList = new ArrayList ();

  /**
   * No-Argument constructor for convenience / aliasing.
   */
  public NetUtilities () {
  }

  /**
   * Calculates the clustering coefficient (avg. density of "ego networks")
   * in graph.
   *
   * @param nodes the network for which the ClustCoef will be calculated
   */
  public static double calcClustCoef (List nodes) {
    //will directionality be a problem?  What if k -> i but not i -> k?
    //possibility of multiple edges to one node?
    //IGNORE arcs to i

    //loop over all nodes
    int nNodes = nodes.size ();
    double clustCoef = 0.0;
    HashSet jNodes = new HashSet (); //set of all nodes (j) connected to i
    for (int i = 0; i < nNodes; i++) {
      jNodes.clear ();
      int iDens = 0;
      Node iNode = (Node) nodes.get (i);
      ArrayList edges = iNode.getOutEdges ();
      int iDegree = edges.size ();
      for (int j = 0; j < edges.size (); j++) {
        jNodes.add ((Node) ((Edge) edges.get (j)).getTo ());
      }
      if (jNodes.contains (iNode)) {
        jNodes.remove (iNode);
      }
      Iterator jIter = jNodes.iterator ();
      while (jIter.hasNext ()) {
        Node jNode = (Node) jIter.next ();
        ArrayList jEdges = jNode.getOutEdges ();
        for (int k = 0; k < jEdges.size (); k++) {
          Node kNode = ((Edge) jEdges.get (k)).getTo ();
          if (jNodes.contains (kNode) && !jNode.equals (kNode)) //make sure not a loop
          {
            iDens++;
          }
        }
      }
      //trap condition of no arcs (density  = 0)
      if (iDens > 0) {
        //cluster coef of i = (actual #)/(max possible number of edges in ngh)
        clustCoef += (double) iDens / (double) (iDegree * (iDegree - 1));
      }
    }
    clustCoef = clustCoef / nNodes;
    return clustCoef;
  }

  /**
   * calculates density (ratio of arcs in network to maximum possible number
   * of arcs) of passed network.  Checks to make sure network is
   * non-multiplex. Includes self-loops.
   */
  public static double calcDensity (List nodes) {
    double density = 0.0;
    int nNodes = nodes.size ();
    int degreeSum = 0;
    //check to make sure that the network is not multiplex
    if (isMultiplexNet (nodes)) {
      String error = "calcDensity expects a non-multiplex network. Please run collapseMultiplexNet() before calculating density.";
      RepastException exception = new RepastException (error);
      SimUtilities.showError (error, exception);
    } else {
      //outdegree only!!
      //COUNTS SELF LOOPS
      for (int i = 0; i < nNodes; i++) {
        degreeSum += ((ArrayList) ((Node) nodes.get (i)).getOutEdges ()).size ();
      }
      //density  = number of possible arcs / number present
      density = (double) degreeSum / (double) (nNodes * nNodes);
    }
    return density;
  }

  /**
   * Calculates density of the network, but if collapseMulti is true, it first
   * collapses any multiplex ties.  If collapseMulti is false, it runs without
   * checking for multiplex ties.  Note: if the argument is false, and
   * multiplex ties exist, calcDensity will still return a value, but it will
   * not exactly correspond to the density.  But it will run much faster than
   * calcDensity(ArrayList nodes).  The possibility of self-loops is assumed
   * @param nodes the ArrayList of nodes to examine
   **/
  public static double calcDensity (List nodes, boolean collapseMulti) {
    double density = 0.0;
    int nNodes = nodes.size ();
    int degreeSum = 0;
    //check to make sure that the network is not multiplex
    if (collapseMulti) {
      //NOT IMPLEMENTED UNTIL PROBLEM WITH MODIFYING ORIGINAL NET RESOLVED
      //nodes = collapseMultiplexNet(nodes);
    } else {
      //outdegree only!!
      //COUNTS SELF LOOPS
      for (int i = 0; i < nNodes; i++) {
        degreeSum += ((ArrayList) ((Node) nodes.get (i)).getOutEdges ()).size ();
      }
      //density  = number of possible arcs / number present
      density = (double) degreeSum / (double) (nNodes * nNodes);
    }
    return density;
  }

  /**
   * Checks if there are any nodes i j for which there is more than one
   * tie i -> j (almost all network statistics assume that the network is
   * NOT multiplex)
   */
  public static boolean isMultiplexNet (List nodes) {
    boolean multiplex = false;
    int nNodes = nodes.size ();
    HashSet jNodes = new HashSet (nNodes);
    Iterator nodeIter = nodes.iterator ();
    while (nodeIter.hasNext () && !multiplex) {
      jNodes.clear ();
      ArrayList edges = ((Node) nodeIter.next ()).getOutEdges ();
      Iterator edgeIter = edges.iterator ();
      while (edgeIter.hasNext () && !multiplex)   //can break after 1st multiple tie is discovered
      {
        Node jNode = (Node) ((Edge) edgeIter.next ()).getTo ();
        if (jNodes.contains (jNode)) {
          multiplex = true;
        } else {
          jNodes.add (jNode);
        }
      }
    }
    return multiplex;
  }

  /**
   * Returns a boolean indicating whether the network contains self-loops
   * (links from i -> i)
   *
   * @param nodes the ArrayList of nodes to examine for loops
   */
  public static boolean hasSelfLoops (List nodes) {
    for (Iterator iter = nodes.iterator(); iter.hasNext(); ) {
      Node iNode = (Node) iter.next ();
      if (iNode.hasEdgeTo(iNode) || iNode.hasEdgeFrom(iNode)) return true;
    }
    return false;
  }

  /**
   * Removes redundant (same direction) ties between node pairs. Ignores weight
   * of ties, first encountered is kept, subsequent redundant ties are removed.
   * @param nodes the network from which to remove redundant ties
   **/
  /* MODIFIES PASSED NETWORK DISABLED UNTIL ISSUES RESOLVED
     public static ArrayList collapseMultiplexNet(ArrayList nodes)
     {
     ArrayList collapsed = new ArrayList();
     int nNodes = nodes.size();
     HashSet jNodes = new HashSet();
     Iterator nodeIter = nodes.iterator();
     while (nodeIter.hasNext())
     {
     jNodes.clear();
     //make clone so as not to alter original
     Node node = (Node)(nodeIter.next().clone());
     ArrayList edges = node.getOutEdges();
     for (int e = 0; e<edges.size(); e++)
     {
     Edge edge = (Edge)edges.get(e);
     if(jNodes.contains(edge.getTo()))
     {
     node.removeOutEdge(edge);
     ((Node)edge.getTo()).removeInEdge(edge);
     }
     else
     {
     jNodes.add(edge.getTo());
     }
     }
     collapsed.add(node);
     }
     return collapsed;
     }
  */

  /**
   * Returns 1 if a tie from i to j exists, 0 otherwise.
   *
   * @param iNode the node the tie is from
   * @param jNode the node the tie is to
   **/
  public static int getIJTie (Node iNode, Node jNode) {
    return iNode.hasEdgeTo(jNode) ? 1 : 0;
  }

  /**
   * Returns double value from getStrength() if tie from i to j exists,
   * returns 0.0 if none exists.  Note: will also return 0.0 if strength of
   * existing tie is 0.0. Use getIJTie(iNode, jNode) to test for existence.
   * If more than one tie exists, this returns the value of first tie encountered.
   *
   * @param iNode the node the tie is from
   * @param jNode the node the tie is to
   **/
  public static double getIJTieStrength (Node iNode, Node jNode) {
    for (Iterator arcIter = iNode.getOutEdges ().iterator (); arcIter.hasNext(); ) {
      Edge arc = (Edge) arcIter.next ();
      if (arc.getTo().equals(jNode)) return arc.getStrength();
    }
    return 0;
  }

  /**
   * Returns the number of ties from i to j.  Note: this is not necessarily the
   * same as the number of ties from j to i !
   *
   * @param iNode the node ties are from
   * @param jNode the node ties are to
   */
  public static int countIJTies (Node iNode, Node jNode) {
    int tieCount = 0;
    for (Iterator arcIter = iNode.getOutEdges ().iterator (); arcIter.hasNext(); ) {
      Edge arc = (Edge) arcIter.next ();
      if (arc.getTo ().equals (jNode)) tieCount++;
    }
    return tieCount;
  }

  /**
   * Returns the out degree (number of out edges) of the node.
   * @param iNode the node the degree will be returned for
   **/
  public static int getOutDegree (Node iNode) {
    return iNode.getOutEdges ().size ();
  }

  /**
   * Returns the in degree (number of in edges) of the node.
   * (Assumes inEdges have been set correctly)
   * @param iNode the node the degree will be returned for
   **/
  public static int getInDegree (Node iNode) {
    return iNode.getInEdges ().size ();
  }

  /**
   * Returns the number of arcs node is involved in. (Outdegree + Indegree).
   * (Assumes inEdges have been set correctly)
   * @param iNode the node the degree will be returned for
   **/
  public static int getAllDegree (Node iNode) {
    int degree = iNode.getInEdges ().size ();
    degree += iNode.getOutEdges ().size ();
    return degree;
  }

  /**
   * Finds and returns the number of "parents" (nodes with links TO both
   * i and j)
   *
   * @param iNode one node of triad
   * @param jNode the second node of the triad
   */
  public static int getNumDirectTriads (Node iNode, Node jNode) {
    int parentCount = 0;
    //get all nodes with links to i
    HashSet iLinks = new HashSet ();
    Iterator arcIter = ((ArrayList) iNode.getInEdges ()).iterator ();
    while (arcIter.hasNext ()) {
      iLinks.add ((Node) ((Edge) arcIter.next ()).getFrom ());
    }
    //count how many also have links to j
    arcIter = ((ArrayList) jNode.getInEdges ()).iterator ();
    while (arcIter.hasNext ()) {
      if (iLinks.contains ((Node) ((Edge) arcIter.next ()).getFrom ())) {
        parentCount++;
      }
    }
    return parentCount;
  }


  /**
   * Returns a double with a value equal to  the length of the shortest path
   * between i and j.  Paths are calculated according to an implementation of
   * "Dijkstra's" algorithm. Distances are obtained from the outedge's
   * getStrength() method, and are assumed to be symmetric and non-negitive.
   * If it is known that all edge strengths are equal to 1 (the default), the
   * graph distance can be obtained by casting the double to an int.
   *
   * @param nodes the complete network of nodes
   * @param iNode the originating node for the path
   * @param jNode the destination node for the path
   */
  public static double getIJShortPathLength (List nodes, Node iNode,
                                             Node jNode) {
    //CHECK FOR MULTIPLEX!!
    //WILL WORK ON NON_SYMMETRIC?
    HashMap nodeIndexMap = new HashMap();
    int nNodes = nodes.size ();
    double[] distList = new double[nNodes];
    // ArrayList nodeIndexer = new ArrayList (nNodes);
    DoubleArrayList priorityList = new DoubleArrayList ();
    ArrayList nodeQueue = new ArrayList ();
    HashSet checkedNodes = new HashSet ();
    for (int i = 0; i < nNodes; i++) {
      //get lowestlevel nodes so that they will compare with what edges return
      //nodeIndexer.add (((Node) nodes.get (i)).getNode ());
      nodeIndexMap.put(nodes.get(i), new Integer(i));
      distList[i] = Double.POSITIVE_INFINITY;
    }

    checkedNodes.clear ();
    priorityList.clear ();
    nodeQueue.clear ();
    distList[((Integer)nodeIndexMap.get(iNode)).intValue()] = 0.0;
    checkedNodes.add (iNode);
    priorityList.add (0.0);
    nodeQueue.add (iNode);
    while (nodeQueue.size () >= 1) {
      //find node with smallest priority value
      double fringeNodePrior = Double.POSITIVE_INFINITY;
      int fringeNodeIndex = Integer.MAX_VALUE;
      for (int n = 0; n < priorityList.size (); n++) {
        if (priorityList.getQuick (n) < fringeNodePrior) {
          fringeNodeIndex = n;
          fringeNodePrior = priorityList.getQuick (fringeNodeIndex);
        }
      }
      Node fringeNode = (Node) nodeQueue.get (fringeNodeIndex);
      double fringeNodeDist = priorityList.getQuick (fringeNodeIndex);
      nodeQueue.remove (fringeNodeIndex);
      priorityList.remove (fringeNodeIndex);
      checkedNodes.add (fringeNode);
      //put distance in matrix
      distList[((Integer)nodeIndexMap.get(fringeNode)).intValue()] = fringeNodeDist;
      if (fringeNode.equals (jNode)) {
        //finished, so break
        break;
      }
      //loop over its edges, adding nodes to queue
      Iterator edgeIter = ((ArrayList) fringeNode.getOutEdges ()).iterator ();
      while (edgeIter.hasNext ()) {
        Edge edge = (Edge) edgeIter.next ();
        Node workNode = edge.getTo ();
        if (!checkedNodes.contains (workNode)) //to avoid backtracks
        {
          //calc workNode's distance from iNode
          double workNodeDist = fringeNodeDist + edge.getStrength ();
          int prevDistIndex = nodeQueue.indexOf (workNode);
          if (prevDistIndex >= 0) {
            //check if it has a lower distance
            if (priorityList.getQuick (prevDistIndex) > workNodeDist) {
              //replace it with new value
              priorityList.set (prevDistIndex, workNodeDist);
            }
          } else {
            //add the worknode to the queue with priority
            priorityList.add (workNodeDist);
            nodeQueue.add (workNode);

          }
        }
      }
    }
    //returns POSTITIVE_INFINITY if there is no path
    return distList[((Integer)nodeIndexMap.get(jNode)).intValue()];
  }

  /**
   * Returns a DenseDoubleMatrix2D in which the i,j th entry gives the
   * length of the shortest path between i and j. i and j indexes
   * refer to the position of the node in the ArrayList parameter.
   * Paths are calculated according to an implementation of Dijkstra's
   * algorithm, and timing will be approx. O(N E log N).  Distances
   * are obtained from the outedge's getStrength() method, and are
   * assumed to be symmetric and non-negitive.
   *
   * !!!!BETA!!!!
   *
   * @param nodes the network which the matrix will be returned for
   */
  public static DenseDoubleMatrix2D getAllShortPathMatrix (List nodes) {
    //CHECK FOR MULTIPLEX!!
    //SYMMETRY?
    int nNodes = nodes.size ();
    DenseDoubleMatrix2D distMatrix = new DenseDoubleMatrix2D (nNodes, nNodes);
    distMatrix.assign (Double.POSITIVE_INFINITY);
    //ArrayList nodeIndexer = new ArrayList(nNodes);

    // index of nodes to there index in the
    HashMap nodeIndexer = new HashMap ();
    DoubleArrayList priorityList = new DoubleArrayList ();
    ArrayList nodeQueue = new ArrayList ();
    HashSet checkedNodes = new HashSet ();

    for (int i = 0; i < nNodes; i++) {
      // get lowest level nodes so that they will compare with
      // what edges return
      nodeIndexer.put ((Node) nodes.get (i), new Integer (i));
    }


    for (int i = 0; i < nNodes; i++) {
      checkedNodes.clear ();
      priorityList.clear ();
      nodeQueue.clear ();
      //find paths to all nodes connected to i
      Node iNode = (Node) nodes.get (i);
      distMatrix.setQuick (i, i, 0.0);
      checkedNodes.add (iNode);
      priorityList.add (0.0);
      nodeQueue.add (iNode);
      while (nodeQueue.size () > 0) {
        //find node with smallest priority value
        double fringeNodePrior = Double.POSITIVE_INFINITY;
        int fringeNodeIndex = Integer.MAX_VALUE;
        for (int n = 0; n < priorityList.size (); n++) {
          if (priorityList.getQuick (n) < fringeNodePrior) {
            fringeNodeIndex = n;
            fringeNodePrior = priorityList.getQuick (fringeNodeIndex);
          }
        }
        Node fringeNode = (Node) nodeQueue.get (fringeNodeIndex);
        double fringeNodeDist = priorityList.getQuick (fringeNodeIndex);
        nodeQueue.remove (fringeNodeIndex);
        priorityList.remove (fringeNodeIndex);
        checkedNodes.add (fringeNode);

        //put distance in matrix
        int index = ((Integer) nodeIndexer.get (fringeNode)).intValue ();
        distMatrix.setQuick (i, index, fringeNodeDist);
        distMatrix.setQuick (index, i, fringeNodeDist);
        //loop over its edges, adding nodes to queue
        Iterator edgeIter =
                ((ArrayList) fringeNode.getOutEdges ()).iterator ();
        while (edgeIter.hasNext ()) {
          Edge edge = (Edge) edgeIter.next ();
          Node workNode = edge.getTo ();
          if (!checkedNodes.contains (workNode)) {
            //calc workNode's distance from iNode
            double workNodeDist = fringeNodeDist + edge.getStrength ();
            int prevDistIndex = nodeQueue.indexOf (workNode);
            if (prevDistIndex >= 0) {
              //check if it has a lower distance
              if (priorityList.getQuick (prevDistIndex) > workNodeDist) {
                //repace it with new value
                priorityList.set (prevDistIndex, workNodeDist);
              }
            } else {
              //add the worknode to the queue with priority
              priorityList.add (workNodeDist);
              nodeQueue.add (workNode);
            }
          }
        }
      }
    }

    return distMatrix;
  }

  /**
   * Returns a double equal to the graph-theoretic diameter of the passed
   * network (the length of the longest shortest path). Requires the
   * calculation of all shortest paths using "Dijkstra's" algorithm, and
   * timing will be approx. O(N E log N).Distances are obtained from the
   * outedge's getStrength() method, and are assumed to be symmetric and
   * non-negitive.
   *
   * @param nodes the network for which the diameter will be calculated
   */
  public static double calcDiameter (List nodes) {
    double graphDiam = 0.0;
    int nNodes = nodes.size ();
    DenseDoubleMatrix2D distMatrix = getAllShortPathMatrix (nodes);
    for (int i = 0; i < nNodes; i++) {
      for (int j = 0; j < nNodes; j++) {
        graphDiam = Math.max (graphDiam, distMatrix.getQuick (i, j));
      }
    }
    return graphDiam;
  }

  /**
   * Returns a double equal to the average or "characteristic" path length of
   * the graph. Uses the "Dijkstra's" shortest path method. Ignores diagonal.
   *
   * BETA AND TAKES AN UNREASONABLY LONG PERIOD OF TIME
   *
   * @param nodes the network to evaluate
   */
  public static double calcAvgPathLength (List nodes) {
    //might be for efficient to implement with the matrix,
    //but i think this will be robust for non-symmetric graphs
    int nNodes = nodes.size ();
    double pathLength = 0;
    for (int i = 0; i < nNodes; i++) {
      for (int j = 0; j < nNodes; j++) {
        //ignore diagonal
        if (i != j) {
          //System.out.println("i: " + i + ", j: " + j);
          pathLength += getIJShortPathLength (nodes, (Node) nodes.get (i),
                                              (Node) nodes.get (j));
        }
      }
    }
    if (nNodes > 0) {
      pathLength = pathLength / ((nNodes - 1) * (nNodes - 1));
    }
    return pathLength;
  }

  /**
   * Returns a double indicating the amount of symmetry in the network:
   * fraction of existing i -> j ties for which there is a j -> i tie.
   * Assumes that inEdges have been set. Doesn't make sense for multiplex nets
   * Will return 1.0 if network is Symmetric, or if size of network is 0;
   * @param nodes the network to evaluate for symmetry
   */
  public static double calcSymmetry (List nodes) {
    int nNodes = nodes.size ();
    int outCount = 0;
    int symmCount = 0;
    double symmetry = 1.0;
    for (int i = 0; i < nNodes; i++) {
      Node iNode = (Node) nodes.get (i);
      HashSet fromNodes = new HashSet ();
      Iterator inEdgeIter = (iNode.getInEdges ()).iterator ();
      while (inEdgeIter.hasNext ()) {
        fromNodes.add (((Edge) inEdgeIter.next ()).getFrom ());
      }

      Iterator outEdgeIter = (iNode.getOutEdges ()).iterator ();
      while (outEdgeIter.hasNext ()) {
        Node toNode = ((Edge) outEdgeIter.next ()).getTo ();
        outCount++;
        if (fromNodes.contains (toNode)) {
          symmCount++;
        }
      }
    }
    if (outCount > 0) {
      symmetry = (double) symmCount / (double) outCount;
    }
    return symmetry;
  }

  /**
   * Returns an ArrayList of length equal to the number of components in the
   * graph, each entry of which is an ArrayList of the nodes in that component.
   * @param nodes the network in which components will be counted
   */

  public static ArrayList getComponents (List nodes) {
    ComponentFinder finder = new ComponentFinder ();
    return finder.findComponents (nodes);
  }

  // class is constructed to make possible the use of recursive
  // tree search methods within a static context of netUtilities
  private static class ComponentFinder {

    ArrayList nodeList;
    int nNodes = 0;
    HashSet checked = new HashSet (nNodes);
    ArrayList currentComps = new ArrayList ();
    HashSet currentComp = new HashSet ();

    public ArrayList findComponents (List nodeList) {
      nNodes = nodeList.size ();
      //Hashtable nodeNodeIndexer = new Hashtable (nNodes);
      ArrayList returnList = new ArrayList ();
      //make it so we can get the passed nodes from the low level nodes
      //for (int i = 0; i < nNodes; i++) {
      //  Node iNode = (Node) nodeList.get (i);
      //  nodeNodeIndexer.put (iNode.getNode (), iNode);
      //}

      checked.clear ();
      for (int i = 0; i < nNodes; i++) {
        Node iNode = (Node) nodeList.get (i);
        if (!checked.contains (iNode)) {
          currentComp = new HashSet ();
          currentComps.add (currentComp);
          //puts iNode and all connected nodes into currentComponent
          findConnectedNodes (iNode);
        }
      }

      int size = currentComps.size ();
      for (int i = 0; i < size; i++) {
        HashSet set = (HashSet) currentComps.get (i);
        ArrayList component = new ArrayList (set.size ());
        //pull the items out of nodeList which correspond to the internal
        //node objects found by the search
        Iterator nodeIter = set.iterator ();
        while (nodeIter.hasNext ()) {
          component.add (nodeIter.next ());
        }

        returnList.add (component);
      }

      return returnList;
    }

    // recursively calls itself to find all nodes connected to iNode
    private void findConnectedNodes (Node iNode) {
      checked.add (iNode);
      currentComp.add (iNode);
      Iterator edgeIter = ((ArrayList) iNode.getOutEdges ()).iterator ();
      while (edgeIter.hasNext ()) {
        Edge edge = (Edge) edgeIter.next ();
        Node nextNode = edge.getTo ();
        if (!checked.contains (nextNode))
          findConnectedNodes (nextNode);
        else if (!currentComp.contains (nextNode)) {
          HashSet set = getComponentFor (nextNode);
          set.addAll (currentComp);
          currentComps.remove (currentComp);
          currentComp = set;
        }
      }
    }

    private HashSet getComponentFor (Node node) {
      int size = currentComps.size ();
      for (int i = 0; i < size; i++) {
        HashSet set = (HashSet) currentComps.get (i);
        if (set.contains (node)) {
          return set;
        }
      }

      // should never get here
      return null;
    }
  }


  /**
   * Returns a network of the same size and density as the passed network,
   * but randomly "rewires" a fraction of the edges to randomly chosen
   * target nodes.  Will not create self loops or multiplex ties.
   * @param rewireProb the fraction of edges to rewire
   */
  public static List randomRewire (List nodes, double rewireProb) {
    ArrayList edges = new ArrayList ();
    ArrayList nodeIndexer = new ArrayList (nodes.size ());
    Iterator nodeIter = nodes.iterator ();
    while (nodeIter.hasNext ()) {
      Node node = (Node) nodeIter.next ();
      edges.addAll (node.getOutEdges ());
      nodeIndexer.add (node);
    }
    for (int e = 0; e < edges.size (); e++) {
      if (rewireProb > Random.uniform.nextDoubleFromTo (0, 1)) {
        Edge edge = (Edge) edges.get (e);
        int i = nodeIndexer.indexOf (edge.getFrom ());
        int j = nodeIndexer.indexOf (edge.getTo ());
        int newJ = Random.uniform.nextIntFromTo (0, nodes.size () - 1);
        if ((i != newJ) && (getIJTie ((Node) nodeIndexer.get (i),
                                      (Node) nodeIndexer.get (newJ)) < 1)) {

          ((Node) nodes.get (j)).removeInEdge (edge);
          edge.setTo ((Node) nodeIndexer.get (newJ));
          ((Node) nodes.get (newJ)).addInEdge (edge);
        }
      }
    }
    return nodes;
  }

  /**
   * Returns a network of the same size and density as the passed network,
   * but randomly "rewires" a fraction of the edges to randomly chosen
   * target nodes.  Will not create self loops or multiplex ties. Will also
   * rewire j to i ties, throws repast exception if network is not symmetric.
   * @param rewireProb the fraction of edges to rewire
   */
  public static List randomRewireSymmetric (List nodes,
                                                 double rewireProb) {
    ArrayList edges = new ArrayList ();
    ArrayList nodeIndexer = new ArrayList (nodes.size ());
    Iterator nodeIter = nodes.iterator ();
    while (nodeIter.hasNext ()) {
      Node node = (Node) nodeIter.next ();
      edges.addAll (node.getOutEdges ());
      nodeIndexer.add (node);
    }
    for (int e = 0; e < edges.size (); e++) {
      if (rewireProb > Random.uniform.nextDoubleFromTo (0, 1)) {
        Edge edge = (Edge) edges.get (e);
        int i = nodeIndexer.indexOf (edge.getFrom ());
        int j = nodeIndexer.indexOf (edge.getTo ());
        int newJ = Random.uniform.nextIntFromTo (0, nodes.size () - 1);
        if ((i != newJ) && (getIJTie ((Node) nodeIndexer.get (i),
                                      (Node) nodeIndexer.get (newJ)) < 1)) {
          Node jNode = (Node) nodes.get (j);
          //move i -> j tie
          jNode.removeInEdge (edge);
          edge.setTo ((Node) nodeIndexer.get (newJ));
          ((Node) nodes.get (newJ)).addInEdge (edge);
          //move j -> i tie
          Iterator jEdgeIter = ((ArrayList) jNode.getOutEdges ()).iterator ();
          Edge otherEdge = null;
          while (jEdgeIter.hasNext ()) {
            otherEdge = (Edge) jEdgeIter.next ();
            if (otherEdge.getTo () == (Node) nodeIndexer.get (i)) {
              break;
            } else {
              otherEdge = null;
            }
          }
          if (otherEdge == null) {
            //network is not symmetric, so throw error
            String error = "randomRewireSymmetric expects a symmetric network";
            RepastException exception = new RepastException (error);
            SimUtilities.showError (error, exception);
          } else {
            jNode.removeOutEdge (otherEdge);
            otherEdge.setFrom ((Node) nodeIndexer.get (newJ));
            ((Node) nodes.get (newJ)).addOutEdge (otherEdge);
          }
        }
      }
    }

    return nodes;
  }

  /**
   * Returns a string, each line of which consists of the name of a graph
   * statistic and its value. The order is:
   * Size: int
   * NumComponents: int
   * Density: double
   * ClustringCoeff: double
   * isMultiplex: boolean
   * hasSelfLoops: boolean
   * Diameter: double
   * AvgPathLength: double
   * Symmetry: double
   * <BR><BR>
   *
   * Please note that this call will be quite "expensive" on large networks!
   * @param nodes the network for which the statistics will be calculated
   */
  public static String getStatsString (List nodes) {
    String statsString = "NETWORK STATISTICS\n"
            + "Size: " + nodes.size () + "\n"
            + "NumComponents: " + ((ArrayList) getComponents (nodes)).size () + "\n"
            + "Density: " + calcDensity (nodes) + "\n"
            + "ClustringCoeff: " + calcClustCoef (nodes) + "\n"
            + "isMultiplex: " + isMultiplexNet (nodes) + "\n"
            + "hasSelfLoops: " + hasSelfLoops (nodes) + "\n"
            + "Diameter: " + calcDiameter (nodes) + "\n"
            + "AvgPathLength: " + calcAvgPathLength (nodes) + "\n"
            + "Symmetry: " + calcSymmetry (nodes) + "\n";
    return statsString;
  }
}
