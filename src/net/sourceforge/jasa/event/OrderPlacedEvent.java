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

package net.sourceforge.jasa.event;

import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

/**
 * An event that is fired every time an order is placed in an market.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class OrderPlacedEvent extends MarketEvent {

	/**
	 * The shout that led to this event.
	 */
	protected Order shout;

	public OrderPlacedEvent(Market auction, int time, Order shout) {
		super(auction, time);
		this.shout = shout;
	}
	
	public OrderPlacedEvent() {
		this(null, 0, null);
	}

	public Order getOrder() {
		return shout;
	}
}
