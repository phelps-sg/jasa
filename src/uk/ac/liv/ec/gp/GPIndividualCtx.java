package uk.ac.liv.ec.gp;

import ec.gp.*;
import ec.EvolutionState;
import ec.Problem;

/**
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 *
 */

public class GPIndividualCtx extends GPIndividual {

  GPContext context;

  public void setGPContext( EvolutionState state, int thread, ADFStack stack,
                        Problem problem ) {
    context.setState(state);
    context.setThread(thread);
    context.setStack(stack);
    context.setProblem(problem);
  }

  public void evaluateTree( int treeNumber, GPData input ) {
    trees[treeNumber].child.eval(context.state, context.thread, input,
                                    context.stack, this, context.problem);
  }

}