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

import net.sourceforge.jabm.event.SimEvent;

import org.jfree.data.xy.XYDataset;

/**
 * A FreeChartGraph consisting multiple data series or markers.
 * 
 * <p>
 * <b>Parameters</b><br>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.series.n</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the number of data series included)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.series.<i>i</i></tt><br>
 * <font size=-1> classname inheriting net.sourceforge.jasa.report.FreeChartSeries
 * </font></td>
 * <td valign=top>(the type of series)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.marker.n</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the number of markers included)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.marker.<i>i</i></tt><br>
 * <font size=-1> classname inheriting net.sourceforge.jasa.report.FreeChartMarker
 * </font></td>
 * <td valign=top>(the type of marker)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.renderer.n</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the number of renderers for data series or markers to use )</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.renderer.<i>i</i></tt><br>
 * <font size=-1> classname inheriting
 * org.jfree.chart.renderer.xy.XYItemRenderer </font></td>
 * <td valign=top>(the type of renderer)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.x</tt><br>
 * <font size=-1> string </font></td>
 * <td valign=top>(the label for X axis)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.y</tt><br>
 * <font size=-1> string </font></td>
 * <td valign=top>(the label for Y axis)</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class MultipleSeriesGraph extends FreeChartGraph {

	protected FreeChartSeriesCollection fcCollections;

	protected FreeChartMarker markers[];

	private XYDataset datasets[];

	public MultipleSeriesGraph() {
		fcCollections = new FreeChartSeriesCollection();
	}

	public void eventOccurred(SimEvent event) {

		for (int i = 0; i < fcCollections.getSeriesCount(); i++) {
			fcCollections.getSeries(i).eventOccurred(event);
		}

		for (int i = 0; i < markers.length; i++) {
			markers[i].eventOccurred(event);
		}
	}

}
