/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

import java.io.*;

import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.util.*;

import uk.ac.liv.ec.coevolve.*;

import uk.ac.liv.util.*;
import uk.ac.liv.util.io.*;

import uk.ac.liv.ec.gp.*;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.electricity.*;
import uk.ac.liv.auction.stats.*;
import uk.ac.liv.auction.ec.gp.func.*;

/**
 * Scenario in which we co-evolve trading strategies using GP.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPCoEvolveStrategyProblem extends GPElectricityTradingProblem
                                       implements CoEvolutionaryProblem {

  protected CummulativeStatCounter[] strategyFitnesses;

  protected void initialiseFitnesses() {
    strategyFitnesses = new CummulativeStatCounter[numTraders];
    for( int s=0; s<numTraders; s++ ) {
      strategyFitnesses[s] = new CummulativeStatCounter("strategyFitness");
    }
  }

  protected void resetFitnesses() {
    for( int s=0; s<numTraders; s++ ) {
      strategyFitnesses[s].reset();
    }
  }

  protected void setFitnesses( Vector[] group ) {
    setStrategyFitnesses(group);
  }

  protected void computeFitnesses() {
    computeStrategyFitnesses();
  }

  protected void setStrategyFitnesses( Vector[] group ) {
    for( int s=0; s<numTraders; s++ ) {
      GPTradingStrategy strategy = (GPTradingStrategy) getStrategy(s, group);
      KozaFitness fitness = (KozaFitness) strategy.fitness;
      fitness.setStandardizedFitness(context.getState(),
                                      (float) strategyFitnesses[s].getMean());
    }
  }


  protected void computeStrategyFitnesses() {

    Iterator traders = allTraders.iterator();

    for( int i=0; traders.hasNext(); i++ ) {

      GPElectricityTrader trader = (GPElectricityTrader) traders.next();
      double profits = trader.getProfits();
      double fitness = Float.MAX_VALUE;

      if ( ! (Double.isNaN(profits)
              || ((GPTradingStrategy) trader.getStrategy()).misbehaved()) ) {
        fitness = 200000 - profits;
      }
      if ( fitness < 0 ) {
        System.err.println("WARNING: trader " + trader + " had negative fitness!");
        fitness = 0;
      }

      strategyFitnesses[i].newData(fitness );
      if ( generateCSV ) {
        statsOut.newData(profits);
      }

    }
  }


  protected void preAuctionProcessing() {
    super.preAuctionProcessing();
  }


  protected void postAuctionProcessing() {
    super.postAuctionProcessing();
  }

  public Object protoClone() throws CloneNotSupportedException {

    GPCoEvolveStrategyProblem myobj = (GPCoEvolveStrategyProblem) super.protoClone();
    //TODO?
    return myobj;
  }

  public int getFirstStrategySubpop() {
    return 0;
  }

}

