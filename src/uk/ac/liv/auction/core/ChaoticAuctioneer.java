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

import uk.ac.liv.prng.GlobalPRNG;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class ChaoticAuctioneer extends KDoubleAuctioneer {
  
  protected double lambda = 0.0;
  
  protected boolean payAsBid = false;

  public void endOfRoundProcessing() {
    if ( GlobalPRNG.getInstance().uniform(0, 1) <= lambda ) {
      switchPricing();
    }
    super.endOfRoundProcessing();
  }
  
  public void switchPricing() {
    payAsBid = !payAsBid;
    if ( payAsBid ) {
      pricingPolicy.setK(1);
    } else {
      pricingPolicy.setK(0);
    }
  }
}
