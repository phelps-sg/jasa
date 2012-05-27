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

package net.sourceforge.jasa.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jabm.report.DataWriter;
import net.sourceforge.jasa.market.AscendingOrderComparator;
import net.sourceforge.jasa.market.DescendingOrderComparator;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;


/**
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class AuctionStateStats extends SupplyAndDemandStats {

	static Logger logger = Logger.getLogger(AuctionStateStats.class);

	/**
	 * Constructor.
	 * 
	 * @param market
	 *          The market to compute supply and demand stats for.
	 * @param supplyStats
	 *          The DataWriter to write the supply curve to.
	 * @param demandStats
	 *          The DataWriter to write the demand curve to.
	 */
	public AuctionStateStats(Market auction, DataWriter supplyStats,
	    DataWriter demandStats) {
		super(auction, supplyStats, demandStats);
	}

	public void writeSupplyStats() {
		Iterator<Order> i = auction.getAuctioneer().askIterator();
		List<Order> asks = new ArrayList<Order>();
		while (i.hasNext()) {
			Order ask = i.next();
			assert ask.isAsk();
			asks.add(ask);
		}
		writeStats(supplyStats, asks, new AscendingOrderComparator());
	}

	public void writeDemandStats() {
		Iterator<Order> i = auction.getAuctioneer().bidIterator();
		List<Order> bids = new ArrayList<Order>();
		while (i.hasNext()) {
			Order bid = i.next();
			assert bid.isBid();
			bids.add(bid);
		}
		writeStats(demandStats, bids, new DescendingOrderComparator());
	}

	@Override
	public String getName() {
		return "Supply and demand: auction state";
	}

}
