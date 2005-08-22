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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionEventListener;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * a JFreeChart graph that can be included in a FreeChartReport.
 * 
 * <p><b>Parameters</b><br>
 *
 * <table>
 *
 * <tr><td valign=top><i>base</i><tt>.name</tt><br>
 * <font size=-1> string </font></td>
 * <td valign=top></td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>.width</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the width of the graph and 350 by default)</td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>.height</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the height of the graph and 300 by default)</td></tr>
 * 
 * </table>
 *
 * @author Jinzhong Niu
 * @version $Revision$
 */
public abstract class FreeChartGraph extends JPanel implements AuctionEventListener {

  static Logger logger = Logger.getLogger(FreeChartGraph.class);

  public static final String P_NAME = "name";

  public static final String P_X = "x";
  public static final String P_Y = "y";
  public static final String P_WIDTH = "width";
  public static final String P_HEIGHT = "height";

  private FreeChartReport report;

  private JFreeChart jfreechart;
  
  private ChartPanel chartPanel;

  public FreeChartGraph() {
    setLayout(new BorderLayout());
  }

  public void setup(ParameterDatabase parameters, Parameter base) {

    setName(parameters.getStringWithDefault(base.push(P_NAME), "Name here"));

    setPreferredSize(new Dimension(parameters.getIntWithDefault(base.push(P_WIDTH), null, 350),
        parameters.getIntWithDefault(base.push(P_HEIGHT), null, 300)));

    setupChart(parameters, base);

  }
  
  protected abstract void setupChart(ParameterDatabase parameters, Parameter base);

  
  public void eventOccurred(AuctionEvent event) {
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
   * @return Returns the report.
   */
  public FreeChartReport getReport() {
    return report;
  }

  /**
   * @param report
   *          The report to set.
   */
  public void setReport(FreeChartReport report) {
    this.report = report;
  }
}