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

package uk.ac.liv.auction;

import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.*;

import uk.ac.liv.ai.learning.Learner;
import uk.ac.liv.ai.learning.StochasticLearner;
import uk.ac.liv.ai.learning.WidrowHoffLearner;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Seedable;
import uk.ac.liv.util.CummulativeStatCounter;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 *  @author Steve Phelps
 */

public class ZIPExperiment implements Parameterizable, Runnable,
                                          Serializable {

  protected RoundRobinAuction auction;

  protected MarketDataLogger marketData;

  protected MarketStats stats;

  protected boolean gatherStats;

  protected long prngSeed;

  protected int tradeEntitlement = 1;

  protected double privValueRangeMin = 75;

  protected double privValueIncrement = 25;

  protected int numDays = 10;

  protected int numSellers = 11;

  protected int numBuyers = 11;

  protected int numSamples = 50;

  protected ZITraderAgent[] buyers;

  protected ZITraderAgent[] sellers;

  protected StatsMarketDataLogger marketDataLogger;

  protected CummulativeStatCounter[] transPriceStats;

  protected MersenneTwisterFast paramPRNG = new MersenneTwisterFast();

  public static final String P_AUCTION = "auction";
  public static final String P_NUM_AGENT_TYPES = "n";
  public static final String P_NUM_AGENTS = "numagents";
  public static final String P_AGENT_TYPE = "agenttype";
  public static final String P_AGENTS = "agents";
  public static final String P_CONSOLE = "console";
  public static final String P_SIMULATION = "simulation";
  public static final String P_STATS = "stats";
  public static final String P_GATHER_STATS = "gatherstats";
  public static final String P_SEED = "seed";
  public static final String P_TRADEENTITLEMENT = "tradeentitlement";
  public static final String P_PRIVVALUERANGEMIN = "privvaluerangemin";
  public static final String P_PRIVVALUEINCREMENT = "increment";
  public static final String P_NUMBUYERS = "numbuyers";
  public static final String P_NUMSELLERS = "numsellers";
  public static final String P_DAYS = "days";
  public static final String P_NUMSAMPLES = "samples";




  static Logger logger = Logger.getLogger(ZIPExperiment.class);



  public static void main( String[] args ) {

    try {

      if ( args.length < 1 ) {
        fatalError("Must specify a parameter file");
      }

      String fileName = args[0];
      File file = new File(fileName);
      if ( ! file.canRead() ) {
        fatalError("Cannot read parameter file " + fileName);
      }

      org.apache.log4j.PropertyConfigurator.configure(fileName);

      ParameterDatabase parameters = new ParameterDatabase(file);
      ZIPExperiment experiment = new ZIPExperiment();
      experiment.setup(parameters, new Parameter(P_SIMULATION));
      experiment.run();
      experiment.report();

    } catch ( Exception e ) {
      logger.error(e);
      e.printStackTrace();
    }
  }


  public void setup( ParameterDatabase parameters, Parameter base ) {
    logger.info("Setup.. ");

    prngSeed =
        parameters.getLongWithDefault(base.push(P_SEED), null,
                                       System.currentTimeMillis());


    gatherStats =
        parameters.getBoolean(base.push(P_GATHER_STATS), null, false);

    tradeEntitlement =
        parameters.getIntWithDefault(base.push(P_TRADEENTITLEMENT), null,
                                      tradeEntitlement);

    privValueRangeMin =
        parameters.getDoubleWithDefault(base.push(P_PRIVVALUERANGEMIN),
                                         null, privValueRangeMin);

    privValueIncrement =
        parameters.getDoubleWithDefault(base.push(P_PRIVVALUEINCREMENT),
                                         null, privValueIncrement);

    numDays =
        parameters.getIntWithDefault(base.push(P_DAYS), null, numDays);


    numSamples =
        parameters.getIntWithDefault(base.push(P_NUMSAMPLES), null, numSamples);

    auction =
      (RoundRobinAuction)
        parameters.getInstanceForParameterEq(base.push(P_AUCTION),
                                              null,
                                              RoundRobinAuction.class);
    auction.setup(parameters, base.push(P_AUCTION));

    marketDataLogger = new StatsMarketDataLogger();
    auction.setMarketDataLogger(marketDataLogger);

    buyers = new ZITraderAgent[numBuyers];
    sellers = new ZITraderAgent[numSellers];

    registerTraders(buyers, false);
    registerTraders(sellers, true);

    logger.info("seed = " + prngSeed);
    logger.info("privValueRangeMin = " + privValueRangeMin);
    logger.info("privValueIncrement = " + privValueIncrement);
    logger.info("numDays = " + numDays);
    logger.info("numSamples = " + numSamples);
    logger.info("numBuyers = " + numBuyers);
    logger.info("numSellers = " + numSellers);

    paramPRNG.setSeed(prngSeed);
    seedStrategies();

    logger.info("done.");
  }


  public void run() {
    transPriceStats = new CummulativeStatCounter[numDays];
    for( int day=0; day<numDays; day++ ) {
      transPriceStats[day] = new CummulativeStatCounter("Transaction price day " + day);
    }
    for( int sample=0; sample<numSamples; sample++ ) {
      logger.info("Sample " + sample + "... ");
      selectRandomLearnerParameters();
      for( int day=0; day<numDays; day++ ) {
        logger.info("Day " + day + "... ");
        auction.run();
        double meanTransPrice = marketDataLogger.getTransPriceStats().getMean();
        transPriceStats[day].newData(meanTransPrice);
        logger.info("Auction terminated at round " + auction.getAge());
        logger.info(marketDataLogger.getTransPriceStats());
        auction.reset();
        logger.info("Day " + day + " done.");
      }
      logger.info("Sample " + sample + " done.");
    }
  }


  public void report() {
    for( int day=0; day<numDays; day++ ) {
      logger.info(transPriceStats[day]);
    }
  }


  protected static void fatalError( String message ) {
    System.err.println(message);
    System.exit(1);
  }


  protected void seedStrategies() {
    MersenneTwisterFast prng = new MersenneTwisterFast(prngSeed);
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      AbstractTraderAgent agent = (AbstractTraderAgent) i.next();
      Strategy s = agent.getStrategy();
      if ( s instanceof Seedable ) {
        ((Seedable) s).setSeed(prng.nextLong());
      }
      if ( s instanceof AdaptiveStrategy ) {
        Learner l = ((AdaptiveStrategy) s).getLearner();
        if ( l instanceof StochasticLearner ) {
          ((StochasticLearner) l).setSeed(prng.nextLong());
        }
      }
    }
  }



  public void registerTraders( ZITraderAgent[] traders, boolean areSellers ) {
    MersenneTwisterFast paramPRNG = new MersenneTwisterFast(prngSeed);
    double privValue = privValueRangeMin;
    for( int i=0; i<traders.length; i++ ) {
      traders[i] = new ZITraderAgent(privValue, tradeEntitlement, areSellers);
      auction.register(traders[i]);
      privValue += privValueIncrement;
    }
  }


  protected void selectRandomLearnerParameters() {
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      ZITraderAgent trader = (ZITraderAgent) i.next();
      ZIPStrategy strategy = new ZIPStrategy();
      double learningRate = 0.1 + paramPRNG.nextDouble() * 0.4;
      double momentum = 0.2 + paramPRNG.nextDouble() * 0.6;
      WidrowHoffLearner learner = new WidrowHoffLearner(learningRate, momentum);
      strategy.setLearner(learner);
      trader.setStrategy(strategy);
    }
  }

}
