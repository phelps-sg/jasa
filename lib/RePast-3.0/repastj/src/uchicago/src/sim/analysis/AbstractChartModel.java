/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Chicago nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.sim.analysis;

import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import uchicago.src.sim.analysis.plot.OpenGraph;
import uchicago.src.sim.engine.SimModel;
/**
 * Model (MVC) class used by the Chart GUI. A Model should contain
 * enough data to create the appropriate plot object (OpenSequenceGraph, etc.)
 * for that particular kind of plot. The actual models for
 * Sequence Graphs, Histograms and so forth will extend this class.
 *
 * @version $Revision$ $Date$
 */
public abstract class AbstractChartModel {

  protected static final String SEQ_GIF = "/uchicago/src/sim/images/LineGraph.gif";

  static final Icon SEQUENCE_ICON = new ImageIcon(AbstractChartModel.class.getResource(SEQ_GIF));

  protected String title = "A Chart";
  protected String xAxisTitle = "X-Axis";
  protected String yAxisTitle = "Y-Axis";
  protected double xRangeMin = 0;
  protected double xRangeMax = 100;
  protected double xRangeIncr = 5;
  protected double yRangeMin = 0;
  protected double yRangeMax = 100;
  protected double yRangeIncr = 5;
  protected SimModel simModel;
  protected ArrayList dataSources = new ArrayList();

  public abstract String getType();

  public abstract AbstractChartModel copy();

  public abstract ArrayList getModelDataSources();

  public abstract Icon getIcon();

  public abstract OpenGraph createChart();

  public abstract String toXML();

  /**
   * Creates an AbstractChartModel.
   *
   * @param model the SimModel associated with this AbstractChartModel
   */
  public AbstractChartModel(SimModel model) {
    simModel = model;
  }

  /**
   * Returns a deep copy of this AbstractChartModel. This is used
   * by super class to copy their parent class attributes.
   *
   * @param aCopy the subclass object to copy.
   * @return a copy
   */
  protected AbstractChartModel copy(AbstractChartModel aCopy) {
    aCopy.title = title;
    aCopy.xAxisTitle = xAxisTitle;
    aCopy.yAxisTitle = yAxisTitle;
    aCopy.xRangeMax = xRangeMax;
    aCopy.xRangeMin = xRangeMin;
    aCopy.xRangeIncr = xRangeIncr;
    aCopy.yRangeMax = yRangeMax;
    aCopy.yRangeMin = yRangeMin;
    aCopy.yRangeIncr = yRangeIncr;
    aCopy.simModel = simModel;
    ArrayList list = new ArrayList();
    for (int i = 0, n = dataSources.size(); i < n; i++) {
      GuiChartDataSource ds = (GuiChartDataSource) dataSources.get(i);
      list.add(ds.copy());
    }
    aCopy.dataSources = list;

    return aCopy;
  }

  /**
   * Returns the SimModel associated with this AbstractChartModel.
   * @return
   */
  public SimModel getSimModel() {
    return simModel;
  }

  /**
   * Returns the title for the chart produced by this AbstractChartModel.
   * @return
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title for the chart produced by this AbstractChartModel.
   * @param title the title for the chart produced by this model.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the x-axis title for the chart produced by this AbstractChartModel.
   * @return
   */
  public String getXAxisTitle() {
    return xAxisTitle;
  }

  /**
   * Sets the x-axis title for the chart produced by this AbstractChartModel.
   *
   * @param xAxisTitle the x-axis title
   */
  public void setXAxisTitle(String xAxisTitle) {
    this.xAxisTitle = xAxisTitle;
  }

  /**
   * Gets the y-axis title for the chart produced by this AbstractChartModel.
   * @return
   */
  public String getYAxisTitle() {
    return yAxisTitle;
  }


  /**
   * Sets the y-axis title for the chart produced by this AbstractChartModel.
   *
   * @param yAxisTitle the y-axis title
   */
  public void setYAxisTitle(String yAxisTitle) {
    this.yAxisTitle = yAxisTitle;
  }

  /**
   * Gets the initial minimum value for the x-axis of the chart produced by
   * this AbstractChartModel.
   *
   * @return the minimum x-axis value
   */
  public double getXRangeMin() {
    return xRangeMin;
  }

  /**
   * Sets the initial minimum value for the x-axis of the chart produced by
   * this AbstractChartModel.
   *
   * @param xRangeMin the minimum x-axis value
   */
  public void setXRangeMin(String xRangeMin) {
    setXRangeMin(Double.parseDouble(xRangeMin));
  }

  /**
   * Sets the initial minimum value for the x-axis of the chart produced by
   * this AbstractChartModel.
   *
   * @param xRangeMin the minimum x-axis value
   */
  public void setXRangeMin(double xRangeMin) {
    this.xRangeMin = xRangeMin;
  }

  /**
   * Gets the initial maximum value for the x-axis of the chart produced by
   * this AbstractChartModel.
   *
   * @return the maximum x-axis value
   */
  public double getXRangeMax() {
    return xRangeMax;
  }

  /**
   * Sets the initial maximum value for the x-axis of the chart produced by
   * this AbstractChartModel.
   *
   * @param xRangeMax the maximum x-axis value
   */
  public void setXRangeMax(String xRangeMax) {
    setXRangeMax(Double.parseDouble(xRangeMax));
  }

  /**
   * Sets the initial maximum value for the x-axis of the chart produced by
   * this AbstractChartModel.
   *
   * @param xRangeMax the maximum x-axis value
   */
  public void setXRangeMax(double xRangeMax) {
    this.xRangeMax = xRangeMax;
  }

  /**
   * Gets the amount to increment the displayed x-axis range when
   * autoscaling the the chart produced by this AbstractChartModel.
   *
   * @return the x-axis range increment
   */
  public double getXRangeIncr() {
    return xRangeIncr;
  }

  /**
   * Sets the amount to increment the displayed x-axis range when
   * autoscaling the the chart produced by this AbstractChartModel.
   *
   * @param xRangeIncr the x-axis range increment
   */
  public void setXRangeIncr(String xRangeIncr) {
    setXRangeIncr(Double.parseDouble(xRangeIncr));
  }

  /**
   * Sets the amount to increment the displayed x-axis range when
   * autoscaling the the chart produced by this AbstractChartModel.
   *
   * @param xRangeIncr the x-axis range increment
   */
  public void setXRangeIncr(double xRangeIncr) {
    this.xRangeIncr = xRangeIncr;
  }

  /**
   * Gets the initial minimum value for the y-axis of the chart produced by
   * this AbstractChartModel.
   *
   * @return the minimum y-axis value
   */
  public double getYRangeMin() {
    return yRangeMin;
  }

  /**
   * Sets the initial minimum value for the y-axis of the chart produced by
   * this AbstractChartModel.
   *
   * @param yRangeMin the minimum x-axis value
   */
  public void setYRangeMin(String yRangeMin) {
    setYRangeMin(Double.parseDouble(yRangeMin));
  }

  /**
   * Sets the initial minimum value for the y-axis of the chart produced by
   * this AbstractChartModel.
   *
   * @param yRangeMin the minimum x-axis value
   */
  public void setYRangeMin(double yRangeMin) {
    this.yRangeMin = yRangeMin;
  }

  /**
   * Gets the initial maximum value for the y-axis of the chart produced by
   * this AbstractChartModel.
   *
   * @return the maximum y-axis value
   */
  public double getYRangeMax() {
    return yRangeMax;
  }

  /**
   * Sets the initial maximum value for the y-axis of the chart produced by
   * this AbstractChartModel.
   *
   * @param yRangeMax the maximum x-axis value
   */
  public void setYRangeMax(String yRangeMax) {
    setYRangeMax(Double.parseDouble(yRangeMax));
  }

  /**
   * Sets the initial maximum value for the y-axis of the chart produced by
   * this AbstractChartModel.
   *
   * @param yRangeMax the maximum x-axis value
   */
  public void setYRangeMax(double yRangeMax) {
    this.yRangeMax = yRangeMax;
  }

  /**
   * Sets the amount to increment the displayed y-axis range when
   * autoscaling the the chart produced by this AbstractChartModel.
   *
   * @return the y-axis range increment
   */
  public double getYRangeIncr() {
    return yRangeIncr;
  }

  /**
   * Sets the amount to increment the displayed y-axis range when
   * autoscaling the the chart produced by this AbstractChartModel.
   *
   * @param yRangeIncr the y-axis range increment
   */
  public void setYRangeIncr(String yRangeIncr) {
    setYRangeIncr(Double.parseDouble(yRangeIncr));
  }

  /**
   * Sets the amount to increment the displayed y-axis range when
   * autoscaling the the chart produced by this AbstractChartModel.
   *
   * @param yRangeIncr the y-axis range increment
   */
  public void setYRangeIncr(double yRangeIncr) {
    this.yRangeIncr = yRangeIncr;
  }


  /**
   * Returns an ArrayList of GuiChartDataSource objects.
   *
   * @return ArrayList of GuiChartDataSource objects.
   */
  public ArrayList getDataSources() {
    return dataSources;
  }

  /**
   * Sets the list of GuiChartDataSource objects.
   *
   * @param dataSources GuiChartDataSources for this AbstractChartModel
   */
  public void setDataSources(ArrayList dataSources) {
    this.dataSources = dataSources;
  }

  /**
   * Returns an XML string of the attributes of this AbstractChartModel.
   * @return
   */
  protected String getXML() {

    StringBuffer b = new StringBuffer("title =\"");
    b.append(title);
    b.append("\" xAxisTitle=\"");
    b.append(xAxisTitle);
    b.append("\" yAxisTitle=\"");
    b.append(yAxisTitle);
    b.append("\" xRangeMin=\"");
    b.append(xRangeMin);
    b.append("\" xRangeMax=\"");
    b.append(xRangeMax);
    b.append("\" xRangeIncr=\"");
    b.append(xRangeIncr);
    b.append("\" yRangeMin=\"");
    b.append(yRangeMin);
    b.append("\" yRangeMax=\"");
    b.append(yRangeMax);
    b.append("\" yRangeIncr=\"");
    b.append(yRangeIncr);
    b.append("\" >\n");

    for (int i = 0, n = dataSources.size(); i < n; i++) {
      GuiChartDataSource ds = (GuiChartDataSource)dataSources.get(i);
      b.append(ds.toXML());
      b.append("\n");
    }

    return b.toString();
  }
}
