/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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
import uk.ac.liv.util.Prototypeable;

import uk.ac.liv.prng.GlobalPRNG;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.io.Serializable;

/**
 * An implementation of the Widrow-Hoff learning algorithm 
 * for 1-dimensional training sets.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class WidrowHoffLearner extends AbstractLearner
    implements MimicryLearner, Prototypeable, Serializable {

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

  public static final double DEFAULT_LEARNING_RATE = 0.1;

  public static final String P_LEARNINGRATE = "learningrate";

  public WidrowHoffLearner( double learningRate ) {
    this.learningRate = learningRate;
    initialise();
  }

  public WidrowHoffLearner() {
    this(DEFAULT_LEARNING_RATE);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
    learningRate = parameters.getDouble(base.push(P_LEARNINGRATE), null, 0); 
  }

  public Object protoClone() {
    WidrowHoffLearner clone = new WidrowHoffLearner(learningRate);
    return clone;
  }

  public double act() {
    return currentOutput;
  }

  public void train( double target ) {
    currentOutput += delta(target);
  }

  public double delta( double target ) {
    delta = learningRate * (target - currentOutput);
    return delta;
  }

  public void setOutputLevel( double currentOutput ) {
    this.currentOutput = currentOutput;
  }

  public void dumpState( DataWriter out ) {
    // TODO
  }

  public double getLearningDelta() {
    return delta;
  }

  public void initialise() {
    delta = 0;
    currentOutput = 0;
  }

  public void reset() {
    initialise();
  }

  public void setLearningRate( double learningRate ) {
    this.learningRate = learningRate;
  }

  public double getLearningRate() {
    return learningRate;
  }


  public void randomInitialise() {
    learningRate = GlobalPRNG.getInstance().uniform(0.1, 0.4);       
  }

  public String toString() {
    return ("(" + getClass() + " learningRate:" + learningRate +
             " delta:" + delta + " currentOutput:" + currentOutput + ")");
  }

}
