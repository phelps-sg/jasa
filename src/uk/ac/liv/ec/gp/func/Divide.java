package uk.ac.liv.ec.gp.func;

import uk.ac.liv.util.GenericNumber;

import ec.gp.*;


public class Divide extends GPArithmeticBinaryOperator {

  public GenericNumber arithmeticOperator( GenericNumber op1, GenericNumber op2 ) {
    return op1.divide(op2);
  }

  public String toString() {
    return "/";
  }

}