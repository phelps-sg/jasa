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

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.core.RoundClosedListener;
import uk.ac.liv.auction.core.EndOfDayListener;
import uk.ac.liv.auction.core.AuctionClosedListener;

/**
 * The interface used to log market-data on an auction as it progresses.
 * Different implementations of this interface can be used to record
 * or process this data in different ways, for example to log it to
 * CSV files or to keep cummulative statistics on each variable, or some
 * combination thereof.
 *
 * @author Steve Phelps
 * @version $Revision$
 */
public interface MarketDataLogger
    extends RoundClosedListener, EndOfDayListener, AuctionClosedListener  {

  /**
   * Record the market quote.
   *
   * @param time  The time of this event in unspecified units.
   * @param quote The market-quote data
   */
  public void updateQuoteLog( int time, MarketQuote quote );

  /**
   * Record a transaction that has occured in the market.
   *
   * @param time  The time of this event in unspecified units.
   * @param ask   The shout of the seller that gave rise to this transaction
   * @param price The actual price of the transaction that took place.
   */
  public void updateTransPriceLog( int time, Shout ask, Shout bid, double price,
                                    int quantity );

  /**
   * Record an individual shout that occured in the auction.
   *
   * @param time  The time of this event in unspecified units.
   * @param shout The shout that was placed in the auction.
   */
  public void updateShoutLog( int time, Shout shout );

  /**
   * Generate a report on the market data.  Implementing classes
   * may choose to do nothing for this method.
   */
  public void finalReport();

  public void setAuction( RoundRobinAuction auction );

}