package uk.ac.liv.ec.gp.func;

import ec.gp.*;

import uk.ac.liv.util.GenericNumber;

public class Multiply extends GPArithmeticBinaryOperator {

  public GenericNumber arithmeticOperator( GenericNumber op1, GenericNumber op2 ) {
    return op1.multiply(op2);
  }

  public String toString() {
    return "*";
  }
}