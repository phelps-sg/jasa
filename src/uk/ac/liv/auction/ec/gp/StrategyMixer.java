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

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.agent.Strategy;

import uk.ac.liv.util.Parameterizable;

import java.util.Vector;

/**
 * Implementations of this interface specify mappings between
 * agents and strategies for GPElectricityTradingProblems.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class StrategyMixer implements Parameterizable {

  protected GPTradingProblem problem;


  public StrategyMixer( GPTradingProblem problem ) {
    this.problem = problem;
  }

  public StrategyMixer() {
  }

  public void setProblem( GPTradingProblem problem ) {
    this.problem = problem;

  }


  public void setup( ParameterDatabase parameters, Parameter base ) {
  }

  public int subpopOffset() {
    return problem.getFirstStrategySubpop();
  }

  public abstract Strategy getStrategy( int i, Vector[] group );

}

