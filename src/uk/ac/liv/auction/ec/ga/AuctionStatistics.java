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

package uk.ac.liv.auction.ec.ga;
import ec.*;
import ec.util.*;
import ec.simple.*;


public class AuctionStatistics extends SimpleStatistics
    {

    public void postEvaluationStatistics(EvolutionState state) {
      super.postEvaluationStatistics(state);
      Individual[] individuals = state.population.subpops[0].individuals;
      for( int i=0; i<individuals.length; i++ ) {
        ((SimpleProblemForm)(state.evaluator.p_problem.protoCloneSimple())).describe(individuals[i], state, 0, statisticslog,Output.V_NO_GENERAL);
      }
    }
    public void finalStatistics(final EvolutionState state, final int result)
	{
	// print out the other statistics
	super.finalStatistics(state,result);

	// we have only one population, so this is kosher
	((SimpleProblemForm)(state.evaluator.p_problem.protoCloneSimple())).describe(best_of_run[0], state, 0, statisticslog,Output.V_NO_GENERAL);
	}

    }
