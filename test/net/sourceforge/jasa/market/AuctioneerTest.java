/*
 * JASA Java Auction Simulator API
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

package net.sourceforge.jasa.market;

import junit.framework.TestCase;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.SpringSimulationController;
import net.sourceforge.jasa.agent.MockTrader;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

public abstract class AuctioneerTest extends TestCase {

	Auctioneer auctioneer;

	MarketSimulation auction;

	MockTrader[] traders;
	
	RandomEngine prng;

	public AuctioneerTest(String name) {
		super(name);
	}

	public void setUp() {
		prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);
		auction = new MarketSimulation();
		auction.setSimulationController(new SpringSimulationController());

		traders = new MockTrader[5];
		traders[0] = new MockTrader(this, 30, 1000, 1000, auction);
		traders[1] = new MockTrader(this, 10, 10000, 1000, auction);
		traders[2] = new MockTrader(this, 15, 10000, 400, auction);
		traders[3] = new MockTrader(this, 10, 10000, 400, auction);
		traders[4] = new MockTrader(this, 15, 10000, 400, auction);
	}

	public void testDelete() {

		// round 0
		Order testShout = null;
		try {
			auctioneer.newOrder(new Order(traders[0], 1, 21, true));
			auctioneer.newOrder(new Order(traders[1], 1, 42, true));
			testShout = new Order(traders[2], 1, 43, false);
			auctioneer.newOrder(testShout);
			auctioneer.newOrder(new Order(traders[3], 1, 23, false));
			auctioneer.newOrder(new Order(traders[4], 1, 10, false));
		} catch (IllegalOrderException e) {
			fail("invalid IllegalOrderException exception thrown " + e);
			e.printStackTrace();
		}

		auctioneer.removeOrder(testShout);
		// auctioneer.endOfRoundProcessing();

		auctioneer.printState();

	}

}
