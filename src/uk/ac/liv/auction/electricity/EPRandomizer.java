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

package uk.ac.liv.auction.electricity;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import ec.util.MersenneTwisterFast;

import uk.ac.liv.util.Parameterizable;

import java.util.*;

import org.apache.log4j.Logger;

/**
 *
 * @author Steve Phelps
 * @version $Revision$
 */
public class EPRandomizer extends StandardRandomizer {

  MersenneTwisterFast equilibPricePRNG;

  static Logger logger = Logger.getLogger(EPRandomizer.class);

  public EPRandomizer() {
    super();
    equilibPricePRNG = new MersenneTwisterFast();
  }

  public void setup(ParameterDatabase parameters, Parameter base) {
    super.setup(parameters, base);
    equilibPricePRNG.setSeed(seed);
  }


  protected double[][] generateRandomizedPrivateValues( int numTraders,
                                                         int numIterations ) {
    double[][] values = new double[numIterations][numTraders];
    for( int i=0; i<numIterations; i++ ) {
      double equilibMinPrice = randomValue(equilibPricePRNG, 0, maxPrivateValue);
      double equilibMaxPrice = randomValue(equilibPricePRNG, equilibMinPrice, maxPrivateValue);
      int equilibQty = (int) (((double) simulation.numSellers)/2 * simulation.sellerCapacity);
      logger.debug("Generating values for target equilibrium range: [" + equilibMinPrice + ", " + equilibMaxPrice + "]");
      values[i] = generateValues(equilibMinPrice, equilibMaxPrice, equilibQty);
    }
    return values;
  }


  public double[] generateValues( double equilibPriceMin,
                                    double equilibPriceMax,
                                    int equilibQty ) {

    int ns = simulation.numSellers;
    int nb = simulation.numBuyers;
    int cs = simulation.sellerCapacity;
    int cb = simulation.buyerCapacity;
    double[] values = new double[ns+nb];

    int b0 = (equilibQty / cb);
    int s0 = equilibQty / cs;
    generateValues(s0, 0, equilibPriceMin,  values, 0);
    generateValues(ns-s0, equilibPriceMax, maxPrivateValue, values, s0);
    generateValues(b0, 0, equilibPriceMin, values, ns);
    generateValues(nb-b0, equilibPriceMax, maxPrivateValue, values, ns+b0);
    return values;
  }

  protected void generateValues( int numAgents, double startPrice, double targetPrice,
                                      double[] values, int offset ) {
    logger.debug("generateValues(" + numAgents + ", " + startPrice + ", " + targetPrice + ", " + values + ", " + offset + ")");
    double p = (targetPrice-startPrice) / numAgents;
    double p0 = startPrice;
    int n = offset+numAgents;
    for( int i=offset; i<n-1; i++ ) {
      logger.debug("generating value between " + p0 + " and " + (p0+p));
      double value = randomPrivateValue(p0, p0+p);
      logger.debug("values[" + i + "] = " + value);
      values[i] = value;
      p0 = value;
    }
    logger.debug("values[" + (n-1) + "] = " + targetPrice);
    values[n-1] = targetPrice;
  }

}
