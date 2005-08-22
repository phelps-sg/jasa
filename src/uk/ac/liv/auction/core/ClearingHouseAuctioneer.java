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

/**
 * An auctioneer for a k-double-auction.
 * The clearing operation is performed at the end of every round.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class ClearingHouseAuctioneer extends AbstractAuctioneer implements Serializable {

  public ClearingHouseAuctioneer() {
    this(null);
  }

  public ClearingHouseAuctioneer( Auction auction ) {
    super(auction);
    setPricingPolicy(new UniformPricingPolicy(0));
  }

  public void generateQuote() {
    currentQuote = new MarketQuote(askQuote(), bidQuote());
  }

  public void endOfRoundProcessing() {
    generateQuote();
    clear();
  }

  public void endOfAuctionProcessing() {
    // do nothing
  }

  public boolean shoutsVisible() {
    return true;
  }

}