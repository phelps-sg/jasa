package uk.ac.liv.ec.gp.func;

import ec.gp.*;
import ec.*;

import uk.ac.liv.util.GenericNumber;


public class Subtract extends GPArithmeticBinaryOperator {

  public GenericNumber arithmeticOperator( GenericNumber op1, GenericNumber op2 ) {
    return op1.subtract(op2);
  }

  public String toString() {
    return "-";
  }

}