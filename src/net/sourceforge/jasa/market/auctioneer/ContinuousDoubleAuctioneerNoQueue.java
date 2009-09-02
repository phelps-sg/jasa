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

package net.sourceforge.jasa.market.auctioneer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;

/**
 * An auctioneer for a k-double-market with continuous clearing and no order
 * queuing. Every time an offer is cleared any pending offers are discarded.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class ContinuousDoubleAuctioneerNoQueue extends
    ContinuousDoubleAuctioneer implements Serializable {

	public ContinuousDoubleAuctioneerNoQueue(Market auction) {
		super(auction);
	}

	public void clear() {
		clearingQuote = new MarketQuote(askQuote(), bidQuote());
		List shouts = orderBook.getMatchedShouts();
		if (shouts.size() > 0) {
			orderBook.reset();
		}
		Iterator i = shouts.iterator();
		while (i.hasNext()) {
			Order bid = (Order) i.next();
			Order ask = (Order) i.next();
			double price = determineClearingPrice(bid, ask);
			clear(ask, bid, price);
		}
	}

}
