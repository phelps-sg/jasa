/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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
import uk.ac.liv.auction.event.AuctionEvent;

import uk.ac.liv.util.Prototypeable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.io.Serializable;

/**
 * This strategy decorates a component strategy by bidding a fixed
 * proportional markup over the price specified by the underlying component
 * strategy.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class MarkupStrategyDecorator extends FixedQuantityStrategyImpl
                                    implements Serializable, Prototypeable {

  /**
   * The component strategy to decorate.
   */
  protected Strategy subStrategy;
  
  /**
   * The proportional markup on the sub strategy.
   */
  protected double markup;
  
  public static final String P_SUBSTRATEGY = "substrategy";
  public static final String P_MARKUP = "markup";

  public MarkupStrategyDecorator() {
    super(null);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
    markup = parameters.getDoubleWithDefault(base.push(P_MARKUP), null, 0);
    subStrategy = (Strategy)
      parameters.getInstanceForParameter(base.push(P_SUBSTRATEGY), null, 
          										Strategy.class);
  }

  public boolean modifyShout( Shout.MutableShout shout ) {
    assert agent.equals(((AbstractStrategy) subStrategy).getAgent());
    double delta;
    Shout strategicShout = subStrategy.modifyShout(shout, auction);
    double strategicPrice = strategicShout.getPrice();
    if ( strategicShout != null ) {
      if ( agent.isSeller() ) {
        delta = markup * strategicPrice;
      } else {
        delta = -markup * strategicPrice;
      }
      shout.setPrice(strategicPrice + delta);
      shout.setQuantity(quantity);
      if ( shout.getPrice() < 0 ) {
        shout.setPrice(0);
      }
      return super.modifyShout(shout);
    } else {
      return false;
    }
  }
  
  public void endOfRound( Auction auction ) {
    
  }
 
  public void eventOccurred( AuctionEvent event ) {
    super.eventOccurred(event);
    subStrategy.eventOccurred(event);
  }

  public void setAgent( AbstractTradingAgent agent ) {
    super.setAgent(agent);
    subStrategy.setAgent(agent);
  }
  
  public Object protoClone() {
  	Object clonedStrategy;
  	try {
  		clonedStrategy = this.clone();
  		((MarkupStrategyDecorator) clonedStrategy).subStrategy = 
  		  (Strategy) subStrategy.protoClone();
  	} catch ( CloneNotSupportedException e ) {
  		throw new Error(e);
  	}
    return clonedStrategy;
  }

  
}