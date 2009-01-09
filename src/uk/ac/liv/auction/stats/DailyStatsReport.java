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

package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.EndOfDayEvent;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.util.Vector;

import uk.ac.liv.util.SummaryStats;
import uk.ac.liv.util.Parameterizable;

import org.apache.log4j.Logger;

/**
 * A report that collects price statistics for each trading day.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class DailyStatsReport extends PriceStatisticsReport implements
    Parameterizable {

	protected Vector dailyStats;

	static Logger logger = Logger.getLogger(DailyStatsReport.class);

	public DailyStatsReport() {
		super();
		initialise();
	}

	public void setup(ParameterDatabase params, Parameter base) {
		// auction.setDailyStats(this);
	}

	public void eventOccurred(AuctionEvent event) {
		super.eventOccurred(event);
		if (event instanceof EndOfDayEvent) {
			endOfDay((EndOfDayEvent) event);
		}
	}

	public SummaryStats getTransPriceStats(int day) {
		if (day > dailyStats.size() - 1) {
			return null;
		}
		return ((SummaryStats[]) dailyStats.get(day))[TRANS_PRICE];
	}

	public SummaryStats getPreviousDayTransPriceStats() {
		if (auction.getDay() <= 0) {
			return null;
		}
		return getTransPriceStats(auction.getDay() - 1);
	}

	public void endOfDay(EndOfDayEvent event) {
		// Make a copy of the current stats, reset them and record
		try {
			SummaryStats[] currentStats = new SummaryStats[stats.length];
			for (int i = 0; i < stats.length; i++) {
				currentStats[i] = (SummaryStats) stats[i].clone();
				stats[i].reset();
			}
			dailyStats.add(currentStats);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new Error(e.getMessage());
		}
	}

	public void produceUserOutput() {
		for (int day = 0; day < dailyStats.size(); day++) {
			SummaryStats[] todaysStats = (SummaryStats[]) dailyStats
			    .get(day);
			logger.info("Stats for day " + day);
			logger.info("");
			for (int i = 0; i < todaysStats.length; i++) {
				printStats(todaysStats[i]);
			}
		}
	}

	public void initialise() {
		super.initialise();
		dailyStats = new Vector();
	}

	public void reset() {
		super.reset();
		dailyStats.clear();
	}

}
