/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

import ec.util.MersenneTwisterFast;

import uk.ac.liv.util.io.DataWriter;

/**
 * A learner that simply plays a random action on each iteration 
 * without any learning.  This is useful for control experiments.
 *
 * @author Steve Phelps
 */

public class DumbRandomLearner implements StochasticLearner {
  
  protected MersenneTwisterFast prng;
  
  protected int numActions;
  
  public DumbRandomLearner( int numActions ) {
    prng = new MersenneTwisterFast();
    this.numActions = numActions;
  }
  
  public void setSeed( long seed ) {
    prng.setSeed(seed);
  }
  
  public int act() {
    return prng.nextInt(numActions);
  }
  
  public double getLearningDelta() {
    return 0.0;
  }
  
  public void dumpState( DataWriter out ) {
    //TODO
  }
  
  public int getNumberOfActions() {
    return numActions;
  }
  
}
