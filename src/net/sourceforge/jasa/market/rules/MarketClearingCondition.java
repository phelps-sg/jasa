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

package net.sourceforge.jasa.market.rules;

import java.util.Observable;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.MarketEventListener;


/**
 * The interface for expressing the condition of clearing the current market.
 * Whenever, it's time to do so, notifyObservers() is called.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 * 
 */

public class MarketClearingCondition extends Observable implements
    MarketEventListener {

	public MarketClearingCondition() {
		initialise();
	}

	protected void initialise() {
	}

	public void reset() {
	}

	/**
	 * by default, no clearing
	 */
	public void eventOccurred(SimEvent event) {
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + ")";
	}

}