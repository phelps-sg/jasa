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

package uk.ac.liv.util;

import ec.util.MersenneTwisterFast;

import java.io.Serializable;

public class DiscreteProbabilityDistribution
    implements Resetable, Serializable {

  /**
   * The probability distribution
   */
  double p[];

  /**
   * The number of possible events for this distribution.
   */
  int k;

  /**
   * Random number generator.
   */
  MersenneTwisterFast randGenerator = new MersenneTwisterFast();


  public DiscreteProbabilityDistribution( int k ) {
    this.k = k;
    p = new double[k];
  }

  public void setProbability( int i, double probability ) {
    p[i] = probability;
  }

  public double getProbability( int i ) {
    return p[i];
  }

  public int generateRandomEvent() {
    double rand = randGenerator.nextDouble();
    double cummProb = 0;
    for( int i=0; i<k; i++ ) {
      cummProb += p[i];
      if ( rand <= cummProb ) {
        return i;
      }
    }
    reset();
    return randGenerator.nextInt(k);
  }

  public void reset() {
    for( int i=0; i<k; i++ ) {
      p[i] = 0;
    }
  }

  public void setSeed( long seed ) {
    randGenerator.setSeed(seed);
  }

}