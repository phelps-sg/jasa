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
import net.sourceforge.jabm.SpringSimulationController;
import net.sourceforge.jabm.init.BasicAgentInitialiser;
import net.sourceforge.jabm.mixing.RandomRobinAgentMixer;
import net.sourceforge.jasa.market.MarketSimulation;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

public class AdaptiveStrategyTest extends TestCase {

	TestLearnerStrategy strategy;
	
	RandomEngine prng;

	static final int NUM_ROUNDS = 10;


	public AdaptiveStrategyTest(String name) {
		super(name);
	}

	public void setUp() {
		strategy = new TestLearnerStrategy();
		strategy.setQuantity(1);
		prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);
	}

	public void testActionsAndRewards() {
		MarketSimulation auction = new MarketSimulation();
		auction = new MarketSimulation();
		auction.setSimulationController(new SpringSimulationController());
		auction.setPopulation(new Population());
		auction.setAgentMixer(new RandomRobinAgentMixer(prng));
		auction.setAgentInitialiser(new BasicAgentInitialiser());
		Auctioneer auctioneer = new ClearingHouseAuctioneer(auction);
		auction.setAuctioneer(auctioneer);
		auction.setMaximumRounds(NUM_ROUNDS);
		TokenTradingAgent agent = new TokenTradingAgent(10, 100,
				auction.getSimulationController());
		agent.setStrategy(strategy);
		strategy.setBuy(true);
		auction.register(agent);
		auction.run();
		System.out
		    .println("AdaptiveStrategyTest: Testing reward/action cycle count");
		System.out.println("Number of actions = " + strategy.actions);
		System.out.println("Number of rewards = " + strategy.rewards);
		System.out.println("done.");
		assertTrue(strategy.actions == NUM_ROUNDS);
		assertTrue(strategy.rewards == NUM_ROUNDS);
	}

	public void testReset() {
		System.out.println("AdaptiveStrategyTest: Testing reset()");
		testActionsAndRewards();
		strategy.actions = 0;
		strategy.rewards = 0;
		strategy.reset();
		testActionsAndRewards();
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(AdaptiveStrategyTest.class);
	}

}
