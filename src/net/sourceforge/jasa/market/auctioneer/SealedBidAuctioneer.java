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
package net.sourceforge.jasa.market.auctioneer;

import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.ShoutsNotVisibleException;

public abstract class SealedBidAuctioneer extends AbstractAuctioneer {


	public SealedBidAuctioneer(Market auction) {
		super(auction);
	}

	public boolean shoutsVisible() {
		return false;
	}

	public boolean orderFilled(Order shout) throws ShoutsNotVisibleException {
		throw new ShoutsNotVisibleException(getClass()
		    + " implements a sealed-bid policy");
	}

	public boolean transactionsOccurred() throws ShoutsNotVisibleException {
		throw new ShoutsNotVisibleException(getClass()
		    + " implements a sealed-bid policy");
	}

}
