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


package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.*;

import uk.ac.liv.util.DiscreteProbabilityDistribution;

import java.util.*;

/**
 * A class representing a mixed strategy.
 * A mixed strategy is a strategy in which we play a number of pure strategies
 * with different probabilities on each auction round.
 *
 * @author Steve Phelps
 */

public class MixedStrategy extends AbstractStrategy {

  protected DiscreteProbabilityDistribution probabilities;

  protected ArrayList pureStrategies;

  protected Strategy currentStrategy;


  public MixedStrategy( DiscreteProbabilityDistribution probabilities ) {
    pureStrategies = new ArrayList(10);
    this.probabilities = probabilities;
    currentStrategy = null;
  }


  public void addPureStrategy( Strategy newPureStrategy ) {
    pureStrategies.add(newPureStrategy);
  }


  public void addPureStrategies( Collection pureStrategies ) {
    pureStrategies.addAll(pureStrategies);
  }


  public void setProbabilityDistribution( DiscreteProbabilityDistribution probabilities ) {
    this.probabilities = probabilities;
  }


  public void modifyShout( Shout shout, Auction auction ) {

    currentStrategy =
        (Strategy) pureStrategies.get(probabilities.generateRandomEvent());

    currentStrategy.modifyShout(shout, auction);
  }


  public void endOfRound( Auction auction ) {
    currentStrategy.endOfRound(auction);
  }


}


