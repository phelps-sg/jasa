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

/**
 * Classes implementing this interface indicate that they implement
 * a learning algorithm.
 *
 * @author Steve Phelps
 */

public interface Learner {

  /**
   * Specify the next action to take.
   *
   * @return An integer representing the action to be taken.
   */
  public int act();

  /**
   * Return a value indicative of the amount of learning that
   * occured during the last iteration.  Values close to 0.0
   * indicate that the learner has converged to an equilibrium
   * state.  
   *
   * @return A double representing the amount of learning that occured.
   */
  public double getLearningDelta();
  
}