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
package test.uk.ac.liv.auction.agent;

import uk.ac.liv.auction.agent.AbstractStrategy;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class MockStrategy extends AbstractStrategy {

  protected int currentShout = 0;
  
  public Shout[] shouts;
  
  public boolean lastShoutAccepted;
 
  public MockStrategy( Shout[] shouts ) {
    this.shouts = shouts;
  }
  
  public void endOfRound ( Auction auction) {
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
