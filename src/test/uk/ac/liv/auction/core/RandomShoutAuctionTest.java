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

import test.uk.ac.liv.PRNGTestSeeds;
import test.uk.ac.liv.auction.agent.TestTrader;

import uk.ac.liv.auction.core.*;


/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class RandomShoutAuctionTest extends RoundRobinAuctionTest {

  public RandomShoutAuctionTest( String name ) {
    super(name);
  }


  public void setUpAuction() {
    auction = new RandomShoutAuction();
    auction.setAuctioneer(auctioneer);
    ((RandomShoutAuction) auction).setShoutProbability(1.0);
    ((RandomShoutAuction) auction).setSeed(PRNGTestSeeds.UNIT_TEST_SEED);
    auctioneer.setAuction(auction);
    for( int i=0; i<traders.length; i++ ) {
      auction.register(traders[i]);
    }
  }
  
  public void testNoShouts() {
  	setUpAuction();
  	((RandomShoutAuction) auction).setShoutProbability(0.0);
  	auction.run();
  	for( int i=0; i<traders.length; i++ ) {
  		assertTrue(traders[i].receivedRequestShout == 0);
  		checkMessages(traders[i]);
  	}
  }
  
  public void testRandomShouts() {
  	setUpAuction();
  	((RandomShoutAuction) auction).setShoutProbability(0.5);
  	auction.run();
  	for( int i=0; i<traders.length; i++ ) {
  		checkMessages(traders[i]);
  	}
  }
  
  protected void checkMessages( TestTrader trader ) {
  	assertTrue(trader.receivedAuctionOpen);
	assertTrue(trader.receivedAuctionClosed);
	assertTrue(trader.receivedAuctionClosedAfterAuctionOpen);
  }

  public static Test suite() {
    return new TestSuite(RandomShoutAuctionTest.class);
  }

}