/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

import JSci.awt.AbstractGraphModel;
import JSci.awt.Graph2DModel;
import JSci.awt.GraphDataEvent;
import JSci.awt.GraphDataListener;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Parameterizable;

import uk.ac.liv.util.io.DataWriter;
import uk.ac.liv.util.io.DataSeriesWriter;

import java.util.Vector;

import org.apache.log4j.Logger;

import java.util.Iterator;

/**
 * <p>
 * A MarketDataLogger that logs data to a graph model which can be
 * rendered by JSCi's JLineGraph component.
 * </p>
 *
 * @author Steve Phelps
 */

public class GraphMarketDataLogger extends MeanValueDataWriterMarketDataLogger
    implements Graph2DModel, Parameterizable {

  protected int currentSeries;

  protected DataWriter[] allSeries;

  protected static GraphMarketDataLogger singletonInstance;

  private final Vector listenerList = new Vector();
  private final GraphDataEvent event = new GraphDataEvent(this);

  static Logger logger = Logger.getLogger(GraphMarketDataLogger.class);


  public GraphMarketDataLogger() {
    super();
    askQuoteLog = new DataSeriesWriter();
    bidQuoteLog = new DataSeriesWriter();
    askLog = new DataSeriesWriter();
    bidLog = new DataSeriesWriter();
    transPriceLog = new DataSeriesWriter();
    allSeries =
        new DataWriter[] { askLog, bidLog, transPriceLog };
//                            askQuoteLog, bidQuoteLog };
    currentSeries = 0;
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    singletonInstance = this;
  }

  public Iterator seriesIterator() {
    return new SeriesIterator();
  }

  public float getXCoord( int i ) {
    return (float) getCurrentSeries().getXCoord(i);
  }

  public DataSeriesWriter getCurrentSeries() {
    logger.debug("getCurrentSeries()");
    logger.debug("currentSeries = " + currentSeries);
//    logger.debug("allSeries[currentSeries] = " + allSeries[currentSeries]);
    return (DataSeriesWriter) allSeries[currentSeries];
  }

  public float getYCoord( int i ) {
    logger.debug("Getting y coordinate for " + i);
    if ( i < getCurrentSeries().length() ) {
      return (float) getCurrentSeries().getYCoord(i);
    } else {
      return 0f;
    }
  }

  public int seriesLength() {
    int size = getCurrentSeries().length();
    logger.debug("Size of current series = " + size);
    return size;
  }

  public void firstSeries() {
    logger.debug("firstSeries()");
    currentSeries = 0;
  }

  public boolean nextSeries() {
    logger.debug("nextSeries()");
    if ( currentSeries == allSeries.length-1 ) {
      logger.debug("No more serieses..");
      return false;
    } else {
      currentSeries++;
      return true;
    }
  }

  public void dataUpdated() {
    logger.debug("dataUpdated()");
//    fireDataChanged();
  }

  public void fireDataChanged() {
    logger.debug("fireDataChanged()");
    for ( int i = 0; i < listenerList.size(); i++ ) {
      logger.debug("Notifying listener " + listenerList.elementAt(i) + ".. ");
      ( (GraphDataListener) listenerList.elementAt(i)).dataChanged(event);
      logger.debug("Notification done.");
    }
    logger.debug("fireDataChanged() done");
  }

  public final void addGraphDataListener( GraphDataListener l ) {
    listenerList.addElement(l);
  }

  public final void removeGraphDataListener( GraphDataListener l ) {
    listenerList.removeElement(l);
  }

  public static GraphMarketDataLogger getSingletonInstance() {
    return singletonInstance;
  }

  public void clear() {
    for( int i=0; i<allSeries.length; i++ ) {
      ((DataSeriesWriter) allSeries[i]).clear();
    }
  }

  public void endOfDay() {
    //TODO
  }


  class SeriesIterator implements Iterator {

    int currentSeries = 0;

    SeriesIterator() {
    }

    public boolean hasNext() {
      return currentSeries < allSeries.length;
    }

    public Object next() {
      return allSeries[currentSeries++];
    }

    public void remove() {
    }
  }

}
