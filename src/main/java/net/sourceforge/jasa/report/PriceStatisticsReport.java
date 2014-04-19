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

import java.io.Serializable;
import java.util.Map;

import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.util.Distribution;
import net.sourceforge.jabm.util.Resetable;
import net.sourceforge.jabm.util.SummaryStats;
import net.sourceforge.jasa.event.MarketClosedEvent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.MarketSimulation;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;

/**
 * <p>
 * A historicalDataReport that keeps cummulative statistics on bid prices, ask prices,
 * transaction prices, and market quote prices.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class PriceStatisticsReport extends AbstractAuctionReport implements
    Serializable, Cloneable, Resetable {

	protected SummaryStats[] stats;

	static Logger logger = Logger.getLogger(PriceStatisticsReport.class);

	protected static final int TRANS_PRICE = 0;

	protected static final int BID_PRICE = 1;

	protected static final int ASK_PRICE = 2;

	protected static final int BID_QUOTE = 3;

	protected static final int ASK_QUOTE = 4;

	public PriceStatisticsReport() {
		initialise();
	}

	public void eventOccurred(SimEvent event) {
		if (event instanceof RoundFinishedEvent) {
			roundClosed((RoundFinishedEvent) event);
		} else if (event instanceof TransactionExecutedEvent) {
			updateTransPriceLog((TransactionExecutedEvent) event);
		} else if (event instanceof OrderPlacedEvent) {
			updateShoutLog((OrderPlacedEvent) event);
		} else if (event instanceof MarketClosedEvent) {
//			ReportVariableBoard.getInstance().reportValues(getVariables(), event);
			//TODO
		}
	}

	public void roundClosed(RoundFinishedEvent event) {
		MarketSimulation simulation = (MarketSimulation) event.getSimulation();
		MarketQuote quote = simulation.getQuote();
		stats[BID_QUOTE].newData((double) quote.getBid());
		stats[ASK_QUOTE].newData((double) quote.getAsk());
	}

	public void updateTransPriceLog(TransactionExecutedEvent event) {
		stats[TRANS_PRICE].newData(event.getPrice());
	}

	public void updateShoutLog(OrderPlacedEvent event) {
		Order shout = event.getOrder();
		if (shout.isBid()) {
			stats[BID_PRICE].newData(shout.getPrice());
		} else {
			stats[ASK_PRICE].newData(shout.getPrice());
		}
	}

	public SummaryStats getTransPriceStats() {
		return stats[TRANS_PRICE];
	}

	public SummaryStats getBidPriceStats() {
		return stats[BID_PRICE];
	}

	public SummaryStats getAskPriceStats() {
		return stats[ASK_PRICE];
	}

	public SummaryStats getBidQuoteStats() {
		return stats[BID_QUOTE];
	}

	public SummaryStats getAskQuoteStats() {
		return stats[ASK_QUOTE];
	}

	public void initialise() {
		stats = new SummaryStats[] {
		    new SummaryStats("Transaction Price"),
		    new SummaryStats("Bid Price"),
		    new SummaryStats("Ask Price"),
		    new SummaryStats("Bid Quote"),
		    new SummaryStats("Ask Quote") };
	}

	public void reset() {
		for (int i = 0; i < stats.length; i++) {
			((SummaryStats) stats[i]).reset();
		}
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public PriceStatisticsReport newCopy() {
		PriceStatisticsReport copy = null;
		try {
			copy = (PriceStatisticsReport) clone();
			for (int i = 0; i < stats.length; i++) {
				copy.stats[i] = (SummaryStats) stats[i].clone();
			}
		} catch (CloneNotSupportedException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new Error(e.getMessage());
		}
		return copy;
	}

	public void produceUserOutput() {
		reportHeader();
		for (int i = 0; i < stats.length; i++) {
			printStats(stats[i]);
		}
	}

	@Override
	public Map<Object,Number> getVariableBindings() {
		Map<Object,Number> vars = super.getVariableBindings();
		createReportVars(vars, "transactionprice", stats[TRANS_PRICE]);
		createReportVars(vars, "askprice", stats[ASK_PRICE]);
		createReportVars(vars, "bidprice", stats[BID_PRICE]);
		createReportVars(vars, "askquote", stats[ASK_QUOTE]);
		createReportVars(vars, "bidquote", stats[BID_QUOTE]);
		return vars;
	}

	protected void reportHeader() {
		logger.info("");
		logger.info("Auction statistics");
		logger.info("------------------");
		logger.info("");
	}

	protected void printStats(SummaryStats stats) {
		stats.log();
		logger.info("");
	}

	protected void createReportVars(Map<Object,Number> vars, String var, Distribution stats) {
		vars.put(makeVar(var, "mean"), new Double(stats.getMean()));
		vars.put(makeVar(var, "min"), new Double(stats.getMin()));
		vars.put(makeVar(var, "max"), new Double(stats.getMax()));
		vars.put(makeVar(var, "stdev"), new Double(stats.getStdDev()));
	}

	protected ReportVariable makeVar(String varName, String moment) {
		return new ReportVariable("pricestats." + varName + "." + moment, varName
		    + " distribution " + moment);
	}
}
