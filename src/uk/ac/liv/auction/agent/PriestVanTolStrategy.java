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


package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.AuctionError;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionOpenEvent;
import uk.ac.liv.auction.stats.HistoricalDataReport;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class PriestVanTolStrategy extends MomentumStrategy
    implements Serializable  {

  static Logger logger = Logger.getLogger(PriestVanTolStrategy.class);
  
  protected HistoricalDataReport historyStats;

  public void eventOccurred( AuctionEvent event ) {
    super.eventOccurred(event);
    if ( event instanceof AuctionOpenEvent ) {
      auctionOpen((AuctionOpenEvent) event);
    }
  }

  public void auctionOpen( AuctionOpenEvent event ) {
    historyStats = 
      (HistoricalDataReport) auction.getReport(HistoricalDataReport.class);

    if ( historyStats == null ) {
      throw new AuctionError(getClass() + " requires a HistoryStatsMarketDataLogger to be configured");
    }
  }
  
  protected void adjustMargin() {

    double highestBid = historyStats.getHighestBidPrice();
    double lowestAsk = historyStats.getLowestAskPrice();
    if ( agent.isBuyer() ) {
      if ( lowestAsk > highestBid && highestBid > 0 ) {
        adjustMargin(targetMargin(highestBid + perterb(highestBid)));
      } else if ( agent.active() && lowestAsk < Double.POSITIVE_INFINITY ) {
        adjustMargin(targetMargin(lowestAsk - perterb(lowestAsk)));
      }
    } else {
      if ( lowestAsk > highestBid && lowestAsk < Double.POSITIVE_INFINITY ) {
        adjustMargin(targetMargin(lowestAsk - perterb(lowestAsk)));
      } else if ( agent.active() && highestBid > 0 ) {
        adjustMargin(targetMargin(highestBid + perterb(highestBid)));
      }
    }
   
  }
  
}