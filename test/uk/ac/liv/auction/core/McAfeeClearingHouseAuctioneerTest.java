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

import uk.ac.liv.auction.agent.MockStrategy;
import uk.ac.liv.auction.agent.MockTrader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class McAfeeClearingHouseAuctioneerTest extends TestCase {

  McAfeeClearingHouseAuctioneer auctioneer;
  
  RandomRobinAuction auction;
  
  MockTrader[] traders;
  
  static final int N = 6;
  
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
  
  public void testClearingNoUnmatchedShouts() {
    traders[0].setIsSeller(false);
    traders[1].setIsSeller(false);
    traders[0].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[0], 1, 200, true) }));
    traders[1].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[1], 1, 150, true) }));   
    
    traders[2].setIsSeller(true);
    traders[3].setIsSeller(true);    
    traders[2].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[2], 1, 100, false) }));
    traders[3].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[3], 1, 140, false) }));
    
    traders[4].setStrategy( new MockStrategy( new Shout[] {}));    
    traders[5].setStrategy( new MockStrategy( new Shout[] {}));
    
    try {
      auction.begin();
      auction.step();
      
      assertTrue( !traders[1].lastShoutAccepted() );
      assertTrue( !traders[3].lastShoutAccepted() );
      
      assertTrue( traders[0].lastShoutAccepted() );
      assertTrue( traders[2].lastShoutAccepted() );
      
      assertTrue( "clearing should result in budget surplus",
                    auctioneer.getAccount().getFunds() > 0);
      
    } catch ( AuctionClosedException e ) {
      fail(e.getMessage());
    }
    
  }
  
  public void testEfficientClearing() {
    
    traders[0].setIsSeller(false);
    traders[1].setIsSeller(false);
    traders[0].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[0], 1, 200, true) }));
    traders[1].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[1], 1, 140, true) }));
    
    traders[2].setIsSeller(true);
    traders[3].setIsSeller(true);    
    traders[2].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[2], 1, 100, false) }));
    traders[3].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[3], 1, 150, false) }));

    traders[4].setStrategy( new MockStrategy( new Shout[] {}));    
    traders[5].setStrategy( new MockStrategy( new Shout[] {}));
    
    try {
      auction.begin();
      auction.step();
      
      assertTrue( !traders[1].lastShoutAccepted() );
      assertTrue( !traders[3].lastShoutAccepted() );
      
      assertTrue( traders[0].lastShoutAccepted() );
      assertTrue( traders[2].lastShoutAccepted() );
      
      assertTrue( "clearing should result in budget balance",
          auctioneer.getAccount().getFunds() == 0);
            
    } catch ( AuctionClosedException e ) {
      fail(e.getMessage());
    }
  }
  
  public void testInefficientClearing() {
    traders[0].setIsSeller(false);
    traders[1].setIsSeller(false);
    traders[2].setIsSeller(false);
    traders[0].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[0], 1, 300, true) }));    
    traders[1].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[1], 1, 200, true) }));
    traders[2].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[2], 1, 140, true) }));
    
    traders[3].setIsSeller(true);
    traders[4].setIsSeller(true);
    traders[5].setIsSeller(true);
    traders[3].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[3], 1, 50, false) }));    
    traders[4].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[4], 1, 100, false) }));
    traders[5].setStrategy( new MockStrategy( new Shout[] {
        new Shout(traders[5], 1, 1150, false) }));
    
    try {
      auction.begin();
      auction.step();
      
      assertTrue( !traders[5].lastShoutAccepted() );
      assertTrue( !traders[2].lastShoutAccepted() );
      assertTrue( !traders[4].lastShoutAccepted() );
      assertTrue( !traders[5].lastShoutAccepted() );
      
      assertTrue( traders[0].lastShoutAccepted() );
      assertTrue( traders[3].lastShoutAccepted() );
      assertTrue( traders[0].lastWinningPrice != 300 );
      assertTrue( traders[3].lastWinningPrice != 50 );
      
      assertTrue( "clearing should result in budget surplus",
          auctioneer.getAccount().getFunds() > 0);
            
    } catch ( AuctionClosedException e ) {
      fail(e.getMessage());
    }
  }
  

  public static Test suite() {
    return new TestSuite(McAfeeClearingHouseAuctioneerTest.class);
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run (suite());
  }

}
