package uk.ac.liv.ec.gp.func;

import ec.gp.*;
import ec.*;

public class And extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input,
                      ADFStack stack, GPIndividual individual, Problem problem ) {

    children[0].eval(state, thread,input,stack,individual,problem);
    Boolean result1 = (Boolean) ((GPGenericData) input).data;

    children[1].eval(state,thread,input,stack,individual,problem);
    Boolean result2 = (Boolean) ((GPGenericData) input).data;

    ((GPGenericData) input).data = new Boolean(result1.booleanValue() && result2.booleanValue());

  }

  public String toString() {
    return "And";
  }
}