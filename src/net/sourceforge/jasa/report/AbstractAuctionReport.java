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



import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.sim.util.Parameterizable;

import org.apache.log4j.Logger;

/**
 * <p>
 * An abstract implementation of AuctionReport that provides functionality
 * common to all reports.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AbstractAuctionReport implements AuctionReport,
    Parameterizable {

	static Logger logger = Logger.getLogger(AbstractAuctionReport.class);

	/**
	 * The market we are keeping statistics on.
	 */
	protected MarketFacade auction;

	public AbstractAuctionReport(MarketFacade auction) {
		this.auction = auction;
	}

	public AbstractAuctionReport() {
	}

	public void setAuction(MarketFacade auction) {
		this.auction = auction;
		logger.debug("Set market to " + auction);
	}

	public MarketFacade getAuction() {
		return auction;
	}

}
