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

package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;


public abstract class AdaptiveStrategy extends FixedQuantityStrategyImpl {

  boolean firstShout = true;

  public AdaptiveStrategy( AbstractTraderAgent agent ) {
    super(agent);
  }

  public AdaptiveStrategy() {
    super();
  }

  public void modifyShout( Shout shout, Auction auction ) {

    super.modifyShout(shout, auction);

    if ( firstShout ) {
      firstShout = false;
    } else {
      calculateReward(auction);
    }

    // Generate an action from the learning algorithm
    int action = act();

    // Now turn the action into a price
    double price;
    if ( agent.isSeller() ) {
      price = agent.getPrivateValue() + action;
    } else {
      price = agent.getPrivateValue() - action;
    }
    /* TODO
    if ( price < funds ) {
      price = funds;
    } */
    if ( price < 0 ) {
      price = 0;
    }
    shout.setPrice(price);
    shout.setQuantity(quantity);
  }

  public abstract int act();

  public abstract void calculateReward( Auction auction );

}