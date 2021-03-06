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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jabm.report.DataWriter;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;


/**
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class SupplyAndDemandStats extends DirectRevelationReportVariables {

	/**
	 * The DataWriter to write the supply curve to.
	 */
	protected DataWriter supplyStats;

	/**
	 * The DataWriter to write the demand curve to.
	 */
	protected DataWriter demandStats;
	
	static Logger logger = Logger.getLogger(SupplyAndDemandStats.class);

	/**
	 * Constructor.
	 * 
	 * @param auction
	 *          The market to compute supply and demand stats for.
	 * @param supplyStats
	 *          The DataWriter to write the supply curve to.
	 * @param demandStats
	 *          The DataWriter to write the demand curve to.
	 */
	public SupplyAndDemandStats(Market auction,
	    DataWriter supplyStats, DataWriter demandStats) {
		super("Supply & Demand", auction);
		this.supplyStats = supplyStats;
		this.demandStats = demandStats;
	}

	public void produceUserOutput() {
		writeSupplyStats();
		writeDemandStats();
	}
	public void writeStats(DataWriter stats, List<Order> shouts, 
							Comparator<Order> comparator) {
		int qty = 0, qty1 = 0;
		if (shouts.isEmpty()) {
			return;
		}
		Collections.sort(shouts, comparator);
		Order shout = (Order) shouts.get(0);
		Iterator<Order> i = shouts.iterator();
		while (i.hasNext()) {
			shout = i.next();
			if (logger.isDebugEnabled()) logger.debug(shout);
			qty1 = qty + shout.getQuantity();
			stats.newData(qty);
			stats.newData(shout.getPriceAsDouble());
			stats.newData(qty1);
			stats.newData(shout.getPriceAsDouble());
			qty = qty1;
		}
	}


	public abstract void writeSupplyStats();

	public abstract void writeDemandStats();

}
