package uk.ac.liv.ec.gp.func;

import ec.gp.*;
import ec.*;

import uk.ac.liv.util.GenericNumber;


/**
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 *
 */

public class Equals extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem ) {

    // 1st argument
    children[0].eval(state, thread, input, stack, individual, problem);
    Object op1 = ((GPGenericData) input).data;

    // 2nd argument
    children[1].eval(state, thread, input, stack, individual, problem);
    Object op2 = ((GPGenericData) input).data;

    // return value
    ((GPBoolData) input).data = op1.equals(op2);
  }

  public String toString() {
    return "=";
  }

}