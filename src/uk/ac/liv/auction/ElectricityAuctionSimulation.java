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

package uk.ac.liv.auction;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.*;
import uk.ac.liv.auction.electricity.*;
import uk.ac.liv.util.*;
import uk.ac.liv.util.io.*;
import uk.ac.liv.ai.learning.*;

import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.util.*;

import java.io.*;

/**
 * <p>
 * An implementation of the experiment described in:
 * </p>
 * <p>
 * "Markert Power and Efficiency in a Computational Electricity Market
 * with Discriminatory Double-Auction Pricing"
 * Nicolaisen, J.; Petrov, V.; and Tesfatsion, L.
 * in IEEE Trans. on Evol. Computation, Vol. 5, No. 5. 2001
 * </p>
 *
 * <p>
 * This code was written by Steve Phelps in an attempt to replicate
 * the results in the above paper.  This work was carried out independently
 * from the original authors.  Any corrections to this code are
 * welcome.
 * </p>
 */

public class ElectricityAuctionSimulation implements Parameterizable, Runnable {

  String outputDir = "/tmp";

  int maxRounds = 1000;

  int iterations = 100;

  static final int buyerValues[] = { 37, 17, 12 };

  static final int sellerValues[] = { 35, 16, 11 };

  double R = 0.10;    // Recency
  double E = 0.20;    // Experimentation
  int K = 30;         // No. of possible different actions
  double X = 15000;
  double S1 = 9.0;
  int auctioneerKSamples = 10;

  boolean randomPrivateValues = false;

  double minPrivateValue = 10;
  double maxPrivateValue = 50;

  CSVWriter dataFile, distributionFile;

  ElectricityStats stats;

  RandomRobinAuction auction;

  ContinuousDoubleAuctioneer auctioneer;

  MersenneTwisterFast randGenerator = new MersenneTwisterFast();

  String paramSummary;

  static final String DEFAULT_PARAMETER_FILE = "examples/electricity.params";

  static final String P_MAXROUNDS = "maxrounds";
  static final String P_ITERATIONS = "iterations";
  static final String P_OUTPUTFILENAME = "outputfile";
  static final String P_OUTPUTDIR = "outputdir";
  static final String P_ELECTRICITY = "electricity";
  static final String P_AUCTIONEERKSAMPLES = "ksamples";
  static final String P_MAXPRIVATEVALUE = "maxprivatevalue";
  static final String P_MINPRIVATEVALUE = "minprivatevalue";
  static final String P_RANDOMPRIVATEVALUES = "randomprivatevalues";

  public ElectricityAuctionSimulation() {
  }

  public ElectricityAuctionSimulation( int maxRounds, double R, double E,
                                        int K, double X, double S1,
                                        int iterations ) {
    this.maxRounds = maxRounds;
    this.R = R;
    this.E = E;
    this.K = K;
    this.S1 = S1;
    this.iterations = iterations;
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    maxRounds =
      parameters.getIntWithDefault(base.push(P_MAXROUNDS), null, maxRounds);

    R = parameters.getDoubleWithDefault(base.push("r"), null, R);
    E = parameters.getDoubleWithDefault(base.push("e"), null, E);
    K = parameters.getIntWithDefault(base.push("k"), null, K);
    X = parameters.getDoubleWithDefault(base.push("x"), null, X);
    S1 = parameters.getDoubleWithDefault(base.push("s1"), null, S1);

    auctioneerKSamples =
        parameters.getIntWithDefault(base.push(P_AUCTIONEERKSAMPLES),
                                          null, auctioneerKSamples);

    randomPrivateValues =
        parameters.getBoolean(base.push(P_RANDOMPRIVATEVALUES), null,
                                randomPrivateValues);

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
        new ParameterDatabase( new File(fileName) );

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

      System.out.println("Using global parameters:");
      System.out.println("maxRounds = " + maxRounds);
      System.out.println("R = " + R);
      System.out.println("E = " + E);
      System.out.println("K = " + K);
      System.out.println("X = " + X);
      System.out.println("S1 = " + S1);
      System.out.println("random private values = " + randomPrivateValues);

      auctioneer = new DiscrimPriceCDAAuctioneer(auction, 0.5);

      experiment( 3, 3, 10, 10 );

      auctioneer = new ContinuousDoubleAuctioneer(auction, 0.5);

      experiment( 3, 3, 9, 9 );

      auctioneer = new ControlAuctioneer(auction, 0.5);

      experiment( 3, 3, 1, 1 );

      auctioneer = new DiscrimPriceCDAAuctioneer(auction, 0.5);

      experiment( 30, 30, 10, 10 );

      auctioneer = new ContinuousDoubleAuctioneer(auction, 0.5);

      experiment( 30, 30, 9, 9 );

      auctioneer = new ControlAuctioneer(auction, 0.5);

      experiment( 30, 30, 1, 1 );
/*
      experiment( 6, 3, 10, 40 );
      experiment( 3, 3, 20, 10 );
      experiment( 3, 3, 10, 20 );
      experiment( 3, 6, 40, 10 );
      experiment( 3, 6, 20, 10 );
      experiment( 3, 6, 10, 10 );

*/
    } catch ( FileNotFoundException e ) {
      e.printStackTrace();
    }
  }

  public void experiment( int ns, int nb, int cs, int cb ) throws FileNotFoundException {

    try {
      paramSummary = ns + "-" + nb + "-" + cs + "-" + cb;
      String rothErevDataFileName = outputDir + "/rotherev-"
                                      + paramSummary + ".csv";
      distributionFile = new CSVWriter(
                              new FileOutputStream(rothErevDataFileName), K);

      dataFile = new CSVWriter(
                  new FileOutputStream(outputDir + "/" + "npt-"
                                        + paramSummary + ".csv"), 7);

    } catch ( IOException e ) {
      e.printStackTrace();
    }

    auction = new RandomRobinAuction("Electricity Auction");
    auctioneer.reset();
    auction.setAuctioneer(auctioneer);

    registerTraders(auction, true, ns, cs, sellerValues);
    registerTraders(auction, false, nb, cb, buyerValues);

    auction.setMaximumRounds(maxRounds);

    for( int kMultiple=0; kMultiple<auctioneerKSamples+1; kMultiple++ ) {

      double auctioneerK = kMultiple/(double) auctioneerKSamples;
      auctioneer.setK(auctioneerK);
      auction.reset();

      System.out.println("\n*** Experiment with parameters");
      System.out.println("ns = " + ns);
      System.out.println("nb = " + nb);
      System.out.println("cs = " + cs);
      System.out.println("cb = " + cb);
      System.out.println("k = " + auctioneerK);

      CummulativeStatCounter efficiency = new CummulativeStatCounter("efficiency");
      CummulativeStatCounter mPB = new CummulativeStatCounter("mPB");
      CummulativeStatCounter mPS = new CummulativeStatCounter("mPS");
      CummulativeStatCounter pSA = new CummulativeStatCounter("pSA");
      CummulativeStatCounter pBA = new CummulativeStatCounter("pBA");
      CummulativeStatCounter equilibPrice = new CummulativeStatCounter("equilibPrice");

      String iterResultsDataFileName = outputDir + "/iter-"
                                      + paramSummary + "-"+auctioneerK+".csv";
      FileOutputStream iterOut =
        new FileOutputStream(iterResultsDataFileName);
      CSVWriter iterResults = new CSVWriter(iterOut, 3);

      for( int i=0; i<iterations; i++ ) {

        if ( randomPrivateValues ) {
          randomizePrivateValues();
        }

        ElectricityStats results = runExperiment();

        efficiency.newData(results.eA);
        mPB.newData(results.mPB);
        mPS.newData(results.mPS);
        pBA.newData(results.pBA);
        pSA.newData(results.pSA);
        equilibPrice.newData( (results.standardStats.getMinPrice()
                               + results.standardStats.getMaxPrice()) / 2);


        iterResults.newData(results.eA);
        iterResults.newData(results.mPB);
        iterResults.newData(results.mPS);
      }

      System.out.println("\n*** Summary results for ns = " + ns + " nb = " + nb + " cs = " + cs + " cb = " + cb + "\n");
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

//      Iterator i = auction.getTraderIterator();
//      while ( i.hasNext() ) {
//        ElectricityTrader trader = (ElectricityTrader) i.next();
//        RothErevLearner learner = (RothErevLearner) ((StimuliResponseStrategy) trader.getStrategy()).getLearner();
//        learner.dumpDistributionToCSV(distributionFile);
//      }
    }
    dataFile.close();
  }

  public ElectricityStats runExperiment() {
    auction.reset();
    auction.run();
    stats = new ElectricityStats(auction);
    return stats;
  }


  public void registerTraders( RoundRobinAuction auction,
                                      boolean areSellers, int num, int capacity,
                                      int[] values ) {
    for( int i=0; i<num; i++ ) {

      double value;
      if ( randomPrivateValues ) {
        value = randomPrivateValue();
      } else {
        value = values[i % values.length];
      }

      ElectricityTrader trader =
        new ElectricityTrader(capacity, values[i % values.length], 0, areSellers);

      StimuliResponseStrategy strategy = new StimuliResponseStrategy(trader);
      strategy.setMarkupScale(100);

      strategy.setQuantity(trader.getCapacity());
      strategy.setLearner( new NPTRothErevLearner(K, R, E, S1*X) );
                                //System.currentTimeMillis()) );

      trader.setStrategy(strategy);

      // Register it in the auction
      auction.register(trader);
    }
  }

  public double randomPrivateValue() {
    return minPrivateValue +
              (maxPrivateValue - minPrivateValue) * randGenerator.nextDouble();
  }

  protected void randomizePrivateValues() {
    EquilibriaStats stats = new EquilibriaStats(auction);
    do {
      Iterator i = auction.getTraderIterator();
      while ( i.hasNext() ) {
        ElectricityTrader trader = (ElectricityTrader) i.next();
        trader.setPrivateValue(randomPrivateValue());
      }
      stats.recalculate();
    } while ( ! stats.equilibriaExists() );
  }


}


class ControlAuctioneer extends DiscrimPriceCDAAuctioneer {

  public ControlAuctioneer( RoundRobinAuction auction, double k ) {
    super(auction, k);
  }

  public synchronized void clear() {
    List shouts = shoutEngine.getMatchedShouts();
    Iterator i = shouts.iterator();
    while ( i.hasNext() ) {
      Shout bid = (Shout) i.next();  Debug.assertTrue( bid.isBid() );
      Shout ask = (Shout) i.next();  Debug.assertTrue( ask.isAsk() );
      if ( ! ( bid.getPrice() >= ask.getPrice()) ) {
        System.out.println("bid = " + bid);
        System.out.println("ask = " + ask);
        Debug.assertTrue( bid.getPrice() >= ask.getPrice() );
      }
      double price = getK() * bid.getPrice();
      auction.clear(ask, bid.getAgent(), ask.getAgent(), price, ask.getQuantity());
    }
  }

}
