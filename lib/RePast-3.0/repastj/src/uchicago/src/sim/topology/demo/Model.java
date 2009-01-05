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
package uchicago.src.sim.topology.demo;


import java.util.Iterator;
import java.util.Vector;

import uchicago.src.reflector.ListPropertyDescriptor;
import uchicago.src.sim.engine.AbstractGUIController;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimpleModel;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Displayable;
import uchicago.src.sim.topology.Context;
import uchicago.src.sim.topology.ContextFactory;
import uchicago.src.sim.topology.DisplayableContext;
import uchicago.src.sim.topology.space2.MooreTopology;
import uchicago.src.sim.topology.space2.Object2DGrid;
import uchicago.src.sim.topology.space2.VonNeumannTopology;

public class Model extends SimpleModel {

  final int ALLC = 0;
  final int TFT = 1;
  final int ATFT = 2;
  final int ALLD = 3;

  final int NUMTYPES = 4;

  private Context world;                  // The 2D grid (torus) representing

  private int worldSize;                        // The size of the world's 'sides'
  private int numPlayers;                       // The number of agents
  private double pALLC, pTFT, pATFT, pALLD;     // Probability of types
  private int num[];                            // Number of agents of the different
  // types

  private int maxIter;                          // Number of iteration cycles
  private double pAdapt;                        // Probability of adaptation
  private String topology;

  private DisplaySurface dsurf = null;

  public Model(){
    params = new String[]{"worldSize", "pALLC", "pTFT", "pATFT", "pALLD", "maxIter",
                          "pAdapt", "Topology"};
    name = "GridIPD Model (Tutorial Step #4)";

    worldSize = 5;
    pALLC = 0.25;
    pTFT = 0.25;
    pATFT = 0.25;
    pALLD = 0.25;

    maxIter = 4;
    pAdapt = 0.2;
    topology = "";
    Vector v = new Vector();
    v.add("von neumann");
    v.add("moore");
    v.add("smallworld");
    ListPropertyDescriptor pd = new ListPropertyDescriptor("Topology", v);
    descriptors.put("Topology", pd);
    topology = "von neumann";
  }

  public void setup() {
    super.setup();
    if(dsurf != null){
      dsurf.dispose();
    }
    dsurf = null;
    ((AbstractGUIController) super.getController()).setConsoleErr(false);
    ((AbstractGUIController) super.getController()).setConsoleOut(false);
  }

  public void buildModel() {

    numPlayers = worldSize * worldSize;

    num = new int[4];
    num[ALLC] = (int) (numPlayers * pALLC);
    num[TFT] = (int) (numPlayers * pTFT);
    num[ATFT] = (int) (numPlayers * pATFT);
    num[ALLD] = (int) (numPlayers * pALLD);

    if (num[ALLD] != numPlayers - num[ALLC] - num[TFT] - num[ATFT])
      num[ALLD] = numPlayers - num[ALLC] - num[TFT] - num[ATFT];

    int playerID = 0;

    for (int playerType = ALLC; playerType < NUMTYPES; playerType++)
      for (int i = 0; i<num[playerType]; i++) {
        playerID++;
        Player aPlayer = new Player(playerID, playerType, this);
        agentList.add(aPlayer);
      }
    String relationType;
    System.out.println("topology = " + topology);
    System.out.println("agentList.s = " + agentList.size());
    if(topology.equalsIgnoreCase("von neumann")){
      world = ContextFactory.getGridFromList(worldSize, worldSize, agentList, true);
      relationType = VonNeumannTopology.type;
      dsurf = new DisplaySurface(this, "IPD");
      Displayable display = ((DisplayableContext)world).getGui();
      dsurf.addDisplayableProbeable(display, "IPD ");
      this.registerDisplaySurface("IPD Display", dsurf);
      dsurf.display();
    }else if(topology.equalsIgnoreCase("moore")){
      world = ContextFactory.getGridFromList(worldSize, worldSize, agentList, true);
      relationType = MooreTopology.type;
      dsurf = new DisplaySurface(this, "IPD");
      Displayable display = ((DisplayableContext)world).getGui();
      dsurf.addDisplayableProbeable(display, "IPD ");
      this.registerDisplaySurface("IPD Display", dsurf);
      dsurf.display();
    }else{
      world = ContextFactory.getSmallWorldFromList(agentList, worldSize, worldSize, true,
                                                   1, .01);
      relationType = "SMALL_WORLD";
    }

    Iterator i = agentList.iterator();
    while(i.hasNext()){
      Player p = (Player) i.next();
      p.setWorld(world);
      p.setRelationshipType(relationType);
    }

    reportResults();
  }

  public void resetPlayers() {
    Iterator i = agentList.iterator();
    while(i.hasNext()){
      Player aPlayer = (Player) i.next(); // Pick an agent
      aPlayer.reset();                            // Let it reset it's stats
    }
  }

  public void play(){
    Iterator i = agentList.iterator();
    while(i.hasNext()){
      Player p = (Player) i.next();
      p.interact();
    }
  }


  public void adaptation() {
    Iterator i = agentList.iterator();
    while(i.hasNext()){
      Player aPlayer = (Player) i.next();
      aPlayer.adapt();
    }
    i = agentList.iterator();
    while(i.hasNext()){
      Player aPlayer = (Player) i.next();
      aPlayer.updateType();
    }
  }

  public void reportResults() {

    for (int i=ALLC; i<NUMTYPES; i++)
      num[i] = 0;

    for (int i=0; i<numPlayers; i++) {
      Player aPlayer = (Player) agentList.get(i);
      num[aPlayer.getType()]++;
    }

    System.out.print(getTickCount() + ": ");
    for (int playerType=ALLC; playerType < NUMTYPES; playerType++)
      System.out.print(num[playerType]+" ");
    System.out.println();
  }

  public void checkGrid(){
    Object2DGrid grid = (Object2DGrid) world;
    for(int x = 0 ; x < grid.getSizeX() ; x++){
      StringBuffer buf = new StringBuffer();
      for(int y = 0 ; y < grid.getSizeY() ; y++){
        Object o = grid.getObjectAt(x,y);
        buf.append(o + " ");
      }
      System.out.println(buf);
    }
  }

  public void step() {
    checkGrid();
    resetPlayers();     // Reset the agents' statistics
    play();
    adaptation();       // Let them adapt
    reportResults();    // Calculate and report some statistics
    if(!topology.equalsIgnoreCase("smallworld")){
      System.out.println("topology = " + topology);
      dsurf.updateDisplay();
    }
  }

  public int getWorldSize() {
    return worldSize;
  }

  public void setWorldSize(int worldSize) {
    this.worldSize = worldSize;
  }

  public double getpALLC() {
    return pALLC;
  }

  public void setpALLC(double pALLC) {
    this.pALLC = pALLC;
  }

  public double getpTFT() {
    return pTFT;
  }

  public void setpTFT(double pTFT) {
    this.pTFT = pTFT;
  }

  public double getpATFT() {
    return pATFT;
  }

  public void setpATFT(double pATFT) {
    this.pATFT = pATFT;
  }

  public double getpALLD() {
    return pALLD;
  }

  public void setpALLD(double pALLD) {
    this.pALLD = pALLD;
  }

  public int getMaxIter() {
    return maxIter;
  }

  public void setMaxIter(int maxIter) {
    this.maxIter = maxIter;
  }

  public double getPAdapt() {
    return pAdapt;
  }

  public void setPAdapt(double pAdapt) {
    this.pAdapt = pAdapt;
  }

  public Context getWorld() {
    return world;
  }

  public void setWorld(Context world) {
    this.world = world;
  }

  public String getTopology() {
    return topology;
  }

  public void setTopology(String topology) {
    this.topology = topology;
  }

/////////////////////////////////////////////////////////////////////////
// Creating and starting your model /////////////////////////////////////
/////////////////////////////////////////////////////////////////////////
  public static void main(String[] args) {
    SimInit init = new SimInit();
    Model m = new Model();
    init.loadModel(m, null, false);
  }
}