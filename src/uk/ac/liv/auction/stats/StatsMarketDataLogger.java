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

import java.io.Serializable;

import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.Shout;

import uk.ac.liv.util.CummulativeStatCounter;
import uk.ac.liv.util.Resetable;

import org.apache.log4j.Logger;

/**
 * <p>
 * A market data logger that keeps cummulative statistics on a number of variables.
 * </p>
 *
 * @author Steve Phelps
 */

public class StatsMarketDataLogger
  implements MarketDataLogger, Serializable, Cloneable {

  /**
   * Cummulative statistics on transaction prices.
   */
  protected CummulativeStatCounter transPriceStats;

  /**
   * Cummulative statistics on bid prices.
   */
  protected CummulativeStatCounter bidPriceStats;

  /**
   * Cummulative statistics on ask prices.
   */
  protected CummulativeStatCounter askPriceStats;

  /**
   * Cummulative statistics on the bid part of market quotes.
   */
  protected CummulativeStatCounter bidQuoteStats;

  /**
   * Cumulative statistics on the ask part of market quotes.
   */
  protected CummulativeStatCounter askQuoteStats;

  static Logger logger = Logger.getLogger(StatsMarketDataLogger.class);


  public StatsMarketDataLogger() {
    initialise();
  }

  public void updateQuoteLog( int time, MarketQuote quote ) {
    bidQuoteStats.newData((double) quote.getBid());
    askQuoteStats.newData((double) quote.getAsk());
  }

  public void updateTransPriceLog( int time, Shout ask, double price,
                                    int quantity ) {
    transPriceStats.newData(price);
  }

  public void updateShoutLog( int time, Shout shout ) {
    if ( shout.isBid() ) {
      bidPriceStats.newData(shout.getPrice());
    } else {
      askPriceStats.newData(shout.getPrice());
    }
  }

  public CummulativeStatCounter getTransPriceStats() {
    return transPriceStats;
  }

  public CummulativeStatCounter getBidPriceStats() {
    return bidPriceStats;
  }

  public CummulativeStatCounter getAskPriceStats() {
    return askPriceStats;
  }

  public CummulativeStatCounter getBidQuoteStats() {
    return bidQuoteStats;
  }

  public CummulativeStatCounter getAskQuoteStats() {
    return askQuoteStats;
  }

  public void initialise() {

    transPriceStats =
      new CummulativeStatCounter("Transaction Price");

    bidPriceStats =
      new CummulativeStatCounter("Bid Price");

    askPriceStats =
      new CummulativeStatCounter("Ask Price");

    bidQuoteStats =
      new CummulativeStatCounter("Bid Quote");

    askQuoteStats =
      new CummulativeStatCounter("Ask Quote");

  }

  public void reset() {
    transPriceStats.reset();
    bidPriceStats.reset();
    askPriceStats.reset();
    bidQuoteStats.reset();
    askQuoteStats.reset();
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public StatsMarketDataLogger newCopy() {
    StatsMarketDataLogger copy = null;
    try {
      copy = (StatsMarketDataLogger) clone();
      copy.transPriceStats = (CummulativeStatCounter) transPriceStats.clone();
      copy.bidPriceStats = (CummulativeStatCounter) bidPriceStats.clone();
      copy.askPriceStats = (CummulativeStatCounter) askPriceStats.clone();
      copy.bidQuoteStats = (CummulativeStatCounter) bidQuoteStats.clone();
      copy.askQuoteStats = (CummulativeStatCounter) askQuoteStats.clone();
    } catch ( CloneNotSupportedException e ) {
    }
    return copy;
  }

  public void finalReport() {
    logger.info("");
    logger.info("Auction statistics");
    logger.info("------------------");
    logger.info("");
    printStats(transPriceStats);
    printStats(bidPriceStats);
    printStats(askPriceStats);
    printStats(bidQuoteStats);
    printStats(askQuoteStats);
  }

  public void endOfRound() {
    // Do nothing
  }

  public void endOfDay() {
    //TODO
  }

  protected void printStats( CummulativeStatCounter stats ) {
    stats.log();
    logger.info("");
  }
}
