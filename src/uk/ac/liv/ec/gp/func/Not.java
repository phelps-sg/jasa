package uk.ac.liv.ec.gp.func;

import ec.gp.*;
import ec.*;

public class Not extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input,
                      ADFStack stack, GPIndividual individual, Problem problem ) {

    children[0].eval(state,thread,input,stack,individual,problem);
    boolean arg0 = ((GPBoolData) input).data;

    ((GPBoolData) input).data = !arg0;

  }

  public String toString() {
    return "Not";
  }
}