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
package uchicago.src.repastdemos.enn;

import java.awt.Color;
import java.util.Vector;

import uchicago.src.sim.games.GameAgent;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DTorus;
import uchicago.src.sim.util.SimUtilities;
import cern.jet.random.Uniform;

/**
 * A partial implementation of Randal Picker's Endogenous Neighborhood
 * game as described in his as yet unpublished "Endogenous Neighborhoods and
 * Norms." This simulation extends prior work by Randal Picker. See "Simple
 * Games in a Complex World: A Generative Approach to the Adoption of Norms",
 * 64 University of Chicago Law Review 1225 (1997). Thanks to Randal Picker
 * for allowing us to include this with Repast.
 *
 * This agent plays a coordination game with its neighbors (if any).
 * According its descision strategy an agent plays left or right each turn,
 * calculates the payoffs, and decides what to do next turn.
 *
 * @author Nick Collier and Randal Picker
 * @version $Revision$ $Date$
 */

// Implements Drawable to be displayed and GameAgent to play the EnnGame
// GameAgent is not strictly necessary, but EnnGame as an implementation
// of the Game interface allows any GameAgent to play the EnnGame.
public class EnnAgent implements GameAgent, Drawable {

  // static consts for the strategies
  public static final int PURE_ASPIRATION = 0;
  public static final int STRATEGY_UPGRADE = 1;
  public static final int CHANGE_FIRST = 2;
  public static final int MOVE_AND_CHANGE = 3;

  private int x;
  private int y;
  private boolean content;
  private int aspiration;
  private int curStrategy;
  private int prevStrategy;
  private int contentDescStrategy;
  private int discontentDescStrategy;
  private int decisionStrategy;
  private int prevDescStrategy = -1;
  private float payoff;

  private EnnGame myGame;
  private Object2DTorus mySpace;
  private Vector myNeighborhood;

  class Point {
    int x;
    int y;
  }

  private Point tempPoint = new Point();

	public EnnAgent(int xValue, int yValue, int aspirationValue,
        int curStrategyValue, EnnGame game, Object2DTorus space,
        int contentStrategy, int discontentStrategy)
  {
		x = xValue;
		y = yValue;
		aspiration = aspirationValue;
		curStrategy = curStrategyValue;
    prevStrategy = curStrategy;
    myGame = game;
    mySpace = space;
    contentDescStrategy = contentStrategy;
    discontentDescStrategy = discontentStrategy;
	}

  public float getPayoff() {
    return payoff;
  }


  // get/set methods allowing the agent's state to be probed. For more on
  // this see SugarAgent.java
	public int getX() {
		return x;
	}

	public void setX(int xValue) {
		x = xValue;
	}

	public int getY() {
		return y;
	}

	public void setY(int yValue) {
		y = yValue;
	}

	public boolean getContent() {
		return content;
	}

	public void setContent(boolean contentValue) {
		content = contentValue;
	}

	public int getAspiration() {
		return aspiration;
	}

	public void setAspiration(int aspirationValue) {
		aspiration = aspirationValue;
	}

	public int getCurStrategy() {
		return curStrategy;
	}

	public void setCurStrategy(int curStrategyValue) {
    prevStrategy = curStrategy;
		curStrategy = curStrategyValue;
	}

	public int getPrevStrategy() {
		return prevStrategy;
	}

	public void setPrevStrategy(int prevStrategyValue) {
		prevStrategy = prevStrategyValue;
	}

  /**
   * Performs the first part of agent's behavoir for this turn.
   * This consists of playing the game, and calculating the payoff. The second
   * part of the agents behavoir consists of comparing the payoff to the
   * aspiration level and then performing the appropriate action based on
   * that result and the agent's decision strategy.<p>
   *
   * An agent's action is broken up into two parts in order that all agent's
   * play the same game on the same field. And then take action after all
   * the agent's have played. Collapsing both parts into a "step()" method
   * and calling that from the schedule means that each agent plays, then
   * moves etc. and then the next agent plays etc.
   */
  public void playGame() {
    myNeighborhood = mySpace.getMooreNeighbors(x, y, 2, 2, false);
    myGame.play(this, myNeighborhood);
    if (payoff >= aspiration) {
      content = true;
      decisionStrategy = contentDescStrategy;
    } else {
      content = false;
      if (prevDescStrategy == CHANGE_FIRST) {
        decisionStrategy = MOVE_AND_CHANGE;
      } else {
        decisionStrategy = discontentDescStrategy;
      }
    }
  }

  /**
   * Decide what (left or right) to play next turn depending on how payoff
   * meets aspiration and what the agent's decision strategy is.
   */
  public void makeStrategyDecision() {
    switch (decisionStrategy) {
      case STRATEGY_UPGRADE:
        prevStrategy = curStrategy;
        curStrategy = findBestAverageStrategy(myNeighborhood);
        break;
      case PURE_ASPIRATION:
        prevStrategy = curStrategy;
        break;

      case CHANGE_FIRST:
        prevStrategy = curStrategy;
        if (curStrategy == LEFT)
          curStrategy = RIGHT;
        else
          curStrategy = LEFT;
        break;

      case MOVE_AND_CHANGE:
        prevStrategy = curStrategy;
        agentMove();
        curStrategy = findBestAverageStrategy(mySpace.getMooreNeighbors(x, y, 2, 2, false));
        break;

      default:
        throw new IllegalArgumentException("Illegal decision strategy");
    }
  }

  private int findBestAverageStrategy(Vector neighborhood) {
    int numPlayLeft = 0;
    int numPlayRight = 0;
    float leftPayoff = 0;
    float rightPayoff = 0;

    // count self
    if (curStrategy == LEFT) {
      numPlayLeft++;
      leftPayoff = payoff;
    } else {
      numPlayRight++;
      rightPayoff = payoff;
    }

    int nghStrategy = 0;
    float nghPayoff = 0;
    for (int i = 0; i < neighborhood.size(); i++) {
      EnnAgent a = (EnnAgent)neighborhood.get(i);
      nghStrategy = a.getCurStrategy();
      nghPayoff = a.getPayoff();

      if (nghStrategy == LEFT) {
        numPlayLeft++;
        leftPayoff += nghPayoff;
      } else {
        numPlayRight++;
        rightPayoff += nghPayoff;
      }
    }

    if (numPlayLeft == 0) {
      return RIGHT;
    } else if (numPlayRight == 0) {
      return LEFT;
    } else {
      float rightAvg = rightPayoff / numPlayRight;
      float leftAvg = leftPayoff / numPlayLeft;
      return rightAvg > leftAvg ? RIGHT : rightAvg < leftAvg ? LEFT : curStrategy;
    }
  }

  private void getRandomSpot(int _x, int _y) {

    int heading = Uniform.staticNextIntFromTo(0, 359);
    int distance = Uniform.staticNextIntFromTo(1, 10);

    double[] point = SimUtilities.getPointFromHeadingAndDistance(heading, distance);

    int newY = _y + (int)Math.round(point[1]);
    int newX = _x + (int)Math.round(point[0]);

    tempPoint.x = newX;
    tempPoint.y = newY;
  }

  public void agentMove() {
    getRandomSpot(x, y);
    int i = 0;
    while (mySpace.getObjectAt(tempPoint.x, tempPoint.y) != null ) {
      getRandomSpot(tempPoint.x, tempPoint.y);
      if ( i > 20) break;
      i++;
    }

    if (i <= 20) {
      mySpace.putObjectAt(x, y, null);
      mySpace.putObjectAt(tempPoint.x, tempPoint.y, this);
      x = mySpace.xnorm(tempPoint.x);
      y = mySpace.ynorm(tempPoint.y);
    }
  }


  // GameAgent interface
  public int getStrategy() {
    return curStrategy;
  }

  public void setPayoff(float payoff) {
    this.payoff = payoff;
  }

  public void setStrategy(int newStrategy) {
    prevStrategy = curStrategy;
    curStrategy = newStrategy;
  }

  // drawable interface
  // Agents are drawn different colors to identify their current strategies.
  // Could also draw them as different shapes too.
  public void draw(SimGraphics g) {
    Color c;
    if (prevStrategy == LEFT) {
      if (curStrategy == LEFT) {
        c = Color.blue;
      } else {
        c = Color.yellow;
      }
    } else {
      if (curStrategy == RIGHT) {
        c = Color.red;
      } else {
        c = Color.green;
      }
    }
    g.drawFastRect(c);
  }
}


