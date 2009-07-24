/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.strategy.ZIPStrategy;
import net.sourceforge.jasa.market.auctioneer.ContinuousDoubleAuctioneer;
import net.sourceforge.jasa.market.rules.DiscriminatoryPricingPolicy;
import net.sourceforge.jasa.sim.learning.WidrowHoffLearnerWithMomentum;
import junit.framework.Test;
import junit.framework.TestSuite;




public class ZIPEfficiencyTest extends EfficiencyTest {

	public static final int TRADE_ENTITLEMENT = 5;

	public ZIPEfficiencyTest(String name) {
		super(name);
	}

	protected void assignAuctioneer() {
		auctioneer = new ContinuousDoubleAuctioneer();
		auctioneer.setPricingPolicy(new DiscriminatoryPricingPolicy(0.5));
		auctioneer.setMarket(auction);
		auction.setAuctioneer(auctioneer);
	}

	protected void assignStrategy(AbstractTradingAgent agent) {
		ZIPStrategy strategy = new ZIPStrategy(prng);
		WidrowHoffLearnerWithMomentum learner = new WidrowHoffLearnerWithMomentum(prng);
		learner.setMomentum(0.9);
		learner.setLearningRate(0.45);
		strategy.setLearner(learner);
		strategy.setScaling(0.2);
		agent.setStrategy(strategy);
		strategy.setAgent(agent);
		strategy.initialise();
	}

	protected int getInitialTradeEntitlement() {
		return TRADE_ENTITLEMENT;
	}

	protected double getMinMeanEfficiency() {
		return 90.0;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(ZIPEfficiencyTest.class);
	}
}
