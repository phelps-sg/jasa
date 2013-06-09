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

package net.sourceforge.jasa.replication.zi;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SpringSimulationController;
import net.sourceforge.jabm.init.BasicAgentInitialiser;
import net.sourceforge.jabm.learning.WidrowHoffLearner;
import net.sourceforge.jabm.mixing.RandomRobinAgentMixer;
import net.sourceforge.jasa.agent.TokenTradingAgent;
import net.sourceforge.jasa.agent.strategy.ZIPStrategy;
import net.sourceforge.jasa.market.MarketSimulation;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.report.AuctionReport;
import net.sourceforge.jasa.report.EquilibriumReportVariables;
import net.sourceforge.jasa.sim.PRNGTestSeeds;

import org.apache.log4j.Logger;

import cern.jet.random.engine.MersenneTwister64;

public class ZIPStrategyTest extends TestCase {

	TokenTradingAgent[] buyers;

	TokenTradingAgent[] sellers;

	MarketSimulation auction;

	ClearingHouseAuctioneer auctioneer;

	AuctionReport marketDataLogger;

	MersenneTwister64 prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);

	static final int NUM_ROUNDS = 1000;

	static final int NUM_DAYS = 10;

	static final int TRADE_ENTITLEMENT = 1;

	static final int NUM_BUYERS = 11;

	static final int NUM_SELLERS = 11;

	static final double PRIV_VALUE_RANGE_MIN = 75;

	static final double PRIV_VALUE_INCREMENT = 25;

	static Logger logger = Logger.getLogger(ZIPStrategyTest.class);

	public ZIPStrategyTest(String name) {
		super(name);
		org.apache.log4j.BasicConfigurator.configure();
	}

	public void setUp() {
		auction = new MarketSimulation();
		auction.setSimulationController(new SpringSimulationController());
		auction.setPopulation(new Population());
		auction.setAgentMixer(new RandomRobinAgentMixer(prng));
		auction.setAgentInitialiser(new BasicAgentInitialiser());
		auctioneer = new ClearingHouseAuctioneer(auction);
		auction.setAuctioneer(auctioneer);
		auction.setMaximumRounds(NUM_ROUNDS);
//		marketDataLogger = new PriceStatisticsReport();
//		auction.setReport(marketDataLogger);
		//TODO
		buyers = new TokenTradingAgent[NUM_BUYERS];
		sellers = new TokenTradingAgent[NUM_SELLERS];
		registerTraders(buyers, false);
		registerTraders(sellers, true);
		EquilibriumReportVariables eqStats = 
				new EquilibriumReportVariables(auction);
		eqStats.calculate();
		logger.info(eqStats);
	}

	public void testReplication() {
//		for (int day = 0; day < NUM_DAYS; day++) {
//			logger.debug("Day " + day);
			auction.run();
//			auction.reset();
//		}
	}

	public void registerTraders(TokenTradingAgent[] traders, boolean areSellers) {
		double privValue = PRIV_VALUE_RANGE_MIN;
		for (int i = 0; i < traders.length; i++) {
			traders[i] = new TokenTradingAgent(privValue, TRADE_ENTITLEMENT,
					auction.getSimulationController());
			ZIPStrategy strategy = new ZIPStrategy(prng);
			strategy.setBuy(!areSellers);
			double learningRate = 0.1 + prng.nextDouble() * 0.4;
			WidrowHoffLearner learner = new WidrowHoffLearner(learningRate,
					prng);
			strategy.setLearner(learner);
			traders[i].setStrategy(strategy);
			strategy.setAgent(traders[i]);
			auction.register(traders[i]);
			privValue += PRIV_VALUE_INCREMENT;
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(ZIPStrategyTest.class);
	}

}
