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

import java.util.Iterator;

import ec.EvolutionState;
import ec.Individual;

import ec.gp.GPProblem;

import ec.simple.SimpleProblemForm;
import ec.simple.SimpleFitness;

import ec.util.Parameter;
import ec.util.Output;

import uk.ac.liv.util.CummulativeDistribution;
import uk.ac.liv.util.Distribution;

import uk.ac.liv.ec.gp.GPContext;
import uk.ac.liv.ec.gp.GPGenericIndividual;

import uk.ac.liv.auction.ec.gp.func.GPTradingStrategy;

import uk.ac.liv.auction.heuristic.*;

import org.apache.log4j.Logger;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPHeuristicTradingProblem extends GPProblem implements
    SimpleProblemForm {

  protected GPHeuristicPayoffCalculator payoffCalculator;

  protected GPContext context = new GPContext();

  public static final String P_CALCULATOR = "heuristic";

  static Logger logger = Logger.getLogger(GPHeuristicTradingProblem.class);

  public void setup( EvolutionState state, Parameter base ) {

    super.setup(state, base);

    payoffCalculator = (GPHeuristicPayoffCalculator) state.parameters
        .getInstanceForParameterEq(base.push(P_CALCULATOR), null,
            GPHeuristicPayoffCalculator.class);
    payoffCalculator.setup(state.parameters, base.push(P_CALCULATOR));

  }

  public void evaluate( EvolutionState state, Individual individual, int thread ) {

    context.setState(state);
    context.setThread(thread);
    context.setStack(stack);
    context.setProblem(this);

    ((GPGenericIndividual) individual).setGPContext(context);

    evaluate((GPGenericIndividual) individual);
  }

  protected void evaluate( GPGenericIndividual individual ) {

    individual.printIndividualForHumans(context.getState(), 0,
        Output.V_NO_GENERAL);

    GPTradingStrategy gpStrategy = (GPTradingStrategy) individual.getGPObject();

    payoffCalculator.reset();
    payoffCalculator.setGPStrategy(gpStrategy);

    payoffCalculator.computePayoffMatrix();

    CompressedPayoffMatrix payoffMatrix = 
      payoffCalculator.getCompressedPayoffMatrix();

    int gpStrategyIndex = payoffCalculator.getGPStrategyIndex();
    CummulativeDistribution payoff = new CummulativeDistribution("gp payoff");
    Iterator i = payoffMatrix.compressedEntryIterator();
    while ( i.hasNext() ) {
      CompressedPayoffMatrix.Entry entry = (CompressedPayoffMatrix.Entry) i.next();
      if ( entry.getNumAgents(gpStrategyIndex) > 0 ) {
        double[] payoffs = payoffMatrix.getCompressedPayoffs(entry);
        payoff.newData(payoffs[gpStrategyIndex]);
      }
    }

    payoff.log();

    computeFitness(individual, payoff);
  }

  protected void computeFitness( GPGenericIndividual individual, 
      						Distribution payoff ) {

    float fitness = (float) payoff.getMean();

    SimpleFitness f = (SimpleFitness) individual.fitness;

    if ( !payoffCalculator.gpStrategyMisbehaved() && !individual.misbehaved()
        && !Float.isInfinite(fitness) && fitness > 0 && payoff.getMin() >= 0 ) {
      if ( fitness > 1000 ) {
        logger.warn("Large fitness " + payoff);
      }
      f.setFitness(context.getState(), fitness, false);
    } else {
      logger.info("Individual misbehaved.");
      f.setFitness(context.getState(), 0, false);
    }

    individual.evaluated = true;
  }

}