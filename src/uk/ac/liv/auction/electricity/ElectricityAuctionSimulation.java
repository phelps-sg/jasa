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
import uk.ac.liv.util.io.*;

import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.util.*;
import java.io.*;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

/**
 * <p>
 * An implementation of the fitness-landscape experiment described in the
 * University of Liverpool Computer Science Department technical report
 * number ULCS-02-031.  This work is based largely on the work
 * of Nicolaisen, Petrov, and Tesfatsion described in:
 * </p>
 * <br>
 * <p>
 * "Market Power and Efficiency in a Computational Electricity Market
 * with Discriminatory Double-Auction Pricing"
 * <br>
 * IEEE Transactions on Evolutionary Computation, Vol. 5, No. 5. 2001
 * </p>
 *
 * @author Steve Phelps
 */

public class ElectricityAuctionSimulation implements Parameterizable, Runnable {

  protected String outputDir = "/tmp";

  protected int maxRounds = 1000;

  protected int iterations = 100;

  protected int auctioneerKSamples = 10;

  protected double minK = 0;

  protected double maxK = 1;

  protected double deltaK = 0.01;

  protected int numBuyers, numSellers;
  protected int buyerCapacity, sellerCapacity;

  protected FixedQuantityStrategy[] sellerStrategies;
  protected FixedQuantityStrategy[] buyerStrategies;

  protected DataWriter dataFile, distributionFile, iterResults, strategyData;

  protected ElectricityStats stats;

  protected RoundRobinAuction auction = new RoundRobinAuction("electricity");

  protected Auctioneer auctioneer;

  protected StatsMarketDataLogger marketData;

  protected String paramSummary;

  protected boolean collectIterData = false;
  
  protected boolean collectStrategyData = false;

  protected StandardRandomizer randomizer;

  protected CummulativeStatCounter efficiency = new CummulativeStatCounter("EA");
  protected CummulativeStatCounter mPB = new CummulativeStatCounter("MPB");
  protected CummulativeStatCounter mPS = new CummulativeStatCounter("MPS");
  protected CummulativeStatCounter pSA = new CummulativeStatCounter("PSA");
  protected CummulativeStatCounter pBA = new CummulativeStatCounter("PBA");
  protected CummulativeStatCounter pST = new CummulativeStatCounter("PST");
  protected CummulativeStatCounter pBT = new CummulativeStatCounter("PBT");
  protected CummulativeStatCounter eAN = new CummulativeStatCounter("EAN");
  protected CummulativeStatCounter mPBN = new CummulativeStatCounter("MPBN");
  protected CummulativeStatCounter mPSN = new CummulativeStatCounter("MPSN");
  protected CummulativeStatCounter sMPB = new CummulativeStatCounter("SMPB");
  protected CummulativeStatCounter sMPS = new CummulativeStatCounter("SMPS");
  protected CummulativeStatCounter sMPBN = new CummulativeStatCounter("SMPBN");
  protected CummulativeStatCounter sMPSN = new CummulativeStatCounter("SMPSN");
  protected CummulativeStatCounter pBCE = new CummulativeStatCounter("PBCE");
  protected CummulativeStatCounter pSCE = new CummulativeStatCounter("PSCE");
  protected CummulativeStatCounter equilibPrice =
      new CummulativeStatCounter("EquilibPrice");
  protected CummulativeStatCounter equilibQty =
      new CummulativeStatCounter("EquilibQty");
  protected CummulativeStatCounter learningDelta =
      new CummulativeStatCounter("LD");
  
  protected CummulativeStatCounter[] variables = new CummulativeStatCounter[] {
    efficiency, mPB, mPS, pBA, pSA, pBT, pST, eAN, mPBN, mPSN, sMPB, sMPS,
    sMPBN, sMPSN, pBCE, pSCE, equilibPrice, equilibQty, learningDelta
  };

  static Logger logger = Logger.getLogger(ElectricityAuctionSimulation.class);

  static final String DEFAULT_PARAMETER_FILE = "examples/electricity.params";

  static final String P_MAXROUNDS = "maxrounds";
  static final String P_ITERATIONS = "iterations";
  static final String P_OUTPUTDIR = "outputdir";
  static final String P_ELECTRICITY = "electricity";
  static final String P_AUCTIONEERKSAMPLES = "ksamples";
  static final String P_KMIN = "k0";
  static final String P_KMAX = "k1";
  static final String P_KDELTA = "kdelta";
  static final String P_AUCTIONEER = "auctioneer";
  static final String P_CB = "cb";
  static final String P_CS = "cs";
  static final String P_NS = "ns";
  static final String P_NB = "nb";
  static final String P_SELLER_STRATEGY = "seller";
  static final String P_BUYER_STRATEGY = "buyer";
  static final String P_STRATEGY = "strategy";
  static final String P_STATS = "stats";
  static final String P_ITER_DATA = "iterdata";
  static final String P_STRATEGY_DATA = "strategydata";
  static final String P_RANDOMIZER = "randomizer";


  static final int DATAFILE_NUM_COLUMNS = 77;
  static final int ITERRESULTS_NUM_COLUMNS = 9;


  public ElectricityAuctionSimulation() {

  }


  public void setup( ParameterDatabase parameters, Parameter base ) {

    Debug.assertTrue("CSV file not configured with correct number of columns",
                       variables.length*4 == DATAFILE_NUM_COLUMNS-1);

    maxRounds =
      parameters.getIntWithDefault(base.push(P_MAXROUNDS), null, maxRounds);

    auctioneerKSamples =
        parameters.getIntWithDefault(base.push(P_AUCTIONEERKSAMPLES),
                                          null, auctioneerKSamples);

    minK = parameters.getDoubleWithDefault(base.push(P_KMIN), null, minK);
    maxK = parameters.getDoubleWithDefault(base.push(P_KMAX), null, maxK);
    deltaK = parameters.getDoubleWithDefault(base.push(P_KDELTA), null, deltaK);

    collectIterData =
        parameters.getBoolean(base.push(P_ITER_DATA), null, collectIterData);

    collectStrategyData =
        parameters.getBoolean(base.push(P_STRATEGY_DATA), null, collectStrategyData);

    iterations =
      parameters.getIntWithDefault(base.push(P_ITERATIONS), null, iterations);

    outputDir =
      parameters.getStringWithDefault(base.push(P_OUTPUTDIR), null,
                                        outputDir);

    auctioneer =
      (Auctioneer) parameters.getInstanceForParameter(base.push(P_AUCTIONEER),
                                                      null, ParameterizablePricing.class);
    ((Parameterizable) auctioneer).setup(parameters, base.push(P_AUCTIONEER));

    stats =
        (ElectricityStats) parameters.getInstanceForParameterEq(base.push(P_STATS),
                                                                null, ElectricityStats.class);

    randomizer =
        (StandardRandomizer) parameters.getInstanceForParameterEq(base.push(P_RANDOMIZER),
                                                                   null,
                                                                   StandardRandomizer.class);
    randomizer.setSimulation(this);
    randomizer.setup(parameters, base.push(P_RANDOMIZER));

    numBuyers = parameters.getIntWithDefault(base.push(P_NB), null, 3);
    numSellers = parameters.getIntWithDefault(base.push(P_NS), null, 3);

    buyerCapacity = parameters.getIntWithDefault(base.push(P_CB), null, 10);
    sellerCapacity = parameters.getIntWithDefault(base.push(P_CS), null, 10);

    Parameter strategyParam = base.push(P_STRATEGY);

    buyerStrategies = new FixedQuantityStrategy[numBuyers];
    Parameter buyerStrategyParam = strategyParam.push(P_BUYER_STRATEGY);
    for( int i=0; i<numBuyers; i++ ) {
      buyerStrategies[i] =
               (FixedQuantityStrategy)
                 parameters.getInstanceForParameter(buyerStrategyParam,
                                                     null,
                                                     FixedQuantityStrategy.class);
      ((Parameterizable) buyerStrategies[i]).setup(parameters, buyerStrategyParam);
    }

    sellerStrategies = new FixedQuantityStrategy[numSellers];
    Parameter sellerStrategyParam = strategyParam.push(P_SELLER_STRATEGY);
    for( int i=0; i<numSellers; i++ ) {
      sellerStrategies[i] =
               (FixedQuantityStrategy)
                 parameters.getInstanceForParameter(sellerStrategyParam,
                                                     null,
                                                     FixedQuantityStrategy.class);
      ((Parameterizable) sellerStrategies[i]).setup(parameters, sellerStrategyParam);
    }

  }


  public static void main( String[] args ) {

    try {

      String fileName = null;

      if ( args.length < 1 ) {
        fileName = DEFAULT_PARAMETER_FILE;
      } else {
        fileName = args[0];
      }

      File file = new File(fileName);
      if ( ! file.canRead() ) {
        System.err.println("Cannot read parameter file " + fileName);
        System.exit(1);
      }

      org.apache.log4j.PropertyConfigurator.configure(fileName);

      ParameterDatabase parameters =
        new ParameterDatabase(new File(fileName), args);

      ElectricityAuctionSimulation simulation =
          new ElectricityAuctionSimulation();
      simulation.setup(parameters, new Parameter(P_ELECTRICITY));
      simulation.run();

    } catch ( Exception e ) {
      logger.error(e.getMessage());
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
  }


  public void run() {
    try {
      experiment();
    } catch ( IOException e ) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }
  }


  public void experiment() throws IOException {

    summariseParameters();
    
    int numTraders = numBuyers + numSellers;

    paramSummary = numSellers + "-" + numBuyers + "-" +
                    sellerCapacity + "-" + buyerCapacity;

    dataFile = new CSVWriter(
                new FileOutputStream(outputDir + "/" + "npt-"
                                      + paramSummary + ".csv"),
                                          DATAFILE_NUM_COLUMNS);

    writeDataFileHeadings();

    marketData = new StatsMarketDataLogger();
    ((Resetable) auctioneer).reset();
    auction.setAuctioneer(auctioneer);
    auction.setMarketDataLogger(marketData);
    stats.setAuction(auction);

    registerTraders(auction, true, numSellers, sellerCapacity);
    registerTraders(auction, false, numBuyers, buyerCapacity);

    auction.setMaximumRounds(maxRounds);

    long[][] prngSeeds = randomizer.generatePRNGseeds(numTraders, iterations);

    double[][] randomizedPrivateValues = null;

    randomizedPrivateValues =
        randomizer.generateRandomizedPrivateValues(numTraders, iterations);

    for( double k=minK; k<=maxK; k+=deltaK ) {

      experiment(k, randomizedPrivateValues, prngSeeds);

      reportSummary(k);
      recordVariables(k);
    }

    dataFile.close();
  }


  public void experiment( double auctioneerK,
                            double[][] randomizedPrivateValues,
                            long[][] prngSeeds ) throws FileNotFoundException {

    ((ParameterizablePricing) auctioneer).setK(auctioneerK);
    auction.reset();

    logger.info("\n*** Experiment with parameters");

    logger.info("k = " + auctioneerK);

    initIterResults(outputDir + "/iter-" + paramSummary + "-" + auctioneerK+".csv");

    resetVariables();
    
    String strategyDataFile = null;
    if ( collectStrategyData ) {
      strategyDataFile = outputDir + "/strategy-" + paramSummary + "-k" + auctioneerK;
    }

    for( int i=0; i<iterations; i++ ) {

      randomizer.randomizePrivateValues(randomizedPrivateValues, i);
      randomizer.setStrategyPRNGseeds(prngSeeds, i);
      
      if ( collectStrategyData ) {
        initStrategyData(strategyDataFile + "-" + i);
      }

      auction.reset();
      auction.run();
      
      calculateStatistics();
      dumpIterResults();
      dumpStrategyData();
    }

  }


  protected void registerTraders( RoundRobinAuction auction,
                                      boolean areSellers, int num,
                                      int capacity ) {

    for( int i=0; i<num; i++ ) {

      ElectricityTrader trader =
         new ElectricityTrader(capacity, 0, 0, areSellers);

      FixedQuantityStrategy strategy = null;
      if ( areSellers ) {
        strategy = sellerStrategies[i];
      } else {
        strategy = buyerStrategies[i];
      }
      ((Resetable) strategy).reset();
      trader.setStrategy(strategy);
      strategy.setAgent(trader);
      strategy.setQuantity(trader.getCapacity());

      // Register it in the auction
      auction.register(trader);
    }
  }
  
  
  protected void calculateStatistics() {
    
    stats.calculate();
    stats.calculateStrategicMarketPower();

    efficiency.newData(stats.getEA());
    mPB.newData(stats.getMPB());
    mPS.newData(stats.getMPS());
    sMPB.newData(stats.getSMPB());
    sMPS.newData(stats.getSMPS());
    pBA.newData(stats.getPBA());
    pSA.newData(stats.getPSA());
    eAN.newData(stats.getEA()/100);
    mPBN.newData(mpNormalise(stats.getMPB()));
    mPSN.newData(mpNormalise(stats.getMPS()));
    sMPBN.newData(mpNormalise(stats.getSMPB()));
    sMPSN.newData(mpNormalise(stats.getSMPS()));
    pBT.newData(stats.getPBT());
    pST.newData(stats.getPST());
    pBCE.newData(stats.getPBCE());
    pSCE.newData(stats.getPSCE());

    EquilibriaStats equilibria = stats.getEquilibriaStats();
    double ep = (equilibria.getMinPrice() + equilibria.getMaxPrice()) / 2;
    double eq = (equilibria.getMinQuantity() + equilibria.getMaxQuantity()) / 2;
    equilibPrice.newData(ep);
    equilibQty.newData(eq);
    
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      ElectricityTrader t = (ElectricityTrader) i.next();
      Strategy s = t.getStrategy();
      if ( s instanceof AdaptiveStrategy ) {
        Learner l = ((AdaptiveStrategy) s).getLearner();
        learningDelta.newData(l.getLearningDelta());
      }
    }
  }

  
  protected void summariseParameters() {
    logger.info("\nUsing global parameters:\n");
    logger.info("maxRounds = " + maxRounds);
    logger.info("iterations = " + iterations);
    logger.info("auctioneer = " + auctioneer + "\n");
    logger.info("ns = " + numSellers);
    logger.info("nb = " + numBuyers);
    logger.info("cs = " + sellerCapacity);
    logger.info("cb = " + buyerCapacity + "\n");
    logger.info("stats = " + stats + "\n");
    logger.info("randomizer = " + randomizer + "\n");
    logger.info("buyer strategy = " + buyerStrategies[0] + "\n");
    logger.info("seller strategy = " + sellerStrategies[0] + "\n");
  }


  protected void recordVariables( double auctioneerK ) {
    dataFile.newData(auctioneerK);
    for( int i=0; i<variables.length; i++ ) {
      dataFile.newData(variables[i].getMean());
      dataFile.newData(variables[i].getStdDev());
      dataFile.newData(variables[i].getMin());
      dataFile.newData(variables[i].getMax());
    }
  }


  protected void resetVariables() {
    for( int i=0; i<variables.length; i++ ) {
      variables[i].reset();
    }
  }
  
  protected void writeDataFileHeadings() throws IOException {
    CSVWriter headings = new CSVWriter(
            new FileOutputStream(outputDir + "/" + "headings-"
                                  + paramSummary + ".csv"),
                                      DATAFILE_NUM_COLUMNS);

    headings.newData("k");
    for( int i=0; i<variables.length; i++ ) {
      String name = variables[i].getName();
      headings.newData(name + "_mean");
      headings.newData(name + "_stdev");
      headings.newData(name + "_min");
      headings.newData(name + "_max");
    }
    headings.close();
  }


  protected void reportSummary( double auctioneerK ) {
    logger.info("\n*** Summary results for: k = " + auctioneerK +"\n");
    for( int i=0; i<variables.length; i++ ) {
      logger.info(variables[i]);
    }
  }


  protected void dumpIterResults() {
    if ( collectIterData ) {
      iterResults.newData(stats.getEA());
      iterResults.newData(stats.getMPB());
      iterResults.newData(stats.getMPS());
      iterResults.newData(stats.getSMPB());
      iterResults.newData(stats.getSMPS());
      iterResults.newData(marketData.getTransPriceStats().getMean());
      iterResults.newData(marketData.getAskPriceStats().getMean());
      iterResults.newData(marketData.getBidPriceStats().getMean());
      iterResults.newData(stats.calculateEquilibriumPrice());
    }
  }
  
  
  protected void dumpStrategyData() {
    if ( collectStrategyData ) {
      Iterator i = auction.getTraderIterator();
      while ( i.hasNext() ) {
        ElectricityTrader t = (ElectricityTrader) i.next();
        Strategy s = t.getStrategy();
        if ( s instanceof AdaptiveStrategy ) {
          Learner l = ((AdaptiveStrategy) s).getLearner();
          l.dumpState(strategyData);          
        }
      }
    }
  }


  protected void initIterResults( String filename ) throws FileNotFoundException {
    if ( collectIterData ) {
      FileOutputStream iterOut = new FileOutputStream(filename);
      iterResults = new CSVWriter(iterOut, ITERRESULTS_NUM_COLUMNS);
    }
  }
  
  protected void initStrategyData( String filename ) throws FileNotFoundException {    
    AdaptiveStrategy s = (AdaptiveStrategy) buyerStrategies[0];
    Learner l = (Learner) s.getLearner();
    int numColumns = l.getNumberOfActions();
    FileOutputStream strategyDataOut = new FileOutputStream(filename);
    strategyData = new CSVWriter(strategyDataOut, numColumns);   
  }


  public  double mpNormalise( double marketPower ) {
    return 1/(1+Math.abs(marketPower));
  }

}
