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

import ec.util.ParameterDatabase;
import ec.util.Parameter;
import ec.util.MersenneTwisterFast;

public class RandomConstrainedStrategy extends AbstractStrategy {

  double maxMarkup = 50;

  static MersenneTwisterFast randGenerator = new MersenneTwisterFast(System.currentTimeMillis());

  static final String P_MAX_MARKUP = "maxmarkup";

  public RandomConstrainedStrategy() {
    super();
  }

  public RandomConstrainedStrategy( AbstractTraderAgent agent, double maxMarkup ) {
    super(agent);
    this.maxMarkup = maxMarkup;
  }

  public void modifyShout( Shout shout, Auction auction ) {
    shout.setQuantity(agent.determineQuantity(auction));
    double markup = randGenerator.nextDouble() * maxMarkup;
    double price = 0;
    if ( agent.isBuyer() ) {
      price = agent.getPrivateValue() - markup;
    } else {
      price = agent.getPrivateValue() + markup;
    }
    if ( price > 0 ) {
      shout.setPrice(price);
    } else {
      shout.setPrice(0);
    }
    shout.setIsBid(agent.isBuyer());

  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    maxMarkup = parameters.getDoubleWithDefault(base.push(P_MAX_MARKUP), null, 100);
  }

}
