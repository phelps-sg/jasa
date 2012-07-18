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

package net.sourceforge.jasa.replication.electricity;

import java.util.Iterator;

import junit.framework.TestCase;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SpringSimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.init.BasicAgentInitialiser;
import net.sourceforge.jabm.learning.NPTRothErevLearner;
import net.sourceforge.jabm.mixing.RandomRobinAgentMixer;
import net.sourceforge.jabm.util.SummaryStats;
import net.sourceforge.jasa.agent.FixedDirectionTradingAgent;
import net.sourceforge.jasa.agent.strategy.StimuliResponseStrategy;
import net.sourceforge.jasa.market.MarketSimulation;
import net.sourceforge.jasa.market.auctioneer.AbstractAuctioneer;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.market.rules.DiscriminatoryPricingPolicy;
import net.sourceforge.jasa.sim.PRNGTestSeeds;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.log4j.Logger;

import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

/**
 * Superclass for tests based on
 * 
 * "Market Power and Efficiency in a Computational Electricity Market with
 * Discriminatory Double-Auction Pricing" <br>
 * Nicolaisen, Petrov, and Tesfatsion <i>IEEE Transactions on Evolutionary
 * Computation, Vol. 5, No. 5. 2001</I> </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public abstract class ElectricityTest extends TestCase {

	protected Auctioneer auctioneer;

	protected MarketSimulation auction;

	protected ElectricityStats stats;

	protected static double buyerValues[] = { 37, 17, 12 };

	protected static double sellerValues[] = { 35, 16, 11 };

	protected SummaryStats mPB;

	protected SummaryStats mPS;

	protected SummaryStats eA;

	protected int ns;

	protected int nb;

	protected int cs;

	protected int cb;

	protected RandomEngine prng;

	private SpringSimulationController controller;

	static final int ITERATIONS = 100;

	static final int MAX_ROUNDS = 1000;
	
	static final int K = 40;

	static final double R = 0.10;

	static final double E = 0.20;

	static final double S1 = 9;

	static final double MIN_PRIVATE_VALUE = 30;

	static final double MAX_PRIVATE_VALUE = 100;

	static Logger logger = Logger.getLogger(ElectricityTest.class);

	public ElectricityTest(String name) {
		super(name);
	}

	public void setUp() {
		prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);
	}

	public void runExperiment() {
		logger.info("\nAttempting to replicate NPT results with");
		System.out.println("NS = " + ns + " NB = " + nb + " CS = " + cs
				+ " CB = " + cb);
		System.out.println("R = " + R + " E = " + E + " K = " + K + " S1 = "
				+ S1);
		System.out.println("with " + ITERATIONS + " iterations and "
				+ MAX_ROUNDS + " market rounds.");
		initStats();
		for (int i = 0; i < ITERATIONS; i++) {
			logger.debug("Iteration " + i);
			auction.reset();
			auction.run();
			stats.calculate();
			if (stats.equilibriaExists()) {
				System.out.println("EA = " + stats.getEA());
				updateStats();
			} else {
				System.out.println("no equilibrium price");
			}
		}
		System.out.println(eA);
		System.out.println(mPS);
		System.out.println(mPB);
		traderReport();
	}
	
	public void testSummaryStatistics() {
		SummaryStats summaryStatistics = new SummaryStats();
		summaryStatistics.newData(100.0);
		for(int i=0; i<99; i++) {
			summaryStatistics.newData(0.0);
		}
		assertTrue(summaryStatistics.getMean() <= summaryStatistics.getMax());
		System.out.println(summaryStatistics);
	}

	public void updateStats() {
		mPS.newData(stats.getMPS());
		mPB.newData(stats.getMPB());
		eA.newData(stats.getEA());
		assertTrue(eA.getMean() <= eA.getMax());
	}

	public void initStats() {
		mPS = new SummaryStats("MPS");
		mPB = new SummaryStats("MPB");
		eA = new SummaryStats("EA");
	}

	public void initialiseAuction() {
		auction = new MarketSimulation();
		controller = new SpringSimulationController();
		auction.setSimulationController(controller);
		auction.setPopulation(new Population());
		auction.setAgentMixer(new RandomRobinAgentMixer(prng));
		auction.setAgentInitialiser(new BasicAgentInitialiser());
	}
	
	public void experimentSetup(int ns, int nb, int cs, int cb) {
		this.ns = ns;
		this.nb = nb;
		this.cs = cs;
		this.cb = cb;
		initialiseAuction();
		auctioneer = new ClearingHouseAuctioneer(auction);
		((AbstractAuctioneer) auctioneer)
				.setPricingPolicy(new DiscriminatoryPricingPolicy(0.5));
		auction.setAuctioneer(auctioneer);
		auction.setMaximumRounds(MAX_ROUNDS);
		registerTraders(auction, true, ns, cs, sellerValues);
		registerTraders(auction, false, nb, cb, buyerValues);
		stats = new ElectricityStats(auction);
	}

	public void registerTraders(MarketSimulation auction, boolean areSellers,
			int num, int capacity, double[] values) {
		for (int i = 0; i < num; i++) {
			double value = values[i % values.length];
			FixedDirectionTradingAgent agent = new FixedDirectionTradingAgent(auction.getSimulationController());
			assignStrategy(capacity, agent);
			agent.setIsSeller(areSellers);
			assignValuer(agent);
			auction.register(agent);
		}
	}

	public void assignStrategy(int capacity, FixedDirectionTradingAgent agent) {
		StimuliResponseStrategy strategy = new StimuliResponseStrategy(agent);
		strategy.setQuantity(capacity);
		NPTRothErevLearner learner = new NPTRothErevLearner(K, R, E, S1, prng);
		strategy.setLearner(learner);
		agent.setStrategy(strategy);
		strategy.setAgent(agent);
		assert agent.getVolume(auction) == capacity;
		agent.initialise();
	}

	public void assignValuer(FixedDirectionTradingAgent agent) {
		// Stick with default fixed valuation
	}

	public void traderReport() {
		Iterator<Agent> i = auction.getTraderIterator();
		while (i.hasNext()) {
			FixedDirectionTradingAgent agent = 
				(FixedDirectionTradingAgent) i.next();
			System.out.println(agent);
		}
	}

}