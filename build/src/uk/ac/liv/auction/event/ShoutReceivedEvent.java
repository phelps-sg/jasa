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

package uk.ac.liv.auction.event;

import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;

/**
 * An event that is fired every time a shout is received in an auction
 * (may not be accepted eventually), in contrast to ShoutPlacedEvent, which represents
 * a shout is received and accepted.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class ShoutReceivedEvent extends AuctionEvent {

  /**
   * The shout that led to this event.
   * 
   * @uml.property name="shout"
   * @uml.associationEnd multiplicity="(1 1)"
   */
  protected Shout shout;

  public ShoutReceivedEvent( Auction auction, int time, Shout shout ) {
    super(auction, time);
    this.shout = shout;
  }

  /**
   * @uml.property name="shout"
   */
  public Shout getShout() {
    return shout;
  }
}
