/*
 * JASA Java Auction Simulator API
 * Copyright (C) Steve Phelps
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

import java.util.List;
import java.util.Iterator;

import uk.ac.liv.util.Debug;

/**
 * @author Steve Phelps
 */

public class DiscrimPriceCDAAuctioneer extends ContinuousDoubleAuctioneer {

  public DiscrimPriceCDAAuctioneer( Auction auction, double k ) {
    super(auction, k);
  }

  public synchronized void clear() {
    List shouts = shoutEngine.getMatchedShouts();
    Iterator i = shouts.iterator();
    while ( i.hasNext() ) {
      Shout bid = (Shout) i.next();  Debug.assert( bid.isBid() );
      Shout ask = (Shout) i.next();  Debug.assert( ask.isAsk() );
      if ( ! ( bid.getPrice() >= ask.getPrice()) ) {
        System.out.println("bid = " + bid);
        System.out.println("ask = " + ask);
        Debug.assert( bid.getPrice() >= ask.getPrice() );
      }
      double price = (k * bid.getPrice() + (1.0 - k) * ask.getPrice());
      auction.clear(ask, bid.getAgent(), ask.getAgent(), price, ask.getQuantity());
    }
  }

}