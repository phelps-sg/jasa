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

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.io.*;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.io.*;

/**
 * A utility class for helping Auctions to write market data
 * to CSV log files.
 *
 * <p><b>Parameters</b><br></p>
 * <table>
 *
 * <tr><td valign=top><i>base</i><tt>.quotelogfile</tt><br>
 * <font size=-1>string</font></td>
 * <td valign=top>(the filename to store the quote data)</td><tr> 
 *
 * <tr><td valign=top><i>base</i><tt>.shoutlogfile</tt><br>
 * <font size=-1>string</font></td>
 * <td valign=top>(the filename to store the shout data)</td><tr> 
 *
 * <tr><td valign=top><i>base</i><tt>.translogfile</tt><br>
 * <font size=-1>string</font></td>
 * <td valign=top>(the filename to store the transaction price data)</td><tr> 
 *
 * </table>
 *
 * @author Steve Phelps
 */

public class CSVMarketDataLogger implements MarketDataLogger, Parameterizable {

  /**
   * The number of columns in each CSV file.
   */
  static final int CSV_QUOTE_COLS       = 3;
  static final int CSV_SHOUT_COLS       = 4;
  static final int CSV_TRANSPRICE_COLS  = 4;

  /**
   * CSV output for market quotes as time series.
   */
  DataWriter csvQuoteLog = null;

  /**
   * CSV output for shout data as time series.
   */
  DataWriter csvShoutLog = null;

  /*
   * CSV output for transaction price time series.
   */
  DataWriter csvTransPriceLog = null;

  static final String P_QUOTE_LOG_FILE = "quotelogfile";
  static final String P_SHOUT_LOG_FILE = "shoutlogfile";
  static final String P_TRANS_LOG_FILE = "translogfile";

  
  public void setup( ParameterDatabase parameters, Parameter base ) {
    String quoteLogFile = parameters.getString(base.push(P_QUOTE_LOG_FILE), null);
    String shoutLogFile = parameters.getString(base.push(P_SHOUT_LOG_FILE), null);
    String transLogFile = parameters.getString(base.push(P_TRANS_LOG_FILE), null);
    try {
      setCSVQuoteLog( new FileOutputStream(new File(quoteLogFile)) );
      setCSVShoutLog( new FileOutputStream(new File(shoutLogFile)) );
      setCSVTransPriceLog( new FileOutputStream(new File(transLogFile)) );
    } catch ( java.io.IOException e ) {
      e.printStackTrace();
    }

  }

  /**
   * Assign an output stream for logging market quote data
   * in comma-separated variable (CSV) format.
   */
  public void setCSVQuoteLog( OutputStream stream ) {
    csvQuoteLog = new CSVWriter(stream, CSV_QUOTE_COLS);
  }

  /**
   * Assign an output stream for logging shouts
   * in comma-separated variable (CSV) format.
   * This can significantly impact the performance
   * of an auction.
   */
  public void setCSVShoutLog( OutputStream stream ) {
    csvShoutLog = new CSVWriter(stream, CSV_SHOUT_COLS);
  }

  /**
   * Assign an output stream for logging transaction price data
   * in CSV format.
   */
  public void setCSVTransPriceLog( OutputStream stream ) {
    csvTransPriceLog = new CSVWriter(stream, CSV_TRANSPRICE_COLS);
  }


  public void updateQuoteLog( int time, MarketQuote quote ) {
    if ( csvQuoteLog != null ) {
      csvQuoteLog.newData(time);
      csvQuoteLog.newData(quote.getAsk());
      csvQuoteLog.newData(quote.getBid());
    }
  }

  public void updateTransPriceLog( int time, Shout ask, double price ) {
    if ( csvTransPriceLog != null ) {
      csvTransPriceLog.newData(time);
      csvTransPriceLog.newData(ask.getAgent().getId());
      csvTransPriceLog.newData(ask.getPrice());
      csvTransPriceLog.newData(price);
    }
  }

  public void updateShoutLog( int time, Shout shout ) {
    if ( csvShoutLog != null ) {
      csvShoutLog.newData(time);
      csvShoutLog.newData(shout.getAgent().getId());
      csvShoutLog.newData(shout.getPrice());
      csvShoutLog.newData(shout.isBid());
    }
  }

  public void reset() {
    //TODO: what?
  }

  public void finalReport() {
  }


}
