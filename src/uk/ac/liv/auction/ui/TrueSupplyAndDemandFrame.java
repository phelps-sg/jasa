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

package uk.ac.liv.auction.ui;

import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.stats.SupplyAndDemandStats;
import uk.ac.liv.auction.stats.TrueSupplyAndDemandStats;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class TrueSupplyAndDemandFrame extends SupplyAndDemandFrame {

	public static final String TITLE = "Supply and Demand Graph";

	public TrueSupplyAndDemandFrame(RandomRobinAuction auction) {
		super(auction);
	}

	public String getGraphName() {
		return TITLE;
	}

	public SupplyAndDemandStats getSupplyAndDemandStats() {
		return new TrueSupplyAndDemandStats(auction, supplyCurve, demandCurve);
	}

	public void eventOccurred(AuctionEvent event) {
		if (event instanceof RoundClosedEvent) {
			updateGraph();
		}
	}

}
