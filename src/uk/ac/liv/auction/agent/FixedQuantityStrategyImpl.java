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

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.util.Parameterizable;

/**
 * @author Steve Phelps
 */

public class FixedQuantityStrategyImpl extends AbstractStrategy
    implements FixedQuantityStrategy, Parameterizable {

  int quantity = 1;

  static final String P_QUANTITY = "quantity";

  public FixedQuantityStrategyImpl( AbstractTraderAgent agent ) {
    super(agent);
  }

  public FixedQuantityStrategyImpl() {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    quantity = parameters.getIntWithDefault(base.push(P_QUANTITY), null, quantity);
  }

  public void setQuantity( int quantity ) {
    this.quantity = 1;
  }

}