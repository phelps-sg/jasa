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

import java.util.Iterator;

/**
 * An implementation of the mechanism described in 
 * 
 * "A Dominant Strategy Double Auction"
 * R. Preston McAfee
 * Journal of Economic Theory Vol 56 pages 434-450
 * 1992
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public class McAfeeClearingHouseAuctioneer extends ClearingHouseAuctioneer {

  protected ZeroCreditAccount account;
  
  public McAfeeClearingHouseAuctioneer() {
    this(null);    
  }

  public McAfeeClearingHouseAuctioneer( Auction auction ) {
    super(auction);
    account = new ZeroCreditAccount(this);
  }
  
  public void clear() {
    boolean efficientClearing;
    double a0 = -1, a1 = -1;
    double b0 = -1, b1 = -1;
    double p0 = -1;    
    if ( shoutEngine.noMatchedShouts() ) {
      return;
    }
    if ( shoutEngine.getHighestUnmatchedBid() == null 
          || shoutEngine.getLowestUnmatchedAsk() == null ) {
      efficientClearing = false;
    } else {
      a0 = shoutEngine.getHighestUnmatchedBid().getPrice();
      b0 = shoutEngine.getLowestUnmatchedAsk().getPrice();      
      p0 = (a0 + b0) / 2;
      efficientClearing = shoutEngine.getHighestMatchedAsk().getPrice() <= p0 && p0 <= shoutEngine.getLowestMatchedBid().getPrice();
    }
    if ( ! efficientClearing ) {
      a1 = shoutEngine.getLowestMatchedBid().getPrice();
      b1 = shoutEngine.getHighestMatchedAsk().getPrice();      
    }
    Iterator matchedShouts = shoutEngine.getMatchedShouts().iterator();
    while ( matchedShouts.hasNext() ) {
      Shout bid = (Shout) matchedShouts.next();
      Shout ask = (Shout) matchedShouts.next();      
      if ( efficientClearing ) {       
        clear(ask, bid, p0);
      } else {
        if ( bid.getPrice() > a1 ) {
          clear(ask, bid, a1, b1, ask.getQuantity());
        }
      }
    }
  }


  public Account getAccount() {
    return account;
  }
  
  public void reset() {
    super.reset();
    account.setFunds(0);
  }


}
