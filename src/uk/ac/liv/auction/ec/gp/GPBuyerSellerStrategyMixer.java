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

package uk.ac.liv.auction.ec.gp;

import uk.ac.liv.auction.agent.Strategy;

import uk.ac.liv.auction.ec.gp.func.GPTradingStrategy;

import uk.ac.liv.ec.gp.GPGenericIndividual;

import java.util.Vector;

/**
 * <p>
 * Use this mixer if you want to have one population of buyer strategies
 * and one population of seller strategies.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPBuyerSellerStrategyMixer extends StrategyMixer {

  public GPBuyerSellerStrategyMixer( GPElectricityTradingProblem problem ) {
    super(problem);
  }

  public GPBuyerSellerStrategyMixer() {
    super();
  }

  public Strategy getStrategy( int i, Vector[] group ) {
    GPGenericIndividual strategy = null;
    int numSellers = problem.getNumSellers();
    if ( i < problem.getNumSellers() ) {
      strategy = (GPGenericIndividual) group[subpopOffset()].get(i);
    } else {
      strategy = (GPGenericIndividual) group[subpopOffset()+1].get(i-numSellers);
    }
    strategy.setGPContext(problem.getGPContext());
    return (GPTradingStrategy) strategy.getGPObject();
  }

}