/*
 * JASA Java Auction Simulator API Copyright (C) 2001-2004 Steve Phelps
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package uk.ac.liv.ec.gp;

import ec.EvolutionState;
import ec.Problem;

import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

import scheme.kernel.ScmObject;
import scheme.kernel.ScmSymbol;

import uk.ac.liv.ec.gp.func.GPGenericData;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class GPSchemeNode extends GPNode {

  protected GPContext context = new GPContext();

  protected GPIndividual currentIndividual;

  public void eval( EvolutionState state, int thread, GPData input,
      ADFStack stack, GPIndividual individual, Problem problem ) {
    context.setProblem(problem);
    context.setStack(stack);
    context.setState(state);
    context.setThread(thread);
    currentIndividual = individual;
    eval((GPGenericData) input);
  }

  public void evaluateChild( int childNumber, GPData input ) {
    children[childNumber].eval(context.getState(), context.getThread(), input,
        context.getStack(), currentIndividual, context.getProblem());
  }

  public ScmObject toScheme() {
    return new ScmSymbol(toString());
  }

  public abstract void eval( GPGenericData input );

}