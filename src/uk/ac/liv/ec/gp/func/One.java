package uk.ac.liv.ec.gp.func;

import ec.gp.*;
import ec.*;

import uk.ac.liv.util.GenericInteger;

public class One extends GPNode {

  static Integer one = new Integer(1);

  public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
    ((GPNumberData) input).data = new GenericInteger(one);
  }

  public String toString() {
    return "1";
  }

}