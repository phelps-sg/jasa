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

package uk.ac.liv.auction.heuristic;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.agent.Strategy;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;

import uk.ac.liv.util.Parameterizable;


/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class FactoryStrategy implements Parameterizable, Strategy {
  
  private StrategyFactory factory;
  
  private Strategy strategy;
  
  public static final String P_FACTORY = "factory";
     
  public void setup( ParameterDatabase parameters, Parameter base ) {
    
    factory = 
      (StrategyFactory) 
        parameters.getInstanceForParameterEq(base.push(P_FACTORY),
                                                null, StrategyFactory.class);
    factory.setup(parameters, base.push(P_FACTORY));
    
    strategy = factory.create();
    
  }
  
  public Shout modifyShout( Shout shout, Auction auction ) {
    return strategy.modifyShout(shout, auction);
  }

  public void endOfRound( Auction auction ) {
    strategy.endOfRound(auction);
  }

  public void setAgent( AbstractTradingAgent agent ) {
    strategy.setAgent(agent);
  }

  public int determineQuantity( Auction auction ) {
    return strategy.determineQuantity(auction);
  }

  public Object protoClone() {
    return strategy.protoClone();
  }

  public void reset() {
    strategy.reset();
  }

}
