/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at e;your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.ai.learning;

import uk.ac.liv.util.io.DataWriter;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.io.Serializable;

/**
 * An implementation of the Widrow-Hoff learning algorithm with momentum
 * for 1-dimensional training sets.
 *
 * @author Steve Phelps
 */

public class WidrowHoffLearner implements MimicryLearner,
                                             Resetable, Serializable {

  /**
   * The learning rate.
   */
  protected double learningRate;

  /**
   * The current output level.
   */
  protected double currentOutput;

  /**
   * The current amount of adjustment to the output.
   */
  protected double delta;

  /**
   * The momentum of the learner.
   */
  protected double momentum;

  public static final double DEFAULT_LEARNING_RATE = 0.1;

  public static final double DEFAULT_MOMENTUM = 0.1;

  public static final String P_MOMENTUM = "momentum";
  public static final String P_LEARNINGRATE = "learningrate";

  public WidrowHoffLearner( double learningRate, double momentum ) {
    this.learningRate = learningRate;
    this.momentum = momentum;
    initialise();
  }

  public WidrowHoffLearner() {
    this(DEFAULT_LEARNING_RATE, DEFAULT_MOMENTUM);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    learningRate = parameters.getDouble(base.push(P_LEARNINGRATE), null, 0);
    momentum = parameters.getDouble(base.push(P_MOMENTUM), null, 0);
  }

  public double act() {
    return currentOutput;
  }

  public void train( double target ) {
    currentOutput = currentOutput*momentum + delta(target);
  }

  public double delta( double target ) {
    delta = learningRate * (target - currentOutput);
    return delta;
  }

  public void dumpState( DataWriter out ) {
    // TODO
  }

  public double getLearningDelta() {
    return delta;
  }

  protected void initialise() {
    delta = 0;
    currentOutput = 0;
  }

  public void reset() {
    initialise();
  }

}
