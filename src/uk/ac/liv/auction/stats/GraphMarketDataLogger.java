/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.MarketQuote;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;

import uk.ac.liv.util.io.DataWriter;
import uk.ac.liv.util.io.DataSeriesWriter;

import javax.swing.event.EventListenerList;
import java.awt.Dimension;

import org.apache.log4j.Logger;

import uchicago.src.sim.analysis.plot.RepastPlot;

/**
 * <p>
 * A MarketDataLogger that logs data to a graph model which can be
 * rendered by JSCi's JLineGraph component.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class GraphMarketDataLogger extends MeanValueDataWriterMarketDataLogger
    implements  Parameterizable, Resetable {

  protected int currentSeries;

  protected DataWriter[] allSeries;

  protected static RepastPlot graph;

  protected EventListenerList listenerList = new EventListenerList();
//  protected GraphDataEvent event = new GraphDataEvent(this);

  static Logger logger = Logger.getLogger(GraphMarketDataLogger.class);


  public GraphMarketDataLogger() {
    super();
    initialise();
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    graph = new JASAGraph();
  }

  public void initialise() {
    askQuoteLog = new DataSeriesWriter();
    bidQuoteLog = new DataSeriesWriter();
    askLog = new DataSeriesWriter();
    bidLog = new DataSeriesWriter();
    transPriceLog = new DataSeriesWriter();
    allSeries =
        new DataWriter[] {
        askLog, bidLog, transPriceLog};
//                            askQuoteLog, bidQuoteLog };
    currentSeries = 0;
  }


  public void reset() {
    initialise();
//    fireGraphChanged(new GraphDataEvent(this));
  }
  
  public void updateTransPriceLog( int time, Shout ask, Shout bid, 
      							double price, int quantity ) {
    super.updateTransPriceLog(time, ask, bid, price, quantity);
    graph.addPoint(0, time, transPriceStats.getMean(), true);
  }
  
  public void updateQuoteLog( int time, MarketQuote quote ) {
    super.updateQuoteLog(time, quote);
    double bid = quote.getBid();
    if ( ! Double.isInfinite(bid)) {
      graph.addPoint(1, time, bid, true);
    } else {
      graph.addPoint(1, time, 0, false);
    }
    double ask = quote.getAsk();
    if ( ! Double.isInfinite(ask)) {
      graph.addPoint(2, time, ask, true);
    } else {
      graph.addPoint(2, time, 0, false);
    }
  }
  
  public static RepastPlot getGraphSingleton() {
    return graph;
  }



}


class JASAGraph extends RepastPlot {
  
  public JASAGraph() {
    super(null);
    setColor(true);
    addLegend(0, "Mean transaction price");
    addLegend(1, "bid quote");
    addLegend(2, "ask quote");
    setPreferredSize(new Dimension(640, 480));
  }
}
