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

import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.time.TimeSeries;


/**
 * Defined a data series that is fed with values of some <code>ReportVariable
 * </code>
 * when some market event occurs.
 * 
 * <p>
 * <b>Parameters</b><br>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.var</tt><br>
 * <font size=-1> string </font></td>
 * <td valign=top>(the full name of the <code>ReportVariable</code>)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.event</tt><br>
 * <font size=-1> event class inheriting
 * <code>net.sourceforge.jasa.event.MarketEvent</code> </font></td>
 * <td valign=top>(the type of events that trigger sampling of the value of the
 * <code>ReportVariable</code>)</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class ReportVariableSeries extends FreeChartSeries {

	static Logger logger = Logger.getLogger(ReportVariableSeries.class);

	public static final String P_VAR = "var";

	public static final String P_EVENT = "event";

	protected String varName;

	@SuppressWarnings("rawtypes")
	protected Class eventClass;

	public ReportVariableSeries() {
	}

	/**
	 * @param event
	 */
	public void eventOccurred(MarketEvent event) {
		if (eventClass == null || eventClass.isInstance(event)) {
			TimePeriodValue tpValue = (TimePeriodValue) ReportVariableBoard
			    .getInstance().getValue(varName);
			if (tpValue != null) {
				if (getSeries() instanceof TimeSeries) {
					((TimeSeries) getSeries()).addOrUpdate((RegularTimePeriod) tpValue
					    .getPeriod(), tpValue.getValue());
				}
			}
		}
	}

	/**
	 * @param event
	 */
	public void shoutPlaced(OrderPlacedEvent event) {
	}

	/**
	 * @param event
	 */
	public void transactionExecuted(TransactionExecutedEvent event) {
	}

	/**
	 * @param event
	 */
	public void roundClosed(RoundFinishedEvent event) {
	}

}