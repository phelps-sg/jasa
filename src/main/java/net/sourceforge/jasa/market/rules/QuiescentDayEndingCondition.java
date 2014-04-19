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

package net.sourceforge.jasa.market.rules;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.event.MarketEventListener;
import net.sourceforge.jasa.event.OrderReceivedEvent;

/**
 * The interface for expressing the condition of closing an market.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 * 
 */

public class QuiescentDayEndingCondition extends TimingCondition implements
    DayEndingCondition, MarketEventListener {

	protected boolean shoutsProcessed;

	/*
	 * @see net.sourceforge.jasa.market.TimingCondition#eval()
	 */
	public boolean eval() {
		return isQuiescent();
	}

	/**
	 * Returns true if no bidding activity occured in the latest market round.
	 */
	private boolean isQuiescent() {
		return !shoutsProcessed || (getAuction().getNumberOfTraders() == 0);
	}

	public void eventOccurred(SimEvent event) {
		if (event instanceof OrderReceivedEvent) {
			shoutsProcessed = true;
		}

	}
}