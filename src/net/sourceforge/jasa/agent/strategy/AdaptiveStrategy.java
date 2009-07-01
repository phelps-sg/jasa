/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2009 Steve Phelps
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

package net.sourceforge.jasa.agent.strategy;

import net.sourceforge.jasa.agent.TradingStrategy;
import net.sourceforge.jasa.sim.learning.Learner;

/**
 * <p>
 * Strategies implementing this interface indicate that they are based on a
 * learning algorithm.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public interface AdaptiveStrategy extends TradingStrategy {

	public Learner getLearner();

	public void setLearner(Learner learner);

}