package uk.ac.liv.ec.gp.func;

import ec.gp.*;
import ec.*;

public class Not extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input,
                      ADFStack stack, GPIndividual individual, Problem problem ) {

    children[0].eval(state,thread,input,stack,individual,problem);
    boolean arg0 = ((Boolean) ((GPGenericData) input).data).booleanValue();

    ((GPGenericData) input).data = new Boolean(!arg0);

  }

  public String toString() {
    return "Not";
  }
}