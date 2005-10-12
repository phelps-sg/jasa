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

package uk.ac.liv.auction.agent;

import junit.framework.*;

import uk.ac.liv.PRNGTestSeeds;

import uk.ac.liv.auction.agent.RandomConstrainedStrategy;
import uk.ac.liv.auction.zi.ZITraderAgent;

import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.core.ClearingHouseAuctioneer;

import uk.ac.liv.auction.stats.PriceStatisticsReport;

import uk.ac.liv.util.CummulativeDistribution;

import uk.ac.liv.prng.GlobalPRNG;

public class RandomConstrainedStrategyTest extends TestCase {

  /**
   * @uml.property name="testStrategy"
   * @uml.associationEnd
   */
  protected RandomConstrainedStrategy testStrategy;

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

  static final double MAX_MARKUP = 100.0;

  static final double PRIV_VALUE = 7.0;

  static final int MAX_ROUNDS = 200000;

  public RandomConstrainedStrategyTest( String name ) {
    super(name);
  }

  public void setUp() {
    GlobalPRNG.initialiseWithSeed(PRNGTestSeeds.UNIT_TEST_SEED);
    testAgent = new ZITraderAgent(PRIV_VALUE, 100, true);
    testStrategy = new RandomConstrainedStrategy(testAgent, MAX_MARKUP);
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
    logger.produceUserOutput();
    CummulativeDistribution askStats = logger.getAskPriceStats();
    assertTrue(approxEqual(askStats.getMin(), PRIV_VALUE));
    assertTrue(approxEqual(askStats.getMax(), MAX_MARKUP + PRIV_VALUE));
    assertTrue(approxEqual(askStats.getMean(), (MAX_MARKUP / 2) + PRIV_VALUE));
  }

  public void testBidFloor() {
    testAgent.setIsSeller(false);
    System.out.println(getClass() + ": testBidFloor()");
    System.out.println("testAgent = " + testAgent);
    System.out.println("testStrategy = " + testStrategy);
    auction.run();
    logger.produceUserOutput();
    CummulativeDistribution bidStats = logger.getBidPriceStats();
    assertTrue(bidStats.getMin() >= 0);
    assertTrue(bidStats.getMax() <= PRIV_VALUE);
  }

  public boolean approxEqual( double x, double y ) {
    return Math.abs(x - y) < 0.1;
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(RandomConstrainedStrategyTest.class);
  }

}