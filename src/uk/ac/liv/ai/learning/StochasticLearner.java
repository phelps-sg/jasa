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
 * learning algorithms with a stochastic component whose PRNG
 * can be seeded.
 *
 * @author Steve Phelps
 */

public interface StochasticLearner extends Learner {

  public void setSeed( long seed );

}