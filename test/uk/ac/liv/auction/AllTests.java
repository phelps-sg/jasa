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

package uk.ac.liv.auction;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllTests {

	public static void main(String[] args) {
//		org.apache.log4j.BasicConfigurator.configure();
		TestRunner.run(suite());
	}

	public static Test suite() {

		TestSuite suite = new TestSuite("auction test suite");

		suite.addTest(uk.ac.liv.util.DiscreteProbabilityDistributionTest.suite());
		suite.addTest(uk.ac.liv.util.CummulativeDistributionTest.suite());
		suite.addTest(uk.ac.liv.util.FixedLengthQueueTest.suite());

		suite.addTest(uk.ac.liv.prng.GlobalPRNGTest.suite());

		suite.addTest(uk.ac.liv.auction.NPTReplicationTest.suite());
		suite.addTest(uk.ac.liv.auction.DirectRevelationTest.suite());
		// suite.addTest(test.uk.ac.liv.auction.SerializationTests.suite());

		suite.addTest(uk.ac.liv.auction.agent.AbstractTraderAgentTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.AdaptiveStrategyTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.MixedStrategyTest.suite());
		suite
		    .addTest(uk.ac.liv.auction.agent.RandomConstrainedStrategyTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.RandomUnconstrainedStrategyTest
		    .suite());
		suite.addTest(uk.ac.liv.auction.agent.MomentumStrategyTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.RandomValuerTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.BuyerIntervalValuerTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.SellerIntervalValuerTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.CommodityHoldingTest.suite());

		suite.addTest(uk.ac.liv.auction.zi.ZITraderAgentTest.suite());

		suite.addTest(uk.ac.liv.auction.electricity.ElectricityStatsTest.suite());
		suite.addTest(uk.ac.liv.auction.stats.EquilibriumSurplusLoggerTest.suite());
		suite.addTest(uk.ac.liv.auction.stats.EquilibriaStatsTest.suite());

		suite.addTest(uk.ac.liv.auction.core.ClearingHouseAuctioneerTest.suite());
		suite.addTest(uk.ac.liv.auction.core.McAfeeClearingHouseAuctioneerTest
		    .suite());
		suite
		    .addTest(uk.ac.liv.auction.core.ContinuousDoubleAuctioneerTest.suite());
		suite.addTest(uk.ac.liv.auction.core.RandomRobinAuctionTest.suite());
		suite.addTest(uk.ac.liv.auction.core.FourHeapTest.suite());
		suite.addTest(uk.ac.liv.auction.core.KPricingPolicyTest.suite());

		suite.addTest(uk.ac.liv.ai.learning.RothErevLearnerTest.suite());
		suite.addTest(uk.ac.liv.ai.learning.QLearnerTest.suite());
		suite.addTest(uk.ac.liv.ai.learning.WidrowHoffLearnerTest.suite());

		suite.addTest(uk.ac.liv.auction.agent.RandomScheduleValuerTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.KaplanStrategyTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.RothErevEfficiencyTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.EPEfficiencyTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.GDEfficiencyTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.QLearnerEfficiencyTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.PriestVanTolEfficiencyTest.suite());
		suite.addTest(uk.ac.liv.auction.agent.ZIPEfficiencyTest.suite());

		return suite;
	}

}
