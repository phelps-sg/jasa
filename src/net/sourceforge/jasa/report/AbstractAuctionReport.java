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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jabm.util.Parameterizable;
import net.sourceforge.jabm.util.Resetable;
import net.sourceforge.jasa.market.Market;

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

public abstract class AbstractAuctionReport implements AuctionReport, Resetable,
    Parameterizable, Serializable {

	static Logger logger = Logger.getLogger(AbstractAuctionReport.class);

	/**
	 * The market we are keeping statistics on.
	 */
	protected Market auction;

	public AbstractAuctionReport(Market auction) {
		this.auction = auction;
	}

	public AbstractAuctionReport() {
	}

	public void setAuction(Market auction) {
		this.auction = auction;
		logger.debug("Set market to " + auction);
	}

	public Market getAuction() {
		return auction;
	}

	@Override
	public void eventOccurred(SimEvent event) {
		if (event instanceof SimulationStartingEvent) {
			reset();
		} 
	}
	
	@Override
	public Map<Object, Number> getVariableBindings() {
		return new HashMap<Object,Number>();
	}
	
	@Override
	public String getName() {
		return null;
	}

	public void produceUserOutput() {
	}
	
	

}
