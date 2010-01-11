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

import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

import net.sourceforge.jasa.market.AuctionClosedException;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.market.auctioneer.AbstractAuctioneer;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.market.rules.UniformPricingPolicy;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.event.EventListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AbstractTraderAgentTest extends TestCase implements EventListener {

	MockTrader trader1;

	MockTrader trader2;

	MarketFacade auction;	

	public static final int TRADER1_STOCK = 0;

	public static final int TRADER2_STOCK = 5;

	public static final double TRADER1_FUNDS = 10000;

	public static final double TRADER2_FUNDS = 20000;

	public static final double TRADER1_VALUE = 250;

	public static final double TRADER2_VALUE = 140;

	public AbstractTraderAgentTest(String name) {
		super(name);
	}

	public void setUp() {

		RandomEngine prng = new MersenneTwister64();		
		auction = new MarketFacade(prng);		
		AbstractAuctioneer auctioneer = new ClearingHouseAuctioneer(auction);
		auctioneer.setPricingPolicy(new UniformPricingPolicy(0.5));
		auction.setAuctioneer(auctioneer);
		

		trader1 = new MockTrader(this, TRADER1_STOCK, TRADER1_FUNDS,
				TRADER1_VALUE, auction);

		trader2 = new MockTrader(this, TRADER2_STOCK, TRADER2_FUNDS,
				TRADER2_VALUE, auction);

		trader1.setStrategy(new MockStrategy(new Order[] {
		    new Order(trader1, 1, TRADER1_VALUE - 100, true),
		    new Order(trader1, 1, TRADER1_VALUE - 50, true),
		    new Order(trader1, 1, TRADER1_VALUE, true),
		    new Order(trader1, 1, TRADER1_VALUE - 100, true) }));

		trader2.setStrategy(new MockStrategy(new Order[] {
		    new Order(trader2, 1, TRADER2_VALUE + 100, false),
		    new Order(trader2, 1, TRADER2_VALUE + 50, false),
		    new Order(trader2, 1, TRADER2_VALUE, false),
		    new Order(trader2, 1, TRADER2_VALUE + 100, false) }));

		auction.register(trader1);
		auction.register(trader2);
		auction.addListener(this);
	}

	public void testPurchase() {
		System.out.println("trader1 = " + trader1);
		System.out.println("trader2 = " + trader2);
		trader1.getAccount().transfer(trader2.getAccount(), 5000);
		trader2.getCommodityHolding().transfer(trader1.getCommodityHolding(), 5);
		System.out.println("after purchase");
		System.out.println("trader1 = " + trader1);
		System.out.println("trader2 = " + trader2);
		assertTrue(trader1.getStock() == TRADER1_STOCK + 5);
		assertTrue(trader2.getStock() == TRADER2_STOCK - 5);
		assertTrue(trader1.getFunds() == TRADER1_FUNDS - 5000);
		assertTrue(trader2.getFunds() == TRADER2_FUNDS + 5000);
		trader1.initialise();
		assertTrue(trader1.getFunds() == TRADER1_FUNDS);
		assertTrue(trader1.getStock() == TRADER1_STOCK);
		assertTrue(trader1.getLastPayoff() == 0);
		assertTrue(trader1.getTotalPayoff() == 0);
		trader2.initialise();
		assertTrue(trader2.getFunds() == TRADER2_FUNDS);
		assertTrue(trader2.getStock() == TRADER2_STOCK);
		assertTrue(trader2.getLastPayoff() == 0);
		assertTrue(trader2.getTotalPayoff() == 0);

	}

	public void testLastShoutAccepted() {

		assertTrue(!trader1.lastOrderFilled());
		assertTrue(!trader2.lastOrderFilled());

		auction.begin();

		try {

			auction.step();
			assertTrue(!((MockStrategy) trader1.getStrategy()).lastShoutAccepted);
			assertTrue(!((MockStrategy) trader2.getStrategy()).lastShoutAccepted);
			assertTrue(!trader1.lastOrderFilled());
			assertTrue(!trader2.lastOrderFilled());

			auction.step();
			assertTrue(trader1.lastOrderFilled());
			// trader1.purchaseFrom(market, trader2, 1, TRADER1_VALUE);
			assertTrue(trader2.lastOrderFilled());

			auction.step();
			assertTrue(((MockStrategy) trader1.getStrategy()).lastShoutAccepted);
			assertTrue(((MockStrategy) trader2.getStrategy()).lastShoutAccepted);
			assertTrue(trader1.lastOrderFilled());
			assertTrue(trader2.lastOrderFilled());

			auction.step();
			assertTrue(((MockStrategy) trader1.getStrategy()).lastShoutAccepted);
			assertTrue(((MockStrategy) trader2.getStrategy()).lastShoutAccepted);
			assertTrue(!trader1.lastOrderFilled());
			assertTrue(!trader2.lastOrderFilled());

		} catch (AuctionClosedException e) {
			fail("we tried to step through a closed market.");
		}

	}

	public void testLastProfit() {

		assertTrue(trader1.getLastPayoff() == 0);
		assertTrue(trader2.getLastPayoff() == 0);

		auction.begin();

		try {

			auction.step();
			assertTrue(trader1.getLastPayoff() == 0);
			assertTrue(trader2.getLastPayoff() == 0);

			auction.step();
			assertTrue(trader1.getLastPayoff() == 55);
			assertTrue(trader2.getLastPayoff() == 55);

			auction.step();
			assertTrue(trader1.getLastPayoff() == 55);
			assertTrue(trader2.getLastPayoff() == 55);

			auction.step();
			assertTrue(trader1.getLastPayoff() == 0);
			assertTrue(trader2.getLastPayoff() == 0);

		} catch (AuctionClosedException e) {
			fail("we tried to step through a closed market.");
		}

	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(AbstractTraderAgentTest.class);
	}

	@Override
	public void eventOccurred(SimEvent event) {
		System.out.println(event);
	}
}
