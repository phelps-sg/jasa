package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.core.*;

import uk.ac.liv.util.io.CSVWriter;

import java.io.OutputStream;

/**
 * A utility class for helping Auctions to write market data
 * to CSV log files.
 *
 * @author Steve Phelps
 */

public class CSVMarketDataLogger implements MarketDataLogger {

  /**
   * The number of columns in each CSV file.
   */
  static final int CSV_QUOTE_COLS       = 3;
  static final int CSV_SHOUT_COLS       = 4;
  static final int CSV_TRANSPRICE_COLS  = 4;

  /**
   * CSV output for market quotes as time series.
   */
  CSVWriter csvQuoteLog = null;

  /**
   * CSV output for shout data as time series.
   */
  CSVWriter csvShoutLog = null;

  /*
   * CSV output for transaction price time series.
   */
  CSVWriter csvTransPriceLog = null;


  public CSVMarketDataLogger() {
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

}