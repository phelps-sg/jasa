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

import java.io.Serializable;

import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;

import uk.ac.liv.util.CummulativeDistribution;
import uk.ac.liv.util.Resetable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import org.apache.log4j.Logger;

/**
 * <p>
 * A market data logger that keeps cummulative statistics on a number of
 * market variables.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class StatsMarketDataLogger extends AbstractMarketDataLogger
   implements Serializable, Cloneable, Resetable {


  protected CummulativeDistribution[] stats;


  static Logger logger = Logger.getLogger(StatsMarketDataLogger.class);

  protected static final int TRANS_PRICE = 0;
  protected static final int BID_PRICE = 1;
  protected static final int ASK_PRICE = 2;
  protected static final int BID_QUOTE = 3;
  protected static final int ASK_QUOTE = 4;

  public StatsMarketDataLogger() {
    initialise();
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
  }

  public void updateQuoteLog( int time, MarketQuote quote ) {
    stats[BID_QUOTE].newData((double) quote.getBid());
    stats[ASK_QUOTE].newData((double) quote.getAsk());
  }

  public void updateTransPriceLog( int time, Shout ask, Shout bid, double price,
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

  public CummulativeDistribution getTransPriceStats() {
    return stats[TRANS_PRICE];
  }

  public CummulativeDistribution getBidPriceStats() {
    return stats[BID_PRICE];
  }

  public CummulativeDistribution getAskPriceStats() {
    return stats[ASK_PRICE];
  }

  public CummulativeDistribution getBidQuoteStats() {
    return stats[BID_QUOTE];
  }

  public CummulativeDistribution getAskQuoteStats() {
    return stats[ASK_QUOTE];
  }

  public void initialise() {
    stats = new CummulativeDistribution[] {
     new CummulativeDistribution("Transaction Price"),
     new CummulativeDistribution("Bid Price"),
     new CummulativeDistribution("Ask Price"),
     new CummulativeDistribution("Bid Quote"),
     new CummulativeDistribution("Ask Quote")
   };

  }

  public void reset() {
    for( int i=0; i<stats.length; i++ ) {
      ((CummulativeDistribution) stats[i]).reset();
    }
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public StatsMarketDataLogger newCopy() {
    StatsMarketDataLogger copy = null;
    try {
      copy = (StatsMarketDataLogger) clone();
      for( int i=0; i<stats.length; i++ ) {
        copy.stats[i] = (CummulativeDistribution) stats[i].clone();
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


  public void auctionClosed( Auction auction ) {
    // Do nothing
  }

  public void endOfDay( Auction auction ) {
    // Do nothing
  }

  public void roundClosed( Auction auction ) {
    // Do nothing
  }

  protected void reportHeader() {
    logger.info("");
    logger.info("Auction statistics");
    logger.info("------------------");
    logger.info("");
  }

  protected void printStats( CummulativeDistribution stats ) {
    stats.log();
    logger.info("");
  }
}
