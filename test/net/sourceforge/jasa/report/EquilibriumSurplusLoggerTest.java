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

package net.sourceforge.jasa.report;

import java.util.Iterator;

import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

import net.sourceforge.jasa.agent.TokenTradingAgent;
import net.sourceforge.jasa.agent.strategy.TruthTellingStrategy;
import net.sourceforge.jasa.agent.valuation.DailyRandomValuer;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.MarketEventListener;
import net.sourceforge.jasa.event.EndOfDayEvent;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.market.auctioneer.AbstractAuctioneer;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.market.rules.UniformPricingPolicy;
import net.sourceforge.jasa.report.DynamicSurplusReport;
import net.sourceforge.jasa.report.EquilibriumReport;
import net.sourceforge.jasa.report.SurplusReport;
import net.sourceforge.jasa.sim.Agent;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.util.MathUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class EquilibriumSurplusLoggerTest extends TestCase implements
    MarketEventListener {

	protected ClearingHouseAuctioneer auctioneer;

	protected MarketFacade auction;

	protected DynamicSurplusReport eqLogger;
	
	protected RandomEngine prng;

	protected double computedSurplus = 0;

	protected static final int NUM_SELLERS = 11;

	protected static final int NUM_BUYERS = 11;

	protected static final double BUYER_MIN_VALUE = 75;

	protected static final double BUYER_MAX_VALUE = 300;

	protected static final double SELLER_MIN_VALUE = 75;

	protected static final double SELLER_MAX_VALUE = 300;

	protected static final int TRADE_ENT = 10;

	protected static final int MAX_DAYS = 150;

	protected static final int DAY_LEN = 20;

	public EquilibriumSurplusLoggerTest(String name) {
		super(name);
	}

	public void setUp() {
		prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);
	}

	public void initWithParams(int ns, int nb, int tradeEnt) {
		auction = new MarketFacade(
				new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED));
		auctioneer = new ClearingHouseAuctioneer(auction);
		((AbstractAuctioneer) auctioneer)
		    .setPricingPolicy(new UniformPricingPolicy(0.5));
		auction.setAuctioneer(auctioneer);
		auction.setMaximumDays(MAX_DAYS);
		auction.setLengthOfDay(DAY_LEN);
		eqLogger = new DynamicSurplusReport();
		auction.addReport(eqLogger);  
		eqLogger.setAuction(auction);
		eqLogger.setQuantity(tradeEnt);

		for (int i = 0; i < ns + nb; i++) {
			TokenTradingAgent agent = new TokenTradingAgent();

			agent.setStrategy(new TruthTellingStrategy(agent));
			agent.setInitialTradeEntitlement(tradeEnt);
			if (i < ns) {
				agent.setIsSeller(true);
				agent.setValuationPolicy(new DailyRandomValuer(SELLER_MIN_VALUE,
				    SELLER_MAX_VALUE, prng));
			} else {
				agent.setIsSeller(false);
				agent.setValuationPolicy(new DailyRandomValuer(BUYER_MIN_VALUE,
				    BUYER_MAX_VALUE, prng));
			}
			auction.register(agent);
		}
	}

	public void testTotalEqSurplus() {
		checkTotalEqSurplus(NUM_SELLERS, NUM_BUYERS, TRADE_ENT);
	}

	public void testWithZeroTradeEnt() {
		checkTotalEqSurplus(NUM_SELLERS, NUM_BUYERS, 0);
		double totEqSurplus = eqLogger.calculateTotalEquilibriumSurplus();
		assertTrue(
		    "Total equilibrium surplus should be zero with no trade entitlement",
		    totEqSurplus == 0);
	}

	public void testWithNoAgents() {
		checkTotalEqSurplus(0, 0, TRADE_ENT);
		double totEqSurplus = eqLogger.calculateTotalEquilibriumSurplus();
		assertTrue(
		    "Total equilibrium surplus should be zero with no agents entitlement",
		    totEqSurplus == 0);
	}

	public void testWithMoreBuyers() {
		checkTotalEqSurplus(NUM_SELLERS / 2, NUM_BUYERS * 2, TRADE_ENT);
	}

	public void testWithMoreSellers() {
		checkTotalEqSurplus(NUM_SELLERS * 2, NUM_BUYERS / 2, TRADE_ENT);
	}

	/**
	 * Check that the following stats all agree, even when we change valuations on
	 * a daily basis. - theoretical equilibrium surplus as reported by
	 * EquilibriumSurplusLogger - the actual surplus of truthful agents in a CH -
	 * theoretical equilibrium surplus as computed by this class by tallying the
	 * theoretical surplus each day.
	 * 
	 * @param ns
	 * @param nb
	 * @param tradeEnt
	 */

	public void checkTotalEqSurplus(int ns, int nb, int tradeEnt) {

		System.out.println("\ncheckTotalEqSurplus(" + ns + ", " + nb + ", "
		    + tradeEnt + ")");

		initWithParams(ns, nb, tradeEnt);
		computedSurplus = 0;
		auction.addListener(this);
		auction.run();

		double totEqSurplus = eqLogger.calculateTotalEquilibriumSurplus();
		SurplusReport surplusStats = new SurplusReport(auction);
		surplusStats.calculate();
		double totActualSurplus = surplusStats.getPSA() + surplusStats.getPBA();

		System.out.println("Total equilibrium surplus = " + totEqSurplus);
		System.out.println("As calculated using EquilibriaStats = "
		    + computedSurplus);
		System.out.println("Actual surplus = " + totActualSurplus);

		double actualToTheoretical = totActualSurplus / totEqSurplus;
		double computedToTheoretical = computedSurplus / totEqSurplus;

		System.out.println("Ratio of actual to theoretical = "
		    + actualToTheoretical);
		System.out.println("Ratio of computed to theoretical = "
		    + computedToTheoretical);

		assertTrue(
		    "theoretical surplus reported by EquilibriumSurplusLogger differs from truthful agents bidding in a CH",
		    ((totEqSurplus == 0) && (totActualSurplus == 0))
		        || MathUtil.approxEqual(actualToTheoretical, 1.0, 10E-3));

		assertTrue(
		    "theoretical surplus reported by EquilibriumSurplusLogger differs from that calculated using repeated EquilibriaStats calculations",
		    ((totEqSurplus == 0) && (computedSurplus == 0))
		        || MathUtil.approxEqual(computedToTheoretical, 1.0, 0.005));
	}

	public void eventOccurred(SimEvent event) {
		if (event instanceof EndOfDayEvent) {
			// System.out.println("At end of day " + market.getDay());
			EquilibriumReport eqStats = new EquilibriumReport(auction);
			eqStats.calculate();
			double eqPrice = eqStats.calculateMidEquilibriumPrice();
			// System.out.println("equilibria found? " + eqStats.equilibriaExists());
			// System.out.println("equilibrium price = " + eqPrice);
			double dailySurplus = 0;
			double agentSurplus = 0;
			Iterator<Agent> i = auction.getTraderIterator();
			while (i.hasNext()) {
				TokenTradingAgent agent = (TokenTradingAgent) i.next();
				if (agent.isSeller(auction)) {
					agentSurplus = (eqPrice - agent.getValuation(auction))
					    * agent.getInitialTradeEntitlement();
				} else {
					agentSurplus = (agent.getValuation(auction) - eqPrice)
					    * agent.getInitialTradeEntitlement();
				}
				if (agentSurplus < 0) {
					agentSurplus = 0;
				}
				dailySurplus += agentSurplus;
			}
			computedSurplus += dailySurplus;
			// System.out.println("surplus = " + dailySurplus);
			// System.out.println();
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(EquilibriumSurplusLoggerTest.class);
	}
}
