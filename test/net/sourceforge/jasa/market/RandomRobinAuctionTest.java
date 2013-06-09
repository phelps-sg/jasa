/*
 * Copyright (C) 2013 Steve Phelps
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package net.sourceforge.jasa.market;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SpringSimulationController;
import net.sourceforge.jabm.init.BasicAgentInitialiser;
import net.sourceforge.jabm.mixing.RandomRobinAgentMixer;
import net.sourceforge.jasa.agent.MockStrategy;
import net.sourceforge.jasa.agent.MockTrader;
import net.sourceforge.jasa.market.auctioneer.AbstractAuctioneer;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.market.rules.UniformPricingPolicy;
import net.sourceforge.jasa.sim.PRNGTestSeeds;

import org.apache.log4j.Logger;

import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

public class RandomRobinAuctionTest extends TestCase {

	Auctioneer auctioneer;

	MarketSimulation auction;

	MockTrader[] traders;
	
	protected RandomEngine prng;

	static Logger logger = Logger.getLogger(RandomRobinAuctionTest.class);

	public RandomRobinAuctionTest(String name) {
		super(name);
//		org.apache.log4j.BasicConfigurator.configure();
	}

	public void setUpTraders() {
		traders = new MockTrader[3];

		traders[0] = new MockTrader(this, 30, 1000, 500, auction);
		traders[1] = new MockTrader(this, 10, 10000, 500, auction);
		traders[2] = new MockTrader(this, 15, 10000, 725, auction);

		MockTrader trader = traders[0];
		trader.setStrategy(new MockStrategy(new Order[] {
		    new Order(trader, 1, 500, true), new Order(trader, 1, 600, true),
		    new Order(trader, 1, 700, true) }, auction));

		trader = traders[1];
		trader.setStrategy(new MockStrategy(new Order[] {
		    new Order(trader, 1, 500, true), new Order(trader, 1, 550, true),
		    new Order(trader, 1, 750, true) }, auction));

		trader = traders[2];
		trader.setStrategy(new MockStrategy(new Order[] {
		    new Order(trader, 1, 900, false), new Order(trader, 1, 950, false),
		    new Order(trader, 1, 725, false) }, auction));
		
		for (int i = 0; i < traders.length; i++) {
			System.out.println("Registering trader " + traders[i]);
			auction.register(traders[i]);			
		}
		
	}

	public void setUp() {
		prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);
		setUpAuction();
		setUpTraders();
		auctioneer = new ClearingHouseAuctioneer(auction);
		((AbstractAuctioneer) auctioneer)
		    .setPricingPolicy(new UniformPricingPolicy(1));
		auction.setAuctioneer(auctioneer);
		auctioneer.setMarket(auction);
		auction.initialise();
	}

	public void setUpAuction() {
		auction = new MarketSimulation();
		auction.setSimulationController(new SpringSimulationController());
		auction.setPopulation(new Population());
		auction.setAgentMixer(new RandomRobinAgentMixer(prng));
		auction.setAgentInitialiser(new BasicAgentInitialiser());
		auction.setMaximumRounds(3);
	}

//	public void testDailyStats() {
//
//		auction.setLengthOfDay(3);
//		auction.setMaximumDays(1);
//
//		DailyStatsReport dailyStatsReport = new DailyStatsReport();
//		dailyStatsReport.setAuction(auction);
//		auction.setReport(dailyStatsReport);
//		dailyStatsReport.setup(new ParameterDatabase(), new Parameter("stats"));
//
//		auction.run();
//
//		Distribution transPrice = dailyStatsReport.getPreviousDayTransPriceStats();
//		report.info("Previous day transaction price statistics = " + transPrice);
//		assertTrue(transPrice.getMean() == 725);
//	}

	/**
	 * See bug #1435981
	 */
	public void testEndOfDayNotification() {

		auction.setLengthOfDay(3);
		auction.setMaximumDays(2);

		auction.begin();

		do {
			try {
				auction.step();
			} catch (AuctionClosedException e) {
				// Do nothing
			}
			for (int i = 0; i < traders.length; i++) {
				assertTrue(traders[i].receivedEndOfDayAfterRequestShout);
			}
		} while (!auction.closed());

	}

//	public void testHistoryStats() {
//		report.info("testHistoryStats()");
//
//		HistoricalDataReport stats = new HistoricalDataReport();
//		stats.setAuction(auction);
//		auction.setReport(stats);
//		stats.setup(new ParameterDatabase(), new Parameter("stats"));
//
//		auction.run();
//
//		int acceptedBids = stats.getNumberOfBids(0, true);
//		int unacceptedBids = stats.getNumberOfBids(0, false);
//		int acceptedAsks = stats.getNumberOfAsks(0, true);
//		int unacceptedAsks = stats.getNumberOfAsks(0, false);
//		System.out.println("Number of accepted bids above 0 = " + acceptedBids);
//		System.out.println("Number of unaccepted bids above 0 = " + unacceptedBids);
//		System.out.println("Number of accepted asks above 0 = " + acceptedAsks);
//		System.out.println("Number of unaccepted asks above 0 = " + unacceptedAsks);
//		assertTrue(acceptedBids == acceptedAsks);
//		assertTrue(acceptedBids == 1);
//
//	}

	public void testProtocol() {

		int n = auction.getNumberOfTraders();
		System.out.println("Simulation reports " + n + " traders");
		assertTrue(n == traders.length);
		assertTrue(!auction.closed());

		auction.setMaximumRounds(2);

		assertTrue(auction.getMaximumRounds() == 2);

		auction.run();

		for (int i = 0; i < traders.length; i++) {
			assertTrue(traders[i].receivedAuctionOpen);
			assertTrue(traders[i].receivedAuctionClosed);
			assertTrue(traders[i].receivedAuctionClosedAfterAuctionOpen);
			assertTrue(traders[i].receivedRequestShout == 2);
//			assertTrue(traders[i].receivedRoundClosed == 2);
		}

	}

	public void testNumberOfTraders() {
		assertTrue(auction.getNumberOfTraders() == traders.length);
		auction.run();
		// check that no traders left active at end of market.
//		assertTrue(auction.getNumberOfTraders() == 0);
		//TODO: sphelps
	}

	public void testClosed() {
		try {
			assertTrue(!auction.closed());
			auction.begin();
			assertTrue(!auction.closed());
			while (!auction.closed()) {
				auction.step();
			}
			assertTrue(auction.closed());
		} catch (AuctionClosedException e) {
			fail("tried to step through closed market");
		}
	}

	/**
	 * Test that transactions occur only in the final (3rd) round.
	 */
	public void testTransactionsOccured() {
		try {

			assertTrue(!auction.transactionsOccurred());
			auction.begin();

			auction.step();
			assertTrue(!auction.transactionsOccurred());

			auction.step();
			assertTrue(!auction.transactionsOccurred());

			auction.step();
			assertTrue(auction.transactionsOccurred());

		} catch (AuctionClosedException e) {
			fail("we tried to step through an market past its closure");
		} catch (ShoutsNotVisibleException e) {
			fail("test is configured incorrectly: we must use an auctioneer that permits shout visibility");
		}
	}

	public void testShoutAccepted() {

		try {

			Order testBid = new Order(traders[0], 1, 500, true);
			Order testAsk = new Order(traders[2], 1, 300, false);

			auction.placeOrder(testBid);
			assertTrue(!auction.orderAccepted(testBid));

			auction.placeOrder(testAsk);
			assertTrue(!auction.orderAccepted(testAsk));

			auctioneer.clear();
			// market.clear(testAsk, testBid, 400);

			assertTrue(auction.orderAccepted(testBid));
			assertTrue(auction.orderAccepted(testAsk));

			auction.runSingleRound();

			assertTrue(!auction.orderAccepted(testBid));
			assertTrue(!auction.orderAccepted(testAsk));

		} catch (AuctionException e) {
			fail(e.getMessage());
		}
	}

	public void testLastShout() {
		try {

			Order testBid = new Order(traders[0], 1, 500, true);
			Order testAsk = new Order(traders[2], 1, 300, false);

			assertTrue(auction.getLastOrder() == null);

			auction.placeOrder(testBid);
			assertTrue(auction.getLastOrder().equals(testBid));

			auction.placeOrder(testAsk);
			assertTrue(auction.getLastOrder().equals(testAsk));

		} catch (AuctionException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test for bug #1614071
	 */
	public void testClear() {

		// We will test a clearing operation that involves a transfer of 2 units
		// from seller to buyer.
		int testQty = 2;
		MockTrader buyer = traders[0];
		MockTrader seller = traders[2];

		// Check initial state
		assertTrue(buyer.lastWinningShout == null);
		assertTrue(seller.lastWinningShout == null);

		// Record initial allocations
		int sellerInitial = seller.getCommodityHolding().getQuantity();
		int buyerInitial = buyer.getCommodityHolding().getQuantity();

		// Set up a purchase of 2 units from by buyer (traders[0]) from seller
		// (traders[2])
		Order testBid = new Order(buyer, testQty, 200, true);
		Order testAsk = new Order(seller, testQty, 100, false);
		auction.clear(testAsk, testBid, 200, 200, testQty);

		// Test that 2 units were transfered from seller to buyer
		assertTrue(buyer.getCommodityHolding().getQuantity() == buyerInitial + 2);

		// Test that 2 units were transfered from the seller
		assertTrue(seller.getCommodityHolding().getQuantity() == sellerInitial
		    - testQty);

		// Check that agents received win notification
		assertTrue(buyer.lastWinningShout == testBid);
		assertTrue(seller.lastWinningShout == testAsk);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(RandomRobinAuctionTest.class);
	}

}