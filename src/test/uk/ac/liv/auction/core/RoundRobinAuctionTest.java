/*
 * JASA Java Auction Simulator API Copyright (C) 2001-2004 Steve Phelps
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package test.uk.ac.liv.auction.core;

import junit.framework.*;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.core.*;

import uk.ac.liv.auction.stats.DailyStatsMarketDataLogger;
import uk.ac.liv.auction.stats.HistoryStatsMarketDataLogger;

import uk.ac.liv.util.Distribution;

import test.uk.ac.liv.auction.agent.TestTrader;

import org.apache.log4j.Logger;

public class RoundRobinAuctionTest extends TestCase {

  KDoubleAuctioneer auctioneer;

  RoundRobinAuction auction;

  TestTrader[] traders;

  static Logger logger = Logger.getLogger(RoundRobinAuctionTest.class);

  public RoundRobinAuctionTest ( String name) {
    super(name);
    org.apache.log4j.BasicConfigurator.configure();
  }

  public void setUpTraders () {
    traders = new TestTrader[3];

    traders[0] = new TestTrader(this, 30, 1000, 500, false);
    traders[1] = new TestTrader(this, 10, 10000, 500, false);
    traders[2] = new TestTrader(this, 15, 10000, 725, true);

    TestTrader trader = traders[0];
    trader.shouts = new Shout[] { new Shout(trader, 1, 500, true),
        						new Shout(trader, 1, 600, true), 
        						new Shout(trader, 1, 700, true) };

    trader = traders[1];
    trader.shouts = new Shout[] { new Shout(trader, 1, 500, true),
        						new Shout(trader, 1, 550, true),
        						new Shout(trader, 1, 750, true) };

    trader = traders[2];
    trader.shouts = new Shout[] { new Shout(trader, 1, 900, false),
        						new Shout(trader, 1, 950, false), 
        						new Shout(trader, 1, 725, false) };
  }

  public void setUp () {
    auctioneer = new KDoubleAuctioneer(auction, 0);
    setUpTraders();
    setUpAuction();
  }

  public void setUpAuction () {
    auction = new RoundRobinAuction("Round Robin Test Auction");
    auction.setAuctioneer(auctioneer);
    auctioneer.setAuction(auction);
    for ( int i = 0; i < traders.length; i++ ) {
      System.out.println("Registering trader " + traders[i]);
      auction.register(traders[i]);
    }
  }

  public void testDailyStats () {

    auction.setLengthOfDay(3);
    auction.setMaximumDays(1);

    DailyStatsMarketDataLogger dailyStats = new DailyStatsMarketDataLogger();
    dailyStats.setAuction(auction);
    auction.setMarketDataLogger(dailyStats);
    dailyStats.setup(new ParameterDatabase(), new Parameter("stats"));

    auction.run();

    try {
      Distribution transPrice = auction.getPreviousDayTransPriceStats();
      logger.info("Previous day transaction price statistics = " + transPrice);
      assertTrue(transPrice.getMean() == 725);
    } catch ( DataUnavailableException e ) {
      fail("caught DataUnavailableException " + e);
    }
  }

  public void testHistoryStats () {
    logger.info("testHistoryStats()");

    HistoryStatsMarketDataLogger stats = new HistoryStatsMarketDataLogger();
    stats.setAuction(auction);
    auction.setMarketDataLogger(stats);
    stats.setup(new ParameterDatabase(), new Parameter("stats"));

    auction.run();

    try {
      int acceptedBids = auction.getNumberOfBids(0, true);
      int unacceptedBids = auction.getNumberOfBids(0, false);
      int acceptedAsks = auction.getNumberOfAsks(0, true);
      int unacceptedAsks = auction.getNumberOfAsks(0, false);
      logger.info("Number of accepted bids above 0 = " + acceptedBids);
      logger.info("Number of unaccepted bids above 0 = " + unacceptedBids);
      logger.info("Number of accepted asks above 0 = " + acceptedAsks);
      logger.info("Number of unaccepted asks above 0 = " + unacceptedAsks);
      assertTrue(acceptedBids == acceptedAsks);
      assertTrue(acceptedBids == 1);
    } catch ( DataUnavailableException e ) {
      fail("caught DataUnavailableException " + e);
    }
  }

  public void testProtocol () {

    assertTrue(auction.getNumberOfTraders() == traders.length);
    assertTrue(!auction.closed());

    auction.setMaximumRounds(2);

    assertTrue(auction.getMaximumRounds() == 2);

    auction.run();

    for ( int i = 0; i < traders.length; i++ ) {
      assertTrue(traders[i].receivedAuctionOpen);
      assertTrue(traders[i].receivedAuctionClosed);
      assertTrue(traders[i].receivedAuctionClosedAfterAuctionOpen);
      assertTrue(traders[i].receivedRequestShout == 2);
      assertTrue(traders[i].receivedRoundClosed == 2);
    }

  }

  public void testBasic () {
    try {
      assertTrue(auction.getNumberOfTraders() == traders.length);
      assertTrue(!auction.closed());

      assertTrue(!auction.transactionsOccured());

      auction.run();

      logger.debug("quiescent = " + auction.isQuiescent());
      logger.debug("no of traders = " + auction.getNumberOfTraders());

      assertTrue(auction.closed());
      assertTrue(auction.getNumberOfTraders() == 0);
      assertTrue(traders[1].lastWinningPrice == 725);

      assertTrue(auction.getQuote().getBid() == 700);

      auction.reset();

      assertTrue(!auction.closed());
      assertTrue(auction.getNumberOfTraders() == 3);
      assertTrue(auction.getQuote().getBid() < 0);

      auction.run();

      assertTrue(traders[1].lastWinningPrice == 725);

    } catch ( ShoutsNotVisibleException e ) {
      fail("test is configured incorrectly: we must use an auctioneer that permits shout visibility");
    }
  }
  
  /**
   * Test that transactions occur only in the final (3rd) round.
   */
  public void testTransactionsOccured() {
    try {
      
      auction.begin();
      
      auction.step();
      assertTrue(!auction.transactionsOccured());
      
      auction.step();
      assertTrue(!auction.transactionsOccured());
      
      auction.step();
      assertTrue(auction.transactionsOccured());
      
    } catch ( AuctionClosedException e ) {
      fail("we tried to step through an auction past its closure");
    } catch ( ShoutsNotVisibleException e ) {
      fail("test is configured incorrectly: we must use an auctioneer that permits shout visibility");
    }
  }

  public static void main ( String[] args) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite () {
    return new TestSuite(RoundRobinAuctionTest.class);
  }

}