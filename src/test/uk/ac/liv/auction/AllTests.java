/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

package test.uk.ac.liv.auction;

import junit.framework.*;
import junit.swingui.TestRunner;


public class AllTests {

  public static void main( String[] args ) {
    try {
      org.apache.log4j.BasicConfigurator.configure();
      TestRunner.run(Class.forName("test.uk.ac.liv.auction.AllTests"));
    } catch ( ClassNotFoundException e ) {
      e.printStackTrace();
    }
  }

  public static Test suite() {

    TestSuite suite = new TestSuite("auction test suite");

    suite.addTest(test.uk.ac.liv.util.BinaryHeapTest.suite());
    suite.addTest(test.uk.ac.liv.util.DiscreteProbabilityDistributionTest.suite());
    suite.addTest(test.uk.ac.liv.util.CummulativeStatCounterTest.suite());
    
    suite.addTest(test.uk.ac.liv.auction.NPTReplicationTest.suite());
    
    suite.addTest(test.uk.ac.liv.auction.agent.RoundRobinTraderTest.suite());
    suite.addTest(test.uk.ac.liv.auction.agent.AdaptiveStrategyTest.suite());
    suite.addTest(test.uk.ac.liv.auction.agent.MixedStrategyTest.suite());
    suite.addTest(test.uk.ac.liv.auction.agent.RandomConstrainedStrategyTest.suite());
    
    suite.addTest(test.uk.ac.liv.auction.core.AuctioneerTest.suite());
    suite.addTest(test.uk.ac.liv.auction.core.RoundRobinAuctionTest.suite());
    suite.addTest(test.uk.ac.liv.auction.core.RandomRobinAuctionTest.suite());
    suite.addTest(test.uk.ac.liv.auction.core.FourHeapTest.suite());
    
    suite.addTest(test.uk.ac.liv.ai.learning.RothErevLearnerTest.suite());
    suite.addTest(test.uk.ac.liv.ai.learning.QLearnerTest.suite());
    
    return suite;
  }


}

