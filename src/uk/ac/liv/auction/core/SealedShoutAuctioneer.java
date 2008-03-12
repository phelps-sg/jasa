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

public abstract class SealedShoutAuctioneer extends AbstractAuctioneer {

	public SealedShoutAuctioneer() {
		super();
	}

	public SealedShoutAuctioneer(Auction auction) {
		super(auction);
	}

	public boolean shoutsVisible() {
		return false;
	}

	public boolean shoutAccepted(Shout shout) throws ShoutsNotVisibleException {
		throw new ShoutsNotVisibleException(getClass()
		    + " implements a sealed-bid policy");
	}

	public boolean transactionsOccurred() throws ShoutsNotVisibleException {
		throw new ShoutsNotVisibleException(getClass()
		    + " implements a sealed-bid policy");
	}

}
