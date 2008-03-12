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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.util.io.DataWriter;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class SupplyAndDemandStats extends DirectRevelationReport {

	/**
	 * The DataWriter to write the supply curve to.
	 * 
	 * @uml.property name="supplyStats"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected DataWriter supplyStats;

	/**
	 * The DataWriter to write the demand curve to.
	 * 
	 * @uml.property name="demandStats"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected DataWriter demandStats;

	/**
	 * Constructor.
	 * 
	 * @param auction
	 *          The auction to compute supply and demand stats for.
	 * @param supplyStats
	 *          The DataWriter to write the supply curve to.
	 * @param demandStats
	 *          The DataWriter to write the demand curve to.
	 */
	public SupplyAndDemandStats(RandomRobinAuction auction,
	    DataWriter supplyStats, DataWriter demandStats) {
		super(auction);
		this.supplyStats = supplyStats;
		this.demandStats = demandStats;
	}

	public void produceUserOutput() {
		writeSupplyStats();
		writeDemandStats();
	}

	public Map getVariables() {
		return new HashMap();
	}

	public abstract void writeSupplyStats();

	public abstract void writeDemandStats();

	public void writeStats(DataWriter stats, List shouts, Comparator comparator) {
		int qty = 0, qty1 = 0;
		if (shouts.isEmpty()) {
			return;
		}
		Collections.sort(shouts, comparator);
		Shout shout = (Shout) shouts.get(0);
		Iterator i = shouts.iterator();
		while (i.hasNext()) {
			shout = (Shout) i.next();
			qty1 = qty + shout.getQuantity();
			stats.newData(qty);
			stats.newData(shout.getPrice());
			stats.newData(qty1);
			stats.newData(shout.getPrice());
			qty = qty1;
		}
	}
}
