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

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister64;
import net.sourceforge.jasa.agent.strategy.RandomConstrainedStrategy;
import net.sourceforge.jasa.market.RandomRobinAuction;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.report.PriceStatisticsReport;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import net.sourceforge.jasa.sim.prng.GlobalPRNG;
import net.sourceforge.jasa.sim.util.SummaryStats;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RandomConstrainedStrategyTest extends TestCase {

	/**
	 * @uml.property name="testStrategy"
	 * @uml.associationEnd
	 */
	protected RandomConstrainedStrategy testStrategy;

	/**
	 * @uml.property name="testAgent"
	 * @uml.associationEnd
	 */
	protected TokenTradingAgent testAgent;

	/**
	 * @uml.property name="auctioneer"
	 * @uml.associationEnd
	 */
	protected ClearingHouseAuctioneer auctioneer;

	/**
	 * @uml.property name="market"
	 * @uml.associationEnd
	 */
	protected RandomRobinAuction auction;

	/**
	 * @uml.property name="logger"
	 * @uml.associationEnd
	 */
	protected PriceStatisticsReport logger;

	static final double MAX_MARKUP = 100.0;

	static final double PRIV_VALUE = 7.0;

	static final int MAX_ROUNDS = 200000;

	public RandomConstrainedStrategyTest(String name) {
		super(name);
	}

	public void setUp() {
		GlobalPRNG.initialiseWithSeed(PRNGTestSeeds.UNIT_TEST_SEED);
		testAgent = new TokenTradingAgent(PRIV_VALUE, 100, true);
		testStrategy = new RandomConstrainedStrategy(testAgent);
		testStrategy.setMarkupDistribution(new Uniform(0, MAX_MARKUP, GlobalPRNG.getInstance()));
		testAgent.setStrategy(testStrategy);
		auction = new RandomRobinAuction(new MersenneTwister64(
				PRNGTestSeeds.UNIT_TEST_SEED));
		auctioneer = new ClearingHouseAuctioneer(auction);
		auction.setAuctioneer(auctioneer);
		logger = new PriceStatisticsReport();
		auction.addReport(logger);
		auction.register(testAgent);
		auction.setMaximumRounds(MAX_ROUNDS);
	}

	public void testBidRange() {
		System.out.println(getClass() + ": testBidRange()");
		System.out.println("testAgent = " + testAgent);
		System.out.println("testStrategy = " + testStrategy);
		auction.run();
		logger.produceUserOutput();
		SummaryStats askStats = logger.getAskPriceStats();
		assertTrue(approxEqual(askStats.getMin(), PRIV_VALUE));
		assertTrue(approxEqual(askStats.getMax(), MAX_MARKUP + PRIV_VALUE));
		assertTrue(approxEqual(askStats.getMean(), (MAX_MARKUP / 2) + PRIV_VALUE));
	}

	public void testBidFloor() {
		testAgent.setIsSeller(false);
		System.out.println(getClass() + ": testBidFloor()");
		System.out.println("testAgent = " + testAgent);
		System.out.println("testStrategy = " + testStrategy);
		auction.run();
		logger.produceUserOutput();
		SummaryStats bidStats = logger.getBidPriceStats();
		assertTrue(bidStats.getMin() >= 0);
		assertTrue(bidStats.getMax() <= PRIV_VALUE);
	}

	public boolean approxEqual(double x, double y) {
		return Math.abs(x - y) < 0.1;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(RandomConstrainedStrategyTest.class);
	}

}
