/*
 * JASA Java Auction Simulator API
 * Copyright (C) Steve Phelps
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

/**
 * <p>
 * A market data logger that keeps cummulative statistics on a number of variables
 * </p>
 *
 * @author Steve Phelps
 */

public class StatsMarketDataLogger
  implements MarketDataLogger, Serializable, Cloneable, Resetable {

  /**
   * Cummulative statistics on transaction prices.
   */
  CummulativeStatCounter transPriceStats;

  /**
   * Cummulative statistics on bid prices.
   */
  CummulativeStatCounter bidPriceStats;

  /**
   * Cummulative statistics on ask prices.
   */
  CummulativeStatCounter askPriceStats;

  /**
   * Cummulative statistics on the bid part of market quotes.
   */
  CummulativeStatCounter bidQuoteStats;

  /**
   * Cumulative statistics on the ask part of market quotes.
   */
  CummulativeStatCounter askQuoteStats;


  public StatsMarketDataLogger() {
    initialise();
  }

  public void updateQuoteLog( int time, MarketQuote quote ) {
    bidQuoteStats.newData((double) quote.getBid());
    askQuoteStats.newData((double) quote.getAsk());
  }

  public void updateTransPriceLog( int time, Shout ask, double price ) {
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
    initialise();
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public StatsMarketDataLogger newCopy() {
    StatsMarketDataLogger copy = null;
    try {
      copy = (StatsMarketDataLogger) clone();
    } catch ( CloneNotSupportedException e ) {
    }
    return copy;
  }

}