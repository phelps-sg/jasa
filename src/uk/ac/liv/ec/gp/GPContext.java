package uk.ac.liv.ec.gp;

import ec.*;
import ec.gp.*;

/**
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 *
 */

public class GPContext {

  EvolutionState state;
  int thread;
  ADFStack stack;
  Problem problem;

  public GPContext( EvolutionState state, int thread, ADFStack stack,
                        Problem problem ) {
    this.state = state;
    this.thread = thread;
    this.stack = stack;
    this.problem = problem;
  }

  public GPContext() {
  }

  public void setState( EvolutionState state ) { this.state = state; }
  public void setThread( int thread ) { this.thread = thread; }
  public void setStack( ADFStack stack ) { this.stack = stack; }
  public void setProblem( Problem problem ) { this.problem = problem; }

  public EvolutionState getState() { return state; }
  public int getThread() { return thread; }
  public ADFStack getStack() { return stack; }
  public Problem getProblem() { return problem; }

}