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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * An auctioneer for a k-double-auction with continuous clearing and
 * no order queuing.  Every time an offer is cleared any pending offers
 * are discarded.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class ContinuousDoubleAuctioneerNoQueue
    extends ContinuousDoubleAuctioneer implements Serializable {

  public void clear() {    
    clearingQuote = new MarketQuote(askQuote(), bidQuote());
    List shouts = shoutEngine.getMatchedShouts();
    if ( shouts.size() > 0 ) {
      shoutEngine.reset();
    }
    Iterator i = shouts.iterator();
    while (i.hasNext()) {
      Shout bid = (Shout) i.next();
      Shout ask = (Shout) i.next();
      double price = determineClearingPrice(bid, ask);
      clear(ask, bid, price);
    }
  }

}
