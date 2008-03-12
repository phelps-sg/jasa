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

package uk.ac.liv.auction.core;

import junit.framework.TestCase;
import uk.ac.liv.auction.agent.MockTrader;

public abstract class AuctioneerTest extends TestCase {

	/**
	 * @uml.property name="auctioneer"
	 * @uml.associationEnd
	 */
	Auctioneer auctioneer;

	/**
	 * @uml.property name="auction"
	 * @uml.associationEnd
	 */
	RandomRobinAuction auction;

	/**
	 * @uml.property name="traders"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	MockTrader[] traders;

	public AuctioneerTest(String name) {
		super(name);
	}

	public void setUp() {
		auction = new RandomRobinAuction("unit test auction");

		traders = new MockTrader[5];
		traders[0] = new MockTrader(this, 30, 1000, 1000, false);
		traders[1] = new MockTrader(this, 10, 10000, 1000, false);
		traders[2] = new MockTrader(this, 15, 10000, 400, true);
		traders[3] = new MockTrader(this, 10, 10000, 400, true);
		traders[4] = new MockTrader(this, 15, 10000, 400, true);
	}

	public void testDelete() {

		// round 0
		Shout testShout = null;
		try {
			auctioneer.newShout(new Shout(traders[0], 1, 21, true));
			auctioneer.newShout(new Shout(traders[1], 1, 42, true));
			testShout = new Shout(traders[2], 1, 43, false);
			auctioneer.newShout(testShout);
			auctioneer.newShout(new Shout(traders[3], 1, 23, false));
			auctioneer.newShout(new Shout(traders[4], 1, 10, false));
		} catch (IllegalShoutException e) {
			fail("invalid IllegalShoutException exception thrown " + e);
			e.printStackTrace();
		}

		auctioneer.removeShout(testShout);
		// auctioneer.endOfRoundProcessing();

		auctioneer.printState();

	}

}
