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

package net.sourceforge.jasa.agent;

import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;
import net.sourceforge.jasa.agent.valuation.RandomScheduleValuer;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.RandomRobinAuction;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import net.sourceforge.jasa.sim.prng.GlobalPRNG;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RandomScheduleValuerTest extends TestCase {

	protected MockTrader agent;

	protected MockRandomScheduleValuer valuer;

	public static final double MIN = 10;

	public static final double MAX = 100;

	public RandomScheduleValuerTest(String name) {
		super(name);
	}

	public void setUp() {
		agent = new MockTrader(this, 2, 0);
		valuer = new MockRandomScheduleValuer(MIN, MAX, GlobalPRNG.getInstance());
		agent.setValuationPolicy(valuer);
	}

	public void testValueChanges() {
		RandomRobinAuction auction = new RandomRobinAuction(
				new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED));
		agent.shoutAccepted(auction, new Order(agent, 1, 100, true), 100, 1);
		assertTrue(valuer.consumed);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(RandomScheduleValuerTest.class);
	}

}

class MockRandomScheduleValuer extends RandomScheduleValuer {

	public boolean consumed = false;

	public MockRandomScheduleValuer(double min, double max, RandomEngine prng) {
		super(min, max, prng);
	}

	public void consumeUnit(Market auction) {
		super.consumeUnit(auction);
		consumed = true;
	}
}