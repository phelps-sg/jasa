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

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Prototypeable;
import uk.ac.liv.util.Resetable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.io.Serializable;

/**
 * A memory-less version of the Q-Learning algorithm.
 * 
 * This class implements StimuliResponseLearner instead of MDPLearner, and so
 * can be used in place of, e.g. a RothErevLearner.
 * 
 * We use the standard MDP QLearner class, but fool it with this wrapper into
 * thinking that there is only one state.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class StatelessQLearner extends AbstractLearner implements
    StimuliResponseLearner, Parameterizable, Resetable, Serializable,
    Prototypeable {

  /**
   * @uml.property name="qLearner"
   * @uml.associationEnd multiplicity="(1 1)"
   */
  QLearner qLearner;

  public StatelessQLearner() {
    qLearner = new QLearner();
  }

  public StatelessQLearner( int numActions, double epsilon,
      double learningRate, double discountRate ) {

    qLearner = new QLearner(1, numActions, epsilon, learningRate, discountRate);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    super.setup(parameters, base);

    double learningRate = parameters.getDoubleWithDefault(base
        .push(QLearner.P_LEARNING_RATE), null, QLearner.DEFAULT_LEARNING_RATE);

    double discountRate = parameters.getDoubleWithDefault(base
        .push(QLearner.P_DISCOUNT_RATE), null, QLearner.DEFAULT_DISCOUNT_RATE);

    double epsilon = parameters.getDoubleWithDefault(base
        .push(QLearner.P_EPSILON), null, QLearner.DEFAULT_EPSILON);

    int numActions = parameters.getInt(base.push(QLearner.P_NUM_ACTIONS), null);

    qLearner.setStatesAndActions(1, numActions);
    qLearner.setLearningRate(learningRate);
    qLearner.setEpsilon(epsilon);
    qLearner.setDiscountRate(discountRate);
  }

  public int act() {
    return qLearner.act();
  }

  public void reward( double reward ) {
    qLearner.newState(reward, 0);
  }

  public void reset() {
    qLearner.reset();
  }

  public double getLearningDelta() {
    return qLearner.getLearningDelta();
  }

  public int getNumberOfActions() {
    return qLearner.getNumberOfActions();
  }

  public void setNumberOfActions( int n ) {
    qLearner.setStatesAndActions(1, n);
  }

  public void dumpState( uk.ac.liv.util.io.DataWriter out ) {
    qLearner.dumpState(out);
  }

  public Object protoClone() {
    try {
      StatelessQLearner cloned = (StatelessQLearner) this.clone();
      cloned.qLearner = (QLearner) this.qLearner.protoClone();
      return cloned;
    } catch ( CloneNotSupportedException e ) {
      throw new Error(e);
    }
  }

}