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

package uk.ac.liv.util;

import ec.util.MersenneTwisterFast;

import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * A class representing a discrete probability distribution which
 * can used to generate random events according to the specified
 * distribution.  The output from a uniform PRNG is used to to select
 * from the different possible events.
 *
 * @author Steve Phelps
 */

public class DiscreteProbabilityDistribution
    implements Resetable, Serializable, Seedable {

  /**
   * The probability distribution.
   */
  protected double p[];

  /**
   * The number of possible events for this distribution.
   */
  protected int k;

  /**
   * The uniform-distribution PRNG.
   */
  protected MersenneTwisterFast randGenerator = new MersenneTwisterFast();

  /**
   * The log4j logger.
   */
  static Logger logger = Logger.getLogger(DiscreteProbabilityDistribution.class);


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
      if ( rand < cummProb ) {
        return i;
      }
    }
    throw new ProbabilityError();    
    //logger.warn("generateRandomEvent(): probabilities do not sum to 1");
    //reset();
    //return randGenerator.nextInt(k);   
  }

  public void reset() {
    for( int i=0; i<k; i++ ) {
      p[i] = 0;
    }
  }

  public void setSeed( long seed ) {
    randGenerator.setSeed(seed);
  }
  
  
  public double computeMean() {
    double total = 0;
    for( int i=0; i<k; i++ ) {
      total += i*p[i];
    }
    return total;      
  }
  
  
  public int computeMin() {
    for( int i=0; i<k; i++ ) {
      if ( p[i] > 0 ) {
        return i;
      }
    }
    throw new ProbabilityError();    
  }
  
  public int computeMax() {
    for( int i=k-1; i>=0; i-- ) {
      if ( p[i] > 0 ) {
        return i;
      }
    }
    throw new ProbabilityError();    
  }
  
  public void computeStats( CummulativeStatCounter stats ) {  
  }
  
  
  public class ProbabilityError extends Error {
    
    public ProbabilityError() {
      super("Probabilities do not sum to 1");
    }
  }

}

