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

package uk.ac.liv.auction.heuristic;

import java.util.Iterator;

import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.agent.RandomValuer;
import uk.ac.liv.auction.agent.ValuationPolicy;

import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.AuctionEventListener;

import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.event.AuctionEvent;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class ValuationShocker implements AuctionEventListener {

  protected int shockInterval = 40;
  
  public void eventOccurred( AuctionEvent event ) {
    RoundRobinAuction auction = (RoundRobinAuction) event.getAuction();
    if ( (auction.getAge() % shockInterval) == 0 ) {
      resetValuations(auction);
    }
  }
  
  public void resetValuations( RoundRobinAuction auction ) {
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      AbstractTradingAgent agent = (AbstractTradingAgent) i.next();
      ValuationPolicy policy = agent.getValuationPolicy();
      if ( policy instanceof RandomValuer ) {
        ((RandomValuer) policy).drawRandomValue();
      }
    }
  }
  
  public void endOfDay( Auction auction ) {
    // TODO Auto-generated method stub

  }
  public void auctionClosed( Auction auction ) {
    // TODO Auto-generated method stub

  }
}
