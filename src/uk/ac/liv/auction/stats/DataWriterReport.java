/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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

import java.util.HashMap;
import java.util.Map;

import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;
import uk.ac.liv.util.io.DataWriter;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * This class writes auction data to the specified DataWriter objects, and thus
 * can be used to log data to eg, CSV files, a database backend, etc.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class DataWriterReport extends AbstractAuctionReport {

	/**
	 * output for the ask component of market quotes as time series.
	 * 
	 * @uml.property name="askQuoteLog"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected DataWriter askQuoteLog = null;

	/**
	 * output for the bid component of market quotes as time series.
	 * 
	 * @uml.property name="bidQuoteLog"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected DataWriter bidQuoteLog = null;

	/**
	 * output for bid data as time series.
	 * 
	 * @uml.property name="bidLog"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected DataWriter bidLog = null;

	/**
	 * output for ask data as time series.
	 * 
	 * @uml.property name="askLog"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected DataWriter askLog = null;

	/*
	 * output for transaction price time series.
	 */
	/**
	 * @uml.property name="transPriceLog"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected DataWriter transPriceLog = null;

	/**
	 * The auction we are keeping statistics on.
	 * 
	 * @uml.property name="auction"
	 * @uml.associationEnd
	 */
	protected RandomRobinAuction auction;

	public DataWriterReport() {
		this(null, null, null, null, null);
	}

	public DataWriterReport(DataWriter askQuoteLog, DataWriter bidQuoteLog,
	    DataWriter bidLog, DataWriter askLog, DataWriter transPriceLog) {
		this.askQuoteLog = askQuoteLog;
		this.bidQuoteLog = bidQuoteLog;
		this.askLog = askLog;
		this.bidLog = bidLog;
		this.transPriceLog = transPriceLog;
	}

	public void setup(ParameterDatabase parameters, Parameter base) {

	}

	public void eventOccurred(AuctionEvent event) {
		if (event instanceof TransactionExecutedEvent) {
			updateTransPriceLog((TransactionExecutedEvent) event);
		} else if (event instanceof ShoutPlacedEvent) {
			updateShoutLog((ShoutPlacedEvent) event);
		} else if (event instanceof RoundClosedEvent) {
			updateQuoteLog((RoundClosedEvent) event);
		}
	}

	public void updateQuoteLog(RoundClosedEvent event) {
		int time = event.getTime();
		MarketQuote quote = event.getAuction().getQuote();
		if (askQuoteLog != null) {
			askQuoteLog.newData(time);
			askQuoteLog.newData(quote.getAsk());
		}
		if (bidQuoteLog != null) {
			bidQuoteLog.newData(time);
			bidQuoteLog.newData(quote.getBid());
		}
		dataUpdated();
	}

	public void updateTransPriceLog(TransactionExecutedEvent event) {
		if (transPriceLog != null) {
			transPriceLog.newData(event.getTime());
			transPriceLog.newData(event.getPrice());
		}
		dataUpdated();
	}

	public void updateShoutLog(ShoutPlacedEvent event) {
		Shout shout = event.getShout();
		int time = event.getTime();
		if (shout.isBid()) {
			if (bidLog != null) {
				bidLog.newData(time);
				bidLog.newData(shout.getPrice());
			}
		} else {
			if (askLog != null) {
				askLog.newData(time);
				askLog.newData(shout.getPrice());
			}
		}
		dataUpdated();
	}

	public void dataUpdated() {
	}

	public void produceUserOutput() {
	}

	public Map getVariables() {
		return new HashMap();
	}

	/**
	 * @uml.property name="auction"
	 */
	public void setAuction(RandomRobinAuction auction) {
		this.auction = auction;
	}

}
