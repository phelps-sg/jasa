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

import uk.ac.liv.auction.agent.RoundRobinTrader;

import uk.ac.liv.util.Seedable;

import java.util.ArrayList;
import java.util.Iterator;

import ec.util.MersenneTwisterFast;   // Fast random number generator

/**
 * <p>
 * A round-robin auction in which the ordering of traders in
 * randomized for each round.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class RandomRobinAuction extends RoundRobinAuction
                                  implements Seedable {

  MersenneTwisterFast randGenerator = new MersenneTwisterFast();

  public RandomRobinAuction() {
    super();
  }

  public RandomRobinAuction( String name ) {
    super(name);
  }

  public void requestShouts() {
    Object[] candidates = activeTraders.toArray();
    int numCandidates = candidates.length;
    for( int i=0; i<numTraders; i++ ) {
      int choice = randGenerator.nextInt(numCandidates);
      RoundRobinTrader trader = (RoundRobinTrader) candidates[choice];
      candidates[choice] = candidates[numCandidates-1];
      numCandidates--;
      trader.requestShout(this);
    }
  }
  
  public void setSeed( long seed ) {
    randGenerator.setSeed(seed);
  }

}