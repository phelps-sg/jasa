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

package uk.ac.liv.ec.gp;

import ec.gp.*;
import ec.EvolutionState;
import ec.Problem;

import java.util.HashMap;
import java.util.ArrayList;

import uk.ac.liv.ec.gp.func.*;
import uk.ac.liv.util.FastNumber;

import java.io.Serializable;

/**
 * A temporary place to put some misc ECJ extensions.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPIndividualCtx extends GPIndividual implements Serializable {

  GPContext context = new GPContext();

  protected boolean misbehaved = false;

  static final GPNode[] GPNODE_ARR = new GPNode[0];

  public void setGPContext( EvolutionState state, int thread, ADFStack stack,
                        Problem problem ) {
    context.setState(state);
    context.setThread(thread);
    context.setStack(stack);
    context.setProblem(problem);
  }

  public void setGPContext( GPContext context ) {
    this.context = context;
  }

  public GPContext getGPContext() {
    return context;
  }

  public void evaluateTree( int treeNumber, GPData input ) {
    trees[treeNumber].child.eval(context.state, context.thread, input,
                                    context.stack, this, context.problem);
    context.getStack().reset();
  }

  public FastNumber evaluateNumberTree( int treeNumber ) {
    misbehaved = false;
    GPGenericData input = GPGenericDataPool.fetch();
    try {
      evaluateTree(treeNumber, input);
    } catch ( ArithmeticException e ) {
      misbehaved = true;
    }
    GPGenericDataPool.release(input);
    return (FastNumber) input.data;
  }

  public GPTree getTree( int treeNumber ) {
    return trees[treeNumber];
  }

  public boolean misbehaved() {
    return misbehaved;
  }

  public void doneEvaluating() {
    evaluated = true;
  }

  public void prepareForEvaluating() {
  }

}
