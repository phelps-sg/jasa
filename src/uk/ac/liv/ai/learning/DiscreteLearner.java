/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

import uk.ac.liv.util.io.DataWriter;

/**
 * A learner that learns a discrete number of different actions.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public interface DiscreteLearner extends Learner {

  /**
   * Specify the next action to take.
   *
   * @return An integer representing the action to be taken.
   */
  public int act();

  /**
   * Get the number of different possible actions this learner
   * can choose from when it performs an action.
   *
   * @return An integer value representing the number of actions available.
   */
  public int getNumberOfActions();

}