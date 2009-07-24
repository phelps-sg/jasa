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

package net.sourceforge.jasa.agent;

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.strategy.StimuliResponseStrategy;
import net.sourceforge.jasa.sim.learning.StatelessQLearner;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steve Phelps
 * @version $Revision$
 */
public class QLearnerEfficiencyTest extends EfficiencyTest {

	public static final double BENCHMARK_EFFICIENCY = 90.0;

	public QLearnerEfficiencyTest(String name) {
		super(name);
	}

	protected void assignStrategy(AbstractTradingAgent agent) {
		StimuliResponseStrategy strategy = new StimuliResponseStrategy();
		StatelessQLearner ql = new StatelessQLearner(prng);
		ql.setNumberOfActions(100);
		strategy.setLearner(ql);
		agent.setStrategy(strategy);
		strategy.setAgent(agent);
	}

	protected double getMinMeanEfficiency() {
		return BENCHMARK_EFFICIENCY;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(QLearnerEfficiencyTest.class);
	}
}
