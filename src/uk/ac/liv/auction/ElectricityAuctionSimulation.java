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

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.*;
import uk.ac.liv.auction.electricity.*;

import uk.ac.liv.ai.learning.*;

import uk.ac.liv.util.*;
import uk.ac.liv.util.io.*;

import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.util.*;

import java.io.*;

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

  String outputDir = "/tmp";

  int maxRounds = 1000;

  int iterations = 100;

  int auctioneerKSamples = 10;

  int numBuyers, numSellers;
  int buyerCapacity, sellerCapacity;

  FixedQuantityStrategy[] sellerStrategies;
  FixedQuantityStrategy[] buyerStrategies;

  double minPrivateValue = 10;
  double maxPrivateValue = 50;

  CSVWriter dataFile, distributionFile, iterResults;

  ElectricityStats stats;

  RandomRobinAuction auction;

  Auctioneer auctioneer;

  StatsMarketDataLogger marketData;

  MersenneTwisterFast randGenerator = new MersenneTwisterFast();

  String paramSummary;

  boolean collectIterData = false;

  static final String DEFAULT_PARAMETER_FILE = "examples/electricity.params";

  static final String P_MAXROUNDS = "maxrounds";
  static final String P_ITERATIONS = "iterations";
  static final String P_OUTPUTDIR = "outputdir";
  static final String P_ELECTRICITY = "electricity";
  static final String P_AUCTIONEERKSAMPLES = "ksamples";
  static final String P_MAXPRIVATEVALUE = "maxprivatevalue";
  static final String P_MINPRIVATEVALUE = "minprivatevalue";
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

    minPrivateValue =
        parameters.getDoubleWithDefault(base.push(P_MINPRIVATEVALUE), null,
                                            minPrivateValue);
    maxPrivateValue =
        parameters.getDoubleWithDefault(base.push(P_MAXPRIVATEVALUE), null,
                                            maxPrivateValue);

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

    numBuyers = parameters.getIntWithDefault(base.push(P_NB), null, 3);
    numSellers = parameters.getIntWithDefault(base.push(P_NS), null, 3);

    buyerCapacity = parameters.getIntWithDefault(base.push(P_CB), null, 10);
    sellerCapacity = parameters.getIntWithDefault(base.push(P_CS), null, 10);

    Parameter strategyParam = base.push(P_STRATEGY);

    buyerStrategies = new FixedQuantityStrategy[numBuyers];
    Parameter buyerStrategyParam = strategyParam.push(P_BUYER_STRATEGY);
    for( int i=0; i<numBuyers; i++ ) {
      buyerStrategies[i] =
               (FixedQuantityStrategy) parameters.getInstanceForParameter(buyerStrategyParam,
                                                              null, FixedQuantityStrategy.class);
      ((Parameterizable) buyerStrategies[i]).setup(parameters, buyerStrategyParam);
    }

    sellerStrategies = new FixedQuantityStrategy[numSellers];
    Parameter sellerStrategyParam = strategyParam.push(P_SELLER_STRATEGY);
    for( int i=0; i<numSellers; i++ ) {
      sellerStrategies[i] =
               (FixedQuantityStrategy) parameters.getInstanceForParameter(sellerStrategyParam,
                                                              null, FixedQuantityStrategy.class);
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

      System.out.println("\nUsing global parameters:\n");
      System.out.println("maxRounds = " + maxRounds);
      System.out.println("iterations = " + iterations);
      System.out.println("auctioneer = " + auctioneer);
      System.out.println("ns = " + numSellers);
      System.out.println("nb = " + numBuyers);
      System.out.println("cs = " + sellerCapacity);
      System.out.println("cb = " + buyerCapacity);
      System.out.println("stats = " + stats + "\n");

      experiment(numSellers, numBuyers, sellerCapacity, buyerCapacity);

    } catch ( FileNotFoundException e ) {
      e.printStackTrace();
    }
  }


  public void experiment( int ns, int nb, int cs, int cb ) throws FileNotFoundException {

    try {
      paramSummary = ns + "-" + nb + "-" + cs + "-" + cb;

      dataFile = new CSVWriter(
                  new FileOutputStream(outputDir + "/" + "npt-"
                                        + paramSummary + ".csv"), 13);

    } catch ( IOException e ) {
      e.printStackTrace();
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

    long[][] prngSeeds = generatePRNGseeds(ns+nb, iterations);

    double[][] randomizedPrivateValues = null;

    randomizedPrivateValues = generateRandomizedPrivateValues(ns+nb,
                                                                iterations);


    for( int kMultiple=0; kMultiple<auctioneerKSamples+1; kMultiple++ ) {

      double auctioneerK = kMultiple/(double) auctioneerKSamples;
      ((ParameterizablePricing) auctioneer).setK(auctioneerK);
      auction.reset();

      System.out.println("\n*** Experiment with parameters");

      System.out.println("k = " + auctioneerK);

      CummulativeStatCounter efficiency = new CummulativeStatCounter("efficiency");
      CummulativeStatCounter mPB = new CummulativeStatCounter("mPB");
      CummulativeStatCounter mPS = new CummulativeStatCounter("mPS");
      CummulativeStatCounter pSA = new CummulativeStatCounter("pSA");
      CummulativeStatCounter pBA = new CummulativeStatCounter("pBA");
      CummulativeStatCounter equilibPrice = new CummulativeStatCounter("equilibPrice");
      CummulativeStatCounter eAN = new CummulativeStatCounter("eAN");
      CummulativeStatCounter mPBN = new CummulativeStatCounter("mPBN");
      CummulativeStatCounter mPSN = new CummulativeStatCounter("mPSN");

      initIterResults(outputDir + "/iter-" + paramSummary + "-" + auctioneerK+".csv");

      for( int i=0; i<iterations; i++ ) {

        randomizePrivateValues(randomizedPrivateValues, i);

        setStrategyPRNGseeds(prngSeeds, i);

        auction.reset();
        auction.run();
        stats.calculate();

        efficiency.newData(stats.getEA());
        mPB.newData(stats.getMPB());
        mPS.newData(stats.getMPS());
        pBA.newData(stats.getPBA());
        pSA.newData(stats.getPSA());
        eAN.newData(stats.getEA()/100);
        mPBN.newData(1/(1+Math.abs(stats.getMPB())));
        mPSN.newData(1/(1+Math.abs(stats.getMPS())));
        double ep = (stats.getEquilibriaStats().getMinPrice()
                               + stats.getEquilibriaStats().getMaxPrice()) / 2;
        equilibPrice.newData(ep);

        dumpIterResults();
      }

      System.out.println("\n*** Summary results for: k = " + auctioneerK + " ns = " + ns + " nb = " + nb + " cs = " + cs + " cb = " + cb + "\n");
      System.out.println(efficiency);
      System.out.println(mPB);
      System.out.println(mPS);
      System.out.println(pSA);
      System.out.println(pBA);
      System.out.println(equilibPrice);

      dataFile.newData(auctioneerK);
      dataFile.newData(efficiency.getMean());
      dataFile.newData(efficiency.getStdDev());
      dataFile.newData(mPB.getMean());
      dataFile.newData(mPB.getStdDev());
      dataFile.newData(mPS.getMean());
      dataFile.newData(mPS.getStdDev());
      dataFile.newData(eAN.getMean());
      dataFile.newData(eAN.getStdDev());
      dataFile.newData(mPBN.getMean());
      dataFile.newData(mPBN.getStdDev());
      dataFile.newData(mPSN.getMean());
      dataFile.newData(mPSN.getStdDev());
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



  public double randomPrivateValue() {
    return minPrivateValue +
              (maxPrivateValue - minPrivateValue) * randGenerator.nextDouble();
  }


  protected void randomizePrivateValues( double[][] values, int iteration ) {

    Iterator i = auction.getTraderIterator();
    int traderNumber=0;
    while ( i.hasNext() ) {
      ElectricityTrader trader = (ElectricityTrader) i.next();
      trader.setPrivateValue(values[traderNumber++][iteration]);
    }
  }


  protected double[][] generateRandomizedPrivateValues( int numTraders,
                                                         int numIterations ) {
    double[][] values = new double[numTraders][numIterations];
    EquilibriaStats stats = new EquilibriaStats(auction);
    for( int i=0; i<numIterations; i++ ) {
      do {
        Iterator traderIterator = auction.getTraderIterator();
        for( int t=0; t<numTraders; t++ ) {
          ElectricityTrader trader = (ElectricityTrader) traderIterator.next();
          double value = randomPrivateValue();
          trader.setPrivateValue(value);
          values[t][i] = value;
        }
        stats.recalculate();
      } while ( ! stats.equilibriaExists() );
    }
    return values;
  }


  protected long[][] generatePRNGseeds( int numTraders, int numIterations ) {
    long[][] seeds = new long[numTraders][numIterations];
    long seed = System.currentTimeMillis();
    System.out.println("PRNG seed = " + seed);
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
      if ( strategy instanceof StimuliResponseStrategy ) {
        StimuliResponseLearner learner = ((StimuliResponseStrategy) strategy).getLearner();
        if ( learner instanceof RothErevLearner ) {
          ((RothErevLearner) learner).setSeed(seeds[traderNumber++][iteration]);
        }
      }
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

}


