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

import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

/**
 * An event that is fired every time a shout is received in an market (may not
 * be allowed to place eventually), in contrast to OrderPlacedEvent, which
 * represents a shout is received and placed.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class OrderReceivedEvent extends MarketEvent {

	/**
	 * The shout that led to this event.
	 */
	protected Order shout;

	public OrderReceivedEvent(Market auction, int time, Order shout) {
		super(auction, time);
		this.shout = shout;
	}

	public Order getShout() {
		return shout;
	}
}
