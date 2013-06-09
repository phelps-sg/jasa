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
import net.sourceforge.jasa.agent.strategy.KaplanStrategy;
import net.sourceforge.jasa.market.Account;
import net.sourceforge.jasa.market.AuctionClosedException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.MarketSimulation;
import net.sourceforge.jasa.market.ZeroFundsAccount;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import net.sourceforge.jasa.market.auctioneer.TransparentAuctioneer;
import net.sourceforge.jasa.report.DailyStatsReport;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

public class KaplanStrategyTest extends TestCase {

	MarketSimulation auction;

	MockTrader trader;

	KaplanStrategy strategy;

	DailyStatsReport dailyStats;

	Auctioneer auctioneer;

	MarketQuote quote;

	public static final double S = 0.1;

	public static final int T = 10;

	public KaplanStrategyTest(String name) {
		super(name);
	}

	public void setUp() {
		RandomEngine prng = new MersenneTwister64();
		this.auction = new MarketSimulation();
		SimulationController controller = new SpringSimulationController();
		auction.setSimulationController(controller);
		auction.setPopulation(new Population());
		auction.setAgentMixer(new RandomRobinAgentMixer(prng));
		auction.setAgentInitialiser(new BasicAgentInitialiser());
		trader = new MockTrader(this, 0, 0, 10, auction);
		strategy = new KaplanStrategy();
		strategy.setS(S);
		strategy.setT(T);
		strategy.setBuy(true);
		trader.setStrategy(strategy);
		strategy.setAgent(trader);
		auction.setMaximumRounds(100);
		auction.register(trader);
		dailyStats = new DailyStatsReport();
//		auction.addReport(dailyStats);
		controller.addListener(dailyStats);
		dailyStats.setAuction(auction);
		strategy.setDailyStatsReport(dailyStats);
		quote = new MarketQuote(100, 110);
		auctioneer = new MockAuctioneer(auction, quote);
		auction.setAuctioneer(auctioneer);
		auction.begin();
	}

	public void testTimeRunningOut() {
		try {
			auction.step();
			assertTrue(!strategy.timeRunningOut());
			for (int i = 0; i < 89; i++) {
				auction.step();
			}
			assertTrue(!strategy.timeRunningOut());
			auction.step();
			assertTrue(strategy.timeRunningOut());
			auction.step();
			assertTrue(strategy.timeRunningOut());
		} catch (AuctionClosedException e) {
			fail(e.getMessage());
		}
	}

	public void testSmallSpread() {
		try {
			auction.step();

			strategy.setBuy(false);

			quote.setAsk(109);
			quote.setBid(100);
			assertTrue(strategy.smallSpread());

			quote.setAsk(120);
			quote.setBid(100);
			assertTrue(!strategy.smallSpread());

			quote.setBid(109);
			quote.setAsk(100);
			assertTrue(strategy.smallSpread());

			quote.setAsk(1000);
			quote.setBid(1100);
			assertTrue(strategy.smallSpread());

			strategy.setBuy(true);

			quote.setAsk(1000);
			quote.setBid(1100);
			assertTrue(!strategy.smallSpread());

		} catch (AuctionClosedException e) {
			fail(e.getMessage());
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(KaplanStrategyTest.class);
	}

}

class MockAuctioneer extends TransparentAuctioneer {

	protected MarketQuote staticQuote;

	protected ZeroFundsAccount account;

	public MockAuctioneer(Market market, MarketQuote staticQuote) {
		super(market);
		this.staticQuote = staticQuote;
		account = new ZeroFundsAccount(this);
	}

	public void generateQuote() {
		currentQuote = staticQuote;
	}

//	public void endOfAuctionProcessing() {
//		super.endOfAuctionProcessing();
//	}

	public void onRoundClosed() {
		super.onRoundClosed();
	}

	public boolean shoutsVisible() {
		return true;
	}

	public Account getAccount() {
		return account;
	}

}