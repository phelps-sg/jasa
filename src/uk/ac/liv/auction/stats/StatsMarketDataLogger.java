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
import java.util.HashMap;
import java.util.Map;

import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;

import uk.ac.liv.util.CummulativeDistribution;
import uk.ac.liv.util.Distribution;
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

  public void eventOccurred( AuctionEvent event ) {
    if ( event instanceof RoundClosedEvent ) {
      roundClosed((RoundClosedEvent) event);      
    } else if ( event instanceof TransactionExecutedEvent ) {
      updateTransPriceLog((TransactionExecutedEvent) event);
    } else if ( event instanceof ShoutPlacedEvent ) {
      updateShoutLog((ShoutPlacedEvent) event);
    }
  }
  
  public void roundClosed( RoundClosedEvent event ) { 
    MarketQuote quote = event.getAuction().getQuote();
    stats[BID_QUOTE].newData((double) quote.getBid());
    stats[ASK_QUOTE].newData((double) quote.getAsk());
  }

  public void updateTransPriceLog( TransactionExecutedEvent event ) {
    stats[TRANS_PRICE].newData(event.getPrice());
  }

  public void updateShoutLog( ShoutPlacedEvent event ) {
    Shout shout = event.getShout();
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

  public void generateReport() {
    reportHeader();
    for( int i=0; i<stats.length; i++ ) {
      printStats(stats[i]);
    }
  }
  
  public Map getVariables() {
    HashMap vars = new HashMap();
    createReportVars(vars, "askprice", stats[ASK_PRICE]);
    createReportVars(vars, "bidprice", stats[BID_PRICE]);
    createReportVars(vars, "askquote", stats[ASK_QUOTE]);
    createReportVars(vars, "bidquote", stats[BID_QUOTE]);
    return vars;
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
  
  protected void createReportVars( Map vars, String var, Distribution stats ) {
    vars.put(makeVar(var, "mean"), new Double(stats.getMean()));
    vars.put(makeVar(var, "min"), new Double(stats.getMin()));
    vars.put(makeVar(var, "max"), new Double(stats.getMax()));
    vars.put(makeVar(var, "stdev"), new Double(stats.getStdDev()));
  }
  
  protected ReportVariable makeVar( String varName, String moment ) {
    return new ReportVariable("auctionstats." + varName + "." + moment, 
         					 varName + " distribution " + moment);
  }
}
