/*
 * Copyright (C) 2001-2005 Steve Phelps
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

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.stats.DailyStatsReport;
import uk.ac.liv.auction.stats.HistoricalDataReport;
import uk.ac.liv.util.Distribution;

import test.uk.ac.liv.auction.agent.MockTrader;
import test.uk.ac.liv.auction.agent.MockStrategy;

import org.apache.log4j.Logger;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class RoundRobinAuctionTest extends TestCase {

  Auctioneer auctioneer;

  RoundRobinAuction auction;

  MockTrader[] traders;

  static Logger logger = Logger.getLogger(RoundRobinAuctionTest.class);

  public RoundRobinAuctionTest ( String name) {
    super(name);
    org.apache.log4j.BasicConfigurator.configure();
  }

  public void setUpTraders () {
    traders = new MockTrader[3];

    traders[0] = new MockTrader(this, 30, 1000, 500, false);
    traders[1] = new MockTrader(this, 10, 10000, 500, false);
    traders[2] = new MockTrader(this, 15, 10000, 725, true);

    MockTrader trader = traders[0];
    trader.setStrategy( new MockStrategy(
        						new Shout[] { 
        						    new Shout(trader, 1, 500, true),
        						    new Shout(trader, 1, 600, true), 
        						    new Shout(trader, 1, 700, true) } ));

    trader = traders[1];
    trader.setStrategy( new MockStrategy( 
        						new Shout[] {
        							new Shout(trader, 1, 500, true),
        							new Shout(trader, 1, 550, true),
        							new Shout(trader, 1, 750, true) } ));

    trader = traders[2];
    trader.setStrategy( new MockStrategy( 
      						new Shout[] { 
      						    new Shout(trader, 1, 900, false),
      						    new Shout(trader, 1, 950, false), 
      						    new Shout(trader, 1, 725, false) } ));
  }	

  public void setUp () {
    auctioneer = new KDoubleAuctioneer(auction, 1);
    setUpTraders();
    setUpAuction();
  }

  public void setUpAuction () {
    auction = new RoundRobinAuction("Round Robin Test Auction");
    auction.setAuctioneer(auctioneer);
    auctioneer.setAuction(auction);
    auction.setMaximumRounds(3);
    for ( int i = 0; i < traders.length; i++ ) {
      System.out.println("Registering trader " + traders[i]);
      auction.register(traders[i]);
    }
  }
  
  public void testDailyStats() {

    auction.setLengthOfDay(3);
    auction.setMaximumDays(1);

    DailyStatsReport dailyStats = new DailyStatsReport();
    dailyStats.setAuction(auction);
    auction.setReport(dailyStats);
    dailyStats.setup(new ParameterDatabase(), new Parameter("stats"));

    auction.run();

    Distribution transPrice = dailyStats.getPreviousDayTransPriceStats();
    logger.info("Previous day transaction price statistics = " + transPrice);
    assertTrue(transPrice.getMean() == 725);
  }

  public void testHistoryStats() {
    logger.info("testHistoryStats()");

    HistoricalDataReport stats = new HistoricalDataReport();
    stats.setAuction(auction);
    auction.setReport(stats);
    stats.setup(new ParameterDatabase(), new Parameter("stats"));

    auction.run();

    int acceptedBids = stats.getNumberOfBids(0, true);
    int unacceptedBids = stats.getNumberOfBids(0, false);
    int acceptedAsks = stats.getNumberOfAsks(0, true);
    int unacceptedAsks = stats.getNumberOfAsks(0, false);
    System.out.println("Number of accepted bids above 0 = " + acceptedBids);
    System.out.println("Number of unaccepted bids above 0 = " + unacceptedBids);
    System.out.println("Number of accepted asks above 0 = " + acceptedAsks);
    System.out.println("Number of unaccepted asks above 0 = " + unacceptedAsks);
    assertTrue(acceptedBids == acceptedAsks);
    assertTrue(acceptedBids == 1);

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
  
  public void testNumberOfTraders() {
    assertTrue(auction.getNumberOfTraders() == traders.length);
    auction.run();
    // check that no traders left active at end of auction.
    assertTrue(auction.getNumberOfTraders() == 0);
  }
  
  public void testClosed() {
    try {
      assertTrue(!auction.closed());
      auction.begin();
      assertTrue(!auction.closed());
      while ( !auction.closed() ) {
        auction.step();
      }
      assertTrue(auction.closed());
    } catch ( AuctionClosedException e ) {
      fail("tried to step through closed auction");
    }
  }

 
  /**
   * Test that transactions occur only in the final (3rd) round.
   */
  public void testTransactionsOccured() {
    try {
      
      assertTrue(!auction.transactionsOccured());
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
  
  public void testShoutAccepted() {
    
    try {

      Shout testBid = new Shout(traders[0], 1, 500, true);
      Shout testAsk = new Shout(traders[2], 1, 300, false);
      
      auction.newShout(testBid);
      assertTrue(!auction.shoutAccepted(testBid));
     
      auction.newShout(testAsk);
      assertTrue(!auction.shoutAccepted(testAsk));
      
      auctioneer.clear();
      //auction.clear(testAsk, testBid, 400);
      
      assertTrue(auction.shoutAccepted(testBid));
      assertTrue(auction.shoutAccepted(testAsk));
      
      auction.runSingleRound();
      
      assertTrue(!auction.shoutAccepted(testBid));
      assertTrue(!auction.shoutAccepted(testAsk));
      
    } catch ( AuctionException e ) {
      fail(e.getMessage());
    }
  }
  
  public void testLastShout() {
    try {

      Shout testBid = new Shout(traders[0], 1, 500, true);
      Shout testAsk = new Shout(traders[2], 1, 300, false);
      
      assertTrue( auction.getLastShout() == null );
      
      auction.newShout(testBid);
      assertTrue( auction.getLastShout().equals(testBid) );
      
      auction.newShout(testAsk);
      assertTrue( auction.getLastShout().equals(testAsk) );
      
    } catch ( AuctionException e ) {
      fail(e.getMessage());
    }
  }
  

  public static void main ( String[] args) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite () {
    return new TestSuite(RoundRobinAuctionTest.class);
  }

}