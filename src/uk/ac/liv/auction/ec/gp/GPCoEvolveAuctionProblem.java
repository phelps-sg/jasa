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
import ec.multiobjective.MultiObjectiveFitness;

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

  boolean auctioneerMisbehaved;

  protected void postEvaluationStats() {
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
    auctioneerMisbehaved = false;
    super.resetFitnesses();
  }


  protected void initialiseFitnesses() {
    super.initialiseFitnesses();
  }


  protected void setFitnesses( Vector[] group ) {
    setAuctioneerFitness( (GPIndividual) group[0].get(0));
    super.setFitnesses(group);
  }


  protected void computeFitnesses() {
    if ( ((GPAuctioneer) auctioneer).misbehaved() ) {
      auctioneerMisbehaved = true;
    }
    super.computeFitnesses();
  }


  protected void setAuctioneerFitness( GPIndividual auctioneer ) {

    MultiObjectiveFitness fitness = (MultiObjectiveFitness) auctioneer.fitness;

    if ( auctioneerMisbehaved ) {
      for( int i=0; i<3; i++ ) {
        fitness.multifitness[i] = 0;
      }
      return;
    }

    fitness.multifitness[0] = efficiencyFitness(efficiency.getMean());
    fitness.multifitness[1] = mpFitness(buyerMP.getMean());
    fitness.multifitness[2] = mpFitness(sellerMP.getMean());

    auctioneer.evaluated = true;
  }


  protected float mpFitness( double mp ) {
    return (float) (1 / (Math.abs(mp) + 1));
  }

  protected float efficiencyFitness( double eA ) {
    if ( eA > 1 && Math.abs(eA) < 2 ) {
      return 1f;
    } else {
      return (float) eA;
    }
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
    if ( i < numSellers ) {
      return (GPTradingStrategy) group[1].get(i);
    } else {
      return (GPTradingStrategy) group[2].get(i-numSellers);
    }
  }



  public Object protoClone() throws CloneNotSupportedException {

    GPCoEvolveAuctionProblem myobj = (GPCoEvolveAuctionProblem) super.protoClone();
    //TODO?
    return myobj;
  }

}

