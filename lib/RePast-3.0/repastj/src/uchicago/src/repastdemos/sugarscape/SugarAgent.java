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
package uchicago.src.repastdemos.sugarscape;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import cern.jet.random.Uniform;

/**
 * The agent for the sugar scape simulation. This agent implements
 * movement rule M, pg. 182, where best is defined by most sugar
 * at the closest location.<p>
 *
 * The source is annotated so see that for more info.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

// If an agent, or any other object is to be displayed in a display it
// needs to implement at least the drawable interface which has one method:
// draw(SimGraphics g)
public class SugarAgent implements Drawable {

  int x, y;                  // my coordinates
  int metabolism;            // how much sugar eat per turn
  int sugar;                // amount of sugar I own
  int vision;                // how far can see
  int maxAge, currentAge;

  private SugarSpace space;
  private SugarModel model;

  public SugarAgent(SugarSpace ss, SugarModel model) {
    space = ss;
    this.model = model;
  }

  public void setXY(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  // Just as the model needs get and set accessor methods in order for
  // its initial parameters to be displayed and thus subject to modification,
  // an agent (or any other object) can be probed through similar get and set
  // methods.
  //
  // Probing consists of clicking on an object in the display, causing that
  // object's current state to be displayed. What is displayed depends on
  // the various get and set methods implemented by the object. For example,
  // if an object has a setMetabolism and a getMetabolism method, a Metabolism
  // field will be displayed providing the current value of the metabolism
  // variable and allowing the user to change the value by entering a new value
  // and pressing enter. As of this release only number, String, and boolean
  // fields can be displayed. Of course a user can use the get and set methods
  // to turn a Vector, for example, into a String or whatever is appropriate.

  public void setMetabolism(int meta) {
    metabolism = meta;
  }

  public void setVision(int vis) {
    vision = vis;
  }

  public int getMetabolism() {
    return metabolism;
  }

  public int getVision() {
    return vision;
  }

  public void setSugar(int sugar) {
    this.sugar = sugar;
  }

  public int getSugar() {
    return sugar;
  }

  public int getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(int age) {
    maxAge = age;
  }

  public int getCurrentAge() {
    return currentAge;
  }

  // step() is called by the scheduler once per tick.
  public void step() {

    // move to the best spot (movement rule M)
    moveToBestSpot();

    // get the sugar at the best spot
    sugar += space.takeSugarAt(x, y);

    // consume an amount of sugar == to my metabolism
    sugar -= metabolism;

    // increae my age
    currentAge++;

    // die if no sugar or my age is greater than my max age
    if (sugar <=0 || currentAge >= maxAge) {
      model.agentDeath(this);
      //model.addNewAgent();
    }
  }

  // implements movement rule M - p. 182 where best spot is most sugar
  // at nearest location.
  private void moveToBestSpot() {
    int bestSugar = -1;
    int bestDistance = -9999;
    int goodx[] = new int[16];
    int goody[] = new int[16];
    int bestSpots = 0;

    int xLook, yLook;

    yLook = y;

    // This should be rewritten to use the Object2DGrid.getVonNeumannNeighbors()
    // methods. Then iterate through those looking for nulls, although
    // distance would be harder to calculate then
    for (xLook = x - vision; xLook <= x + vision; xLook++) {
      if (model.getAgentAt(xLook, yLook) == null) {
        if (space.getSugarAt(xLook, yLook) > bestSugar) {
          bestSugar = space.getSugarAt(xLook, yLook);
          bestDistance = Math.abs(x - xLook);
          bestSpots = 0;
          goodx[0] = xLook;
          goody[0] = yLook;
          bestSpots++;
        } else if (space.getSugarAt(xLook, yLook) == bestSugar) {
          if (Math.abs(x - xLook) < bestDistance) {
            bestDistance = Math.abs(x - xLook);
            bestSpots = 0;
            goodx[0] = xLook;
            goody[0] = yLook;
            bestSpots++;
          } else if (Math.abs(x - xLook) == bestDistance) {
            goodx[bestSpots] = xLook;
            goody[bestSpots] = yLook;
            bestSpots++;
          }
        }
      }
    }

    xLook = x;

    for (yLook = y - vision; yLook <= y + vision; yLook++) {
      if (model.getAgentAt(xLook, yLook) == null) {
        if (space.getSugarAt(xLook, yLook) > bestSugar) {
          bestSugar = space.getSugarAt(xLook, yLook);
          bestDistance = Math.abs(y - yLook);
          bestSpots = 0;
          goodx[0] = xLook;
          goody[0] = yLook;
          bestSpots++;
        } else if (space.getSugarAt(xLook, yLook) == bestSugar) {
          if (Math.abs(y - yLook) < bestDistance) {
            bestDistance = Math.abs(y - yLook);
            bestSpots = 0;
            goodx[0] = xLook;
            goody[0] = yLook;
            bestSpots++;
          } else if (Math.abs(y - yLook) == bestDistance) {
            goodx[bestSpots] = xLook;
            goody[bestSpots] = yLook;
            bestSpots++;
          }
        }
      }
    }

    int chosenSpotIndex = 0;
    // agent go to the best spot
    if (bestSpots != 0) {
      if (bestSpots == 1) {
        chosenSpotIndex = 0;
      } else {
        chosenSpotIndex = Uniform.staticNextIntFromTo(0, bestSpots - 1);
      }
      model.moveAgent(this, goodx[chosenSpotIndex], goody[chosenSpotIndex]);
    }
  }

  // drawable implementation
  // draw() is called whenever the display is updated, assuming the agent
  // is part of what is being displayed.
  public void draw(SimGraphics g) {
    g.drawFastRoundRect(java.awt.Color.red);
  }
}
