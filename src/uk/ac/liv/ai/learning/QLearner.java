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

import java.io.Serializable;

import uk.ac.liv.util.DiscreteProbabilityDistribution;
import uk.ac.liv.util.Resetable;
import uk.ac.liv.util.Parameterizable;

import ec.util.MersenneTwisterFast;
import ec.util.ParameterDatabase;
import ec.util.Parameter;

/**
 * An implementation of the Q-learning algorithm,
 * with epsilon-greedy exploration.
 *
 * @author Steve Phelps
 */


public class QLearner
    implements MDPLearner, Resetable, Serializable,
                Parameterizable {


  /**
   * The number of possible states
   */
  int numStates;

  /**
   * The number of possible actions
   */
  int numActions;

  /**
   * The matrix representing the estimated payoff of each
   * possible action in each possible state.
   */
  double q[][];

  /**
   * The learning rate.
   */
  double learningRate;

  /**
   * The discountRate for future payoffs.
   */
  double discountRate;

  /**
   * The parameter representing the probability of choosing
   * a random action on any given iteration.
   */
  double epsilon;

  /**
   * The previous state
   */
  int previousState;

  /**
   * The current state
   */
  int currentState;

  /**
   * The last action that was chosen.
   */
  int lastActionChosen;

  MersenneTwisterFast randGenerator = new MersenneTwisterFast();

  int bestAction;

  static final double DEFAULT_EPSILON = 0.2;
  static final double DEFAULT_LEARNING_RATE = 0.5;
  static final double DEFAULT_DISCOUNT_RATE = 0.8;

  static final String P_EPSILON = "e";
  static final String P_LEARNING_RATE = "p";
  static final String P_DISCOUNT_RATE = "g";
  static final String P_NUM_ACTIONS = "k";
  static final String P_NUM_STATES = "s";

  public QLearner( int numStates, int numActions, double epsilon,
                    double learningRate, double discountRate  ) {
    setStatesAndActions(numStates, numActions);
    this.learningRate = learningRate;
    this.discountRate = discountRate;
    this.epsilon = epsilon;
    initialise();
  }

  public QLearner() {
    this(0, 0, DEFAULT_EPSILON, DEFAULT_LEARNING_RATE,
          DEFAULT_DISCOUNT_RATE);
  }

  public void initialise() {
    for( int s=0; s<numStates; s++ ) {
      for( int a=0; a<numActions; a++ ) {
        q[s][a] = 0;
      }
    }
  }

  public void setStatesAndActions( int numStates, int numActions ) {
    this.numStates = numStates;
    this.numActions = numActions;
    q = new double[numStates][numActions];
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    learningRate =
      parameters.getDoubleWithDefault(base.push(P_LEARNING_RATE), null,
                                      DEFAULT_LEARNING_RATE);

    discountRate =
      parameters.getDoubleWithDefault(base.push(P_DISCOUNT_RATE), null,
                                      DEFAULT_DISCOUNT_RATE);

    epsilon =
      parameters.getDoubleWithDefault(base.push(P_EPSILON), null,
                                      DEFAULT_EPSILON);

    numStates = parameters.getInt(base.push(P_NUM_STATES), null, 0);
    numActions = parameters.getInt(base.push(P_NUM_ACTIONS), null, 0);
    setStatesAndActions(numStates, numActions);
  }

  public void setState( int newState ) {
    previousState = currentState;
    currentState = newState;
  }

  public int act() {
    double e = randGenerator.nextDouble();
    if ( e <= epsilon ) {
      return randGenerator.nextInt(numActions);
    } else {
      return bestAction(currentState);
    }
  }

  public void newState( double reward, int newState ) {
    updateQ(reward, newState);
    setState(newState);
  }

  protected void updateQ( double reward, int newState ) {
    q[currentState][lastActionChosen] =
      learningRate * (reward + discountRate * maxQ(newState))
        + (1-learningRate) * q[currentState][lastActionChosen];
  }

  public double maxQ( int newState ) {
    double max = Double.NEGATIVE_INFINITY;
    for( int a=0; a<numActions; a++ ) {
      if ( q[newState][a] > max ) {
        max = q[newState][a];
        bestAction = a;
      }
    }
    return max;
  }

  public int bestAction( int state ) {
    double payoff = maxQ(state);
    return bestAction;
  }

  public void reset() {
    initialise();
  }

}