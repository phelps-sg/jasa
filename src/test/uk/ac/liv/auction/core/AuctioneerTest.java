/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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

public class AuctioneerTest extends TestCase {

  ContinuousDoubleAuctioneer auctioneer;
  RoundRobinAuction auction;
  TestTrader trader1, trader2, trader3, trader4, trader5;


  public AuctioneerTest( String name ) {
    super(name);
  }

  public void setUp() {
    auction = new RoundRobinAuction("unit test auction");
    auctioneer = new ContinuousDoubleAuctioneer(auction);
    auction.setAuctioneer(auctioneer);

    trader1 = new TestTrader(this, 30, 1000);
    trader2 = new TestTrader(this, 10, 10000);
    trader3 = new TestTrader(this, 15, 10000);
    trader4 = new TestTrader(this, 10, 10000);
    trader5 = new TestTrader(this, 15, 10000);
  }

  public void testAuction1() {

    MarketQuote quote;

    // round 0
    try {
      auctioneer.newShout( new Shout(trader1, 1, 500, true) );
      auctioneer.newShout( new Shout(trader2, 1, 400, true) );
      auctioneer.newShout( new Shout(trader3, 2, 900, false) );
    } catch ( IllegalShoutException e ) {
      fail("invalid IllegalShoutException exception thrown " + e);
      e.printStackTrace();
    }

    auctioneer.endOfRoundProcessing();
    auctioneer.printState();

    quote = auctioneer.getQuote();
    assertTrue( quote.getAsk() == 900 );

    System.out.println("quote = " + quote);

    // round 1
    System.out.println("round1");
    try {
      auctioneer.newShout( new Shout(trader1, 1, 920, true) );
      auctioneer.newShout( new Shout(trader2, 1, 950, true) );

    } catch ( IllegalShoutException e ) {
      fail("invalid IllegalShoutException thrown " + e );
      e.printStackTrace();
    }

    auctioneer.endOfRoundProcessing();
    auctioneer.printState();

    quote = auctioneer.getQuote();
    System.out.println("quote = " + quote);
    System.out.println("trader1's price = " + trader1.lastWinningPrice);
    System.out.println("trader2's price = " + trader2.lastWinningPrice);

//    assertTrue( quote.getAsk() > 900 );
    assertTrue( trader1.lastWinningPrice == 900 );
    assertTrue( trader2.lastWinningPrice == 900 );

    auctioneer.reset();
    System.out.println("after reseting, quote = " + auctioneer.getQuote());

    assertTrue( auctioneer.getQuote().getBid() < 0 );

  }

  public void testDelete() {

    // round 0
    Shout testShout = new Shout(trader3, 1, 13, false);
    try {
      auctioneer.newShout( new Shout(trader1, 1, 21, false) );
      auctioneer.newShout( new Shout(trader2, 1, 42, false) );
      auctioneer.newShout(testShout);
      auctioneer.newShout( new Shout(trader4, 1, 23, true) );
      auctioneer.newShout( new Shout(trader5, 1, 10, true) );
    } catch ( IllegalShoutException e ) {
      fail("invalid IllegalShoutException exception thrown " + e);
      e.printStackTrace();
    }

    auctioneer.removeShout(testShout);
    //auctioneer.endOfRoundProcessing();

    auctioneer.printState();

  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run (suite());
  }

  public static Test suite() {
    return new TestSuite(AuctioneerTest.class);
  }

}

