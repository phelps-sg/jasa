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

package uk.ac.liv.auction.event;

import uk.ac.liv.auction.core.Auction;

/**
 * Superclass for all types of auction event.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AuctionEvent {

	/**
	 * The auction that this event occurred in.
	 * 
	 * @uml.property name="auction"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected Auction auction;

	/**
	 * The time at which this event occurred.
	 * 
	 * @uml.property name="time"
	 */
	protected int time;

	/**
	 * The physical time at which this event occurred.
	 * 
	 * @uml.property name="pTime"
	 */

	protected long pTime;

	public AuctionEvent(Auction auction, int time) {
		this.auction = auction;
		this.time = time;
		this.pTime = System.currentTimeMillis();
	}

	/**
	 * Get the auction that this event occured in.
	 * 
	 * @uml.property name="auction"
	 */
	public Auction getAuction() {
		return auction;
	}

	public long getPhysicalTime() {
		return pTime;
	}

	public int getTime() {
		return time;
	}

}
