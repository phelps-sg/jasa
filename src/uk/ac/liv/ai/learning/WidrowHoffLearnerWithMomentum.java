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
 
package uk.ac.liv.ai.learning;

import uk.ac.liv.prng.GlobalPRNG;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class WidrowHoffLearnerWithMomentum extends WidrowHoffLearner {

  protected double momentum;
  
  public static final String P_MOMENTUM = "momentum";  

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
    momentum = parameters.getDouble(base.push(P_MOMENTUM), null, 0); 
  }

  public void train( double target ) {
    currentOutput = (1 - momentum) * currentOutput + momentum * delta(target);
  }

  public void randomInitialise() {
    super.randomInitialise();
    momentum = GlobalPRNG.getInstance().uniform(0.1, 0.4);       
  }
  
  public double getMomentum () {
    return momentum;
  }
  
  public void setMomentum ( double momentum) {
    this.momentum = momentum;
  }
  
}
