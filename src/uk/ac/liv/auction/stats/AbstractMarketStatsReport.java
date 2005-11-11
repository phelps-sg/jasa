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

import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.event.AuctionClosedEvent;
import uk.ac.liv.auction.event.AuctionEvent;

/**
 * A report that performs additional calculations at the end of an auction
 * before producing its results.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public abstract class AbstractMarketStatsReport extends AbstractAuctionReport {

  public AbstractMarketStatsReport( RandomRobinAuction auction ) {
    super(auction);
  }
  
  public AbstractMarketStatsReport() {
    super();
  }
  
  public void eventOccurred( AuctionEvent event ) {
    if ( event instanceof AuctionClosedEvent ) {
      calculate();
      ReportVariableBoard.getInstance().reportValues(getVariables(), event);
    }
  }
  
  /**
   * Perform final calculations at the end of the auction.   
   */
  public abstract void calculate();

}
