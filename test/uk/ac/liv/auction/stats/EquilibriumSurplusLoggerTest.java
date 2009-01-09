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

package uk.ac.liv.auction.stats;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.liv.PRNGTestSeeds;
import uk.ac.liv.auction.agent.DailyRandomValuer;
import uk.ac.liv.auction.agent.TruthTellingStrategy;
import uk.ac.liv.auction.core.AbstractAuctioneer;
import uk.ac.liv.auction.core.ClearingHouseAuctioneer;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.UniformPricingPolicy;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionEventListener;
import uk.ac.liv.auction.event.EndOfDayEvent;
import uk.ac.liv.auction.zi.ZITraderAgent;
import uk.ac.liv.prng.GlobalPRNG;
import uk.ac.liv.util.MathUtil;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class EquilibriumSurplusLoggerTest extends TestCase implements
    AuctionEventListener {

	/**
	 * @uml.property name="auctioneer"
	 * @uml.associationEnd
	 */
	protected ClearingHouseAuctioneer auctioneer;

	/**
	 * @uml.property name="auction"
	 * @uml.associationEnd
	 */
	protected RandomRobinAuction auction;

	/**
	 * @uml.property name="eqLogger"
	 * @uml.associationEnd
	 */
	protected DynamicSurplusReport eqLogger;

	/**
	 * @uml.property name="computedSurplus"
	 */
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
		GlobalPRNG.initialiseWithSeed(PRNGTestSeeds.UNIT_TEST_SEED);
	}

	public void initWithParams(int ns, int nb, int tradeEnt) {
		auction = new RandomRobinAuction(getName());
		auctioneer = new ClearingHouseAuctioneer(auction);
		((AbstractAuctioneer) auctioneer)
		    .setPricingPolicy(new UniformPricingPolicy(0.5));
		auction.setAuctioneer(auctioneer);
		auction.setMaximumDays(MAX_DAYS);
		auction.setLengthOfDay(DAY_LEN);
		eqLogger = new DynamicSurplusReport();
		auction.setReport(eqLogger);
		eqLogger.setAuction(auction);
		eqLogger.setQuantity(tradeEnt);

		for (int i = 0; i < ns + nb; i++) {
			ZITraderAgent agent = new ZITraderAgent();

			agent.setStrategy(new TruthTellingStrategy(agent));
			agent.setInitialTradeEntitlement(tradeEnt);
			if (i < ns) {
				agent.setIsSeller(true);
				agent.setValuationPolicy(new DailyRandomValuer(SELLER_MIN_VALUE,
				    SELLER_MAX_VALUE));
			} else {
				agent.setIsSeller(false);
				agent.setValuationPolicy(new DailyRandomValuer(BUYER_MIN_VALUE,
				    BUYER_MAX_VALUE));
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
		auction.addAuctionEventListener(this);
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

	public void eventOccurred(AuctionEvent event) {
		if (event instanceof EndOfDayEvent) {
			// System.out.println("At end of day " + auction.getDay());
			EquilibriumReport eqStats = new EquilibriumReport(auction);
			eqStats.calculate();
			double eqPrice = eqStats.calculateMidEquilibriumPrice();
			// System.out.println("equilibria found? " + eqStats.equilibriaExists());
			// System.out.println("equilibrium price = " + eqPrice);
			double dailySurplus = 0;
			double agentSurplus = 0;
			Iterator i = auction.getTraderIterator();
			while (i.hasNext()) {
				ZITraderAgent agent = (ZITraderAgent) i.next();
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
