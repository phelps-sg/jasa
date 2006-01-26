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

package uk.ac.liv.auction.core;

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionEventListener;
import uk.ac.liv.auction.event.ShoutPlacedEvent;

/**
 * The interface for expressing the condition of closing an auction.
 *
 * @author Jinzhong Niu
 * @version $Revision$
 *
 */

public class QuiescentDayEndingCondition extends TimingCondition implements
		DayEndingCondition, AuctionEventListener {

  protected boolean shoutsProcessed;
  

  /*
   * @see uk.ac.liv.auction.core.TimingCondition#eval()
   */
  public boolean eval() {
    return isQuiescent();
  }
  
  /**
   * Returns true if no bidding activity occured in the latest auction round.
   */
  private boolean isQuiescent() {
    return !shoutsProcessed || 
              (getAuction().getNumberOfTraders() == 0);
  }

	public void eventOccurred(AuctionEvent event) {
		if ( event instanceof ShoutPlacedEvent ) {
			shoutsProcessed = true;
		}
		
	}
}