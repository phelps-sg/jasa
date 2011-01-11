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

import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jasa.event.MarketClosedEvent;
import net.sourceforge.jasa.market.MarketFacade;

/**
 * A historicalDataReport that performs additional calculations at the end of an market
 * before producing its results.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public abstract class AbstractMarketStatsReport extends AbstractAuctionReport {

	public AbstractMarketStatsReport(MarketFacade auction) {
		super(auction);
	}

	public AbstractMarketStatsReport() {
		super();
	}

	public void eventOccurred(SimEvent event) {
		if (event instanceof MarketClosedEvent) {
			calculate();
//			ReportVariableBoard.getInstance().reportValues(getVariables(), event);
		}
		if (event instanceof SimulationFinishedEvent) {			
			produceUserOutput();
		}
	}

	/**
	 * Perform final calculations at the end of the market.
	 */
	public abstract void calculate();

}
