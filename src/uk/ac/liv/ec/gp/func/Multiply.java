/*
 * JASA Java Auction Simulator API
 * Copyright (C) Steve Phelps
 *
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 */

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