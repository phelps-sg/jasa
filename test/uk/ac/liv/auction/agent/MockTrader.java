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

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.event.AuctionEvent;

import uk.ac.liv.auction.agent.AbstractTradingAgent;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

public class MockTrader extends AbstractTradingAgent {

  /**
   * @uml.property name="lastWinningShout"
   * @uml.associationEnd
   */
  public Shout lastWinningShout = null;

  /**
   * @uml.property name="lastWinningPrice"
   */
  public double lastWinningPrice = 0;

  /**
   * @uml.property name="lastWinningQuantity"
   */
  public int lastWinningQuantity;

  /**
   * @uml.property name="receivedAuctionOpen"
   */
  public boolean receivedAuctionOpen = false;

  /**
   * @uml.property name="receivedAuctionClosed"
   */
  public boolean receivedAuctionClosed = false;

  /**
   * @uml.property name="receivedAuctionClosedAfterAuctionOpen"
   */
  public boolean receivedAuctionClosedAfterAuctionOpen = false;

  /**
   * @uml.property name="receivedRoundClosed"
   */
  public int receivedRoundClosed = 0;

  /**
   * @uml.property name="receivedRequestShout"
   */
  public int receivedRequestShout = 0;

  /**
   * @uml.property name="test"
   * @uml.associationEnd multiplicity="(1 1)"
   */
  TestCase test;

  static Logger logger = Logger.getLogger(AbstractTradingAgent.class);

  public MockTrader( TestCase test, int stock, long funds ) {
    super(stock, funds);
    this.test = test;
  }

  public MockTrader( TestCase test, int stock, double funds,
      double privateValue, boolean isSeller ) {
    super(stock, funds, privateValue, isSeller);
    this.test = test;
  }
  
  
  

//  public void informOfSeller( Auction auction, Shout winningShout,
//      TradingAgent seller, double price, int quantity ) {
//    super.informOfSeller(auction, winningShout, seller, price, quantity);
//    test.assertTrue(((AbstractTradingAgent) seller).isSeller());
//    System.out.println(this + ": winning shout " + winningShout + " at price "
//        + price + " and quantity " + quantity + " and seller: " + seller);
//    lastWinningShout = winningShout;
//    lastWinningPrice = price;
//    purchaseFrom(auction, (AbstractTradingAgent) seller, quantity, price);
//  }
//
//  public void informOfBuyer( Auction auction, TradingAgent buyer, double price,
//      int quantity ) {
//    super.informOfBuyer(auction, buyer, price, quantity);
//    test.assertTrue(((AbstractTradingAgent) buyer).isBuyer());
//    lastWinningPrice = price;
//    lastWinningShout = getCurrentShout();
//  }

  public void shoutAccepted( Auction auction, Shout shout, double price, int quantity ) {    
    super.shoutAccepted(auction, shout, price, quantity);
    System.out.println(this + ": winning shout " + shout + " at price " + price + " and quantity " + quantity);
    lastWinningShout = shout;
    lastWinningPrice = price;
  }

  public int determineQuantity( Auction auction ) {
    return 1;
  }

  public void requestShout( Auction auction ) {
    super.requestShout(auction);
    System.out.println(this + ": placed " + currentShout);
    receivedRequestShout++;
  }

  public void auctionOpen( AuctionEvent event ) {
    super.auctionOpen(event);
    receivedAuctionOpen = true;
  }

  public void auctionClosed( AuctionEvent event ) {
    super.auctionClosed(event);
    logger.debug(this + ": recieved auctionClosed()");
    ((RoundRobinAuction) event.getAuction()).remove(this);
    receivedAuctionClosed = true;
    receivedAuctionClosedAfterAuctionOpen = receivedAuctionOpen;
  }

  public void roundClosed( AuctionEvent event ) {
    super.roundClosed(event);
    receivedRoundClosed++;
  }

  public void endOfDay( AuctionEvent event ) {
    // TODO
  }

  public boolean active() {
    return true;
  }

  public double equilibriumProfits( Auction auction, double equilibriumPrice,
      int quantity ) {
    // TODO
    return -1;
  }

  public String toString() {
    return "(" + getClass() + " id:" + id + " isSeller:" + isSeller
        + " valuer:" + valuer + " lastProfit:" + getLastProfit() + " funds:"
        + account + " account:" + account + ")";
  }

}