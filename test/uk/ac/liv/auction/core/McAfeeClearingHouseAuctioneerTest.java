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

package uk.ac.liv.auction.core;

import uk.ac.liv.auction.agent.MockTrader;
import junit.framework.TestCase;

public class McAfeeClearingHouseAuctioneerTest extends TestCase {

  McAfeeClearingHouseAuctioneer auctioneer;
  
  RoundRobinAuction auction;
  
  MockTrader[] traders;
  
  static final int N = 4;
  
  public McAfeeClearingHouseAuctioneerTest( String name ) {
    super(name);   
  }
  
  public void setUp() {
    auction = new RandomRobinAuction();
    auctioneer = new McAfeeClearingHouseAuctioneer();
    auction.setAuctioneer(auctioneer);
    
    traders = new MockTrader[N];
    for( int i=0; i<N; i++ ) {
      traders[i] = new MockTrader(this, 0, 0);
      auction.register(traders[i]);
    }
  }
  
  public void testClearingNoShouts() {
    auction.auctioneer.clear();
    for( int i=0; i<N; i++ ) {
      assertTrue(traders[i].lastWinningShout == null);
    }
  }
  
  
  public static void main( String[] args ) {

  }

}
