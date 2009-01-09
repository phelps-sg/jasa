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

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.UnitType;

import uk.ac.liv.auction.event.AuctionEvent;

import ec.util.ParamClassLoadException;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

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
 * <font size=-1> classname inheriting uk.ac.liv.auction.stats.FreeChartSeries
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
 * <font size=-1> classname inheriting uk.ac.liv.auction.stats.FreeChartMarker
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

	public static final String P_DEF_BASE = "multipleseriesgraph";

	public static final String P_SERIES = "series";

	public static final String P_MARKER = "marker";

	public static final String P_RENDERER = "renderer";

	public static final String P_NUM = "n";

	protected FreeChartSeriesCollection fcCollections;

	protected FreeChartMarker markers[];

	private XYDataset datasets[];

	public MultipleSeriesGraph() {
		fcCollections = new FreeChartSeriesCollection();
	}

	protected void setupChart(ParameterDatabase parameters, Parameter base) {

		Parameter defBase = new Parameter(P_DEF_BASE);

		// series
		Parameter defSeriesBase = defBase.push(P_SERIES);

		int num = parameters.getIntWithDefault(base.push(P_SERIES).push(P_NUM),
		    defSeriesBase.push(P_NUM), 0);
		datasets = new XYDataset[num];
		for (int i = 0; i < num; i++) {
			fcCollections.addSeries((FreeChartSeries) parameters
			    .getInstanceForParameterEq(base.push(P_SERIES)
			        .push(String.valueOf(i)), defSeriesBase.push(String.valueOf(i)),
			        FreeChartSeries.class));
			fcCollections.getSeries(i).setGraph(this);
			fcCollections.getSeries(i).setup(parameters,
			    base.push(P_SERIES).push(String.valueOf(i)));
			datasets[i] = fcCollections.getSeries(i).getDataset();
		}

		// markers
		Parameter defMarkerBase = defBase.push(P_MARKER);

		num = parameters.getIntWithDefault(base.push(P_MARKER).push(P_NUM),
		    defMarkerBase.push(P_NUM), 0);
		markers = new FreeChartMarker[num];
		for (int i = 0; i < num; i++) {
			markers[i] = (FreeChartMarker) parameters.getInstanceForParameter(base
			    .push(P_MARKER).push(String.valueOf(i)), defMarkerBase.push(String
			    .valueOf(i)), FreeChartMarker.class);
			markers[i].setGraph(this);
			markers[i].setup(parameters, base.push(P_MARKER).push(String.valueOf(i)));
		}

		// freechart and xyplot
		Parameter defXAxisBase = defBase.push(P_X);
		Parameter defYAxisBase = defBase.push(P_Y);

		String xAxisLabel = parameters.getStringWithDefault(base.push(P_X),
		    defXAxisBase, "X");
		String yAxisLabel = parameters.getStringWithDefault(base.push(P_Y),
		    defYAxisBase, "Y");
		setChart(ChartFactory.createTimeSeriesChart(getName(), xAxisLabel,
		    yAxisLabel, null, true, true, false));
		XYPlot xyplot = getChart().getXYPlot();
		xyplot.setForegroundAlpha(0.7f);
		xyplot.setBackgroundPaint(Color.white);
		xyplot.setDomainGridlinePaint(Color.lightGray);
		xyplot.setRangeGridlinePaint(Color.lightGray);
		xyplot
		    .setAxisOffset(new RectangleInsets(UnitType.ABSOLUTE, 4D, 4D, 4D, 4D));

		// axises
		int numOfRanges = parameters.getIntWithDefault(base.push(P_Y).push(P_NUM),
		    defYAxisBase.push(P_NUM), 1);
		for (int i = 0; i < numOfRanges; i++) {
			NumberAxis axis = new NumberAxis(parameters.getStringWithDefault(base
			    .push(P_Y).push(String.valueOf(i)), defYAxisBase.push(String
			    .valueOf(i)), "Y " + i));
			xyplot.setRangeAxis(i, axis);
		}

		for (int i = 0; i < fcCollections.getSeriesCount(); i++) {
			int axisIndex = fcCollections.getSeries(i).getAxisIndex();
			if (axisIndex >= 0 && axisIndex < numOfRanges) {
				xyplot.setDataset(i, fcCollections.getSeries(i).getDataset());
				xyplot.mapDatasetToRangeAxis(i, axisIndex);
			} else {
				logger.error("Invaid axis number: " + axisIndex);
			}
		}

		// renderers
		Parameter defRenderBase = defBase.push(P_RENDERER);

		int numOfRenderers = parameters.getIntWithDefault(base.push(P_RENDERER)
		    .push(P_NUM), defRenderBase.push(P_NUM), 1);
		XYItemRenderer standardRenderer = xyplot.getRenderer();
		int index = 0;
		for (int i = 0; i < fcCollections.getSeriesCount(); i++) {

			int rendererIndex = fcCollections.getSeries(i).getRendererIndex();
			if (rendererIndex < 0 || rendererIndex >= numOfRenderers) {
				logger.error("Invaid renderer number: " + rendererIndex);
			}

			try {
				xyplot.setRenderer(i, (XYItemRenderer) parameters
				    .getInstanceForParameterEq(base.push(P_RENDERER).push(
				        String.valueOf(rendererIndex)), defRenderBase.push(String
				        .valueOf(rendererIndex)), XYItemRenderer.class));
			} catch (ParamClassLoadException e) {
				xyplot.setRenderer(new StandardXYItemRenderer());
			}

			xyplot.getRenderer(i).setSeriesPaint(0,
			    standardRenderer.getSeriesPaint(index++));
		}

		xyplot.getDomainAxis().setUpperMargin(
		    xyplot.getDomainAxis().getUpperMargin() + 0.05D);
		xyplot.getDomainAxis().setLowerMargin(
		    xyplot.getDomainAxis().getLowerMargin() + 0.05D);

		xyplot.setDomainCrosshairLockedOnData(true);
		xyplot.setDomainCrosshairVisible(true);
		xyplot.setRangeCrosshairLockedOnData(true);
		xyplot.setRangeCrosshairVisible(true);
	}

	public void eventOccurred(AuctionEvent event) {

		for (int i = 0; i < fcCollections.getSeriesCount(); i++) {
			fcCollections.getSeries(i).eventOccurred(event);
		}

		for (int i = 0; i < markers.length; i++) {
			markers[i].eventOccurred(event);
		}
	}

}
