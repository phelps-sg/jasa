/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

package test.uk.ac.liv.auction.zi;

import junit.framework.*;

import uk.ac.liv.auction.zi.*;
import uk.ac.liv.auction.core.*;

import java.util.Observer;
import java.util.Observable;

import org.apache.log4j.Logger;


public class ZITraderAgentTest extends TestCase implements Observer {

  ZITraderAgent buyer, seller;

  RoundRobinAuction auction;

  KDoubleAuctioneer auctioneer;

  static final int NUM_ROUNDS = 1000;

  static final int TRADE_ENTITLEMENT = 100;

  static final double BUYER_PRIV_VALUE = 1000;

  static final double SELLER_PRIV_VALUE = 900;

  static Logger logger = Logger.getLogger(ZITraderAgentTest.class);

  public ZITraderAgentTest( String name ) {
    super(name);
    org.apache.log4j.BasicConfigurator.configure();
  }

  public void setUp() {
    buyer = new ZITraderAgent(BUYER_PRIV_VALUE, TRADE_ENTITLEMENT, false);
    seller = new ZITraderAgent(SELLER_PRIV_VALUE, TRADE_ENTITLEMENT, true);
    auction = new RoundRobinAuction("ZIPStrategyTest auction");
    auction.register(buyer);
    auction.register(seller);
    auctioneer = new KDoubleAuctioneer(auction);
    auction.setAuctioneer(auctioneer);
    auction.setMaximumRounds(NUM_ROUNDS);
  }

  /**
   * Test that the agent drops out of the auction after its trade entitlement
   * has been depleted.
   */
  public void testTradeEntitlement() {
    auction.addObserver(this);
    auction.run();
  }

  public void update( Observable observable, Object o ) {
    RoundRobinAuction auction = (RoundRobinAuction) observable;
    assertTrue( buyer.getQuantityTraded() <= TRADE_ENTITLEMENT);
    assertTrue( seller.getQuantityTraded() <= TRADE_ENTITLEMENT);
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(ZITraderAgentTest.class);
  }

}
