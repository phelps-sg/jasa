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

package test.uk.ac.liv.auction.agent;

import junit.framework.*;

import uk.ac.liv.auction.agent.RandomConstrainedStrategy;
import uk.ac.liv.auction.agent.ZITraderAgent;

import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.core.KDoubleAuctioneer;

import uk.ac.liv.auction.stats.StatsMarketDataLogger;

import uk.ac.liv.util.CummulativeStatCounter;


public class RandomConstrainedStrategyTest extends TestCase {

  protected RandomConstrainedStrategy testStrategy;

  protected ZITraderAgent testAgent;

  protected KDoubleAuctioneer auctioneer;

  protected RoundRobinAuction auction;

  protected StatsMarketDataLogger logger;

  static final double MAX_MARKUP = 100.0;
  static final double PRIV_VALUE = 7.0;

  static final int MAX_ROUNDS = 100000;

  public RandomConstrainedStrategyTest( String name ) {
    super(name);
  }

  public void setUp() {
    testAgent = new ZITraderAgent(PRIV_VALUE, 100, true);
    testStrategy = new RandomConstrainedStrategy(testAgent, MAX_MARKUP);
    auction = new RoundRobinAuction();
    auctioneer = new KDoubleAuctioneer(auction);
    auction.setAuctioneer(auctioneer);
    logger = new StatsMarketDataLogger();
    auction.setMarketDataLogger(logger);
    auction.register(testAgent);
    auction.setMaximumRounds(MAX_ROUNDS);
  }

  public void testBidRange() {
    System.out.println(getClass() + ": testBidRange()");
    System.out.println("testAgent = " + testAgent);
    System.out.println("testStrategy = " + testStrategy);
    auction.run();
    logger.finalReport();
    CummulativeStatCounter askStats = logger.getAskPriceStats();
    assertTrue(approxEqual(askStats.getMin(), PRIV_VALUE));
    assertTrue(approxEqual(askStats.getMax(), MAX_MARKUP + PRIV_VALUE));
    assertTrue(approxEqual(askStats.getMean(), (MAX_MARKUP/2) + PRIV_VALUE));
  }

  public void testBidFloor() {
    testAgent.setIsSeller(false);
    System.out.println(getClass() + ": testBidFloor()");
    System.out.println("testAgent = " + testAgent);
    System.out.println("testStrategy = " + testStrategy);
    auction.run();
    logger.finalReport();
    CummulativeStatCounter bidStats = logger.getBidPriceStats();
    assertTrue(bidStats.getMin() >= 0);
    assertTrue(bidStats.getMax() <= PRIV_VALUE);
  }

  public boolean approxEqual( double x, double y ) {
    return Math.abs(x-y) < 0.05;
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(RandomConstrainedStrategyTest.class);
  }

}

