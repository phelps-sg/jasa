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
package uchicago.src.repastdemos.gisModel;

import java.util.ArrayList;

import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.space.RasterSpace;
import cern.jet.random.Uniform;

/**
 * A simple model where "bugs" run around a landscape created out of
 * a GIS raster file.
 *
 * @version $Revision$ $Date$
 */
public class GisBugsModel extends SimModelImpl {

  private int pauseVal = -1;
  private Schedule schedule;
  private Object2DGrid world;
  private RasterSpace space;
  private int numAgents;
  private DisplaySurface dsurf;
  private ArrayList agentList = new ArrayList();
  private float randomMoveProbability = 0.5f;
  private double maxDistance = 90;

  public GisBugsModel() {}

  private void buildModel() {
    try{
      String file = "/uchicago/src/repastdemos/gisModel/sample.txt";
      java.io.InputStream stream = getClass().getResourceAsStream(file);
      space = new RasterSpace(stream);
    }catch(Exception e){
      System.out.println(e);
    }
    world = new Object2DGrid(space.getSizeX(), space.getSizeY());
    for(int i = 0 ; i < numAgents ; i++){
      double x, y;
      do{
	x = Uniform.staticNextDoubleFromTo(space.getOriginX(), space.getTermX());
	y = Uniform.staticNextDoubleFromTo(space.getOriginY(), space.getTermY());
     } while (world.getObjectAt(space.getCellCol(x), space.getCellRow(y)) != null);
    GisBug agent = new GisBug(space, world, x, y, randomMoveProbability, maxDistance);
      world.putObjectAt(space.getCellCol(x), space.getCellRow(y), agent);
      agentList.add(agent);
    }
  }

  private void buildDisplay() {
    Object2DDisplay agentDisplay = new Object2DDisplay(world);
    agentDisplay.setObjectList(agentList);
    // 64 shades of red
    ColorMap map = new ColorMap();
    for (int i = 0; i < 9; i++) {
      map.mapColor(i, i / 8.0, 0, 0);
    }
    Value2DDisplay rasterDisplay = new Value2DDisplay(space, map);
    rasterDisplay.setZeroTransparent(true);
    rasterDisplay.setDisplayMapping(1, 0);
    dsurf.addDisplayable(rasterDisplay, "Gis Space");
    dsurf.addDisplayableProbeable(agentDisplay, "Agents");
    addSimEventListener(dsurf);
  }

  public void step() {
    for (int i = 0 ; i < agentList.size() ; i++){
      ((GisBug) agentList.get(i)).step();
    }
    dsurf.updateDisplay();
  }

  private void buildSchedule() {
    schedule.scheduleActionBeginning(1, this, "step");
  }

  public void begin() {
    //setRngSeed(1972L);
    buildModel();
    buildDisplay();
    buildSchedule();

    dsurf.display();
  }

  public void setup() {

    if (dsurf != null)
      dsurf.dispose();
    dsurf = null;
    schedule = null;
    System.gc();

    numAgents = 5;
    dsurf = new DisplaySurface(this, "Gis Model Display");
    registerDisplaySurface("Main", dsurf);
    schedule = new Schedule(1);
    agentList = new ArrayList();

    pauseVal = -1;

    world = null;
    space = null;
  }

  public int getNumAgents(){
    return numAgents;
  }

  public void setNumAgents(int agents){
    numAgents = agents;
  }
 
  public float getRandomMoveProbability(){
    return randomMoveProbability;
  }

  public void setRandomMoveProbability(float chance){
    randomMoveProbability = chance;
  } 
 
  public double getMaxDistance(){
    return maxDistance;
  }

  public void setMaxDistance(double max){
    maxDistance = max;
  } 
  
  public String[] getInitParam() {
    String[] params = {"Pause","numAgents","randomMoveProbability","maxDistance"};
    return params;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public String getName() {
    return "GisModel";
  }

  public void setPause(int tick) {
    schedule.scheduleActionAt(tick, this, "pause", Schedule.LAST);
    pauseVal = tick;
  }

  public int getPause() {
    return pauseVal;
  }


  public static void main(String[] args){
    uchicago.src.sim.engine.SimInit init = new uchicago.src.sim.engine.SimInit();
    GisBugsModel model = new GisBugsModel();
    init.loadModel(model, null, false);
  }
}
