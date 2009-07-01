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

import java.awt.BasicStroke;
import java.awt.Color;

import org.apache.log4j.Logger;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

/**
 * Defines a marker on a JFreeChart graph, which can be a point, a line, etc. in
 * the space.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class FreeChartMarker extends FreeChartItem {

	static Logger logger = Logger.getLogger(FreeChartMarker.class);

	public FreeChartMarker() {

	}

	protected static Marker createMarker(double value, Color color, String label) {
		ValueMarker marker = new ValueMarker(value);
		setupMarker(marker, color, label);
		return marker;
	}

	protected static Marker createMarker(double start, double end, Color color,
	    String label) {
		IntervalMarker marker = new IntervalMarker(start, end);
		setupMarker(marker, color, label);
		return marker;
	}

	protected static void setupMarker(Marker marker, Color color, String label) {
		marker.setPaint(color);
		marker.setStroke(new BasicStroke(1f, BasicStroke.CAP_SQUARE,
		    BasicStroke.JOIN_BEVEL, 1f, new float[] { 5 }, 0));
		marker.setLabel(label);
		marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
		marker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
	}

}