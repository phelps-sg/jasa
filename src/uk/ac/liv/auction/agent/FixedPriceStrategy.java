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

package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.util.Prototypeable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.io.Serializable;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class FixedPriceStrategy extends FixedQuantityStrategyImpl
                                    implements Serializable, Prototypeable {

  protected double price;

  static final String P_PRICE = "price";

  public FixedPriceStrategy( AbstractTradingAgent agent, double price, int quantity ) {
    super(agent);
    this.price = price;
    this.quantity = quantity;
  }

  public FixedPriceStrategy() {
    super(null);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
    price = parameters.getDoubleWithDefault(base.push(P_PRICE), null, 0);
  }

  public Object protoClone() {
  	Object clonedStrategy;
  	try {
  		clonedStrategy = this.clone();
  	} catch ( CloneNotSupportedException e ) {
  		throw new Error(e);
  	}
    return clonedStrategy;
  }

  public boolean modifyShout( Shout.MutableShout shout ) {
    if ( agent.isBuyer() && price <= agent.getValuation(auction)
           || agent.isSeller() && price >= agent.getValuation(auction) )  {
      shout.setPrice(price);
    } else {
      shout.setPrice(agent.getValuation(auction));
    } 
    
    return super.modifyShout(shout);
  }

  public void endOfRound( Auction auction ) {
    // Do nothing
  }
  
  public void setPrice( double price ) {
    this.price = price;
  }
  
  public double getPrice() {
    return price;
  }

}