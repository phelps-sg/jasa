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

package test.uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.*;

import uk.ac.liv.auction.agent.AbstractTraderAgent;
import uk.ac.liv.auction.agent.RoundRobinTrader;

import junit.framework.TestCase;

import org.apache.log4j.Logger;


public class TestTrader extends AbstractTraderAgent {

  public Shout lastWinningShout = null;
  public double lastWinningPrice;
  public int lastWinningQuantity;
  public Shout[] shouts;
  public boolean receivedAuctionOpen = false;
  public boolean receivedAuctionClosed = false;
  public boolean receivedAuctionClosedAfterAuctionOpen = false;
  public int receivedRoundClosed = 0;
  public int receivedRequestShout = 0;
  int currentShoutIndex = 0;
  TestCase test;

  static Logger logger = Logger.getLogger(AbstractTraderAgent.class);


  public TestTrader( TestCase test, int stock, long funds ) {
    super(stock, funds);
    this.test = test;
  }

  public TestTrader( TestCase test, int stock, double funds, double privateValue, boolean isSeller ) {
    super(stock, funds, privateValue, isSeller);
    this.test = test;
  }

  public void informOfSeller( Auction auction, Shout winningShout, RoundRobinTrader seller,
                              double price, int quantity ) {
    logger.debug(this + ": winning shout " + winningShout + " at price " + price + " and quantity " + quantity);
    lastWinningShout = winningShout;
    lastWinningPrice = price;
  }

  public int determineQuantity( Auction auction ) {
    return 1;
  }

  public void requestShout( Auction auction ) {
    if ( currentShoutIndex >= shouts.length ) {
      logger.debug(this + ": all shouts made - quiting from auction");
      ((RoundRobinAuction) auction).remove(this); //TODO
      return;
    }
    if ( currentShoutIndex > 0 ) {
      auction.removeShout(shouts[currentShoutIndex-1]);
    }
    try {
      auction.newShout(shouts[currentShoutIndex++]);
    } catch ( AuctionException e ) {
      e.printStackTrace();
      test.fail();
    }
    receivedRequestShout++;
  }

  public void auctionOpen( Auction auction ) {
    receivedAuctionOpen = true;
  }

  public void auctionClosed( Auction auction ) {
    logger.debug(this + ": recieved auctionClosed()");
    ((RoundRobinAuction) auction).remove(this);
    receivedAuctionClosed = true;
    receivedAuctionClosedAfterAuctionOpen = receivedAuctionOpen;
  }

  public void roundClosed( Auction auction ) {
    receivedRoundClosed++;
  }

  public void endOfDay( Auction auction ) {
    //TODO
  }

  public boolean active() {
    return true;
  }

  public String toString() {
    return "(" + getClass() + " id:" + id + " valuer:" + valuer + " lastProfit:" + getLastProfit() + " funds:" + funds + " stock:" + stock + ")";
  }


}