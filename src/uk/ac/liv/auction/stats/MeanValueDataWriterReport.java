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

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;
import uk.ac.liv.util.CummulativeDistribution;
import uk.ac.liv.util.io.*;

import org.apache.log4j.Logger;

/**
 * This report keeps track of the mean value of each market variable
 * over the course of each round of bidding and logs the mean value
 * to the specified DataWriter objects.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class MeanValueDataWriterReport extends DataWriterReport {


  protected CummulativeDistribution askQuoteStats =
      new CummulativeDistribution("Ask Quote");

  protected CummulativeDistribution bidQuoteStats =
      new CummulativeDistribution("Bid Quote");

  protected CummulativeDistribution bidStats =
      new CummulativeDistribution("Bid");

  protected CummulativeDistribution askStats =
      new CummulativeDistribution("Ask");

  protected CummulativeDistribution transPriceStats =
      new CummulativeDistribution("Transaction Price");

  protected CummulativeDistribution[] allStats = {
      askQuoteStats, bidQuoteStats, askStats, bidStats, transPriceStats
  };

  protected int round;

  static Logger logger =
      Logger.getLogger(MeanValueDataWriterReport.class);


  public MeanValueDataWriterReport( DataWriter askQuoteLog,
                                      DataWriter bidQuoteLog,
                                      DataWriter bidLog,
                                      DataWriter askLog,
                                      DataWriter transPriceLog ) {
    super(askQuoteLog, bidQuoteLog, bidLog, askLog, transPriceLog);
  }

  public MeanValueDataWriterReport() {
    super();
  }


  public void eventOccurred( AuctionEvent event ) {
    super.eventOccurred(event);
    if ( event instanceof RoundClosedEvent ) {
      roundClosed((RoundClosedEvent) event);
    }
  }
  
  public void updateQuoteLog( RoundClosedEvent event ) {
    MarketQuote quote = event.getAuction().getQuote();
    askQuoteStats.newData(quote.getAsk());
    bidQuoteStats.newData(quote.getBid());
  }


  public void updateTransPriceLog( TransactionExecutedEvent event ) {
     transPriceStats.newData(event.getPrice());
  }


  public void updateShoutLog( ShoutPlacedEvent event ) {
    Shout shout = event.getShout();
    if (shout.isBid()) {
      bidStats.newData(shout.getPrice());
    } else {
      askStats.newData(shout.getPrice());
    }
  }


  public void roundClosed( RoundClosedEvent event  ) {

    logger.debug("roundClosed(" + auction + ")");

    update(askQuoteLog, askQuoteStats);
    update(bidQuoteLog, bidQuoteStats);
    update(askLog, askStats);
    update(bidLog, bidStats);
    update(transPriceLog, transPriceStats);

    for( int i=0; i<allStats.length; i++ ) {
      logger.debug(allStats[i]);
      allStats[i].reset();
    }

    round++;
  }



  protected void update( DataWriter writer, CummulativeDistribution stats ) {
    //writer.newData(round);
    writer.newData(stats.getMean());
  }


  public void produceUserOutput() {
  }


}
