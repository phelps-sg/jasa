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

import javax.swing.event.EventListenerList;

import java.util.Iterator;

import org.apache.log4j.Logger;

import uchicago.src.sim.analysis.Sequence;

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

  protected RepastGraphSequence[] allSeries;

  protected static GraphMarketDataLogger singletonInstance;
  
  protected RepastGraphSequence askQuoteSeries, bidQuoteSeries, transPriceSeries;

  protected EventListenerList listenerList = new EventListenerList();
//  protected GraphDataEvent event = new GraphDataEvent(this);

  static Logger logger = Logger.getLogger(GraphMarketDataLogger.class);


  public GraphMarketDataLogger() {
    super();
    askQuoteLog = new RepastGraphSequence("ask quote");
    bidQuoteLog = new RepastGraphSequence("bid quote log");
    transPriceLog = new RepastGraphSequence("transaction price");
    askLog = new RepastGraphSequence("ask");
    bidLog = new RepastGraphSequence("bid");
    allSeries = new RepastGraphSequence[] { (RepastGraphSequence) askQuoteLog,
        (RepastGraphSequence) bidQuoteLog, 
        (RepastGraphSequence) transPriceLog };
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    singletonInstance = this;
  }
  
  public static GraphMarketDataLogger getSingletonInstance() {
    return singletonInstance;
  }

  

  public void reset() {
  // TODO
//    fireGraphChanged(new GraphDataEvent(this));
  }
  
  
  public Iterator getSequenceIterator() {
    return new Iterator() {
      
      int currentSequence = 0;
      
      public boolean hasNext() {
        return currentSequence < allSeries.length;
      }
      
      public Object next() {
        return (Sequence) allSeries[currentSequence++];
      }
      
      public void remove() {        
      }
      
    };
  }


}

