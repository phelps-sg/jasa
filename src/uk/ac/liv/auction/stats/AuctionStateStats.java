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

package uk.ac.liv.auction.stats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import uk.ac.liv.auction.core.AscendingShoutComparator;
import uk.ac.liv.auction.core.DescendingShoutComparator;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.util.io.DataWriter;

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
	 * @param auction
	 *          The auction to compute supply and demand stats for.
	 * @param supplyStats
	 *          The DataWriter to write the supply curve to.
	 * @param demandStats
	 *          The DataWriter to write the demand curve to.
	 */
	public AuctionStateStats(RandomRobinAuction auction, DataWriter supplyStats,
	    DataWriter demandStats) {
		super(auction, supplyStats, demandStats);
	}

	public void writeSupplyStats() {
		Iterator i = auction.getAuctioneer().askIterator();
		List asks = new ArrayList();
		while (i.hasNext()) {
			Shout ask = (Shout) i.next();
			assert ask.isAsk();
			asks.add(ask);
		}
		writeStats(supplyStats, asks, new AscendingShoutComparator());
	}

	public void writeDemandStats() {
		Iterator i = auction.getAuctioneer().bidIterator();
		List bids = new ArrayList();
		while (i.hasNext()) {
			Shout bid = (Shout) i.next();
			assert bid.isBid();
			bids.add(bid);
		}
		writeStats(demandStats, bids, new DescendingShoutComparator());
	}

}
