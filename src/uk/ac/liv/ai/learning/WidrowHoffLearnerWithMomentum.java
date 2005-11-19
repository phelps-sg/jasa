/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class WidrowHoffLearnerWithMomentum extends WidrowHoffLearner {

  /**
   * cumulative discounted delta
   * 
   * @uml.property name="gamma"
   */
	protected double gamma;

	/**
   * @uml.property name="momentum"
   */
  protected double momentum;

  public static final String P_MOMENTUM = "momentum";  


  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
    gamma = 0;
    momentum = parameters.getDouble(base.push(P_MOMENTUM), 
    		new Parameter(P_DEF_BASE).push(P_MOMENTUM), 0);
  }

  public void train( double target ) {
  	gamma = momentum * gamma + (1-momentum) * delta(target);
    currentOutput += gamma;
  }

  public void randomInitialise() {
    super.randomInitialise();
    gamma = 0;
    momentum = randomParamDistribution.nextDouble();
  }

  /**
   * @uml.property name="momentum"
   */
  public double getMomentum() {
    return momentum;
  }

  /**
   * @uml.property name="momentum"
   */
  public void setMomentum( double momentum ) {
    this.momentum = momentum;
  }
}
