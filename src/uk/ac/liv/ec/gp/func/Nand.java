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

public class Nand extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input,
                      ADFStack stack, GPIndividual individual, Problem problem ) {

    children[0].eval(state, thread, input, stack,individual, problem);
    boolean arg1 = ((Boolean) ((GPGenericData) input).data).booleanValue();

    children[1].eval(state, thread, input, stack, individual, problem);
    boolean arg2 = ((Boolean) ((GPGenericData) input).data).booleanValue();

    ((GPGenericData) input).data = new Boolean(! (arg1 && arg2));

  }

  public String toString() {
    return "Nand";
  }
}