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

package uk.ac.liv.auction.core;


/**
 * Classes implementing this interface can subscribe to an auction
 * to receive notification when the auction is closed.
 *
 * @author Steve Phelps
 * @version $Revision$
 */
public interface AuctionClosedListener extends AuctionEventListener {

  /**
   * Notify us that the specified auction is closed.
   *
   * @param auction The auction in which has closed.
   */
  public void auctionClosed( Auction auction );


}