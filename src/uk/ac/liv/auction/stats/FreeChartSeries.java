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

import org.apache.log4j.Logger;
import org.jfree.data.general.Series;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 * Defines a data series to be included in FreeChartGraph.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class FreeChartSeries extends FreeChartItem {

	static Logger logger = Logger.getLogger(FreeChartSeries.class);

	protected Series series;

	protected XYDataset dataset;

	public Series getSeries() {
		return series;
	}

	public XYDataset getDataset() {
		return dataset;
	}

	public static XYDataset createDataset(Series series) {
		XYDataset ds = null;
		if (series instanceof TimeSeries) {
			ds = new TimeSeriesCollection((TimeSeries) series);
		} else {
			logger.error(series.getClass().getName()
			    + " is not supported in FreeChartat this moment!");
		}

		return ds;
	}

	public void setName(String name) {
		super.setName(name);
		if (getSeries() != null) {
			getSeries().setKey(name);
		}

	}
}