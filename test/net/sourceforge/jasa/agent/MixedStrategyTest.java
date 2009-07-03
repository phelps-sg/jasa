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
import net.sourceforge.jasa.agent.strategy.AbstractStrategy;
import net.sourceforge.jasa.agent.strategy.MixedStrategy;
import net.sourceforge.jasa.market.RandomRobinAuction;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import net.sourceforge.jasa.sim.prng.DiscreteProbabilityDistribution;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MixedStrategyTest extends TestCase {

	/**
	 * @uml.property name="pureStrategy1"
	 * @uml.associationEnd
	 */
	TestLearnerStrategy pureStrategy1;

	/**
	 * @uml.property name="pureStrategy2"
	 * @uml.associationEnd
	 */
	TestLearnerStrategy pureStrategy2;

	/**
	 * @uml.property name="mixedStrategy"
	 * @uml.associationEnd
	 */
	MixedStrategy mixedStrategy;

	/**
	 * @uml.property name="probabilities"
	 * @uml.associationEnd
	 */
	DiscreteProbabilityDistribution probabilities;

	static final int NUM_ROUNDS = 1000;

	static final double STRATEGY1_PROBABILITY = 0.30;

	static final double STRATEGY2_PROBABILITY = 0.70;

	public MixedStrategyTest(String name) {
		super(name);
//		org.apache.log4j.BasicConfigurator.configure();
	}

	public void setUp() {

		pureStrategy1 = new TestLearnerStrategy();
		pureStrategy1.setQuantity(1);

		pureStrategy2 = new TestLearnerStrategy();
		pureStrategy2.setQuantity(1);

		probabilities = new DiscreteProbabilityDistribution(2);
		// probabilities.setSeed(PRNGTestSeeds.UNIT_TEST_SEED);
		probabilities.setProbability(0, STRATEGY1_PROBABILITY);
		probabilities.setProbability(1, STRATEGY2_PROBABILITY);

		mixedStrategy = new MixedStrategy(probabilities, new AbstractStrategy[] {
		    pureStrategy1, pureStrategy2 });

	}

	public void testActionsAndRewards() {
		RandomRobinAuction auction = new RandomRobinAuction(
				new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED));
		Auctioneer auctioneer = new ClearingHouseAuctioneer(auction);
		auction.setAuctioneer(auctioneer);
		auction.setMaximumRounds(NUM_ROUNDS);
		TokenTradingAgent agent = new TokenTradingAgent(10, NUM_ROUNDS, false);
		agent.setStrategy(mixedStrategy);
		pureStrategy1.setAgent(agent);
		pureStrategy2.setAgent(agent);
		auction.register(agent);
		auction.run();
		System.out.println("pureStrategy1 count = " + pureStrategy1.actions);
		System.out.println("pureStrategy2 couint = " + pureStrategy2.actions);
		assertTrue(Math.abs((STRATEGY1_PROBABILITY * NUM_ROUNDS)
		    - pureStrategy1.actions) < 0.05 * NUM_ROUNDS);
		assertTrue(Math.abs((STRATEGY2_PROBABILITY * NUM_ROUNDS)
		    - pureStrategy2.actions) < 0.05 * NUM_ROUNDS);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(MixedStrategyTest.class);
	}

}
