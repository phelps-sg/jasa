/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.ai.learning;

import uk.ac.liv.util.DiscreteProbabilityDistribution;


public class QLearner implements ReinforcementLearner {

  int numStates;

  int numActions;

  DiscreteProbabilityDistribution p[];

  double q[][];

  double learningRate;

  double k;

  int previousState;

  int currentState;

  int lastActionChosen;

  /**
   * The last action chosen.
   */
  int lastAction;

  public QLearner( int numStates, int numActions,
                    double learningRate, double k ) {
    this.numStates = numStates;
    this.numActions = numActions;
    this.learningRate = learningRate;
    this.k = k;
    q = new double[numStates][numActions];
    p = new DiscreteProbabilityDistribution[numStates];
    for( int s=0; s<numStates; s++ ) {
      p[s] = new DiscreteProbabilityDistribution(numActions);
    }
  }

  public void setState( int state ) {
    previousState = state;
    this.currentState = state;
  }

  public int act() {
    lastActionChosen = p[currentState].generateRandomEvent();
    return lastActionChosen;
  }

  public void newState( double reward, int newState ) {
    updateQ(reward, newState);
    updateProbabilities(newState);
    setState(newState);
  }

  protected void updateQ( double reward, int newState ) {
    q[currentState][lastActionChosen] = learningRate * maxQ(newState);
  }

  public double maxQ( int newState ) {
    double max = Double.NEGATIVE_INFINITY;
    for( int a=0; a<numActions; a++ ) {
      if ( q[newState][a] > max ) {
        max = q[newState][a];
      }
    }
    return max;
  }

  protected void updateProbabilities(int newState) {
    double sigmaKq = 0;
    for( int a=0; a<numActions; a++ ) {
      sigmaKq += Math.pow(k, q[newState][a]);
    }
    for( int a=0; a<numActions; a++ ) {
      p[newState].setProbability(a,Math.pow(k, q[newState][a]) / sigmaKq);
    }
  }


}