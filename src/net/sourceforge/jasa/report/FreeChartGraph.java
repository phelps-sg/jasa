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

import java.awt.BorderLayout;

import javax.swing.JPanel;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.event.MarketEventListener;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * a JFreeChart graph that can be included in a FreeChartReport.
 * 
 * <p>
 * <b>Parameters</b><br>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.name</tt><br>
 * <font size=-1> string </font></td>
 * <td valign=top></td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.width</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the width of the graph and 350 by default)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.height</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the height of the graph and 300 by default)</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public abstract class FreeChartGraph extends JPanel implements
    MarketEventListener {

	static Logger logger = Logger.getLogger(FreeChartGraph.class);

	private FreeChartReport report;

	private JFreeChart jfreechart;

	private ChartPanel chartPanel;

	public FreeChartGraph() {
		setLayout(new BorderLayout());
	}

	public void eventOccurred(SimEvent event) {
	}

	public JFreeChart getChart() {
		return jfreechart;
	}

	public void setChart(JFreeChart chart) {
		jfreechart = chart;

		if (chartPanel != null) {
			remove(chartPanel);
		}

		chartPanel = new ChartPanel(getChart());
		chartPanel.setRangeZoomable(true);
		chartPanel.setDomainZoomable(true);
		add(chartPanel, BorderLayout.CENTER);
	}

	public void setName(String name) {
		super.setName(name);
		if (getChart() != null)
			getChart().setTitle(name);
	}

	/**
	 * @return Returns the historicalDataReport.
	 */
	public FreeChartReport getReport() {
		return report;
	}

	/**
	 * @param historicalDataReport
	 *          The historicalDataReport to set.
	 */
	public void setReport(FreeChartReport report) {
		this.report = report;
	}
}