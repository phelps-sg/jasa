package uk.ac.liv.ec.gp.func;

import ec.gp.*;
import ec.*;

public class And extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input,
                      ADFStack stack, GPIndividual individual, Problem problem ) {

    children[0].eval(state,thread,input,stack,individual,problem);
    boolean result1 = ((GPBoolData) input).data;
    if ( ! result1 ) {
      // short-circuit
      ((GPBoolData) input).data = false;
      return;
    }

    children[1].eval(state,thread,input,stack,individual,problem);
    boolean result2 = ((GPBoolData) input).data;

    ((GPBoolData) input).data = result1 && result2;

  }

  public String toString() {
    return "And";
  }
}