/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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


/**
 * @author Steve Phelps
 *
 */

public class Equals extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem ) {

    GPGenericData tmpArg = GPGenericDataPool.fetch();

    // 1st argument
    children[0].eval(state, thread, tmpArg, stack, individual, problem);
    Object op1 = ((GPGenericData) tmpArg).data;

    // 2nd argument
    children[1].eval(state, thread, input, stack, individual, problem);
    Object op2 = ((GPGenericData) tmpArg).data;

    GPGenericDataPool.release(tmpArg);

    // return value
    ((GPGenericData) input).data = new Boolean(op1.equals(op2));
  }

  public String toString() {
    return "=";
  }

}