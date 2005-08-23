/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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

package test.uk.ac.liv.auction.agent;

import junit.framework.*;

import test.uk.ac.liv.PRNGTestSeeds;

import uk.ac.liv.auction.agent.RandomUnconstrainedStrategy;
import uk.ac.liv.auction.zi.ZITraderAgent;

import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.core.ClearingHouseAuctioneer;

import uk.ac.liv.auction.stats.PriceStatisticsReport;

import uk.ac.liv.util.CummulativeDistribution;

import uk.ac.liv.prng.GlobalPRNG;

public class RandomUnconstrainedStrategyTest extends TestCase {

  /**
   * @uml.property name="testStrategy"
   * @uml.associationEnd
   */
  protected RandomUnconstrainedStrategy testStrategy;

  /**
   * @uml.property name="testAgent"
   * @uml.associationEnd
   */
  protected ZITraderAgent testAgent;

  /**
   * @uml.property name="auctioneer"
   * @uml.associationEnd
   */
  protected ClearingHouseAuctioneer auctioneer;

  /**
   * @uml.property name="auction"
   * @uml.associationEnd
   */
  protected RoundRobinAuction auction;

  /**
   * @uml.property name="logger"
   * @uml.associationEnd
   */
  protected PriceStatisticsReport logger;

  static final double MAX_PRICE = 100.0;

  static final double PRIV_VALUE = 57.0;

  static final int MAX_ROUNDS = 100000;

  public RandomUnconstrainedStrategyTest( String name ) {
    super(name);
  }

  public void setUp() {
    GlobalPRNG.initialiseWithSeed(PRNGTestSeeds.UNIT_TEST_SEED);
    testAgent = new ZITraderAgent(PRIV_VALUE, 100, true);
    testStrategy = new RandomUnconstrainedStrategy(testAgent);
    testStrategy.setMaxPrice(MAX_PRICE);
    testAgent.setStrategy(testStrategy);
    auction = new RoundRobinAuction();
    auctioneer = new ClearingHouseAuctioneer(auction);
    auction.setAuctioneer(auctioneer);
    logger = new PriceStatisticsReport();
    auction.setReport(logger);
    auction.register(testAgent);
    auction.setMaximumRounds(MAX_ROUNDS);
  }

  public void testBidRange() {
    System.out.println(getClass() + ": testBidRange()");
    System.out.println("testAgent = " + testAgent);
    System.out.println("testStrategy = " + testStrategy);
    auction.run();
    CummulativeDistribution askStats = logger.getAskPriceStats();
    System.out.println(askStats);
    assertTrue(approxEqual(askStats.getMin(), 0));
    assertTrue(approxEqual(askStats.getMax(), MAX_PRICE));
    assertTrue(approxEqual(askStats.getMean(), (MAX_PRICE / 2)));
  }

  public boolean approxEqual( double x, double y ) {
    return Math.abs(x - y) < 0.1;
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(RandomUnconstrainedStrategyTest.class);
  }

}
