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

package net.sourceforge.jasa.agent.strategy;


import java.io.Serializable;

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.MarketOpenEvent;
import net.sourceforge.jasa.market.AuctionRuntimeException;
import net.sourceforge.jasa.report.HistoricalDataReport;
import net.sourceforge.jasa.sim.event.SimEvent;

import org.apache.log4j.Logger;

import cern.jet.random.engine.RandomEngine;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class PriestVanTolStrategy extends MomentumStrategy implements
    Serializable {

	public PriestVanTolStrategy(AbstractTradingAgent agent, RandomEngine prng) {
		super(agent, prng);
	}

	protected HistoricalDataReport historicalDataReport;

	static Logger logger = Logger.getLogger(PriestVanTolStrategy.class);


	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof MarketOpenEvent) {
			auctionOpen((MarketOpenEvent) event);
		}
	}

	public void auctionOpen(MarketOpenEvent event) {
//		historicalDataReport = (HistoricalDataReport) event.getAuction().getReport(
//		    HistoricalDataReport.class);
//
//		if (historicalDataReport == null) {
//			throw new AuctionRuntimeException(getClass()
//			    + " requires a HistoryStatsMarketDataLogger to be configured");
//		}
	}

	protected void adjustMargin() {

		double aMin = historicalDataReport.getLowestUnacceptedAskPrice();
		double bMax = historicalDataReport.getHighestUnacceptedBidPrice();

		if (agent.isBuyer(auction)) {
			if (aMin > bMax) {
				adjustMargin(targetMargin(bMax + perterb(bMax)));
			} else {
				adjustMargin(targetMargin(aMin + perterb(aMin)));
			}
		} else {
			if (aMin > bMax) {
				adjustMargin(targetMargin(aMin - perterb(aMin)));
			} else {
				adjustMargin(targetMargin(bMax - perterb(bMax)));
			}
		}
	}

	protected double calculatePrice(double margin) {
		double price = super.calculatePrice(margin);
		if (!agent.active()) {
			if (agent.isBuyer(auction)) {
				if (price > currentPrice) {
					return currentPrice;
				}
			} else {
				if (price < currentPrice) {
					return currentPrice;
				}
			}
		}

		return price;
	}

	public HistoricalDataReport getHistoricalDataReport() {
		return historicalDataReport;
	}

	public void setHistoricalDataReport(HistoricalDataReport historicalDataReport) {
		this.historicalDataReport = historicalDataReport;
	}
	
}