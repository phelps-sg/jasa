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
package uchicago.src.repastdemos.jain;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Vector;

import uchicago.src.reflector.ListPropertyDescriptor;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Controller;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.AbstractGraphLayout;
import uchicago.src.sim.gui.CircularGraphLayout;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.FruchGraphLayout;
import uchicago.src.sim.gui.KamadaGraphLayout;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.network.DefaultNode;
import uchicago.src.sim.util.Random;

public class JainModel extends SimModelImpl {

	private int numNodes = 25;
  private ArrayList agentList = new ArrayList();
  private int worldXSize = 400;
  private int worldYSize = 400;
  private int updateEveryN = 5;
  private int initialSteps = 1;
  private double linkProb = 0.05;


  // implementation variables
  private String layoutType = "CircleLayout";
  private DisplaySurface surface;
  private Schedule schedule;
  private AbstractGraphLayout graphLayout;
  //private BasicAction stepMethods;
  private BasicAction initialAction;
  //private BasicAction removeInitialAction;

  public JainModel()
  {
    Vector vect = new Vector();
    vect.add("Fruch");
    vect.add("KK");
    vect.add("CircleLayout");
    ListPropertyDescriptor pd = new ListPropertyDescriptor("LayoutType", vect);
    descriptors.put("LayoutType", pd);
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

  public double getLinkProb() {
    return linkProb;
  }
  
  public void setLinkProb(double prob) {
    linkProb = prob;
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
      agentList.add(new JainNode(x, y, String.valueOf(n)));
    }

  }

	public void removeInitialAction(){
		schedule.removeAction(initialAction);
	}
	
	public void createInitialPop(){
		float total = 0.0f;
		float newtotal = 0.0f;
		for(int i = 0 ; i < agentList.size() ; i++){
			JainNode node = (JainNode) agentList.get(i);
			node.setPop(Random.uniform.nextFloatFromTo(0, 1));
			total += node.getPop();
		}
		for(int i = 0 ; i < agentList.size() ; i++){
			JainNode node = (JainNode) agentList.get(i);
			node.setPop(node.getPop() / total);
			newtotal += node.getPop();
		}
	}
			
	public void createInitialLinks(){
		for(int i = 0 ; i < agentList.size() ; i++){
			JainNode node = (JainNode) agentList.get(i);
			for(int j = 0 ; j < agentList.size() ; j++){
				DefaultNode otherNode = (DefaultNode) agentList.get(j);
				if(!(node == otherNode)){
					if(Random.uniform.nextDoubleFromTo(0,1) < linkProb){
						node.makeEdgeTo(otherNode, Random.uniform.nextFloatFromTo(-1,1), Color.red);
					}
				}
			}
		}
	}

	public void initialAction(){
		graphLayout.updateLayout();
		createInitialPop();
		createInitialLinks();
		surface.updateDisplay();
		
	}

	private void evolvePop(){
		for(int i = 0; i < agentList.size(); i++){
			((JainNode) agentList.get(i)).calcPop(agentList);
		}
		for(int i = 0; i < agentList.size(); i++){
			((JainNode) agentList.get(i)).updatePop();
		}
	}

	private JainNode dropLeastFit(){
		float minPop = 0.0f;
		ArrayList leastFitNodes = new ArrayList();
		for(int i = 0 ; i < agentList.size() ; i++){
			JainNode node = (JainNode) agentList.get(i);
			if(node.getPop() < minPop){
				leastFitNodes.clear();
				minPop = node.getPop();
				leastFitNodes.add(node);
			}else if(node.getPop() == minPop){
				leastFitNodes.add(node);
			}
		}

		JainNode drop = (JainNode) agentList.get(Random.uniform.nextIntFromTo(0, agentList.size() - 1));
		ArrayList inEdge = drop.getFromNodes();
		for(int i = 0 ; i < inEdge.size() ; i++){
			DefaultNode otherNode = (DefaultNode) inEdge.get(i);
			otherNode.removeEdgesTo(drop);
		}
		drop.clearOutEdges();
		agentList.remove(agentList.indexOf(drop));
		return drop;
	}

	private float getMinPop(){
		float minPop = 0.0f;
		ArrayList leastFitNodes = new ArrayList();
		for(int i = 0 ; i < agentList.size() ; i++){
			JainNode node = (JainNode) agentList.get(i);
			if(node.getPop() < minPop){
				leastFitNodes.clear();
				minPop = node.getPop();
			}
		}
		return minPop;
	}
		
	private JainNode addNewNode(){
		int x = Random.uniform.nextIntFromTo(0, worldXSize - 1);
		int y = Random.uniform.nextIntFromTo(0, worldYSize - 1);
		JainNode node = new JainNode(x, y, getMinPop(), String.valueOf(agentList.size() + 1));
		for(int j = 0 ; j < agentList.size() ; j++){
			DefaultNode otherNode = (DefaultNode) agentList.get(j);
			if(Random.uniform.nextDoubleFromTo(0,1) < linkProb){
				if(!(node == otherNode)){
					node.makeEdgeTo(otherNode, Random.uniform.nextFloatFromTo(-1,1), Color.red);
				}else{
					node.makeEdgeTo(otherNode, Random.uniform.nextFloatFromTo(-1,0), Color.green);
				}
			}
		}
		agentList.add(node);
		evolvePop();
		return node;
	}

	public void mainAction(){
		evolvePop();
		dropLeastFit();
		addNewNode();
		graphLayout.setList(agentList);
		graphLayout.updateLayout();
		surface.updateDisplay();
	}

  public void buildDisplay()
  {
    if (layoutType.equals("KK")) {
      graphLayout = new KamadaGraphLayout(agentList, worldXSize, worldYSize,
					  surface, updateEveryN);
    } else if (layoutType.equals("Fruch")) {
      graphLayout = new FruchGraphLayout(agentList, worldXSize, worldYSize,
					 surface, updateEveryN);
    } else if (layoutType.equals("CircleLayout")) {
      graphLayout = new CircularGraphLayout(agentList, worldXSize,
					    worldYSize);
    }
		graphLayout.setUpdate(true);
    Controller c = (Controller)getController();
    c.addStopListener(graphLayout);
    c.addPauseListener(graphLayout);
    c.addExitListener(graphLayout);
    
    Network2DDisplay display = new Network2DDisplay(graphLayout);
    surface.addDisplayableProbeable(display, "Jain Display");

//    surface.addZoomable(display);
    surface.setBackground(java.awt.Color.white);
    addSimEventListener(surface);
  }

  private void buildSchedule() {
		initialAction = schedule.scheduleActionAt(1, this, "initialAction");
		schedule.scheduleActionAt(initialSteps,this, "removeInitialAction",
				Schedule.LAST);
		schedule.scheduleActionBeginning(initialSteps + 1, this, "mainAction");
  }

  public void begin() {
    buildModel();
    buildDisplay();
    buildSchedule();
    surface.display();
  }

  public void setup() {
    Random.createUniform();
    if (surface != null) surface.dispose();
    
    surface = null;
    schedule = null;

    System.gc();

    surface = new DisplaySurface(this, "JainModel Display");
    registerDisplaySurface("Main Display", surface);
    schedule = new Schedule();
    agentList = new ArrayList();
    worldXSize = 500;
    worldYSize = 500;
  }

  public String[] getInitParam() {
    String[] params = {"numNodes","worldXSize", "worldYSize",
		       "updateEveryN","LayoutType", "LinkProb"};
    return params;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public String getName() {
    return "JainModel";
  }
  
  public static void main(String[] args) {
    uchicago.src.sim.engine.SimInit init =
      new uchicago.src.sim.engine.SimInit();
    JainModel model = new JainModel();
    init.loadModel(model, null, false);
  }
}
