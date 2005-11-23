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

import uk.ac.liv.auction.core.AuctionClosedException;
import uk.ac.liv.auction.core.AbstractAuctioneer;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.ClearingHouseAuctioneer;
import uk.ac.liv.auction.core.UniformPricingPolicy;

import junit.framework.*;

public class AbstractTraderAgentTest extends TestCase {

  /**
   * @uml.property   name="trader1"
   * @uml.associationEnd   
   */
  MockTrader trader1;

  /**
   * @uml.property   name="trader2"
   * @uml.associationEnd   
   */
  MockTrader trader2;

  /**
   * @uml.property   name="auction"
   * @uml.associationEnd   
   */
  RandomRobinAuction auction;

  public static final int TRADER1_STOCK = 0;

  public static final int TRADER2_STOCK = 5;

  public static final double TRADER1_FUNDS = 10000;

  public static final double TRADER2_FUNDS = 20000;

  public static final double TRADER1_VALUE = 250;

  public static final double TRADER2_VALUE = 140;

  public AbstractTraderAgentTest( String name ) {
    super(name);
  }

  public void setUp() {

    trader1 = new MockTrader(this, TRADER1_STOCK, TRADER1_FUNDS, TRADER1_VALUE,
        false);

    trader2 = new MockTrader(this, TRADER2_STOCK, TRADER2_FUNDS, TRADER2_VALUE,
        true);

    trader1.setStrategy(new MockStrategy(new Shout[] {
        new Shout(trader1, 1, TRADER1_VALUE - 100, true),
        new Shout(trader1, 1, TRADER1_VALUE - 50, true),
        new Shout(trader1, 1, TRADER1_VALUE, true),
        new Shout(trader1, 1, TRADER1_VALUE - 100, true) }));

    trader2.setStrategy(new MockStrategy(new Shout[] {
        new Shout(trader2, 1, TRADER2_VALUE + 100, true),
        new Shout(trader2, 1, TRADER2_VALUE + 50, true),
        new Shout(trader2, 1, TRADER2_VALUE, true),
        new Shout(trader2, 1, TRADER2_VALUE + 100, true) }));

    auction = new RandomRobinAuction();
    AbstractAuctioneer auctioneer = new ClearingHouseAuctioneer(auction);
    auctioneer.setPricingPolicy(new UniformPricingPolicy(0.5));
    auction.setAuctioneer(auctioneer);
    auction.register(trader1);
    auction.register(trader2);   
  }

  public void testPurchase() {
    System.out.println("trader1 = " + trader1);
    System.out.println("trader2 = " + trader2);
    trader1.getAccount().transfer(trader2.getAccount(), 5000);
    trader2.getCommodityHolding().transfer(trader1.getCommodityHolding(), 5);
    System.out.println("after purchase");
    System.out.println("trader1 = " + trader1);
    System.out.println("trader2 = " + trader2);
    assertTrue(trader1.getStock() == TRADER1_STOCK + 5);
    assertTrue(trader2.getStock() == TRADER2_STOCK - 5);
    assertTrue(trader1.getFunds() == TRADER1_FUNDS - 5000);
    assertTrue(trader2.getFunds() == TRADER2_FUNDS + 5000);
    trader1.reset();
    assertTrue(trader1.getFunds() == TRADER1_FUNDS);
    assertTrue(trader1.getStock() == TRADER1_STOCK);
    assertTrue(trader1.getLastProfit() == 0);
    assertTrue(trader1.getProfits() == 0);
    trader2.reset();
    assertTrue(trader2.getFunds() == TRADER2_FUNDS);
    assertTrue(trader2.getStock() == TRADER2_STOCK);
    assertTrue(trader2.getLastProfit() == 0);
    assertTrue(trader2.getProfits() == 0);

  }

  public void testLastShoutAccepted() {

    assertTrue(!trader1.lastShoutAccepted());
    assertTrue(!trader2.lastShoutAccepted());

    auction.begin();

    try {

      auction.step();
      assertTrue(!((MockStrategy) trader1.getStrategy()).lastShoutAccepted);
      assertTrue(!((MockStrategy) trader2.getStrategy()).lastShoutAccepted);
      assertTrue(!trader1.lastShoutAccepted());
      assertTrue(!trader2.lastShoutAccepted());

      auction.step();    
      assertTrue(trader1.lastShoutAccepted());
//      trader1.purchaseFrom(auction, trader2, 1, TRADER1_VALUE);
      assertTrue(trader2.lastShoutAccepted());

      auction.step();
      assertTrue(((MockStrategy) trader1.getStrategy()).lastShoutAccepted);
      assertTrue(((MockStrategy) trader2.getStrategy()).lastShoutAccepted);
      assertTrue(trader1.lastShoutAccepted());
      assertTrue(trader2.lastShoutAccepted());
      
      auction.step();
      assertTrue(((MockStrategy) trader1.getStrategy()).lastShoutAccepted);
      assertTrue(((MockStrategy) trader2.getStrategy()).lastShoutAccepted);
      assertTrue(!trader1.lastShoutAccepted());
      assertTrue(!trader2.lastShoutAccepted());

    } catch ( AuctionClosedException e ) {
      fail("we tried to step through a closed auction.");
    }

  }

  public void testLastProfit() {

    assertTrue(trader1.getLastProfit() == 0);
    assertTrue(trader2.getLastProfit() == 0);

    auction.begin();

    try {

      auction.step();
      assertTrue(trader1.getLastProfit() == 0);
      assertTrue(trader2.getLastProfit() == 0);

      auction.step();
      assertTrue(trader1.getLastProfit() == 55);
      assertTrue(trader2.getLastProfit() == 55);

      auction.step();
      assertTrue(trader1.getLastProfit() == 55);
      assertTrue(trader2.getLastProfit() == 55);
      
      auction.step();
      assertTrue(trader1.getLastProfit() == 0);
      assertTrue(trader2.getLastProfit() == 0);

    } catch ( AuctionClosedException e ) {
      fail("we tried to step through a closed auction.");
    }

  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(AbstractTraderAgentTest.class);
  }
}
