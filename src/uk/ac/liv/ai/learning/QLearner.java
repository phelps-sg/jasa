/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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

import uk.ac.liv.util.Prototypeable;
import uk.ac.liv.util.Resetable;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.io.DataWriter;

import uk.ac.liv.prng.GlobalPRNG;

//import edu.cornell.lassp.houle.RngPack.RandomElement;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import org.apache.log4j.Logger;



/**
 * <p>
 * An implementation of the Q-learning algorithm,
 * with epsilon-greedy exploration.
 * </p>
 *
 * <p><b>Parameters</b><br></p>
 * <table>
 *
 * <tr><td valign=top><i>base</i><tt>.k</tt><br>
 * <font size=-1>int >= 0</font></td>
 * <td valign=top>(the number of a possible actions)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.s</tt><br>
 * <font size=-1>int >= 0</font></td>
 * <td valign=top>(the number of states)</td><tr>
 *
 * <tr><td valign=top><i>e</i><tt>.e</tt><br>
 * <font size=-1>double [0,1]</font></td>
 * <td valign=top>(the epsilon parameter)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.p</tt><br>
 * <font size=-1>double [0,1]</font></td>
 * <td valign=top>(the learning rate)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.g</tt><br>
 * <font size=-1>double [0,1]</font></td>
 * <td valign=top>(the discount rate)</td><tr>
 *
 * </table>
 *
 * @author Steve Phelps
 * @version $Revision$
 */


public class QLearner extends AbstractLearner
    implements MDPLearner,  Resetable, Serializable,
                Parameterizable, Prototypeable {


  /**
   * The number of possible states
   */
  protected int numStates;

  /**
   * The number of possible actions
   */
  protected int numActions;

  /**
   * The matrix representing the estimated payoff of each
   * possible action in each possible state.
   */
  protected double q[][];

  /**
   * The learning rate.
   */
  protected double learningRate;

  /**
   * The discount rate for future payoffs.
   */
  protected double discountRate;

  /**
   * The parameter representing the probability of choosing
   * a random action on any given iteration.
   */
  protected double epsilon;

  /**
   * The previous state
   */
  protected int previousState;

  /**
   * The current state
   */
  protected int currentState;

  /**
   * The last action that was chosen.
   */
  protected int lastActionChosen;

  /**
   * The best action for the current state
   */
  protected int bestAction;
  
  protected Uniform randomActionDistribution;
  
  static final double DEFAULT_EPSILON = 0.2;
  static final double DEFAULT_LEARNING_RATE = 0.5;
  static final double DEFAULT_DISCOUNT_RATE = 0.8;

  static final String P_EPSILON = "e";
  static final String P_LEARNING_RATE = "p";
  static final String P_DISCOUNT_RATE = "g";
  static final String P_NUM_ACTIONS = "k";
  static final String P_NUM_STATES = "s";

  static Logger logger = Logger.getLogger(QLearner.class);


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


  public Object protoClone() {
    try {
      QLearner cloned = (QLearner) clone();
      return cloned;
    } catch ( CloneNotSupportedException e ) {
      logger.error(e.getMessage());
      throw new Error(e);
    }
  }
  
  public void initialise() {
    for( int s=0; s<numStates; s++ ) {
      for( int a=0; a<numActions; a++ ) {
        q[s][a] = 0;
      }
    }
    currentState = 0;
    previousState = 0;
    bestAction = 0;
    lastActionChosen = 0;   
    randomActionDistribution = new Uniform(GlobalPRNG.getInstance());    
  }


  public void setStatesAndActions( int numStates, int numActions ) {
    this.numStates = numStates;
    this.numActions = numActions;
    q = new double[numStates][numActions];
  }


  public void setup( ParameterDatabase parameters, Parameter base ) {

    super.setup(parameters, base);

    learningRate =
      parameters.getDoubleWithDefault(base.push(P_LEARNING_RATE), null,
                                      DEFAULT_LEARNING_RATE);

    discountRate =
      parameters.getDoubleWithDefault(base.push(P_DISCOUNT_RATE), null,
                                      DEFAULT_DISCOUNT_RATE);

    epsilon =
      parameters.getDoubleWithDefault(base.push(P_EPSILON), null,
                                      DEFAULT_EPSILON);

    numStates = 
      parameters.getInt(base.push(P_NUM_STATES), null);
    
    numActions = 
      parameters.getInt(base.push(P_NUM_ACTIONS), null);
    
    setStatesAndActions(numStates, numActions);
  }


  public void setState( int newState ) {
    previousState = currentState;
    currentState = newState;
  }


  public int getState() {
    return currentState;
  }


  public int act() {
  	RandomEngine prng = GlobalPRNG.getInstance();    
    if ( prng.raw() <= epsilon ) {
      //lastActionChosen = prng.choose(0, numActions-1);
      lastActionChosen = randomActionDistribution.nextIntFromTo(0, numActions-1);
    } else {
      lastActionChosen = bestAction(currentState);
    }
    return lastActionChosen;
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
    maxQ(state);
    return bestAction;
  }


  public void reset() {
    initialise();
  }


  public void setDiscountRate( double discountRate ) {
    this.discountRate = discountRate;
  }


  public double getDiscountRate() {
    return discountRate;
  }


  public void setEpsilon( double epsilon ) {
    this.epsilon = epsilon;
  }


  public double getEpsilon() {
    return epsilon;
  }


  public int getLastActionChosen() {
    return lastActionChosen;
  }


  public double getLearningDelta() {
    return 0; //TODO
  }


  public void dumpState( DataWriter out ) {
    //TODO
  }


  public int getNumberOfActions() {
    return numActions;
  }

  public double getLearningRate() {
    return learningRate;
  }
  
  public void setLearningRate( double learningRate ) {
    this.learningRate = learningRate;
  }
  
  public int getNumActions() {
    return numActions;
  }
  
  public int getNumStates() {
    return numStates;
  }
  
  public int getPreviousState() {
    return previousState;
  }
  
  public String toString() {
    return "(" + getClass() + " lastActionChosen:" + lastActionChosen
              + " epsilon:" + epsilon + " learningRate:"
              + learningRate + " discountRate:" + discountRate + ")";
  }


}