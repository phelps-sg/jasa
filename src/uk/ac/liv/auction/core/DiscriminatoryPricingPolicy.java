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

/**
 * @author Steve Phelps
 */

public class DiscriminatoryPricingPolicy extends KPricingPolicy {

  public DiscriminatoryPricingPolicy() {
    this(0);
  }

  public DiscriminatoryPricingPolicy( double k ) {
    super(k);
  }

  public double determineClearingPrice( Shout bid, Shout ask,
                                         MarketQuote clearingQuote ) {
    return kInterval(ask.getPrice(), bid.getPrice());
  }


}
