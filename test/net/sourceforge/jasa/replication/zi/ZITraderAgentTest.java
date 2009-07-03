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

package net.sourceforge.jasa.replication.zi;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import net.sourceforge.jasa.agent.TokenTradingAgent;
import net.sourceforge.jasa.agent.strategy.TruthTellingStrategy;
import net.sourceforge.jasa.market.AuctionClosedException;
import net.sourceforge.jasa.market.RandomRobinAuction;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.sim.PRNGTestSeeds;

import org.apache.log4j.Logger;

import cern.jet.random.engine.MersenneTwister64;


public class ZITraderAgentTest extends TestCase {

	/**
	 * @uml.property name="buyer"
	 * @uml.associationEnd
	 */
	TokenTradingAgent buyer;

	/**
	 * @uml.property name="seller"
	 * @uml.associationEnd
	 */
	TokenTradingAgent seller;

	/**
	 * @uml.property name="market"
	 * @uml.associationEnd
	 */
	RandomRobinAuction auction;

	/**
	 * @uml.property name="auctioneer"
	 * @uml.associationEnd
	 */
	ClearingHouseAuctioneer auctioneer;

	static final int NUM_ROUNDS = 1000;

	static final int TRADE_ENTITLEMENT = 100;

	static final double BUYER_PRIV_VALUE = 1000;

	static final double SELLER_PRIV_VALUE = 900;

	static Logger logger = Logger.getLogger(ZITraderAgentTest.class);

	public ZITraderAgentTest(String name) {
		super(name);
//		org.apache.log4j.BasicConfigurator.configure();
	}

	public void setUp() {
		buyer = new TokenTradingAgent(BUYER_PRIV_VALUE, TRADE_ENTITLEMENT, false);
		seller = new TokenTradingAgent(SELLER_PRIV_VALUE, TRADE_ENTITLEMENT, true);
		buyer.setStrategy(new TruthTellingStrategy(buyer));
		seller.setStrategy(new TruthTellingStrategy(seller));
		auction = new RandomRobinAuction(
				new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED));
		auction.register(buyer);
		auction.register(seller);
		auctioneer = new ClearingHouseAuctioneer(auction);
		auction.setAuctioneer(auctioneer);
		auction.setMaximumRounds(NUM_ROUNDS);
	}

	/**
	 * Test that the agent drops out of the market (becomes inactive) after its
	 * trade entitlement has been depleted.
	 */
	public void testTradeEntitlement() {

		try {

			auction.begin();

			assertTrue("Agents not active at start of market", buyer.active()
			    && seller.active());

			for (int i = 0; i < TRADE_ENTITLEMENT; i++) {
				auction.step();
			}

			assertTrue("agents did not trade all their units", buyer
			    .getQuantityTraded() == TRADE_ENTITLEMENT
			    && seller.getQuantityTraded() == TRADE_ENTITLEMENT);

			// Agents must still be active after trading their last unit
			// so that reinforcement learning algorithms can still learn from
			// the last trade- v. important if you are only entitled to trade
			// a single unit. See BR #1064544.
			assertTrue("agents not active immediately after trading all units", buyer
			    .active()
			    && seller.active());

			// Ok, after this step they should be inactive.
			auction.step();

			assertTrue("agents not inactive after trading all units", !buyer.active()
			    && !seller.active());

		} catch (AuctionClosedException e) {
			fail(e.getMessage());
		}

	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(ZITraderAgentTest.class);
	}

}
