package uk.ac.liv.ec.gp.func;

import ec.gp.*;
import ec.*;


public class True extends GPNode {

  public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
    ((GPGenericData) input).data = Boolean.TRUE;
  }

  public String toString() {
    return "True";
  }
}