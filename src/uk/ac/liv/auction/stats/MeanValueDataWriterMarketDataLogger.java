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
import uk.ac.liv.util.CummulativeStatCounter;
import uk.ac.liv.util.io.*;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.io.*;

import org.apache.log4j.Logger;

/**
 * @author Steve Phelps
 */

public class MeanValueDataWriterMarketDataLogger extends DataWriterMarketDataLogger {


  protected CummulativeStatCounter askQuoteStats =
      new CummulativeStatCounter("Ask Quote");

  protected CummulativeStatCounter bidQuoteStats =
      new CummulativeStatCounter("Bid Quote");

  protected CummulativeStatCounter bidStats =
      new CummulativeStatCounter("Bid");

  protected CummulativeStatCounter askStats =
      new CummulativeStatCounter("Ask");

  protected CummulativeStatCounter transPriceStats =
      new CummulativeStatCounter("Transaction Price");

  protected CummulativeStatCounter[] allStats = {
      askQuoteStats, bidQuoteStats, askStats, bidStats, transPriceStats
  };

  protected int round;

  static Logger logger =
      Logger.getLogger(MeanValueDataWriterMarketDataLogger.class);


  public MeanValueDataWriterMarketDataLogger( DataWriter askQuoteLog,
                                      DataWriter bidQuoteLog,
                                      DataWriter bidLog,
                                      DataWriter askLog,
                                      DataWriter transPriceLog ) {
    super(askQuoteLog, bidQuoteLog, bidLog, askLog, transPriceLog);
  }

  public MeanValueDataWriterMarketDataLogger() {
    super();
  }


  public void updateQuoteLog( int time, MarketQuote quote ) {
    askQuoteStats.newData(quote.getAsk());
    bidQuoteStats.newData(quote.getBid());
  }


  public void updateTransPriceLog( int time, Shout shout, double price,
                                    int quantity ) {
     transPriceStats.newData(price);
  }


  public void updateShoutLog( int time, Shout shout ) {
    round = time;
    if (shout.isBid()) {
      bidStats.newData(shout.getPrice());
    } else {
      askStats.newData(shout.getPrice());
    }
  }


  public void endOfRound() {

    logger.debug("endOfRound()");

    update(askQuoteLog, askQuoteStats);
    update(bidQuoteLog, bidQuoteStats);
    update(askLog, askStats);
    update(bidLog, bidStats);
    update(transPriceLog, transPriceStats);

    for( int i=0; i<allStats.length; i++ ) {
      allStats[i].reset();
      logger.debug("Finished resetting: " + allStats[i]);
    }
  }


  protected void update( DataWriter writer, CummulativeStatCounter stats ) {
    writer.newData(round);
    writer.newData(stats.getMean());
  }


  public void finalReport() {
  }


}
