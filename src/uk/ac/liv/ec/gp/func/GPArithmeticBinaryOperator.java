package uk.ac.liv.ec.gp.func;

import ec.gp.*;
import ec.*;

import uk.ac.liv.util.GenericNumber;

public abstract class GPArithmeticBinaryOperator extends GPNode {

  public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {

    children[0].eval(state, thread, input, stack, individual, problem);
    GenericNumber op1 = (GenericNumber) ((GPNumberData) input).data;

    children[1].eval(state, thread, input, stack, individual, problem);
    GenericNumber op2 = (GenericNumber) ((GPNumberData) input).data;

    ((GPNumberData) input).data = arithmeticOperator(op1, op2);
  }

  public abstract GenericNumber arithmeticOperator( GenericNumber op1, GenericNumber op2 );

}