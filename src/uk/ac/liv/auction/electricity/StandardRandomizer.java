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

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.*;
import uk.ac.liv.ai.learning.*;
import uk.ac.liv.util.*;

import java.util.*;
import java.io.Serializable;

import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

import org.apache.log4j.Logger;

public class StandardRandomizer implements Parameterizable, Serializable {

  protected RoundRobinAuction auction;

  protected long seed = System.currentTimeMillis();

  protected double minPrivateValue = 30;

  protected double maxPrivateValue = 1000;

  protected MersenneTwisterFast privValuePRNG;
   
  
  protected ElectricityAuctionSimulation simulation;

  static Logger logger = Logger.getLogger(StandardRandomizer.class);

  static final String P_SEED = "seed";
  static final String P_MAXPRIVATEVALUE = "maxprivatevalue";
  static final String P_MINPRIVATEVALUE = "minprivatevalue";


  public StandardRandomizer( ElectricityAuctionSimulation simulation ) {
    this();
    setSimulation(simulation);    
  }

  public StandardRandomizer() {
    privValuePRNG = new MersenneTwisterFast(seed);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    seed = (long)
        parameters.getIntWithDefault(base.push(P_SEED), null, (int) seed);

    minPrivateValue =
        parameters.getDoubleWithDefault(base.push(P_MINPRIVATEVALUE), null,
                                        minPrivateValue);
    maxPrivateValue =
        parameters.getDoubleWithDefault(base.push(P_MAXPRIVATEVALUE), null,
                                        maxPrivateValue);

    privValuePRNG.setSeed(seed);
  }

  public void setSimulation( ElectricityAuctionSimulation simulation ) {
    this.simulation = simulation;
    this.auction = simulation.auction;
  }

  public double randomValue( MersenneTwisterFast prng, double min, double max ) {
    return min + (max - min) * prng.nextDouble();
  }
  
  public double randomPrivateValue( double min, double max ) {
    return randomValue(privValuePRNG, min, max);                
  }
  
  public double randomPrivateValue() {
    return randomPrivateValue(minPrivateValue, maxPrivateValue);
  }

  public void randomizePrivateValues( double[][] values, int iteration ) {
    Iterator i = auction.getTraderIterator();
    int traderNumber = 0;
    while (i.hasNext()) {
      ElectricityTrader trader = (ElectricityTrader) i.next();
      trader.setPrivateValue(values[iteration][traderNumber++]);
    }
  }


  protected double[][] generateRandomizedPrivateValues( int numTraders,
                                                         int numIterations ) {
    double[][] values = new double[numIterations][numTraders];
    EquilibriaStats stats = new EquilibriaStats(auction);
    for( int i=0; i<numIterations; i++ ) {
      do {        
        Iterator traders = auction.getTraderIterator();
        for( int t=0; t<numTraders; t++ ) {      
          double value = randomPrivateValue();
          AbstractTraderAgent agent = (AbstractTraderAgent) traders.next();
          agent.setPrivateValue(value);
          values[i][t] = value;    
        }
        stats.recalculate();
      } while ( ! stats.equilibriaExists() );
    }
    return values;
  }


  protected long[][] generatePRNGseeds( int numTraders, int numIterations ) {
    long[][] seeds = new long[numTraders][numIterations];
    logger.info("PRNG seed = " + seed);
    MersenneTwisterFast metaPRNG = new MersenneTwisterFast((int) seed);
    for( int t=0; t<numTraders; t++ ) {
      for( int i=0; i<numIterations; i++ ) {
        seeds[t][i] = (long) metaPRNG.nextInt();
      }
    }
    return seeds;
  }


  protected void setStrategyPRNGseeds( long[][] seeds, int iteration ) {
    Iterator i = auction.getTraderIterator();
    int traderNumber = 0;
    while ( i.hasNext() ) {
      ElectricityTrader t = (ElectricityTrader) i.next();
      Strategy strategy = t.getStrategy();
      if ( strategy instanceof AdaptiveStrategy ) {
        Learner learner = ((AdaptiveStrategy) strategy).getLearner();
        if ( learner instanceof StochasticLearner ) {
          ((StochasticLearner) learner).setSeed(seeds[traderNumber++][iteration]);
        }
      }
    }
  }

}