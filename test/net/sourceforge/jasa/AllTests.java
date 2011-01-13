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

package net.sourceforge.jasa;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllTests {

	public static void main(String[] args) {
//		org.apache.log4j.BasicConfigurator.configure();
		TestRunner.run(suite());
	}

	public static Test suite() {

		TestSuite suite = new TestSuite("market test suite");

		suite.addTest(net.sourceforge.jasa.sim.util.DiscreteProbabilityDistributionTest.suite());
		suite.addTest(net.sourceforge.jasa.sim.util.CummulativeDistributionTest.suite());
		suite.addTest(net.sourceforge.jasa.sim.util.FixedLengthQueueTest.suite());

		suite.addTest(net.sourceforge.jasa.replication.electricity.NPTReplicationTest.suite());
		suite.addTest(net.sourceforge.jasa.market.DirectRevelationTest.suite());
		// suite.addTest(test.uk.ac.liv.auction.SerializationTests.suite());

		suite.addTest(net.sourceforge.jasa.agent.AbstractTraderAgentTest.suite());
		suite.addTest(net.sourceforge.jasa.agent.AdaptiveStrategyTest.suite());
		suite
		    .addTest(net.sourceforge.jasa.agent.RandomConstrainedStrategyTest.suite());
		suite.addTest(net.sourceforge.jasa.agent.RandomUnconstrainedStrategyTest
		    .suite());
		suite.addTest(net.sourceforge.jasa.agent.MomentumStrategyTest.suite());
		suite.addTest(net.sourceforge.jasa.agent.RandomValuerTest.suite());
		suite.addTest(net.sourceforge.jasa.agent.BuyerIntervalValuerTest.suite());
		suite.addTest(net.sourceforge.jasa.agent.SellerIntervalValuerTest.suite());
		suite.addTest(net.sourceforge.jasa.agent.CommodityHoldingTest.suite());

		suite.addTest(net.sourceforge.jasa.replication.zi.ZITraderAgentTest.suite());

		suite.addTest(net.sourceforge.jasa.replication.electricity.ElectricityStatsTest.suite());
		suite.addTest(net.sourceforge.jasa.report.EquilibriumSurplusLoggerTest.suite());
		suite.addTest(net.sourceforge.jasa.report.EquilibriaStatsTest.suite());

		suite.addTest(net.sourceforge.jasa.market.ClearingHouseAuctioneerTest.suite());
		suite
		    .addTest(net.sourceforge.jasa.market.ContinuousDoubleAuctioneerTest.suite());
		suite.addTest(net.sourceforge.jasa.market.RandomRobinAuctionTest.suite());
		suite.addTest(net.sourceforge.jasa.market.FourHeapTest.suite());
		suite.addTest(net.sourceforge.jasa.market.KPricingPolicyTest.suite());
		suite.addTest(net.sourceforge.jasa.market.auctioneer.SingleSidedPricingTest.suite());

		suite.addTest(net.sourceforge.jasa.sim.ai.learning.RothErevLearnerTest.suite());
		suite.addTest(net.sourceforge.jasa.sim.ai.learning.QLearnerTest.suite());
		suite.addTest(net.sourceforge.jasa.sim.ai.learning.WidrowHoffLearnerTest.suite());

		suite.addTest(net.sourceforge.jasa.agent.RandomScheduleValuerTest.suite());
		suite.addTest(net.sourceforge.jasa.agent.KaplanStrategyTest.suite());
		suite.addTest(net.sourceforge.jasa.agent.RothErevEfficiencyTest.suite());
		suite.addTest(net.sourceforge.jasa.agent.EPEfficiencyTest.suite());
		suite.addTest(net.sourceforge.jasa.agent.GDEfficiencyTest.suite());
		suite.addTest(net.sourceforge.jasa.agent.QLearnerEfficiencyTest.suite());
		suite.addTest(net.sourceforge.jasa.agent.PriestVanTolEfficiencyTest.suite());
		suite.addTest(net.sourceforge.jasa.agent.ZIPEfficiencyTest.suite());

		return suite;
	}

}
