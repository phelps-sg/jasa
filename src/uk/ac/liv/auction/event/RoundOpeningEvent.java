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

/**
 * An event that is fired at the beginning of each round.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class RoundOpeningEvent extends AuctionEvent {

	protected Auction auction;

	public RoundOpeningEvent(Auction auction, int time) {
		super(auction, time);
	}
}
