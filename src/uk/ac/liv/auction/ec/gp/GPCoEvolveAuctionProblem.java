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
 * @author Steve Phelps
 */

public class GPCoEvolveAuctionProblem extends GPCoEvolveStrategyProblem {

  protected CummulativeStatCounter auctioneerFitnesses;


  protected void postEvaluationStats() {
    // Save a copy of the stats for posterity

    try {
      GPAuctioneer auctioneer = (GPAuctioneer) auction.getAuctioneer();
      auctioneer.setMarketStats((CummulativeStatCounter) efficiency.clone());
      auctioneer.setLogStats(logger.newCopy());
    } catch ( CloneNotSupportedException e ) {
      throw new Error(e.getMessage());
    }

    super.postEvaluationStats();
  }


  protected void resetFitnesses() {
    auctioneerFitnesses.reset();
    super.resetFitnesses();
  }


  protected void initialiseFitnesses() {
    auctioneerFitnesses = new CummulativeStatCounter("auctioneerFitness");
    super.initialiseFitnesses();
  }


  protected void setFitnesses( Vector[] group ) {
    setAuctioneerFitness( (GPIndividual) group[0].get(0));
    super.setFitnesses(group);
  }


  protected void computeFitnesses() {
    computeAuctioneerFitness((GPAuctioneer) auctioneer);
    super.computeFitnesses();
  }


  protected void setAuctioneerFitness( GPIndividual auctioneer ) {
    KozaFitness fitness = (KozaFitness) auctioneer.fitness;
    fitness.setStandardizedFitness(context.getState(),
                                    (float) auctioneerFitnesses.getMean());
  }


  protected Auctioneer assignAuctioneer( Vector[] group ) {
    Auctioneer auctioneer = null;
    auctioneer = (GPAuctioneer) group[0].get(0);
    ((GPAuctioneer) auctioneer).setGPContext(context);
    return auctioneer;
  }


  protected LinkedList initialiseTraders( Vector[] group ) {

    LinkedList strategies = super.initialiseTraders(group);

    ((GPAuctioneer) auctioneer).setStrategies(strategies);

    return strategies;
  }


  protected GPTradingStrategy getStrategy( int i, Vector[] group ) {
    return (GPTradingStrategy) group[i+1].get(0);
  }


  protected double computeAuctioneerFitness( GPAuctioneer auctioneer ) {

    if ( verbose && stats.eA > 100 ) {
      System.err.println("eA > 100% !!");
      System.err.println(stats);
    }

    double fitness = Float.MAX_VALUE;

    if ( ! (auctioneer.misbehaved() || Double.isNaN(stats.eA)) ) {
      fitness = 1-(stats.eA/100);
    }

    auctioneerFitnesses.newData(fitness);

    return fitness;
  }


  public Object protoClone() throws CloneNotSupportedException {

    GPCoEvolveAuctionProblem myobj = (GPCoEvolveAuctionProblem) super.protoClone();
    //TODO?
    return myobj;
  }

}

