/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2009 Steve Phelps
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

package uk.ac.liv.auction.event;

import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;

/**
 * An event that is fired every time a shout is placed in an auction.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class ShoutPlacedEvent extends AuctionEvent {

	/**
	 * The shout that led to this event.
	 */
	protected Shout shout;

	public ShoutPlacedEvent(Auction auction, int time, Shout shout) {
		super(auction, time);
		this.shout = shout;
	}

	public Shout getShout() {
		return shout;
	}
}
