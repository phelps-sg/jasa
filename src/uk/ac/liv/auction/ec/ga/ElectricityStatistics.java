package uk.ac.liv.auction.ec.ga;

import ec.simple.*;
import ec.*;
import ec.util.Output;

public class ElectricityStatistics extends SimpleStatistics {

    public void finalStatistics(final EvolutionState state, final int result)
	{
	// print out the other statistics
	super.finalStatistics(state,result);

	// we have only one population, so this is kosher
	((SimpleProblemForm)(state.evaluator.p_problem.protoCloneSimple())).describe(best_of_run[0], state, 0, statisticslog,Output.V_NO_GENERAL);
      }

  public void postEvaluationStatistics( EvolutionState state ) {
    super.postEvaluationStatistics(state);
    ((SimpleProblemForm)(state.evaluator.p_problem.protoCloneSimple())).describe(best_of_run[0], state, 0, statisticslog,Output.V_NO_GENERAL);
  }

}
