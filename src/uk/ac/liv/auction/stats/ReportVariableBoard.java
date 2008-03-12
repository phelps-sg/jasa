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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimePeriodValue;

import uk.ac.liv.auction.event.AuctionEvent;

/**
 * A class recording updates of various ReportVariables.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class ReportVariableBoard {

	static Logger logger = Logger.getLogger(ReportVariableBoard.class);

	private static ReportVariableBoard instance;

	/**
	 * @uml.property name="board"
	 * @uml.associationEnd qualifier="varName:java.lang.String
	 *                     org.jfree.data.time.TimePeriodValue"
	 */
	private Map board;

	private ReportVariableBoard() {
		if (instance != null)
			throw new Error("ReportVariableBoard cannot be instantiated twice!");

		instance = this;
		board = Collections.synchronizedMap(new HashMap());
	}

	public static ReportVariableBoard getInstance() {
		if (instance == null) {
			instance = new ReportVariableBoard();
		}
		return instance;
	}

	public void reset() {
		if (board != null)
			board.clear();
	}

	public Collection getVarNames() {
		return board.keySet();
	}

	public TimePeriodValue getValue(String varName) {
		return (TimePeriodValue) board.get(varName);
	}

	public TimePeriodValue getValue(ReportVariable var) {
		return getValue(var.getName());
	}

	public void reportValue(ReportVariable var, TimePeriodValue value) {
		reportValue(var.getName(), value);
	}

	public void reportValue(String varName, TimePeriodValue value) {
		board.put(varName, value);
	}

	public void reportValue(String varName, double value, AuctionEvent event) {
		Millisecond time = new Millisecond(new Date(event.getPhysicalTime()));
		reportValue(varName, new TimePeriodValue(time, value));
	}

	public void reportValues(Map vars, AuctionEvent event) {
		Millisecond time = new Millisecond(new Date(event.getPhysicalTime()));

		ArrayList list = new ArrayList(vars.keySet());
		Iterator i = list.iterator();
		while (i.hasNext()) {
			ReportVariable var = (ReportVariable) i.next();
			Object value = vars.get(var);
			if (value instanceof Number) {
				double v = ((Number) value).doubleValue();
				if (!Double.isNaN(v)) {
					reportValue(var.getName(), new TimePeriodValue(time, v));
				}
			} else if (value instanceof Boolean) {
				reportValue(var.getName(), new TimePeriodValue(time, ((Boolean) value)
				    .booleanValue() ? 1 : 0));
			}
		}
	}
}
