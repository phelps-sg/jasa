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

package uk.ac.liv.auction.zi;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.*;
import uk.ac.liv.auction.MarketSimulation;

import uk.ac.liv.prng.GlobalPRNG;

import uk.ac.liv.ai.learning.WidrowHoffLearnerWithMomentum;
import uk.ac.liv.util.CummulativeDistribution;
import java.util.Iterator;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * An implementation of Cliff's symetric supply and demand ZIP experiment.
 * See:
 *
 * <p>
 * "Minimal Intelligence Agents for Bargaining Behaviours in
 * Market-based Environments" Dave Cliff 1997
 * </p>
 *
 * Work is currently in progress to attempt a full replication of the
 * results in the above paper.  Currently JASA is not able to replicate
 * these results.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class ZIPExperiment extends MarketSimulation {

  protected DailyStatsReport marketData;

  protected AuctionReport stats;

  protected boolean gatherStats;

  protected long prngSeed;

  protected int tradeEntitlement = 1;

  protected double privValueRangeMin = 75;

  protected double privValueIncrement = 25;

  protected int numDays = 10;

  protected int numSellers = 11;

  protected int numBuyers = 11;

  protected int numSamples = 50;

  protected CummulativeDistribution[] transPriceMean, transPriceStdDev;

  protected boolean console = false;

  public static final String P_SIMULATION = "simulation";
  public static final String P_STATS = "stats";
  public static final String P_GATHER_STATS = "gatherstats";
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

      gnuMessage();

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

    super.setup(parameters, base);

    gatherStats =
        parameters.getBoolean(base.push(P_GATHER_STATS), null, false);


    privValueRangeMin =
        parameters.getDoubleWithDefault(base.push(P_PRIVVALUERANGEMIN),
                                         null, privValueRangeMin);

    privValueIncrement =
        parameters.getDoubleWithDefault(base.push(P_PRIVVALUEINCREMENT),
                                         null, privValueIncrement);

    numSamples =
        parameters.getIntWithDefault(base.push(P_NUMSAMPLES), null, numSamples);

    marketData = new DailyStatsReport();
    auction.addReport(marketData);

    numDays = auction.getMaximumDays();

    setSupplyAndDemand();

    logger.info("");
    logger.info("ZIP Parameters");
    logger.info("--------------");
    logger.info("privValueRangeMin = " + privValueRangeMin);
    logger.info("privValueIncrement = " + privValueIncrement);
    logger.info("numDays = " + numDays);
    logger.info("numSamples = " + numSamples);
    logger.info("numBuyers = " + numBuyers);
    logger.info("numSellers = " + numSellers);

  }


  public void run() {
    transPriceMean = new CummulativeDistribution[numDays];
    transPriceStdDev = new CummulativeDistribution[numDays];

    for( int day=0; day<numDays; day++ ) {
      transPriceMean[day] = new CummulativeDistribution("Mean of mean transaction price for day " + day);
      transPriceStdDev[day] = new CummulativeDistribution("Mean of stddev of transaction price for day " + day);
    }

    for( int sample=0; sample<numSamples; sample++ ) {

      logger.info("\nSample " + sample + "... ");
      initialiseAgents();
      auction.run();
      logger.debug("Auction terminated at round " + auction.getRound());

      for( int day=0; day<numDays; day++ ) {
        CummulativeDistribution stats = marketData.getTransPriceStats(day);
        if ( stats != null ) {
          transPriceMean[day].newData(stats.getMean());
          transPriceStdDev[day].newData(stats.getStdDev());
        }
      }

      marketData.produceUserOutput();
      auction.reset();

      logger.info("Sample " + sample + " done.\n");
    }
  }


  public void report() {
    for( int day=0; day<numDays; day++ ) {
      logger.info("Day " + day + " mean of mean tr price: " +
                    transPriceMean[day].getMean());
    }
    for( int day=0; day<numDays; day++ ) {
      logger.info("Day " + day + " mean of stdev of tr price: " +
                    transPriceStdDev[day].getMean());
    }
  }


  protected static void fatalError( String message ) {
    System.err.println(message);
    System.exit(1);
  }


  protected void initialiseAgents() {
    double momentum = 0.1 + GlobalPRNG.getInstance().raw() * 0.4;
    double learningRate = 0.2 + GlobalPRNG.getInstance().raw() * 0.6;
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      ZITraderAgent trader = (ZITraderAgent) i.next();
      WidrowHoffLearnerWithMomentum l =
          (WidrowHoffLearnerWithMomentum)
            ((AdaptiveStrategy) trader.getStrategy()).getLearner();
      l.setLearningRate(learningRate);      
      l.setMomentum(momentum);
//      l.randomInitialise();
      trader.reset();
    }
  }

  protected void setSupplyAndDemand() {
    double buyerValue = privValueRangeMin;
    double sellerValue = privValueRangeMin;
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      ZITraderAgent trader = (ZITraderAgent) i.next();
      if (trader.isBuyer()) {
        logger.debug("Setting priv value of " + trader + " to " + buyerValue);
        trader.setPrivateValue(buyerValue);
        buyerValue += privValueIncrement;
      } else {
        logger.debug("Setting priv value of " + trader + " to " + sellerValue);
        trader.setPrivateValue(sellerValue);
        sellerValue += privValueIncrement;
      }
    }
  }

}
