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

package uk.ac.liv.auction.agent;

import uk.ac.liv.util.Prototypeable;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.event.AuctionEventListener;

/**
 * <p>
 * Classes implementing this interface define trading strategies for round-robin
 * traders.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public interface Strategy extends Prototypeable, AuctionEventListener {

  /**
   * Modify the trader's current shout according to the trading strategy being
   * implemented.
   * 
   * @param shout
   *          The shout to be updated
   * @param auction
   *          The auction in which this strategy is being employed
   * @return The new shout, or null if no shout is to be placed.
   */
  public Shout modifyShout( Shout shout, Auction auction );

  public void setAgent( AbstractTradingAgent agent );

  public int determineQuantity( Auction auction );

}