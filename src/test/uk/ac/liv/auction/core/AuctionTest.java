/*
 * JASA Java Auction Simulator API
 * Copyright (C) Steve Phelps
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

package test.uk.ac.liv.auction.core;

import junit.framework.*;

import uk.ac.liv.auction.core.*;

import uk.ac.liv.auction.agent.RoundRobinTrader;

import test.uk.ac.liv.auction.agent.TestTrader;


public class AuctionTest extends TestCase {

  ContinuousDoubleAuctioneer auctioneer;
  RoundRobinAuction auction;
  RandomRobinAuction auction2;
  TestTrader[] traders;


  public AuctionTest( String name ) {
    super(name);
  }

  public void setUpTraders() {
    traders = new TestTrader[3];

    traders[0] = new TestTrader(this, 30, 1000);
    traders[1] = new TestTrader(this, 10, 10000);
    traders[2] = new TestTrader(this, 15, 10000);

    TestTrader trader = traders[0];
    trader.shouts = new Shout[] {
      new Shout(trader, 1, 500, true),
      new Shout(trader, 1, 600, true),
      new Shout(trader, 1, 700, true)
    };

    trader = traders[1];
    trader.shouts = new Shout[] {
      new Shout(trader, 1, 500, true),
      new Shout(trader, 1, 550, true),
      new Shout(trader, 1, 750, true)
    };

    trader = traders[2];
    trader.shouts = new Shout[] {
      new Shout(trader, 1, 900, false),
      new Shout(trader, 1, 950, false),
      new Shout(trader, 1, 725, false)
    };
  }

  public void setUp() {
    auctioneer = new ContinuousDoubleAuctioneer(auction);
    setUpTraders();
  }

  public void testRoundRobinAuction() {

    RoundRobinAuction auction =
      new RoundRobinAuction("Round Robin Test Auction");

    auction.setAuctioneer(auctioneer);
    auctioneer.setAuction(auction);

    for( int i=0; i<traders.length; i++ ) {
      auction.register(traders[i]);
    }

    doCommonTests(auction);

  }

  public void testRandomRobinAuction() {

    RandomRobinAuction auction =
      new RandomRobinAuction("Random Robin Test Auction");

    auction.setAuctioneer(auctioneer);
    auctioneer.setAuction(auction);

    for( int i=0; i<traders.length; i++ ) {
      auction.register(traders[i]);
    }

    doCommonTests(auction);

  }

  public void doCommonTests( RoundRobinAuction auction ) {

    assertTrue(auction.getNumberOfTraders() == traders.length);
    assertTrue(!auction.closed());

    auction.run();

    assertTrue( auction.closed() );
    assertTrue( auction.getNumberOfTraders() == 0 );
    assertTrue( traders[1].lastWinningPrice == 725 );

    assertTrue( auction.getQuote().getBid() == 700 );

    auction.reset();


    assertTrue( !auction.closed() );
    assertTrue( auction.getNumberOfTraders() == 3 );
    assertTrue( auction.getQuote().getBid() < 0 );

    auction.run();

    assertTrue( traders[1].lastWinningPrice == 725 );

  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(AuctionTest.class);
  }

}
