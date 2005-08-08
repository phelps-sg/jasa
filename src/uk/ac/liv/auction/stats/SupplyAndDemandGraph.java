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

import java.awt.Color;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import uk.ac.liv.auction.core.ContinuousDoubleAuctioneerEE;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionOpenEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;
import uk.ac.liv.util.io.DataWriter;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * A <code>FreeChartGraph</code> showing supply and demand curves.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class SupplyAndDemandGraph extends FreeChartGraph {

  static Logger logger = Logger.getLogger(SupplyAndDemandGraph.class);

  DataWriterSeries trueSupply, trueDemand;
  DataWriterSeries reportedSupply, reportedDemand;
  CombinedRangeXYPlot combinedPlot;
  XYPlot truePlot, reportedPlot;

  public SupplyAndDemandGraph() {
  }

  
  protected void setupChart(ParameterDatabase parameters, Parameter base) {
    setName("Supply and Demand");
    combinedPlot = new CombinedRangeXYPlot(new NumberAxis("Price"));
    trueSupply = new DataWriterSeries("True Supply");
    trueDemand = new DataWriterSeries("True Demand");
    XYSeriesCollection seriesCollection = new XYSeriesCollection();
    seriesCollection.addSeries((XYSeries)trueSupply.getSeries());
    seriesCollection.addSeries((XYSeries)trueDemand.getSeries());
    XYItemRenderer renderer0 = new StandardXYItemRenderer();
    truePlot = new XYPlot(seriesCollection, new NumberAxis("Amount"), null, renderer0);
    combinedPlot.add(truePlot);
    
    reportedSupply = new DataWriterSeries("Reported Supply");
    reportedDemand = new DataWriterSeries("Reported Demand");
    seriesCollection = new XYSeriesCollection();
    seriesCollection.addSeries((XYSeries)reportedSupply.getSeries());
    seriesCollection.addSeries((XYSeries)reportedDemand.getSeries());      
    XYItemRenderer renderer1 = new StandardXYItemRenderer();
    reportedPlot = new XYPlot(seriesCollection, new NumberAxis("Amount"), null, renderer1);
    renderer1.setSeriesPaint(0, renderer0.getSeriesPaint(0));
    renderer1.setSeriesPaint(1, renderer0.getSeriesPaint(1));
    combinedPlot.add(reportedPlot);

    setChart(new JFreeChart(getName(), combinedPlot));
  }
  
  
  public void eventOccurred(AuctionEvent event) {
    if (event instanceof ShoutPlacedEvent) {

      ((XYSeries)reportedSupply.getSeries()).clear();
      ((XYSeries)reportedDemand.getSeries()).clear();
      ReportedSupplyAndDemandStats rsdStats = new ReportedSupplyAndDemandStats(
          getReport().getAuction(), reportedSupply, reportedDemand);
      rsdStats.calculate();
      rsdStats.produceUserOutput();

    } else if (event instanceof AuctionOpenEvent) {

      double value = ((TimePeriodValue) ReportVariableBoard
          .getInstance().getValue(ReportVariableBoardUpdater.EQUIL_PRICE)).getValue().doubleValue();
      Marker marker = FreeChartMarker.createMarker(value, Color.black, "EE");
      logger.info("Equilibriuim: "+value);
      truePlot.clearRangeMarkers();
      truePlot.addRangeMarker(marker);

      updateTrueDS();
      
    } else if (event instanceof TransactionExecutedEvent) {

      updateTrueDS();
      updateReportedDS();
    
    }
  }
  
  private void updateTrueDS() {
    ((XYSeries)trueSupply.getSeries()).clear();
    ((XYSeries)trueDemand.getSeries()).clear();
    TrueSupplyAndDemandStats tsdStats = new TrueSupplyAndDemandStats(
        getReport().getAuction(), trueSupply, trueDemand);
    tsdStats.calculate();
    tsdStats.produceUserOutput();    

  }
  
  private void updateReportedDS() {
    TimePeriodValue tpValue = (TimePeriodValue) ReportVariableBoard
        .getInstance().getValue(ContinuousDoubleAuctioneerEE.EST_EQUILIBRIUM_PRICE);
    if (tpValue != null) {
      double value = tpValue.getValue().doubleValue();
      Marker marker = FreeChartMarker.createMarker(value, Color.black, "EE");
      logger.info("Marker: " + value);
      reportedPlot.clearRangeMarkers();
      reportedPlot.addRangeMarker(marker);
    }
  }
  
  public class DataWriterSeries extends FreeChartSeries implements DataWriter {

    public DataWriterSeries(String name) {
      series = new XYSeries(name, false, true);
    }

    public void newData(int datum) {
        newData((double) datum);
    }

    public void newData(long datum) {
        newData((double) datum);
    }

    public void newData(float datum) {
        newData((double) datum);
    }

    private double temp;
    private boolean dataExpected;

    public void newData(double datum) {

        if (dataExpected) {
            ((XYSeries)getSeries()).add((int)temp, datum);
        } else {
            temp = datum;
        }

        dataExpected = !dataExpected;
    }

    public void flush() {
    }

    public void close() {
    }

    public void newData(Iterator i) {
      throw newException();
    }

    public void newData(Object[] data) {
      throw newException();
    }

    public void newData(Object data) {
      throw newException();
    }

    public void newData(boolean data) {
        throw newException();
    }
    
    private UnsupportedOperationException newException() {
      return new java.lang.UnsupportedOperationException(
      "Method newData() not yet implemented.");
    }
  }
  
}