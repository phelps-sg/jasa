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

package test.uk.ac.liv.auction.agent;

import junit.framework.*;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.ZISTraderAgent;


public class ZISTraderTest extends TestCase {

  ZISTraderAgent trader1, trader2;

  public ZISTraderTest( String name ) {
    super(name);
  }

  public void setUp() {
    trader1 = new TestZISTrader(150, 100, false);
    trader2 = new TestZISTrader(100, 100, true);
  }

  public void testReset() {
    trader1.purchaseFrom(trader2, 5, 1000L);
    assertTrue(trader1.getFunds() == -5000L);
    assertTrue(trader1.getStock() == 5 );
    assertTrue(trader1.getQuantityTraded() == 5 );
    trader1.reset();
    assertTrue(trader1.getQuantityTraded() == 0 );
    assertTrue(trader1.getStock() == 0 );
  }

  public void testTrading() {
    System.out.println(trader1);
    System.out.println(trader2);
    trader1.reset();
    trader2.reset();
    RoundRobinAuction auction = new RoundRobinAuction("ZIS unit test auction");
    ContinuousDoubleAuctioneer auctioneer = new ContinuousDoubleAuctioneer();
    auction.setAuctioneer(auctioneer);
    auction.register(trader1);
    auction.register(trader2);
    auction.setMaximumRounds(250);
    //auction.activateGUIConsole();
    auction.run();

    // assertTrue( auction.getAge() == 102 );
    System.out.println(trader1);
    System.out.println(trader2);
    auction.printState();
    assertTrue( trader1.getQuantityTraded() == 100 );
    assertTrue( trader2.getQuantityTraded() == 100 );
    System.out.println("after trading, trader1 = " + trader1);
    System.out.println("trader2 = " + trader2);


    auction.reset();
    trader1.reset();
    trader2.reset();
    System.out.println("rerunning");
    System.out.println("age = " + auction.getAge());
    auction.run();

    System.out.println("age = " + auction.getAge());
    System.out.println("q(trader1) = " + trader1.getQuantityTraded());
    System.out.println("q(trader2) = " + trader2.getQuantityTraded());
    // assertTrue( auction.getAge() == 102 );
    assertTrue( trader1.getQuantityTraded() == 100 );
    assertTrue( trader2.getQuantityTraded() == 100 );

    System.out.println("trader1 = " + trader1);
    System.out.println("trader2 = " + trader2);
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run (suite());
  }

  public static Test suite() {
    return new TestSuite(ZISTraderTest.class);
  }

}

class TestZISTrader extends ZISTraderAgent {

  public TestZISTrader( int limitPrice, int tradeEntitlement, boolean isSeller ) {
    super(limitPrice, tradeEntitlement, isSeller);
  }

  public void requestShout( RoundRobinAuction auction ) {
    super.requestShout(auction);
    double price = shout.getPrice();
    if ( isSeller) {
      price = -price;
    }
    //System.out.println(this + ": " + price);
  }

}