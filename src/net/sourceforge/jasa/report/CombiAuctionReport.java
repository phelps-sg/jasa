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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.util.Parameterizable;
import net.sourceforge.jasa.sim.util.Resetable;


/**
 * <p>
 * A historicalDataReport that combines several different reports.
 * </p>
 * 
 * <p>
 * <b>Parameters</b><br>
 * </p>
 * <table>
 * <tr>
 * <td valign=top><i>base</i><tt>.n</tt><br>
 * <font size=-1>int &gt;= 1</font></td>
 * <td valign=top>(the number of different loggers to configure)</td>
 * <tr> </table>
 * 
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class CombiAuctionReport implements AuctionReport, Parameterizable,
    Resetable {

	protected List reports = null;

	protected MarketFacade auction;

	public static final String P_DEF_BASE = "combiauctionreport";

	public static final String P_NUMLOGGERS = "n";

	public CombiAuctionReport(List reports) {
		this.reports = reports;
	}

	public CombiAuctionReport() {
		this.reports = new LinkedList();
	}

//	public void setup(ParameterDatabase parameters, Parameter base) {
//
//		Parameter defBase = new Parameter(P_DEF_BASE);
//
//		int numLoggers = parameters.getInt(base.push(P_NUMLOGGERS), defBase
//		    .push(P_NUMLOGGERS), 1);
//
//		for (int i = 0; i < numLoggers; i++) {
//			AuctionReport historicalDataReport = (AuctionReport) parameters
//			    .getInstanceForParameter(base.push(i + ""), defBase.push(i + ""),
//			        AuctionReport.class);
//			historicalDataReport.setAuction(market);
//			if (historicalDataReport instanceof Parameterizable) {
//				((Parameterizable) historicalDataReport).setup(parameters, base.push(i + ""));
//			}
//			addReport(historicalDataReport);
//		}
//	}

	/**
	 * Add a new report
	 */
	public void addReport(AuctionReport report) {
		reports.add(report);
	}

	public void reset() {
		Iterator i = reports.iterator();
		while (i.hasNext()) {
			AuctionReport logger = (AuctionReport) i.next();
			if (logger instanceof Resetable) {
				((Resetable) logger).reset();
			}
		}
	}

	public void produceUserOutput() {
		Iterator i = reports.iterator();
		while (i.hasNext()) {
			AuctionReport logger = (AuctionReport) i.next();
			logger.produceUserOutput();
		}
	}

	public Iterator reportIterator() {
		return reports.iterator();
	}

	public Map getVariables() {
		HashMap variableMap = new HashMap();
		Iterator i = reports.iterator();
		while (i.hasNext()) {
			AuctionReport logger = (AuctionReport) i.next();
			variableMap.putAll(logger.getVariables());
		}
		return variableMap;
	}

	public void eventOccurred(SimEvent event) {
		Iterator i = reports.iterator();
		while (i.hasNext()) {
			AuctionReport logger = (AuctionReport) i.next();
			logger.eventOccurred(event);
		}
	}

	public void setAuction(MarketFacade auction) {
		this.auction = auction;
		Iterator i = reports.iterator();
		while (i.hasNext()) {
			AuctionReport logger = (AuctionReport) i.next();
			logger.setAuction(auction);
		}
	}

}