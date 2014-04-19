/* JASA Java Auction Simulator API
 * Copyright (C) 2013 Steve Phelps
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

package net.sourceforge.jasa.agent;

import net.sourceforge.jabm.learning.Learner;
import net.sourceforge.jasa.agent.strategy.DiscreteLearnerStrategy;
import net.sourceforge.jasa.market.Market;

class TestLearnerStrategy extends DiscreteLearnerStrategy {

	/**
	 * @uml.property name="actions"
	 */
	public int actions = 0;

	/**
	 * @uml.property name="rewards"
	 */
	public int rewards = 0;

	public int act() {
		return actions++;
	}

	public void learn(Market auction) {
		rewards++;
	}

	public Learner getLearner() {
		return null;
	}

	public void setLearner(Learner l) {
	}

}
