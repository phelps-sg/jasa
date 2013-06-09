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

package net.sourceforge.jasa.report;

import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.report.DataWriter;
import net.sourceforge.jabm.util.SummaryStats;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.MarketSimulation;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;


/**
 * This historicalDataReport keeps track of the mean value of each market variable over the
 * course of each round of bidding and logs the mean value to the specified
 * DataWriter objects.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class MeanValueDataWriterReport extends DataWriterReport {

	protected SummaryStats askQuoteStats = new SummaryStats(
	    "Ask Quote");

	protected SummaryStats bidQuoteStats = new SummaryStats(
	    "Bid Quote");

	protected SummaryStats bidStats = new SummaryStats(
	    "Bid");

	protected SummaryStats askStats = new SummaryStats(
	    "Ask");

	protected SummaryStats transPriceStats = new SummaryStats(
	    "Transaction Price");

	protected SummaryStats[] allStats = { askQuoteStats,
	    bidQuoteStats, askStats, bidStats, transPriceStats };

	protected int round;

	static Logger logger = Logger.getLogger(MeanValueDataWriterReport.class);

	public MeanValueDataWriterReport(DataWriter askQuoteLog,
	    DataWriter bidQuoteLog, DataWriter bidLog, DataWriter askLog,
	    DataWriter transPriceLog) {
		super(askQuoteLog, bidQuoteLog, bidLog, askLog, transPriceLog);
	}

	public MeanValueDataWriterReport() {
		super();
	}

	@Override
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof RoundFinishedEvent) {
			roundClosed((RoundFinishedEvent) event);
		}
	}

	public void updateQuoteLog(RoundFinishedEvent event) {
		MarketQuote quote = ((MarketSimulation) event.getSimulation()).getAuctioneer().getQuote();
		getAuction().getQuote();
		askQuoteStats.newData(quote.getAsk());
		bidQuoteStats.newData(quote.getBid());
	}

	public void updateTransPriceLog(TransactionExecutedEvent event) {
		transPriceStats.newData(event.getPrice());
	}

	public void updateShoutLog(OrderPlacedEvent event) {
		Order shout = event.getOrder();
		if (shout.isBid()) {
			bidStats.newData(shout.getPrice());
		} else {
			askStats.newData(shout.getPrice());
		}
	}

	public void roundClosed(RoundFinishedEvent event) {

		logger.debug("roundClosed(" + auction + ")");

		update(askQuoteLog, askQuoteStats);
		update(bidQuoteLog, bidQuoteStats);
		update(askLog, askStats);
		update(bidLog, bidStats);
		update(transPriceLog, transPriceStats);

		for (int i = 0; i < allStats.length; i++) {
			logger.debug(allStats[i]);
			allStats[i].reset();
		}

		round++;
	}

	protected void update(DataWriter writer, SummaryStats stats) {
		// writer.newData(round);
		writer.newData(stats.getMean());
	}

	public void produceUserOutput() {
	}

}
