/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

public class KDoubleAuctioneer extends KAuctioneer implements Serializable {

  public KDoubleAuctioneer() {
    this(null, 0);
  }

  public KDoubleAuctioneer( double k ) {
    this(null, k);
  }

  public KDoubleAuctioneer( Auction auction, double k ) {
    this(auction, new UniformPricingPolicy(k));
  }

  public KDoubleAuctioneer( Auction auction, KPricingPolicy policy ) {
    super(auction, policy);
  }

  public KDoubleAuctioneer( Auction auction ) {
    this(auction, 0);
  }

  public void generateQuote() {
    currentQuote = new MarketQuote(askQuote(), bidQuote());
  }

  public void endOfRoundProcessing() {
    clear();
    generateQuote();
  }

  public void endOfAuctionProcessing() {
    // do nothing
  }

  public boolean shoutsVisible() {
    return true;
  }

}