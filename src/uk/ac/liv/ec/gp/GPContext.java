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

package uk.ac.liv.ec.gp;

import java.io.Serializable;

import ec.*;
import ec.gp.*;

/**
 * @author Steve Phelps
 */

public class GPContext implements Serializable {

  EvolutionState state;
  int thread;
  ADFStack stack;
  Problem problem;

  public GPContext( EvolutionState state, int thread, ADFStack stack,
                        Problem problem ) {
    this.state = state;
    this.thread = thread;
    this.stack = stack;
    this.problem = problem;
  }

  public GPContext() {
  }

  public void setState( EvolutionState state ) { this.state = state; }
  public void setThread( int thread ) { this.thread = thread; }
  public void setStack( ADFStack stack ) { this.stack = stack; }
  public void setProblem( Problem problem ) { this.problem = problem; }

  public EvolutionState getState() { return state; }
  public int getThread() { return thread; }
  public ADFStack getStack() { return stack; }
  public Problem getProblem() { return problem; }

}