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
import uk.ac.liv.auction.stats.StatsMarketDataLogger;
import uk.ac.liv.auction.stats.MetaMarketStats;
import uk.ac.liv.auction.electricity.*;
import uk.ac.liv.util.*;
import uk.ac.liv.util.io.*;
import uk.ac.liv.ai.learning.*;

import ec.util.MersenneTwisterFast;

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

public class ElectricityAuctionSimulation implements Runnable {

  String outputFileName  = "electricity-data.csv";

  int maxRounds = 1000;

  int iterations = 100;

  static final int buyerValues[] = { 37, 17, 12 };

  static final int sellerValues[] = { 35, 16, 11 };

  double R = 0.10;    // Recency
  double E = 0.20;    // Experimentation
  int K = 30;         // No. of possible different actions
  double X = 15000;
  double S1 = 9.0;
  
  CSVWriter dataFile, distributionFile;

  ElectricityStats stats;

  RandomRobinAuction auction;

  MersenneTwisterFast randGenerator = new MersenneTwisterFast();
  
  public ElectricityAuctionSimulation() {
  }
  
  public ElectricityAuctionSimulation( int maxRounds, double R, double E,
                                        int K, double X, double S1, 
                                        int iterations, String outputFileName ) {
    this.maxRounds = maxRounds;
    this.R = R;
    this.E = E;
    this.K = K;
    this.S1 = S1;
    this.iterations = iterations;
    this.outputFileName = outputFileName;
  }
  
  public static void main( String[] args ) {    
    ElectricityAuctionSimulation simulation = null;
    if ( args.length > 0 && "-set".equals(args[0]) ) {
      int maxRounds = Integer.valueOf(args[1]).intValue();
      double R = Double.valueOf(args[2]).doubleValue();
      double E = Double.valueOf(args[3]).doubleValue();
      int K = Integer.valueOf(args[4]).intValue();
      double X = Double.valueOf(args[5]).doubleValue();
      double S1 = Double.valueOf(args[6]).doubleValue();
      int iterations = Integer.valueOf(args[7]).intValue();
      String outputFileName = args[8];
      simulation = 
        new ElectricityAuctionSimulation(maxRounds, R, E, K, X, S1, iterations, outputFileName);      
    } else {
      simulation = new ElectricityAuctionSimulation();
    }
    simulation.run();
  }
  
  public void run() {


    System.out.println("Using global parameters:");
    System.out.println("maxRounds = " + maxRounds);
    System.out.println("R = " + R);
    System.out.println("E = " + E);
    System.out.println("K = " + K);
    System.out.println("X = " + X);
    System.out.println("S1 = " + S1);
    System.out.println("Data File = " + outputFileName);

    try {
      dataFile = new CSVWriter(new FileOutputStream(outputFileName), 6);
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    auction = new RandomRobinAuction("Electricity Auction");
    stats = new ElectricityStats(auction);

    experiment( 6, 3, 10, 20 );
    experiment( 6, 3, 10, 40 );
    experiment( 3, 3, 20, 10 );
    experiment( 3, 3, 10, 10 );
    experiment( 3, 3, 10, 20 );
    experiment( 3, 6, 40, 10 );
    experiment( 3, 6, 20, 10 );
    experiment( 3, 6, 10, 10 );
  }

  public void experiment( int ns, int nb, int cs, int cb ) {
    System.out.println("\n*** Experiment with parameters");
    System.out.println("ns = " + ns);
    System.out.println("nb = " + nb);
    System.out.println("cs = " + cs);
    System.out.println("cb = " + cb);
    
    try {
      String rothErevDataFileName = "rotherev-" + ns + "-" + nb 
                                      + "-" + cs + "-" + cb + ".csv";
      distributionFile = new CSVWriter(
                              new FileOutputStream(rothErevDataFileName), K); 
    } catch ( IOException e ) {
      e.printStackTrace();
    }
    
    CummulativeStatCounter efficiency = new CummulativeStatCounter("efficiency");
    CummulativeStatCounter mPB = new CummulativeStatCounter("mPB");
    CummulativeStatCounter mPS = new CummulativeStatCounter("mPS");
    CummulativeStatCounter pSA = new CummulativeStatCounter("pSA");
    CummulativeStatCounter pBA = new CummulativeStatCounter("pBA");

    for( int i=0; i<iterations; i++ ) {
      ElectricityStats results = runExperiment(ns, nb, cs, cb);
      efficiency.newData(results.eA);
      mPB.newData(results.mPB);
      mPS.newData(results.mPS);
      pBA.newData(results.pBA);
      pSA.newData(results.pSA);
      //System.out.println("\nResults for iteration " + i + "\n" + results);
    }
    System.out.println("\n*** Summary results for ns = " + ns + " nb = " + nb + " cs = " + cs + " cb = " + cb + "\n");
    System.out.println(efficiency);
    System.out.println(mPB);
    System.out.println(mPS);
    System.out.println(pSA);
    System.out.println(pBA);
    dataFile.newData(efficiency.getMean());
    dataFile.newData(efficiency.getStdDev());
    dataFile.newData(mPB.getMean());
    dataFile.newData(mPB.getStdDev());
    dataFile.newData(mPS.getMean());
    dataFile.newData(mPS.getStdDev());
    dataFile.flush();
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      ElectricityTrader trader = (ElectricityTrader) i.next();
      RothErevLearner learner = (RothErevLearner) ((StimuliResponseStrategy) trader.getStrategy()).getLearner();
      learner.dumpDistributionToCSV(distributionFile);
    }
  }

  public ElectricityStats runExperiment( int ns, int nb, int cs, int cb ) {
  
    StatsMarketDataLogger logger;
    ContinuousDoubleAuctioneer auctioneer;

    auction = new RandomRobinAuction("Electricity Auction");
    auctioneer = new DiscrimPriceCDAAuctioneer(auction, 0.5);
    auction.setAuctioneer(auctioneer);

    registerTraders(auction, true, ns, cs, sellerValues);
    registerTraders(auction, false, nb, cb, buyerValues);

    logger = new StatsMarketDataLogger();
    auction.setMarketDataLogger(logger);

    auction.setMaximumRounds(maxRounds);

    auction.run();

    stats = new ElectricityStats(auction);

    return stats;
  }


  public void registerTraders( RoundRobinAuction auction,
                                      boolean areSellers, int num, int capacity,
                                      int[] values ) {
    for( int i=0; i<num; i++ ) {


      ElectricityTrader trader =
        new ElectricityTrader(capacity, values[i % values.length], 0, areSellers);

      StimuliResponseStrategy strategy = new StimuliResponseStrategy(trader);

      strategy.setQuantity(trader.getCapacity());
      strategy.setLearner( new NPTRothErevLearner(K, R, E, S1*X,
                                System.currentTimeMillis()) );


      trader.setStrategy(strategy);

      // Register it in the auction
      auction.register(trader);
    }
  }

}

