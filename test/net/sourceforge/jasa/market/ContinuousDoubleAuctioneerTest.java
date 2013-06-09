/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2013 Steve Phelps
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

package net.sourceforge.jasa.market;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jasa.market.auctioneer.AbstractAuctioneer;
import net.sourceforge.jasa.market.auctioneer.ContinuousDoubleAuctioneer;
import net.sourceforge.jasa.market.rules.DiscriminatoryPricingPolicy;

/**
 * @author Steve Phelps
 * @version $Revision$
 */
public class ContinuousDoubleAuctioneerTest extends AuctioneerTest {

	public ContinuousDoubleAuctioneerTest(String name) {
		super(name);
	}

	public void setUp() {
		super.setUp();
		auctioneer = new ContinuousDoubleAuctioneer(auction);
		((AbstractAuctioneer) auctioneer)
		    .setPricingPolicy(new DiscriminatoryPricingPolicy(0));
		auction.setAuctioneer(auctioneer);
	}
//
//	public void testImprovementRule() {
//		System.out.println("testImprovementRule()");
//		assertTrue(shoutOK(new Order(traders[0], 1, 21, true)));
//		assertTrue(!shoutOK(new Order(traders[1], 1, 20, true)));
//		assertTrue(shoutOK(new Order(traders[1], 1, 42, true)));
//		assertTrue(shoutOK(new Order(traders[2], 1, 43, false)));
//		assertTrue(shoutOK(new Order(traders[3], 1, 23, false)));
//		assertTrue(!shoutOK(new Order(traders[4], 1, 50, false)));
//		assertTrue(!shoutOK(new Order(traders[3], 1, 51, false)));
//		assertTrue(shoutOK(new Order(traders[3], 1, 20, false)));
//		assertTrue(shoutOK(new Order(traders[4], 1, 25, false)));
//		assertTrue(!shoutOK(new Order(traders[2], 1, 26, false)));
//	}

	public boolean shoutOK(Order newShout) {
		try {
			auctioneer.newOrder(newShout);
		} catch (NotAnImprovementOverQuoteException e) {
			System.out.println("Shout " + newShout + " did not beat quote.");
			return false;
		} catch (IllegalOrderException e) {
			fail("illegal shout " + e.getMessage());
		}
		System.out.println("Placed shout " + newShout);
		return true;
	}

	public static Test suite() {
		return new TestSuite(ContinuousDoubleAuctioneerTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
}
