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
package uchicago.src.repastdemos.heatBugs;

import java.util.ArrayList;

import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.space.Object2DTorus;
import cern.jet.random.Uniform;

/**
 * A translation of the Swarm example simulation Heat Bugs. The heat bugs
 * simulation consists of heat bugs - simple agents that absorb and expel heat
 * and a heatspace which diffuses this heat into the area surrounding the
 * bug. Heat bugs have an ideal temperature and will move about the space
 * in attempt to achieve this idea temperature.
 *
 * @author Swarm Project and Nick Collier
 * @version $Revision$ $Date$
 */

public class HBNoGui extends SimModelImpl {

  private int numBugs = 100;
  private double evapRate = 0.99;
  private double diffusionConstant = 1.0;
  private int worldXSize = 80;
  private int worldYSize = 80;
  private int minIdealTemp = 17000;
  private int maxIdealTemp = 31000;
  private int minOutputHeat = 3000;
  private int maxOutputHeat = 10000;
  private float randomMoveProbability = 0.0f;

  private Schedule schedule;
  private ArrayList heatBugList = new ArrayList();
  private Object2DTorus world;
  private HeatSpace space;

  public HBNoGui() {

  }

  private void buildModel() {
    space = new HeatSpace(diffusionConstant, evapRate, worldXSize, worldYSize);
    world = new Object2DTorus(space.getSizeX(), space.getSizeY());

    for (int i = 0; i < numBugs; i++) {
      int idealTemp = Uniform.staticNextIntFromTo(minIdealTemp, maxIdealTemp);
      int outputHeat = Uniform.staticNextIntFromTo(minOutputHeat, maxOutputHeat);
      int x, y;


      do {
        x = Uniform.staticNextIntFromTo(0, space.getSizeX() - 1);
        y = Uniform.staticNextIntFromTo(0, space.getSizeY() - 1);
      } while (world.getObjectAt(x, y) != null);

      HeatBug bug = new HeatBug(space, world, x, y, idealTemp, outputHeat,
                                randomMoveProbability);
      world.putObjectAt(x, y, bug);
      heatBugList.add(bug);
    }
  }

  public void step() {
    space.diffuse();
    for (int i = 0; i < heatBugList.size(); i++) {
      HeatBug bug = (HeatBug)heatBugList.get(i);
      bug.step();
    }
  }


  private void buildSchedule() {


    /*
    class HeatBugsRunner extends BasicAction {
      public void execute() {
        space.diffuse();
        for (int i = 0; i < heatBugList.size(); i++) {
          HeatBug bug = (HeatBug)heatBugList.get(i);
          bug.step();
        }
      }
    };

    HeatBugsRunner run = new HeatBugsRunner();
    */

    schedule.scheduleActionBeginning(1, this, "step");

    schedule.scheduleActionAt(300, this, "stop");

  }

  public void begin() {
    buildModel();
    buildSchedule();
  }

  public void setup() {

    numBugs = 100;
    evapRate = 0.99;
    diffusionConstant = 1.0;
    worldXSize = 80;
    worldYSize = 80;
    minIdealTemp = 17000;
    maxIdealTemp = 31000;
    minOutputHeat = 3000;
    maxOutputHeat = 10000;
    randomMoveProbability = 0.0f;

    schedule = null;
    schedule = new Schedule(1);
    heatBugList = new ArrayList();
    world = null;
    space = null;

    System.gc();

  }

  public String[] getInitParam() {
    String[] params = {"evapRate", "diffusionConstant", "maxIdealTem",
          "minIdealTemp", "maxOutputHeat", "minOutputHeat", "worldXSize",
          "worldYSize", "numBugs"};
    return params;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public String getName() {
    return "HeatBugs - NoGUI";
  }

  // properties
  public int getNumBugs() {
    return numBugs;
  }

  public void setNumBugs(int numBugs) {
    this.numBugs = numBugs;
  }

  public double getEvapRate() {
    return evapRate;
  }

  public void setEvapRate(double rate) {
    evapRate = rate;
  }

  public double getDiffusionConstant() {
    return diffusionConstant;
  }

  public void setDiffusionConstant(double constant) {
    diffusionConstant = constant;
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

  public int getMinIdealTemp() {
    return minIdealTemp;
  }

  public void setMinIdealTemp(int temp) {
    minIdealTemp = temp;
  }

  public int getMaxIdealTemp() {
    return maxIdealTemp;
  }

  public void setMaxIdealTemp(int temp) {
    maxIdealTemp = temp;
  }

  public int getMinOutputHeat() {
    return minOutputHeat;
  }

  public void setMinOutputHeat(int heat) {
    minOutputHeat = heat;
  }

  public int getMaxOutputHeat() {
    return maxOutputHeat;
  }

  public void setMaxOutputHeat(int heat) {
    maxOutputHeat = heat;
  }

  public float getRandomMoveProbability() {
    return randomMoveProbability;
  }

  public void setRandomMoveProbability(float prob) {
    randomMoveProbability = prob;
  }

  public void stop() {
    System.out.println("Stop");
    this.fireStopSim();
    this.fireEndSim();
  }
}