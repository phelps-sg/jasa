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
package uchicago.src.repastdemos.jinGirNew;

import uchicago.src.collection.RangeMap;
import uchicago.src.reflector.ListPropertyDescriptor;
import uchicago.src.sim.analysis.Histogram;
import uchicago.src.sim.analysis.NetSequenceGraph;
import uchicago.src.sim.analysis.PlotModel;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Controller;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.*;
import uchicago.src.sim.util.Random;

import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This is an implementation of the second model (model II) described
 * in Jin, Girvan, and Newman, "The Structure of Growing Social
 * Networks."  Santa Fe Institute 2001 working paper. This paper can
 * be found on-line at
 * http://www.santafe.edu/sfi/publications/Abstracts/01-06-032abs.html<p>
 * <p/>
 * Their abstract follows:<p>
 * <p/>
 * "We propose some simple models of the growth of social networks,
 * based on three general principles: (1) meetings take place between
 * pairs of individuals at a rate which is high if a pair has one or
 * more mutual friends and low otherwise; (2) acquaintances between
 * pairs of individuals who rarely meet decay over time; (3) there is
 * an upper limit on the number of friendships an individual can
 * maintain. using computer simulations, we find that models that
 * incorporatge all of these features reproduce many of the features of
 * real social networks, including high levels of clustering or network
 * transitivity and strong community structure in which individuals
 * have more links to others within their community than to individuals
 * from other communities."<p>
 * <p/>
 * See the repast/demo/jinGirNew/readme for details on the parameters
 * of this model.
 * 
 * @version $Revision$ $Date$
 */
public class JinGirNewModelII extends SimModelImpl {

  // model variables
  private int numNodes = 250;
  private ArrayList agentList = new ArrayList(16);
  private int worldXSize = 400;
  private int worldYSize = 400;
  private int updateEveryN = 5;
  private int initialSteps = 1;
  private double removeProb = 0.005;
  private int maxDegree = 5;
  private double Rsub0 = 0.0005;
  private double Rsub1 = 2;


  // implementation variables
  private String layoutType = "Fruch";
  private DisplaySurface surface;
  private Schedule schedule;
  private AbstractGraphLayout graphLayout;
  private Histogram degreeDist;
  private boolean showHist = false;
  private NetSequenceGraph graph;
  private boolean showPlot = true;
  private BasicAction initialAction;

  private RangeMap neighborMap = new RangeMap();
  private RangeMap removalMap = new RangeMap();

  public JinGirNewModelII() {
    Vector vect = new Vector();
    vect.add("Fruch");
    vect.add("KK");
    vect.add("CircleLayout");
    ListPropertyDescriptor pd = new ListPropertyDescriptor("LayoutType", vect);
    descriptors.put("LayoutType", pd);
  }

  // get and set accessor methods
  public boolean getDegreeHist() {
    return showHist;
  }

  public void setDegreeHist(boolean val) {
    showHist = val;
  }

  public boolean getPlot() {
    return showPlot;
  }

  public void setPlot(boolean val) {
    showPlot = val;
  }

  public int getMaxDegree() {
    return maxDegree;
  }

  public void setMaxDegree(int degree) {
    maxDegree = degree;
  }

  public double getRsub0() {
    return Rsub0;
  }

  public void setRsub0(double R) {
    Rsub0 = R;
  }

  public double getRsub1() {
    return Rsub1;
  }

  public void setRsub1(double R) {
    Rsub1 = R;
  }

  public String getLayoutType() {
    return layoutType;
  }

  public void setLayoutType(String type) {
    layoutType = type;
  }

  public void setUpdateEveryN(int updates) {
    updateEveryN = updates;
  }

  public int getUpdateEveryN() {
    return updateEveryN;
  }

  public double getRemoveProb() {
    return removeProb;
  }

  public void setRemoveProb(double prob) {
    removeProb = prob;
  }

  public int getStartRemoveAfter() {
    return initialSteps;
  }

  public void setStartRemoveAfter(int steps) {
    initialSteps = steps;
  }

  public void setNumNodes(int n) {
    numNodes = n;
  }

  public int getNumNodes() {
    return numNodes;
  }

  public int getWorldXSize() {
    return worldXSize;
  }

  public void setWorldXSize(int size) {
    worldXSize = size;
  }

  public int getWorldYSize() {
    return worldYSize;
  }

  public void setWorldYSize(int size) {
    worldYSize = size;
  }

  // builds the model
  public void buildModel() {
    for (int n = 0; n < numNodes; n++) {
      int x = Random.uniform.nextIntFromTo(0, worldXSize - 1);
      int y = Random.uniform.nextIntFromTo(0, worldYSize - 1);
      agentList.add(new JinGirNewNode(x, y));
    }

    if (showHist) makeHistogram();
    if (showPlot) makePlot();
  }

  /*
   * "...At each time-step, we chose NpR0 pairs of vertices uniformly
   * at random from the network to meet. If a pair meet who do not have
   * a pre-existing connection, and if neither of them already has the
   * maximum z* connections then a new connection is established
   * between them...."
   */
  private void randomMeeting() {
    int nPairs = (numNodes * (numNodes - 1)) / 2;
    int numPicks = (int) Math.round(nPairs * Rsub0);

    for (int n = 0; n < numPicks; n++) {
      int index = Random.uniform.nextIntFromTo(0, numNodes - 1);
      JinGirNewNode iNode = (JinGirNewNode) agentList.get(index);
      iNode.meetRandom(agentList, maxDegree);
    }
  }

  /**
   * "...At each time-step, we choose NmR1 vertices at random, with
   * probabilites proportional to Zi(Zi-1). For each vertex chosen we
   * randomly choose on pair of its neighbots to meet, and establish a
   * new connection between them if they do not have a preexisting
   * connection and if neither of them already has the maximum number
   * z* of connections...."
   */
  private void neighborMeeting() {
    neighborMap.clear();
    double zeeSum = 0;
    for (int n = 0; n < numNodes; n++) {
      JinGirNewNode node = (JinGirNewNode) agentList.get(n);
      int degree = node.getOutDegree();
      if (degree > 1) {
        neighborMap.put(zeeSum, node);
        zeeSum += degree * (degree - 1);
      }
    }

    int nM = (int) (zeeSum / 2);   //will be even cause #edges even
    int numPicks = (int) Math.round(nM * Rsub1);

    for (int n = 0; n < numPicks; n++) {
      double pick = Random.uniform.nextDoubleFromTo(0, zeeSum);
      JinGirNewNode node = (JinGirNewNode) neighborMap.get(pick);

      /*
      if (node == null) {
	System.out.println("pick: " + pick);
	System.out.println("zeeSum: " + zeeSum);
	neighborMap.print();
      }
      */

      if (node.getOutDegree() > 1) node.meetNeighbor(maxDegree);
    }
  }

  /*
   * "...At each time-step, we choose NeY vertices at random, with
   * probabilities proportional to Zi.  For each vertex chosen we
   * choose one of its neighbors uniformly at random and delete the
   * connection to that neighbor...."
   */
  private void removeRandomFriendship() {

    removalMap.clear();
    double zeeSum = 0;
    int nEdges = 0;

    for (int i = 0; i < numNodes; i++) {
      JinGirNewNode node = (JinGirNewNode) agentList.get(i);
      int degree = node.getOutDegree();
      if (degree > 1) {
        removalMap.put(zeeSum, node);
        zeeSum += degree * (degree - 1);
      }

      nEdges += degree;
    }

    nEdges = nEdges / 2;
    int numPicks = (int) Math.round(nEdges * removeProb);

    for (int n = 0; n < numPicks; n++) {
      double pick = Random.uniform.nextDoubleFromTo(0, zeeSum);
      JinGirNewNode node = (JinGirNewNode) removalMap.get(pick);
      node.removeFriend();
    }
  }

  public void buildDisplay() {
    if (layoutType.equals("KK")) {
      graphLayout = new KamadaGraphLayout(agentList, worldXSize, worldYSize,
              surface, updateEveryN);
    } else if (layoutType.equals("Fruch")) {
      graphLayout = new FruchGraphLayout(agentList, worldXSize, worldYSize, surface, updateEveryN);
    } else if (layoutType.equals("CircleLayout")) {
      graphLayout = new CircularGraphLayout(agentList, worldXSize,
              worldYSize);
    }

    // these four lines hook up the graph layouts to the stop, pause, and
    // exit buttons on the toolbar. When stop, pause, or exit is clicked
    // the graph layouts will interrupt their layout as soon as possible.
    Controller c = (Controller) getController();
    c.addStopListener(graphLayout);
    c.addPauseListener(graphLayout);
    c.addExitListener(graphLayout);

    Network2DDisplay display = new Network2DDisplay(graphLayout);
    surface.addDisplayableProbeable(display, "JinGirNew Display");

    // add the display as a Zoomable. This means we can "zoom" in on
    // various parts of the network.
    surface.addZoomable(display);
    surface.setBackground(java.awt.Color.white);
    addSimEventListener(surface);
  }

  /*
   * Creates a histogram of the degree distribution.
   */
  private void makeHistogram() {

    degreeDist = new Histogram("Degree Distribution", maxDegree + 1, 0,
            maxDegree + 1, this);

    degreeDist.createHistogramItem("Degree Distribution", agentList,
            "getOutDegree");
  }

  /*
   * Creates a Plot of the Clustering Coefficient, the avg. density,
   * and the component count.
   */
  private void makePlot() {

    graph = new NetSequenceGraph("Network Stats", this, "./net.txt", PlotModel.CSV, agentList);
    graph.setAxisTitles("Time", "Statistic Value");
    graph.setXRange(0, 50);
    graph.setYRange(0, numNodes);
    graph.plotDensity("Density", Color.blue, NetSequenceGraph.SQUARE);
    //graph.plotComponentCount("Component Count", Color.green, graph.SQUARE);
    graph.plotClusterCoefficient("Cluster Coef.", Color.red, NetSequenceGraph.SQUARE);
  }

  public void initialAction() {
    randomMeeting();
    neighborMeeting();
    graphLayout.updateLayout();
    surface.updateDisplay();
    if (showHist) degreeDist.step();
    if (showPlot) graph.step();
  }

  public void mainAction() {
    randomMeeting();
    neighborMeeting();
    removeRandomFriendship();
    graphLayout.updateLayout();
    surface.updateDisplay();
    if (showHist) degreeDist.step();
    if (showPlot) graph.step();

    //System.out.println(NetUtilities.getComponents(agentList).size());

  }

  public void removeInitialAction() {
    schedule.removeAction(initialAction);
  }

  private void buildSchedule() {
    initialAction = schedule.scheduleActionAt(1, this, "initialAction");
    schedule.scheduleActionAt(initialSteps, this, "removeInitialAction",
            Schedule.LAST);
    schedule.scheduleActionBeginning(initialSteps + 1, this, "mainAction");
    schedule.scheduleActionAtEnd(graph, "writeToFile");
  }

  public void begin() {
    buildModel();
    buildDisplay();
    buildSchedule();
    graphLayout.updateLayout();
    surface.display();
    if (showHist) degreeDist.display();
    if (showPlot) graph.display();
  }

  public void setup() {
    Random.createUniform();
    if (surface != null) surface.dispose();
    if (degreeDist != null) degreeDist.dispose();
    if (graph != null) graph.dispose();

    surface = null;
    schedule = null;
    degreeDist = null;
    graph = null;

    System.gc();

    surface = new DisplaySurface(this, "JinGirModelII Display");
    registerDisplaySurface("Main Display", surface);
    schedule = new Schedule();
    agentList = new ArrayList();
    worldXSize = 500;
    worldYSize = 500;
  }

  public String[] getInitParam() {
    String[] params = {"numNodes", "worldXSize", "worldYSize",
                       "updateEveryN", "LayoutType", "RemoveProb",
                       "StartRemoveAfter", "MaxDegree", "Rsub1", "Rsub0",
                       "DegreeHist", "Plot"};
    return params;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public String getName() {
    return "JinGirNewModelII";
  }

  public static void main(String[] args) {
    uchicago.src.sim.engine.SimInit init =
            new uchicago.src.sim.engine.SimInit();
    JinGirNewModelII model = new JinGirNewModelII();
    init.loadModel(model, "", false);
  }
}
