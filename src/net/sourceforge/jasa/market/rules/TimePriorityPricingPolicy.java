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

import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;

/**
 * Set the transaction price at the price of the order which arrived 
 * at the market first.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class TimePriorityPricingPolicy extends DiscriminatoryPricingPolicy {

	public TimePriorityPricingPolicy() {
		this(0);
	}

	public TimePriorityPricingPolicy(double k) {
		super(k);
	}

	public double determineClearingPrice(Order bid, Order ask,
	    MarketQuote clearingQuote) {
		if (bid.getTimeStamp().compareTo(ask.getTimeStamp()) > 0) {
//			 ask comes first
			return kInterval(ask.getPrice(), bid.getPrice());
		} else {
//			 bid comes first
			return kInterval(bid.getPrice(), ask.getPrice());
		}
	}

}
