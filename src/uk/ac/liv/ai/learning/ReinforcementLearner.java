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

/**
 * Classes implementing this interface implement
 * reinformcement learning algorithms.
 *
 * @author Steve Phelps
 */

public interface ReinforcementLearner {

  /**
   * Specify the next action to take.
   *
   * @return An integer representing the action to be taken.
   */
  public int act();

  /**
   * The reinforcement learner is called-back after performing
   * an action with a reward value and a new state.
   */
  public void newState( double reward, int newState );

}
