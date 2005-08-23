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

package test.uk.ac.liv.auction.agent;

import uk.ac.liv.auction.agent.AbstractStrategy;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class MockStrategy extends AbstractStrategy {

  /**
   * @uml.property name="currentShout"
   */
  protected int currentShout = 0;

  /**
   * @uml.property name="shouts"
   * @uml.associationEnd multiplicity="(0 -1)"
   */
  public Shout[] shouts;

  /**
   * @uml.property name="lastShoutAccepted"
   */
  public boolean lastShoutAccepted;

  public MockStrategy( Shout[] shouts ) {
    this.shouts = shouts;
  }

  public void eventOccurred( AuctionEvent event ) {
    if ( event instanceof RoundClosedEvent ) {
      endOfRound(event.getAuction());
    }
  }

  public void endOfRound( Auction auction ) {
    currentShout++;
  }

  public int determineQuantity( Auction auction ) {
    return shouts[currentShout].getQuantity();
  }

  public boolean modifyShout( Shout.MutableShout shout ) {
    if ( currentShout >= shouts.length ) {
      return false;
    }
    super.modifyShout(shout);
    lastShoutAccepted = agent.lastShoutAccepted();
    Shout current = shouts[currentShout];
    shout.setPrice(current.getPrice());
    shout.setQuantity(current.getQuantity());
    return true;
  }

}
