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

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosingEvent;

/**
 * The interface for expressing the condition of clearing the current market.
 * Whenever, it's time to do so, notifyObservers() is called.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 * 
 */

public class RoundClearingCondition extends MarketClearingCondition {

	/**
	 * by default, clears the market when each round is closing.
	 */
	public void eventOccurred(AuctionEvent event) {
		super.eventOccurred(event);

		if (event instanceof RoundClosingEvent) {
			trigger();
		}
	}

	protected void trigger() {
		setChanged();
		notifyObservers();
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + ")";
	}

}