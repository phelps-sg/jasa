package uk.ac.liv.ec.gp.func;

import ec.gp.*;
import ec.*;

import uk.ac.liv.util.GenericLong;

public class One extends GPNode {

  static GenericLong one = new GenericLong( new Long(1) );

  public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
    ((GPGenericData) input).data = one;
  }

  public String toString() {
    return "1";
  }

}