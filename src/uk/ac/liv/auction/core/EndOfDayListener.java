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
 * to receive notification that the current trading period has
 * terminated.
 *
 * @author Steve Phelps
 * @version $Revision$
 */
public interface EndOfDayListener extends AuctionEventListener {

  /**
   * Notify us that a period has ended in the specified auction.
   */
  public void endOfDay( Auction auction );


}
