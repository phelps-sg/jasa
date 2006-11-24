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

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.io.*;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.io.*;

/**
 * A report that records data in CSV (comma-separated values) files.
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
 * @version $Revision$
 */

public class CSVReport extends DataWriterReport
    implements Parameterizable {

  public static final String P_DEF_BASE = "csvreport";
  
  static final String P_ASK_QUOTE_LOG_FILE = "askquotelogfile";
  static final String P_BID_QUOTE_LOG_FILE = "bidquotelogfile";
  static final String P_ASK_LOG_FILE = "asklogfile";
  static final String P_BID_LOG_FILE = "bidlogfile";
  static final String P_TRANS_LOG_FILE = "translogfile";

  static final int CSV_COLS = 2;

  public void setup( ParameterDatabase parameters, Parameter base ) {
  	
  	Parameter defBase = new Parameter(P_DEF_BASE);
  	
    String askQuoteLogFile =
        parameters.getString(base.push(P_ASK_QUOTE_LOG_FILE), 
        		defBase.push(P_ASK_QUOTE_LOG_FILE));
    String bidQuoteLogFile =
        parameters.getString(base.push(P_BID_QUOTE_LOG_FILE), 
        		defBase.push(P_BID_QUOTE_LOG_FILE));
    String askLogFile =
        parameters.getString(base.push(P_ASK_LOG_FILE), 
        		defBase.push(P_ASK_LOG_FILE));
    String bidLogFile =
        parameters.getString(base.push(P_BID_LOG_FILE), 
        		defBase.push(P_BID_LOG_FILE));
    String transLogFile =
        parameters.getString(base.push(P_TRANS_LOG_FILE), 
        		defBase.push(P_TRANS_LOG_FILE));
    try {
      if ( askQuoteLogFile != null ) {
        setCSVAskQuoteLog(new FileOutputStream(new File(askQuoteLogFile)));
      }
      if ( bidQuoteLogFile != null ) {
        setCSVBidQuoteLog(new FileOutputStream(new File(bidQuoteLogFile)));
      }
      if ( askLogFile != null ) {
        setCSVAskLog(new FileOutputStream(new File(askLogFile)));
      }
      if ( bidLogFile != null ) {
        setCSVBidLog(new FileOutputStream(new File(bidLogFile)));
      }
      if ( transLogFile != null ) {
        setCSVTransPriceLog(new FileOutputStream(new File(transLogFile)));
      }
    } catch ( java.io.IOException e ) {
      e.printStackTrace();
    }

  }

  /**
   * Assign an output stream for logging market quote data
   * in comma-separated variable (CSV) format.
   */
  public void setCSVAskQuoteLog( OutputStream stream ) {
    askQuoteLog = new CSVWriter(stream, CSV_COLS);
  }

  /**
   * Assign an output stream for logging market quote data
   * in comma-separated variable (CSV) format.
   */
  public void setCSVBidQuoteLog( OutputStream stream ) {
    bidQuoteLog = new CSVWriter(stream, CSV_COLS);
  }

  /**
   * Assign an output stream for logging shouts
   * in comma-separated variable (CSV) format.
   * This can significantly impact the performance
   * of an auction.
   */
  public void setCSVAskLog( OutputStream stream ) {
    askLog = new CSVWriter(stream, CSV_COLS);
  }

  public void setCSVBidLog( OutputStream stream ) {
    bidLog = new CSVWriter(stream, CSV_COLS);
  }

  /**
   * Assign an output stream for logging transaction price data
   * in CSV format.
   */
  public void setCSVTransPriceLog( OutputStream stream ) {
    transPriceLog = new CSVWriter(stream, CSV_COLS);
  }


}
