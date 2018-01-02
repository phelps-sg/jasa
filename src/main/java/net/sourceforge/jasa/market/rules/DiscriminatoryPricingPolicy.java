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

import java.io.Serializable;

import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;

/**
 * <p>
 * A pricing policy in which we set the transaction price in the interval
 * between the matched prices as determined by the parameter k.
 * </p>
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
		assert bid.getPriceAsDouble() >= ask.getPriceAsDouble();

		return kInterval(ask.getPriceAsDouble(), bid.getPriceAsDouble());
	}

}