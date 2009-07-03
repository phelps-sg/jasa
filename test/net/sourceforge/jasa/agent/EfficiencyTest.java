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
import cern.jet.random.engine.RandomEngine;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.valuation.RandomValuer;
import net.sourceforge.jasa.market.RandomRobinAuction;
import net.sourceforge.jasa.market.auctioneer.AbstractAuctioneer;
import net.sourceforge.jasa.market.auctioneer.ContinuousDoubleAuctioneer;
import net.sourceforge.jasa.market.rules.DiscriminatoryPricingPolicy;
import net.sourceforge.jasa.replication.zi.ZITraderAgent;
import net.sourceforge.jasa.report.SurplusReport;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import net.sourceforge.jasa.sim.prng.GlobalPRNG;
import net.sourceforge.jasa.sim.util.SummaryStats;
import junit.framework.TestCase;

/**
 * Tests that a given strategy yields a certain minimum benchmark mean
 * efficiency over many iterations of self-play, and that efficiency values fall
 * within the range (0, 1).
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public abstract class EfficiencyTest extends TestCase {

	/**
	 * @uml.property name="auctioneer"
	 * @uml.associationEnd
	 */
	protected AbstractAuctioneer auctioneer;

	/**
	 * @uml.property name="market"
	 * @uml.associationEnd
	 */
	protected RandomRobinAuction auction;

	/**
	 * @uml.property name="agents"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	protected ZITraderAgent[] agents;

	static final int NS = 6;

	static final int NB = 6;

	static int ITERATIONS = 200;

	static final double MIN_VALUE = 50;

	static final double MAX_VALUE = 300;

	static final double MIN_VALUE_MIN = 161;

	static final double MIN_VALUE_MAX = 260;

	static final double RANGE_MIN = 90;

	static final double RANGE_MAX = 100;

	static final int MAX_DAYS = 100;

	static final int DAY_LENGTH = 20;

	public EfficiencyTest(String name) {
		super(name);
	}

	protected void assignAuctioneer() {
		auctioneer = new ContinuousDoubleAuctioneer();
		auctioneer.setPricingPolicy(new DiscriminatoryPricingPolicy(0.5));
		auctioneer.setAuction(auction);
		auction.setAuctioneer(auctioneer);
	}

	protected void registerTraders() {
		int numAgents = getNumBuyers() + getNumSellers();
		agents = new ZITraderAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new ZITraderAgent();
			agents[i].setInitialTradeEntitlement(getInitialTradeEntitlement());
			assignStrategy(agents[i]);
			assignValuationPolicy(agents[i]);
			agents[i].setIsSeller(i < getNumSellers());
			System.out.println("Registering trader " + agents[i]);
			auction.register(agents[i]);
		}
	}

	public void testEfficiency() {
		GlobalPRNG.initialiseWithSeed(PRNGTestSeeds.UNIT_TEST_SEED);
		System.out.println("\ntestEfficiency()");
		SummaryStats efficiency = new SummaryStats(
		    "efficiency");
		initialiseExperiment();
		for (int i = 0; i < ITERATIONS; i++) {
			auction.reset();
			auction.run();
			SurplusReport surplus = new SurplusReport(auction);
			surplus.calculate();
			System.out
			    .println("Iteration " + i + ": efficiency = " + surplus.getEA());
			if (!Double.isNaN(surplus.getEA())) {
				efficiency.newData(surplus.getEA());
			}
		}
		double meanEfficiency = efficiency.getMean();

		System.out.println("Mean efficiency = " + meanEfficiency);

		assertTrue("infinite efficiency", !Double.isInfinite(meanEfficiency));

		assertTrue("mean efficiency too low",
		    meanEfficiency >= getMinMeanEfficiency());

		assertTrue("max efficiency too high", efficiency.getMax() <= 100 + 10E-6);

		assertTrue("negative efficiency encountered", efficiency.getMin() >= 0);
	}

	protected void initialiseExperiment() {
		initialiseAuction();
		assignAuctioneer();
		registerTraders();
	}

	protected void initialiseAuction() {
		RandomEngine prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);
		auction = new RandomRobinAuction(prng);
		auction.setLengthOfDay(DAY_LENGTH);
		auction.setMaximumDays(MAX_DAYS);
	}

	protected int getNumBuyers() {
		return NB;
	}

	protected int getNumSellers() {
		return NS;
	}

	protected void assignValuationPolicy(AbstractTradingAgent agent) {
		agent.setValuationPolicy(new RandomValuer(MIN_VALUE, MAX_VALUE, GlobalPRNG.getInstance()));
	}

	protected int getInitialTradeEntitlement() {
		return 1;
	}

	protected abstract void assignStrategy(AbstractTradingAgent agent);

	protected abstract double getMinMeanEfficiency();

}
