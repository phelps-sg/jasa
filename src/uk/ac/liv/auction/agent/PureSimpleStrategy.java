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

import uk.ac.liv.util.Parameterizable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author Steve Phelps
 * @version 1.0
 */

public class PureSimpleStrategy extends AbstractStrategy {

  double delta;

  int quantity;

  static final String P_DELTA = "delta";
  static final String P_QUANTITY = "quantity";

  static final double DEFAULT_DELTA = 0;
  static final int DEFAULT_QUANTITY = 1;

  public PureSimpleStrategy( AbstractTraderAgent agent, double margin, int quantity ) {
    super(agent);
    if ( agent.isSeller() ) {
      delta = margin;
    } else {
      delta = -margin;
    }
    this.quantity = quantity;
  }

  public PureSimpleStrategy() {
    super(null);
    delta = DEFAULT_DELTA;
    quantity = DEFAULT_QUANTITY;
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    delta = parameters.getDoubleWithDefault(base.push(P_DELTA), null, DEFAULT_DELTA);
    quantity = parameters.getIntWithDefault(base.push(P_QUANTITY), null, DEFAULT_QUANTITY);
  }

  public void modifyShout( Shout shout, Auction auction ) {
    super.modifyShout(shout, auction);
    shout.setPrice(agent.getPrivateValue() + delta);
    shout.setQuantity(quantity);
    if ( shout.getPrice() < 0 ) {
      shout.setPrice(0);
    }
  }

}