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

import uk.ac.liv.auction.core.*;
import uk.ac.liv.util.io.*;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class DataWriterMarketDataLogger extends AbstractMarketDataLogger {


  /**
   * output for the ask component of market quotes as time series.
   */
  protected DataWriter askQuoteLog = null;

  /**
   * output for the bid component of market quotes as time series.
   */
  protected DataWriter bidQuoteLog = null;

  /**
   * output for bid data as time series.
   */
  protected DataWriter bidLog = null;

  /**
   * output for ask data as time series.
   */
  protected DataWriter askLog = null;

  /*
   * output for transaction price time series.
   */
  protected DataWriter transPriceLog = null;

  /**
   * The auction we are keeping statistics on.
   */
  protected RoundRobinAuction auction;

  public DataWriterMarketDataLogger() {
    this(null, null, null, null, null);
  }

  public DataWriterMarketDataLogger( DataWriter askQuoteLog,
                                      DataWriter bidQuoteLog,
                                      DataWriter bidLog,
                                      DataWriter askLog,
                                      DataWriter transPriceLog ) {
     this.askQuoteLog = askQuoteLog;
     this.bidQuoteLog = bidQuoteLog;
     this.askLog = askLog;
     this.bidLog = bidLog;
     this.transPriceLog = transPriceLog;
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

  }


  public void updateQuoteLog( int time, MarketQuote quote ) {
    if ( askQuoteLog != null ) {
      askQuoteLog.newData(time);
      askQuoteLog.newData(quote.getAsk());
    }
    if ( bidQuoteLog != null ) {
      bidQuoteLog.newData(time);
      bidQuoteLog.newData(quote.getBid());
    }
    dataUpdated();
  }

  public void updateTransPriceLog( int time, Shout shout, double price,
                                    int quantity ) {
    if ( transPriceLog != null ) {
      transPriceLog.newData(time);
      transPriceLog.newData(price);
    }
    dataUpdated();
  }

  public void updateShoutLog( int time, Shout shout ) {
    if (shout.isBid()) {
      if ( bidLog != null ) {
        bidLog.newData(time);
        bidLog.newData(shout.getPrice());
      }
    } else {
      if ( askLog != null ) {
        askLog.newData(time);
        askLog.newData(shout.getPrice());
      }
    }
    dataUpdated();
  }

  public void dataUpdated() {
  }


  public void finalReport() {
  }

  public void endOfRound() {
    // Do nothing
  }

  public void endOfDay() {
    // Do nothing
  }

  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
  }


}
