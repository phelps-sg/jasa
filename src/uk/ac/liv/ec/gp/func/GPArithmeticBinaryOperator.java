/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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
import ec.*;

import uk.ac.liv.util.UntypedNumber;

public abstract class GPArithmeticBinaryOperator extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input,
                     ADFStack stack, GPIndividual individual, Problem problem ) {

    children[0].eval(state, thread, input, stack, individual, problem);
    UntypedNumber op1 = (UntypedNumber) ((GPGenericData) input).data;

    children[1].eval(state, thread, input, stack, individual, problem);
    UntypedNumber op2 = (UntypedNumber) ((GPGenericData) input).data;

    ((GPGenericData) input).data = arithmeticOperator(op1, op2);    
  }

  public abstract UntypedNumber arithmeticOperator( UntypedNumber op1, UntypedNumber op2 );

}