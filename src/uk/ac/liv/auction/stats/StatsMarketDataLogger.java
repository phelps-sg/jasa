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
import uk.ac.liv.auction.core.RoundRobinAuction;

import uk.ac.liv.util.CummulativeStatCounter;
import uk.ac.liv.util.Resetable;

import org.apache.log4j.Logger;

/**
 * <p>
 * A market data logger that keeps cummulative statistics on a number of variables.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class StatsMarketDataLogger
  implements MarketDataLogger, Serializable, Cloneable, Resetable {

  protected static final int TRANS_PRICE = 0;
  protected static final int BID_PRICE = 1;
  protected static final int ASK_PRICE = 2;
  protected static final int BID_QUOTE = 3;
  protected static final int ASK_QUOTE = 4;

  protected CummulativeStatCounter[] stats;

  /**
   * The auction we are keeping statistics on.
   */
  protected RoundRobinAuction auction;

  static Logger logger = Logger.getLogger(StatsMarketDataLogger.class);

  public StatsMarketDataLogger() {
    initialise();
  }

  public void updateQuoteLog( int time, MarketQuote quote ) {
    stats[BID_QUOTE].newData((double) quote.getBid());
    stats[ASK_QUOTE].newData((double) quote.getAsk());
  }

  public void updateTransPriceLog( int time, Shout ask, double price,
                                    int quantity ) {
    stats[TRANS_PRICE].newData(price);
  }

  public void updateShoutLog( int time, Shout shout ) {
    if ( shout.isBid() ) {
      stats[BID_PRICE].newData(shout.getPrice());
    } else {
      stats[ASK_PRICE].newData(shout.getPrice());
    }
  }

  public CummulativeStatCounter getTransPriceStats() {
    return stats[TRANS_PRICE];
  }

  public CummulativeStatCounter getBidPriceStats() {
    return stats[BID_PRICE];
  }

  public CummulativeStatCounter getAskPriceStats() {
    return stats[ASK_PRICE];
  }

  public CummulativeStatCounter getBidQuoteStats() {
    return stats[BID_QUOTE];
  }

  public CummulativeStatCounter getAskQuoteStats() {
    return stats[ASK_QUOTE];
  }

  public void initialise() {
    stats = new CummulativeStatCounter[] {
     new CummulativeStatCounter("Transaction Price"),
     new CummulativeStatCounter("Bid Price"),
     new CummulativeStatCounter("Ask Price"),
     new CummulativeStatCounter("Bid Quote"),
     new CummulativeStatCounter("Ask Quote")
   };

  }

  public void reset() {
    for( int i=0; i<stats.length; i++ ) {
      ((CummulativeStatCounter) stats[i]).reset();
    }
  }

  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public StatsMarketDataLogger newCopy() {
    StatsMarketDataLogger copy = null;
    try {
      copy = (StatsMarketDataLogger) clone();
      for( int i=0; i<stats.length; i++ ) {
        copy.stats[i] = (CummulativeStatCounter) stats[i].clone();
      }
    } catch ( CloneNotSupportedException e ) {
      logger.error(e.getMessage());
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
    return copy;
  }

  public void finalReport() {
    reportHeader();
    for( int i=0; i<stats.length; i++ ) {
      printStats(stats[i]);
    }
  }

  public void endOfRound() {
    // Do nothing
  }

  public void endOfDay() {
    // Do nothing
  }

  protected void reportHeader() {
    logger.info("");
    logger.info("Auction statistics");
    logger.info("------------------");
    logger.info("");
  }

  protected void printStats( CummulativeStatCounter stats ) {
    stats.log();
    logger.info("");
  }
}
