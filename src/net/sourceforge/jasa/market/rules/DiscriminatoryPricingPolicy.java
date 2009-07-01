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

import java.io.Serializable;

import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;

/**
 * <p>
 * A pricing policy in which we set the transaction price in the interval
 * between the matched prices as determined by the parameter k.
 * </p>
 * 
 * <p>
 * <b>Parameters </b> <br>
 * </p>
 * 
 * <table>
 * <tr>
 * <td valign=top><i>base </i> <tt>.inorder</tt><br>
 * <font size=-1>boolean </font></td>
 * <td valign=top>(if true, the interval will be [first shout, second shout];
 * otherwise [ask, bid])</td>
 * <tr></table>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class DiscriminatoryPricingPolicy extends KPricingPolicy implements
    Serializable {

	public DiscriminatoryPricingPolicy() {
		this(0);
	}

	public DiscriminatoryPricingPolicy(double k) {
		super(k);
	}


	public double determineClearingPrice(Order bid, Order ask,
	    MarketQuote clearingQuote) {
		assert bid.getPrice() >= ask.getPrice();

		return kInterval(ask.getPrice(), bid.getPrice());
	}

}