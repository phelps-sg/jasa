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

package net.sourceforge.jasa.agent;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.agent.strategy.AbstractTradingStrategy;
import net.sourceforge.jasa.event.RoundClosedEvent;

import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;


/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class MockStrategy extends AbstractTradingStrategy {

	protected int currentShout = 0;

	public Order[] shouts;

	public MockStrategy(Order[] shouts) {
		this.shouts = shouts;
	}

	@Override
	public void onRoundClosed(Market auction) {
		currentShout++;
	}

	public int determineQuantity(Market auction) {
		return shouts[currentShout].getQuantity();
	}

	public boolean modifyShout(Order shout) {
		if (currentShout >= shouts.length) {
			return false;
		}
		super.modifyShout(shout);
//		lastShoutAccepted = agent.lastOrderFilled();
		Order current = shouts[currentShout];
		shout.setPrice(current.getPrice());
		shout.setQuantity(current.getQuantity());
		shout.setIsBid(current.isBid());
		System.out.println("Placing order " + shout);
		return true;
	}
	
	public boolean isBuy() {
		return shouts[currentShout].isBid();
	}

}
