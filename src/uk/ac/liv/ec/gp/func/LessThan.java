/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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

import uk.ac.liv.util.GenericNumber;


/**
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 *
 */

public class LessThan extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem ) {

    GPGenericData tmpArg = new GPGenericData();

    // 1st argument
    children[0].eval(state, thread, tmpArg, stack, individual, problem);
    Comparable op1 = (Comparable) ((GPGenericData) tmpArg).data;

    // 2nd argument
    children[1].eval(state, thread, tmpArg, stack, individual, problem);
    Comparable op2 = (Comparable) ((GPGenericData) tmpArg).data;

    // return value
    ((GPGenericData) input).data = new Boolean(op1.compareTo(op2) < 0);
  }

  public String toString() {
    return "<";
  }

}