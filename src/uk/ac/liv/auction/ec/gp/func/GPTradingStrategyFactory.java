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

package uk.ac.liv.auction.ec.gp.func;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.auction.agent.Strategy;
import uk.ac.liv.auction.heuristic.StrategyFactory;

import uk.ac.liv.ec.gp.GPGenericIndividualFactory;
import uk.ac.liv.ec.gp.GPGenericIndividual;

import uk.ac.liv.util.Parameterizable;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPTradingStrategyFactory extends StrategyFactory implements Parameterizable {

  private GPGenericIndividualFactory individualFactory;
  
  public static final String P_INDIVIDUALFACTORY = "individualfactory";
  
  public void setup( ParameterDatabase parameters, Parameter base ) {
    individualFactory = (GPGenericIndividualFactory)
      parameters.getInstanceForParameterEq(base.push(P_INDIVIDUALFACTORY),
                                            null,
                                            GPGenericIndividualFactory.class);
    individualFactory.setup(parameters, base.push(P_INDIVIDUALFACTORY));
  }

  
  public Strategy create() {        
    
    GPGenericIndividual individual = individualFactory.create();
    
    return (GPTradingStrategy) individual.getGPObject();    
  }

}
