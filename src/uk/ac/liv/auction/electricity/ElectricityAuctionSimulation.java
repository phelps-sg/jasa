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
 * An implementation of the fitness-landscape experiment described in
 * Technical report ULCS-02-031.  This work is based largely on the work
 * of Nicolaisen, Petrov, and Tesfatsion described in:
 * </p>
 * <br>
 * <p>
 * "Markert Power and Efficiency in a Computational Electricity Market
 * with Discriminatory Double-Auction Pricing"
 * <br>
 * IEEE Transactions on Evolutionary Computation, Vol. 5, No. 5. 2001
 * </p>
 *
 *
 *
 * @author Steve Phelps
 */

public class ElectricityAuctionSimulation implements Parameterizable, Runnable {

  protected String outputDir = "/tmp";

  protected int maxRounds = 1000;

  protected int iterations = 100;

  protected int auctioneerKSamples = 10;

  protected int numBuyers, numSellers;
  protected int buyerCapacity, sellerCapacity;

  protected FixedQuantityStrategy[] sellerStrategies;
  protected FixedQuantityStrategy[] buyerStrategies;

  protected DataWriter dataFile, distributionFile, iterResults;

  protected ElectricityStats stats;

  protected RandomRobinAuction auction;

  protected Auctioneer auctioneer;

  protected StatsMarketDataLogger marketData;

  protected String paramSummary;

  protected boolean collectIterData = false;

  protected StandardRandomizer randomizer;

  static Logger logger = Logger.getLogger(ElectricityAuctionSimulation.class);

  static final String DEFAULT_PARAMETER_FILE = "examples/electricity.params";

  static final String P_MAXROUNDS = "maxrounds";
  static final String P_ITERATIONS = "iterations";
  static final String P_OUTPUTDIR = "outputdir";
  static final String P_ELECTRICITY = "electricity";
  static final String P_AUCTIONEERKSAMPLES = "ksamples";
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
  static final String P_RANDOMIZER = "randomizer";


  static final int DATAFILE_NUM_COLUMNS = 35;

  public ElectricityAuctionSimulation() {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    maxRounds =
      parameters.getIntWithDefault(base.push(P_MAXROUNDS), null, maxRounds);

    auctioneerKSamples =
        parameters.getIntWithDefault(base.push(P_AUCTIONEERKSAMPLES),
                                          null, auctioneerKSamples);

    collectIterData =
        parameters.getBoolean(base.push(P_ITER_DATA), null, collectIterData);

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
        (StandardRandomizer) parameters.getInstanceForParameter(base.push(P_RANDOMIZER),
                                                                  null,
                                                                  StandardRandomizer.class);
    randomizer.setAuction(auction);
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
      e.printStackTrace();
    }
  }


  public void run() {

    try {

      logger.info("\nUsing global parameters:\n");
      logger.info("maxRounds = " + maxRounds);
      logger.info("iterations = " + iterations);
      logger.info("auctioneer = " + auctioneer);
      logger.info("ns = " + numSellers);
      logger.info("nb = " + numBuyers);
      logger.info("cs = " + sellerCapacity);
      logger.info("cb = " + buyerCapacity);
      logger.info("stats = " + stats + "\n");

      experiment(numSellers, numBuyers, sellerCapacity, buyerCapacity);

    } catch ( FileNotFoundException e ) {
      e.printStackTrace();
    }
  }


  public void experiment( int ns, int nb, int cs, int cb )
        throws FileNotFoundException {

    try {
      paramSummary = ns + "-" + nb + "-" + cs + "-" + cb;

      dataFile = new CSVWriter(
                  new FileOutputStream(outputDir + "/" + "npt-"
                                        + paramSummary + ".csv"),
                                            DATAFILE_NUM_COLUMNS);

    } catch ( IOException e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }

    auction = new RandomRobinAuction("Electricity Auction");
    marketData = new StatsMarketDataLogger();
    ((Resetable) auctioneer).reset();
    auction.setAuctioneer(auctioneer);
    auction.setMarketDataLogger(marketData);
    stats.setAuction(auction);

    registerTraders(auction, true, ns, cs);
    registerTraders(auction, false, nb, cb);

    auction.setMaximumRounds(maxRounds);

    long[][] prngSeeds = randomizer.generatePRNGseeds(ns+nb, iterations);

    double[][] randomizedPrivateValues = null;

    randomizedPrivateValues =
        randomizer.generateRandomizedPrivateValues(ns+nb, iterations);

    for( int kMultiple=0; kMultiple<auctioneerKSamples+1; kMultiple++ ) {

      double auctioneerK = kMultiple/(double) auctioneerKSamples;
      ((ParameterizablePricing) auctioneer).setK(auctioneerK);
      auction.reset();

      logger.info("\n*** Experiment with parameters");

      logger.info("k = " + auctioneerK);

      CummulativeStatCounter efficiency = new CummulativeStatCounter("EA");
      CummulativeStatCounter mPB = new CummulativeStatCounter("MPB");
      CummulativeStatCounter mPS = new CummulativeStatCounter("MPS");
      CummulativeStatCounter pSA = new CummulativeStatCounter("PSA");
      CummulativeStatCounter pBA = new CummulativeStatCounter("PBA");
      CummulativeStatCounter pST = new CummulativeStatCounter("PST");
      CummulativeStatCounter pBT = new CummulativeStatCounter("PBT");
      CummulativeStatCounter eAN = new CummulativeStatCounter("EAN");
      CummulativeStatCounter mPBN = new CummulativeStatCounter("MPBN");
      CummulativeStatCounter mPSN = new CummulativeStatCounter("MPSN");
      CummulativeStatCounter sMPB = new CummulativeStatCounter("SMPB");
      CummulativeStatCounter sMPS = new CummulativeStatCounter("SMPS");
      CummulativeStatCounter sMPBN = new CummulativeStatCounter("SMPBN");
      CummulativeStatCounter sMPSN = new CummulativeStatCounter("SMPSN");
      CummulativeStatCounter pBCE = new CummulativeStatCounter("PBCE");
      CummulativeStatCounter pSCE = new CummulativeStatCounter("PSCE");
      CummulativeStatCounter equilibPrice =
          new CummulativeStatCounter("equilibPrice");

      LinkedList variables = new LinkedList();
      variables.add(efficiency);
      variables.add(mPB);
      variables.add(mPS);
      variables.add(pBA);
      variables.add(pSA);
      variables.add(pBT);
      variables.add(pST);
      variables.add(eAN);
      variables.add(mPBN);
      variables.add(mPSN);
      variables.add(sMPB);
      variables.add(sMPS);
      variables.add(sMPBN);
      variables.add(sMPSN);
      variables.add(equilibPrice);
      variables.add(pBCE);
      variables.add(pSCE);


      Debug.assertTrue("CSV file not configured with correct number of columns",
                         variables.size()*2 == DATAFILE_NUM_COLUMNS-1);

      initIterResults(outputDir + "/iter-" + paramSummary + "-" + auctioneerK+".csv");

      for( int i=0; i<iterations; i++ ) {

        randomizer.randomizePrivateValues(randomizedPrivateValues, i);

        randomizer.setStrategyPRNGseeds(prngSeeds, i);

        auction.reset();
        auction.run();
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

        double ep = (stats.getEquilibriaStats().getMinPrice()
                               + stats.getEquilibriaStats().getMaxPrice()) / 2;
        equilibPrice.newData(ep);

        dumpIterResults();
      }

      logger.info("\n*** Summary results for: k = " + auctioneerK + " ns = " + ns + " nb = " + nb + " cs = " + cs + " cb = " + cb + "\n");
      Iterator i = variables.iterator();
      while ( i.hasNext() ) {
        logger.info(i.next());
      }

      dataFile.newData(auctioneerK);

      recordVariables(variables);
    }

    dataFile.close();
  }


  public void registerTraders( RoundRobinAuction auction,
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


  protected void recordVariables( List variables ) {
    Iterator i = variables.iterator();
    while ( i.hasNext() ) {
      CummulativeStatCounter variable = (CummulativeStatCounter) i.next();
      dataFile.newData(variable.getMean());
      dataFile.newData(variable.getStdDev());
    }
  }


  protected void dumpIterResults() {
    if ( collectIterData ) {
      iterResults.newData(stats.getEA());
      iterResults.newData(stats.getMPB());
      iterResults.newData(stats.getMPS());
      iterResults.newData(marketData.getTransPriceStats().getMean());
      iterResults.newData(marketData.getAskPriceStats().getMean());
      iterResults.newData(marketData.getBidPriceStats().getMean());
      iterResults.newData(stats.calculateEquilibriumPrice());
    }
  }


  protected void initIterResults( String filename ) throws FileNotFoundException {
    if ( collectIterData ) {
      FileOutputStream iterOut = new FileOutputStream(filename);
      iterResults = new CSVWriter(iterOut, 7);
    }
  }


  public  double mpNormalise( double marketPower ) {
    return 1/(1+Math.abs(marketPower));
  }

}


