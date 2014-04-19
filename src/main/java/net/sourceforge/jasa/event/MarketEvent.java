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

package net.sourceforge.jasa.event;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.market.Market;

/**
 * Superclass for all types of market event.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class MarketEvent extends SimEvent {

	/**
	 * The market that this event occurred in.
	 */
	protected Market auction;

	/**
	 * The time at which this event occurred.
	 */
	protected int time;

	/**
	 * The physical time at which this event occurred.
	 */

	protected long pTime;

	public MarketEvent(Market auction, int time) {
		this.auction = auction;
		this.time = time;
		this.pTime = System.currentTimeMillis();
	}

	/**
	 * Get the market that this event occured in.
	 */
	public Market getAuction() {
		return auction;
	}

	public long getPhysicalTime() {
		return pTime;
	}

	public int getTime() {
		return time;
	}
	
	public String toString() {
		return "(" + getClass() + " auction:" + auction + " time:" + time + " pTime:" + pTime + ")";
	}

}
