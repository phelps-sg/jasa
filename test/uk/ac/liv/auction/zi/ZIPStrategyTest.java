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

package uk.ac.liv.auction.zi;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;

import uk.ac.liv.ai.learning.WidrowHoffLearner;
import uk.ac.liv.auction.core.ClearingHouseAuctioneer;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.stats.AuctionReport;
import uk.ac.liv.auction.stats.EquilibriumReport;
import uk.ac.liv.auction.stats.PriceStatisticsReport;
import ec.util.MersenneTwisterFast;

public class ZIPStrategyTest extends TestCase {

	/**
	 * @uml.property name="buyers"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	ZITraderAgent[] buyers;

	/**
	 * @uml.property name="sellers"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	ZITraderAgent[] sellers;

	/**
	 * @uml.property name="auction"
	 * @uml.associationEnd
	 */
	RandomRobinAuction auction;

	/**
	 * @uml.property name="auctioneer"
	 * @uml.associationEnd
	 */
	ClearingHouseAuctioneer auctioneer;

	/**
	 * @uml.property name="marketDataLogger"
	 * @uml.associationEnd
	 */
	AuctionReport marketDataLogger;

	/**
	 * @uml.property name="prng"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	MersenneTwisterFast prng = new MersenneTwisterFast();

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
//		org.apache.log4j.BasicConfigurator.configure();
	}

	public void setUp() {
		auction = new RandomRobinAuction();
		auctioneer = new ClearingHouseAuctioneer(auction);
		auction.setAuctioneer(auctioneer);
		auction.setMaximumRounds(NUM_ROUNDS);
		marketDataLogger = new PriceStatisticsReport();
		auction.setReport(marketDataLogger);
		buyers = new ZITraderAgent[NUM_BUYERS];
		sellers = new ZITraderAgent[NUM_SELLERS];
		registerTraders(buyers, false);
		registerTraders(sellers, true);
		EquilibriumReport eqStats = new EquilibriumReport(auction);
		eqStats.calculate();
		logger.info(eqStats);
	}

	public void testReplication() {
		for (int day = 0; day < NUM_DAYS; day++) {
			logger.debug("Day " + day);
			auction.run();
			auction.generateReport();
			auction.reset();

		}
	}

	public void registerTraders(ZITraderAgent[] traders, boolean areSellers) {
		double privValue = PRIV_VALUE_RANGE_MIN;
		for (int i = 0; i < traders.length; i++) {
			traders[i] = new ZITraderAgent(privValue, TRADE_ENTITLEMENT, areSellers);
			ZIPStrategy strategy = new ZIPStrategy();
			double learningRate = 0.1 + prng.nextDouble() * 0.4;
			WidrowHoffLearner learner = new WidrowHoffLearner(learningRate);
			strategy.setLearner(learner);
			traders[i].setStrategy(strategy);
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
