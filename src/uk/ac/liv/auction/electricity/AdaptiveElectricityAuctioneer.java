/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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

package uk.ac.liv.auction.electricity;


import uk.ac.liv.auction.core.*;

import uk.ac.liv.ai.learning.*;

/**
 * @author Steve Phelps
 */

public class AdaptiveElectricityAuctioneer extends DiscrimPriceCDAAuctioneer {

  StimuliResponseLearner learner;

  static final int K = 100;
  static final double R = 0.2;
  static final double E = 0.2;
  static final double S1 = 100.0;

  static final long MIN_PRICE = 0;
  static final long MAX_PRICE = 200;

  public AdaptiveElectricityAuctioneer( Auction auction ) {
    super(auction, 0.0);
    initialise();
  }

  public void initialise() {
    learner = new NPTRothErevLearner(K, R, E, S1);
  }

  public synchronized void clear() {
    setK( ((double) learner.act()) / (double) K );
    super.clear();
    ElectricityStats stats =
      new ElectricityStats(MIN_PRICE, MAX_PRICE, (RoundRobinAuction) auction);
    if ( !Double.isInfinite(stats.eA) && !Double.isNaN(stats.eA) ) {
      learner.reward(stats.eA);
    } else {
      learner.reward(0);
    }
  }


}