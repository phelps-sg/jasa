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

import java.util.Vector;

/**
 * Contains a list of FreeChartSeries.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class FreeChartSeriesCollection {

	Vector collection;

	public FreeChartSeriesCollection() {
		collection = new Vector();
	}

	public void addSeries(FreeChartSeries series) {
		collection.addElement(series);
	}

	public int getSeriesCount() {
		return collection.size();
	}

	public FreeChartSeries getSeries(int index) {
		if (index < 0 || index >= getSeriesCount())
			return null;

		return (FreeChartSeries) collection.elementAt(index);
	}

}
