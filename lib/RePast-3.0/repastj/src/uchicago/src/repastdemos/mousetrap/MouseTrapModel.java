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
package uchicago.src.repastdemos.mousetrap;

import java.awt.Color;

import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.ActionGroup;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Object2DTorus;
import uchicago.src.sim.util.Random;
import cern.jet.random.Uniform;

/**
 * A port of the mousetrap simulation from the Swarm simulation toolkit. This
 * is a good example of dynamic scheduling and how to do discrete-event
 * simulations using Schedule objects.<p>
 *
 * The simulation is as follows. A torus is populated with "mousetraps."
 * These traps each contain some n number of balls. A ball is thrown from
 * the "outside" onto the center mousetrap. This trap triggers and throws
 * its n number of balls into the air. These balls then trigger other traps
 * and so on and so on. In terms of implementation, the first trap has its
 * trigger method scheduled at 0 with Schedule.scheduleActionAt. When
 * a mousetrap is triggered it schedules a trigger method on n of its
 * surrounding mousetraps, where n is the number of balls each mousetrap holds.
 * The actual scheduling is done through the use of a TriggerAction class that
 * extends BasicAction. This TriggerAction is passed the MouseTrap to schedule
 * in its constructor, and
 * its execute method calls trigger on this MouseTrap. See the source for
 * details.
 *
 * @author Nick Collier (Modified by Michael J. North)
 * @version $Revision$ $Date$
 */

public class MouseTrapModel extends SimModelImpl {

  private int numBalls;
  private double triggerProb;
  private int maxTriggerDistance;
  private int maxTriggerTime;
  private int gridSize;
  private double trapDensity;

  private Schedule schedule;
  private DisplaySurface dsurf;
  private Object2DTorus space;
  private OpenSequenceGraph graph;

  private int numTriggered;
  private int numBallsInAir;

  private double lastTriggerTick = 0;

  private void buildModel() {
    space = new Object2DTorus(gridSize, gridSize);

    for (int x = 0; x < gridSize; x++) {
      for (int y = 0; y < gridSize; y++) {

        // always put one in the center so model can start
        if (x == gridSize / 2 && y == gridSize / 2) {
          MouseTrap mt = new MouseTrap(x, y, this, space);
          space.putObjectAt(x, y, mt);
        } else if (trapDensity >= 1.0 ||
            Uniform.staticNextDoubleFromTo(0, 1) < trapDensity)
        {
          MouseTrap mt = new MouseTrap(x, y, this, space);
          space.putObjectAt(x, y, mt);
        }
      }
    }
  }

  private void buildDisplay() {
    final Object2DDisplay display = new Object2DDisplay(space);
    dsurf.addDisplayableProbeable(display, "Mouse Traps");
    dsurf.setBackground(Color.black);

    addSimEventListener(dsurf);

    graph.setYRange(0.0, 40);
    graph.setYIncrement(200);

    // have the graph create the sequence for us from the getNumTriggered method.
    graph.createSequence("Traps Triggered", this, "getNumTriggered");

    // create our own sequence and add it to the graph. We could also have added this
    // in the same ways as above.
    graph.addSequence("Balls In Air", new Sequence() {
      public double getSValue() {
        return getNumBallsInAir();
      }
    });

    graph.setAxisTitles("Time", "Amount");
  }

  private void buildSchedule() {
	  // Using a sequential ActionGroup and executing it
	  // only once at the first tick ensures that the trigger action
	  // given below occurs before the display, graphing actions in
	  // everyTickAction. This keeps the display and graph in synch
	  // with the actual state of the model on the first tick.
    ActionGroup firstGroup = new ActionGroup(ActionGroup.SEQUENTIAL);
    firstGroup.addAction(new BasicAction() {
      public void execute() {
        MouseTrap mt = (MouseTrap)space.getObjectAt(gridSize / 2, gridSize / 2);
        addOneBall();
        mt.trigger();
      }
    });

    BasicAction everyTickAction = new BasicAction() {
      public void execute() {
        dsurf.updateDisplay();
        graph.step();
      }
    };

    firstGroup.addAction(everyTickAction);
    schedule.scheduleActionAt(1, firstGroup);
    //schedule.scheduleActionBeginning(2, everyTickAction);
  }

  public void scheduleTrigger(double time, MouseTrap mt) {
    TriggerAction ta = new TriggerAction(mt);
    schedule.scheduleActionAt(time, ta, Schedule.LAST);
  }

  public int getNumBalls() {
    return numBalls;
  }

  public void setNumBalls(int balls) {
    numBalls = balls;
  }

  public double getTriggerProbability() {
    return triggerProb;
  }

  public void setTriggerProbability(double prob) {
    triggerProb = prob;
  }

  public int getGridSize() {
    return gridSize;
  }

  public void setGridSize(int size) {
    gridSize = size;
  }

  public int getMaxTriggerDistance() {
    return maxTriggerDistance;
  }

  public void setMaxTriggerDistance(int distance) {
    maxTriggerDistance = distance;
  }

  public int getMaxTriggerTime() {
    return maxTriggerTime;
  }

  public void setMaxTriggerTime(int time) {
    maxTriggerTime = time;
  }

  public double getTrapDensity() {
    return trapDensity;
  }

  public void setTrapDensity(double density) {
    trapDensity = density;
  }

  public String[] getInitParam() {
    String[] params = {"numBalls", "gridSize",
          "maxTriggerTime", "maxTriggerDistance", "trapDensity",
          "triggerProbability"};
    return params;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public String getName() {
    return "Mouse Traps";
  }

  public void begin() {
    buildModel();
    buildDisplay();
    buildSchedule();

    dsurf.display();
    graph.display();
  }

  public void setup() {
    Random.createUniform();
    if (dsurf != null) {
      dsurf.dispose();
      dsurf = null;
    }

    if (graph != null) {
      graph.dispose();
      graph = null;
    }

    space = null;
    schedule = null;
    System.gc();

    dsurf = new DisplaySurface(this, "Mouse Trap Display");
    registerDisplaySurface("Mouse Trap Display", dsurf);
    graph = new OpenSequenceGraph("Trigger Data vs. Time", this);
    this.registerMediaProducer("Mouse Graph", graph);

    numBalls = 2;
    gridSize = 50;
    triggerProb = 1.0;
    maxTriggerDistance = 4;
    maxTriggerTime = 16;
    trapDensity = 1.0;

    numBallsInAir = 0;
    numTriggered = 0;

    schedule = new Schedule();

    System.gc();
  }

  public int getNumTriggered() {
    //System.out.println("Num Triggered: " + numTriggered);
    return numTriggered;
  }

  public int getNumBallsInAir() {
    return numBallsInAir;
  }

  public void addOneTriggered() {
    numTriggered++;
    //System.out.println("One Triggered");
  }

  public void addOneBall() {
    numBallsInAir++;
  }

  public void removeOneBall() {
    if (numBallsInAir > 0) {
      numBallsInAir--;
    } else {
      System.out.println("Negative number of balls!!!");
    }
  }

  public static void main(String[] args) {
    SimInit init = new SimInit();
    MouseTrapModel model = new MouseTrapModel();
    init.loadModel(model, null, false);
  }

  class TriggerAction extends BasicAction {

    MouseTrap mt;

    public TriggerAction (MouseTrap mt) {
      this.mt = mt;
    }

    public void execute () {
      mt.trigger ();

      // normally we'd just schedule the graph and display updates
      // every tick but we don't do that here so we can see the tick count
      // clock jump ahead at random increments corresponding to the execution
      // this BasicAction. This will get called many times the same tick
      // once we have lots of "balls" in the air, and we don't want to
      // update the graph etc. every time. So we only update it when the
      // tick count changes.
      double curTick = getTickCount();
      if (lastTriggerTick != curTick) {
        dsurf.updateDisplay ();
        graph.step ();
        lastTriggerTick = curTick;
      }
    }
  }
}



