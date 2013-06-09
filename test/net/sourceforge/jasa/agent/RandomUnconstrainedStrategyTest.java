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

package net.sourceforge.jasa.agent;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.SpringSimulationController;
import net.sourceforge.jabm.init.BasicAgentInitialiser;
import net.sourceforge.jabm.mixing.RandomRobinAgentMixer;
import net.sourceforge.jabm.util.SummaryStats;
import net.sourceforge.jasa.agent.strategy.RandomUnconstrainedStrategy;
import net.sourceforge.jasa.market.MarketSimulation;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.report.PriceStatisticsReport;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

public class RandomUnconstrainedStrategyTest extends TestCase {

	protected RandomUnconstrainedStrategy testStrategy;

	
	protected TokenTradingAgent testAgent;

	
	protected ClearingHouseAuctioneer auctioneer;

	protected MarketSimulation auction;
	
	protected PriceStatisticsReport logger;
	
	protected RandomEngine prng;

	static final double MAX_PRICE = 100.0;

	static final double PRIV_VALUE = 57.0;

	static final int MAX_ROUNDS = 200000;

	public RandomUnconstrainedStrategyTest(String name) {
		super(name);
	}

	public void setUp() {
		prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);
		auction = new MarketSimulation();
		SimulationController controller = new SpringSimulationController();
		auction.setSimulationController(controller);
		auction.setPopulation(new Population());
		auction.setAgentMixer(new RandomRobinAgentMixer(prng));
		auction.setAgentInitialiser(new BasicAgentInitialiser());
		auctioneer = new ClearingHouseAuctioneer(auction);
		auction.setAuctioneer(auctioneer);
		testAgent = new TokenTradingAgent(PRIV_VALUE, 100, controller);
		Uniform distribution = new Uniform(0, MAX_PRICE, prng);
		testStrategy = new RandomUnconstrainedStrategy(distribution, testAgent);
		testStrategy.setBuy(false);
		testAgent.setStrategy(testStrategy);
		
		logger = new PriceStatisticsReport();
//		auction.addReport(logger);
		controller.addListener(logger);
		auction.register(testAgent);
		auction.setMaximumRounds(MAX_ROUNDS);
	}

	public void testBidRange() {
		System.out.println(getClass() + ": testBidRange()");
		System.out.println("testAgent = " + testAgent);
		System.out.println("testStrategy = " + testStrategy);
		auction.run();
		SummaryStats askStats = logger.getAskPriceStats();
		System.out.println(askStats);
		assertTrue(approxEqual(askStats.getMin(), 0));
		assertTrue(approxEqual(askStats.getMax(), MAX_PRICE));
		assertTrue(approxEqual(askStats.getMean(), (MAX_PRICE / 2)));
	}

	public boolean approxEqual(double x, double y) {
		return Math.abs(x - y) < 0.1;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(RandomUnconstrainedStrategyTest.class);
	}

}
