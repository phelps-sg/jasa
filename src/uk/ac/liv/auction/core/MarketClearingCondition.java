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

package uk.ac.liv.auction.core;

import java.util.Observable;

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionEventListener;

/**
 * The interface for expressing the condition of clearing the current market.
 * Whenever, it's time to do so, notifyObservers() is called.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 * 
 */

public class MarketClearingCondition extends Observable implements
    AuctionEventListener {

	public static final String P_DEF_BASE = "marketclearingcondition";

	public MarketClearingCondition() {
		initialise();
	}

	protected void initialise() {
	}

	protected void reset() {
	}

	/**
	 * by default, no clearing
	 */
	public void eventOccurred(AuctionEvent event) {
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + ")";
	}

}