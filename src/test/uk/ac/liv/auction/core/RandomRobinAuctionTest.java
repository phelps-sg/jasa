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

package test.uk.ac.liv.auction.core;

import junit.framework.*;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.RoundRobinTrader;

import test.uk.ac.liv.auction.agent.TestTrader;


public class RandomRobinAuctionTest extends RoundRobinAuctionTest {

  public RandomRobinAuctionTest( String name ) {
    super(name);
  }


  public void setUpAuction() {
    auction = new RandomRobinAuction("RandomRobin Test Auction");
    auction.setAuctioneer(auctioneer);
    auctioneer.setAuction(auction);
    for( int i=0; i<traders.length; i++ ) {
      auction.register(traders[i]);
    }
  }

  public static Test suite() {
    return new TestSuite(RandomRobinAuctionTest.class);
  }

}