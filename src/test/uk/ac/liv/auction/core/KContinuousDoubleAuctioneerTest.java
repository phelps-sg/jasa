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

package test.uk.ac.liv.auction.core;

import junit.framework.Test;
import junit.framework.TestSuite;

import uk.ac.liv.auction.core.IllegalShoutException;
import uk.ac.liv.auction.core.KContinuousDoubleAuctioneer;
import uk.ac.liv.auction.core.NotAnImprovementOverQuoteException;
import uk.ac.liv.auction.core.Shout;

/**
 * @author Steve Phelps
 * @version $Revision$
 */
public class KContinuousDoubleAuctioneerTest extends AuctioneerTest {
  
  public KContinuousDoubleAuctioneerTest( String name ) {
    super(name);
  }
  
  public void setUp() {
    super.setUp();
    auctioneer = new KContinuousDoubleAuctioneer(auction, 0);
    auction.setAuctioneer(auctioneer);
  }
  
  public void testImprovementRule() {
    System.out.println("testImprovementRule()");
    assertTrue(shoutOK(new Shout(traders[0], 1, 21, true)));
    assertTrue(!shoutOK(new Shout(traders[1], 1, 20, true)));
    assertTrue(shoutOK(new Shout(traders[1], 1, 42, true)));
    assertTrue(shoutOK(new Shout(traders[2], 1, 43, false)));
    assertTrue(shoutOK(new Shout(traders[3], 1, 23, false)));
    assertTrue(!shoutOK(new Shout(traders[4], 1, 50, false)));
    assertTrue(!shoutOK(new Shout(traders[3], 1, 51, false)));
    assertTrue(shoutOK(new Shout(traders[3], 1, 20, false)));
    assertTrue(shoutOK(new Shout(traders[4], 1, 25, false)));
    assertTrue(!shoutOK(new Shout(traders[2], 1, 26, false)));
  }
  
  public boolean shoutOK( Shout newShout ) {
    try {
      auctioneer.newShout(newShout);
    } catch ( NotAnImprovementOverQuoteException e ) {
      System.out.println("Shout " + newShout + " did not beat quote.");
      return false;
    } catch ( IllegalShoutException e ) {
      fail("illegal shout " + e.getMessage());
    }
    System.out.println("Placed shout " + newShout);
    return true;
  }

  public static Test suite() {
    return new TestSuite(KContinuousDoubleAuctioneerTest.class);
  }
  
  public static void main( String[] args ) {
    junit.textui.TestRunner.run (suite());
  }
}
