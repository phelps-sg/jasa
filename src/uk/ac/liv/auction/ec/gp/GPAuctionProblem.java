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
import ec.simple.*;

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
 * Use GP to evolve auctioneers against non-GP strategies.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPAuctionProblem extends GPCoEvolveAuctionProblem
                                implements SimpleProblemForm {

  Strategy strategies[];

  Vector[] group;

  static final String P_STRATEGY = "strategy";


  public void setup( EvolutionState state, Parameter base ) {

    super.setup(state, base);

    int numStrategies = numSellers + numBuyers;
    strategies = new Strategy[numStrategies];
    for( int i=0; i<numStrategies; i++ ) {
      Parameter strategyBase = base.push(P_STRATEGY+"."+i);
      Strategy s = (Strategy)
        state.parameters.getInstanceForParameter(strategyBase, null,
                                                  Strategy.class);
      ((Parameterizable) s).setup(state.parameters, strategyBase);
      strategies[i] = s;      
    }
    group = new Vector[1];
    group[0] = new Vector(1);
    group[0].add(0, null);
  }


  public void evaluate( EvolutionState state, Individual individual,
                        int thread ) {
    group[0].set(0, individual);
    evaluate(state, group, thread);
  }


  protected Strategy getStrategy( int i, Vector[] group ) {
    return strategies[i];
  }


  protected void computeStrategyFitnesses() {
  }


  protected void setStrategyFitnesses( Vector[] group ) {
  }


}