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


import java.util.*;

import uk.ac.liv.ec.gp.GPGenericIndividual;
import ec.simple.SimpleFitness;


import uk.ac.liv.ec.coevolve.*;


import uk.ac.liv.auction.agent.AbstractTradingAgent;

import uk.ac.liv.auction.ec.gp.func.*;

/**
 * Scenario in which we co-evolve trading strategies using GP.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPCoEvolveStrategyProblem extends GPTradingProblem
                                       implements CoEvolutionaryProblem {


  protected void computeFitnesses( Vector[] group ) {
    computeStrategyFitnesses(group);
  }


  protected void computeStrategyFitnesses( Vector[] group ) {

    for( int i=0; i<numAgents; i++ ) {

      AbstractTradingAgent trader = agents[i];
      double profits = trader.getProfits();
      double equilibriumProfits = surplusLogger.getEquilibriumProfits(trader);
      double payoff = profits / equilibriumProfits;
            
      GPTradingStrategy strategy = (GPTradingStrategy) trader.getStrategy();
      GPGenericIndividual individual = strategy.getGPIndividual();
      SimpleFitness f = (SimpleFitness) individual.fitness;
            
      if ( !individual.misbehaved() && payoff > 0 && equilibriumProfits > 0 ) { 
      	if ( payoff > 10 ) {
      		System.out.println("Warning: large payoff of " + payoff);
      	}
	      f.setFitness(context.getState(), (float) payoff, false);	     
      } else {
      	f.setFitness(context.getState(), 0, false);
      }
      
      individual.evaluated = true;            
    }
  }

  public int getFirstStrategySubpop() {
    return 0;
  }

}

