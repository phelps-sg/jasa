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

import java.io.Serializable;

/**
 * A pricing policy in which we set the transaction price in the
 * interval between the ask quote and the bid quote as determined by
 * the parameter k.  The pricing policy is uniform in the sense that individual
 * bid and ask prices are ignored, thus all agents performing transactions
 * in the clearing operation will pay the same price.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class UniformPricingPolicy extends KPricingPolicy
                                   implements Serializable {

  public UniformPricingPolicy() {
    this(0);
  }

  public UniformPricingPolicy( double k ) {
    super(k);
  }

  public double determineClearingPrice( Shout bid, Shout ask,
                                         MarketQuote clearingQuote ) {
    return kInterval(clearingQuote.getAsk(), clearingQuote.getBid());
  }


}