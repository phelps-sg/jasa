package uk.ac.liv.ec.gp.func;

import ec.gp.*;
import ec.*;

public class Nand extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input,
                      ADFStack stack, GPIndividual individual, Problem problem ) {

    children[0].eval(state, thread, input, stack,individual, problem);
    boolean arg1 = ((Boolean) ((GPGenericData) input).data).booleanValue();

    children[1].eval(state, thread, input, stack, individual, problem);
    boolean arg2 = ((Boolean) ((GPGenericData) input).data).booleanValue();

    ((GPGenericData) input).data = new Boolean(! (arg1 && arg2));

  }

  public String toString() {
    return "Nand";
  }
}