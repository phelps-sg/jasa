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
	((SimpleProblemForm)(state.evaluator.p_problem.protoCloneSimple())).describe(best_of_run, state, 0, statisticslog,Output.V_NO_GENERAL);
	}

    }
