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

public class ElectricityAuctionSimulation  {

  static final String OUTPUT_FILE   = "electricity-data.csv";

  static int MAX_ROUNDS = 1000;

  static final int ITERATIONS = 100;

  static final int buyerValues[] = { 37, 17, 12 };

  static final int sellerValues[] = { 35, 16, 11 };

  static double R = 0.10;    // Recency
  static double E = 0.20;    // Experimentation
  static int K = 30;         // No. of possible different actions
  static double X = 15000;
  static double S1 = 9.0;

  static String dataFileName  = "electricity-data.csv";

  static CSVWriter dataFile;

  static ec.util.MersenneTwisterFast randGenerator = new ec.util.MersenneTwisterFast();

  public static void main( String[] args ) {

    if ( args.length > 0 && "-set".equals(args[0]) ) {
      MAX_ROUNDS = Integer.valueOf(args[1]).intValue();
      R = Double.valueOf(args[2]).doubleValue();
      E = Double.valueOf(args[3]).doubleValue();
      K = Integer.valueOf(args[4]).intValue();
      X = Double.valueOf(args[5]).doubleValue();
      S1 = Double.valueOf(args[6]).doubleValue();
      dataFileName = args[7];
    }

    System.out.println("Using global parameters:");
    System.out.println("MAX_ROUNDS = " + MAX_ROUNDS);
    System.out.println("R = " + R);
    System.out.println("E = " + E);
    System.out.println("K = " + K);
    System.out.println("X = " + X);
    System.out.println("S1 = " + S1);
    System.out.println("Data File = " + dataFileName);

    try {
      dataFile = new CSVWriter(new FileOutputStream(dataFileName), 6);
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    experiment( 6, 3, 10, 20 );
    experiment( 6, 3, 10, 40 );
    experiment( 3, 3, 20, 10 );
    experiment( 3, 3, 10, 10 );
    experiment( 3, 3, 10, 20 );
    experiment( 3, 6, 40, 10 );
    experiment( 3, 6, 20, 10 );
    experiment( 3, 6, 10, 10 );
  }

  public static void experiment( int ns, int nb, int cs, int cb ) {
    System.out.println("\n*** Experiment with parameters");
    System.out.println("ns = " + ns);
    System.out.println("nb = " + nb);
    System.out.println("cs = " + cs);
    System.out.println("cb = " + cb);
    CummulativeStatCounter efficiency = new CummulativeStatCounter("efficiency");
    CummulativeStatCounter mPB = new CummulativeStatCounter("mPB");
    CummulativeStatCounter mPS = new CummulativeStatCounter("mPS");
    CummulativeStatCounter pSA = new CummulativeStatCounter("pSA");
    CummulativeStatCounter pBA = new CummulativeStatCounter("pBA");

    for( int i=0; i<ITERATIONS; i++ ) {
      ElectricityStats results = runSimulation(ns, nb, cs, cb);
      efficiency.newData(results.eA);
      mPB.newData(results.mPB);
      mPS.newData(results.mPS);
      pBA.newData(results.pBA);
      pSA.newData(results.pSA);
      System.out.println("\nResults for iteration " + i + "\n" + results);
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
  }

  public static ElectricityStats runSimulation( int ns, int nb, int cs, int cb ) {

    RandomRobinAuction auction;
    HashMap gridGraph;
    StatsMarketDataLogger logger;
    ContinuousDoubleAuctioneer auctioneer;

    auction = new RandomRobinAuction("Electricity Auction ns:" + ns + " nb:" + nb + " cs:" + cs + " cb:" + cb);
    auctioneer = new DiscrimPriceCDAAuctioneer(auction, 0.5);
    auction.setAuctioneer(auctioneer);

    registerTraders(auction, true, ns, cs, sellerValues);
    registerTraders(auction, false, nb, cb, buyerValues);

    logger = new StatsMarketDataLogger();
    auction.setMarketDataLogger(logger);

    auction.setMaximumRounds(MAX_ROUNDS);

    auction.run();

    ElectricityStats stats = new ElectricityStats(0, 200, auction);

    return stats;
  }


  public static void registerTraders( RoundRobinAuction auction,
                                      boolean areSellers, int num, int capacity,
                                      int[] values ) {
    for( int i=0; i<num; i++ ) {


      ElectricityTrader trader =
        new ElectricityTrader(capacity, values[i % values.length], 0, areSellers);

      StimuliResponseStrategy strategy =
        new StimuliResponseStrategy(trader);

      strategy.setLearner( new NPTRothErevLearner(K, R, E, S1*X, System.currentTimeMillis()) );

      trader.setStrategy(strategy);

      // Register it in the auction
      auction.register(trader);
    }
  }

}


class RandomLearner implements StimuliResponseLearner {

  MersenneTwisterFast randGenerator;

  int k;


  public RandomLearner( int k, int seed ) {
    this.k = k;
    randGenerator = new MersenneTwisterFast(seed);
  }


  public int act() {
    return randGenerator.nextInt(k);
  }

  public void reward( double r ) {
  }


}
