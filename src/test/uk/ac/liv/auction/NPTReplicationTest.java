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

package test.uk.ac.liv.auction;

import junit.framework.*;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.electricity.*;
import uk.ac.liv.ai.learning.NPTRothErevLearner;
import uk.ac.liv.util.CummulativeStatCounter;

import ec.util.MersenneTwisterFast;

import java.util.*;

/**
 *
 * Attempt an approximate replication of some of the experiments
 * described in
 *
 * "Markert Power and Efficiency in a Computational Electricity Market
 * with Discriminatory Double-Auction Pricing"
 * <br>
 * Nicolaisen, Petrov, and Tesfatsion
 * <i>IEEE Transactions on Evolutionary Computation, Vol. 5, No. 5. 2001</I>
 * </p>
 *
 * @author Steve Phelps
 */

public class NPTReplicationTest extends TestCase {

  DiscrimPriceCDAAuctioneer auctioneer;

  RandomRobinAuction auction;

  ElectricityStats stats;

  static final int buyerValues[] = { 37, 17, 12 };

  static final int sellerValues[] = { 35, 16, 11 };

  static long seeds[][] = null;

  CummulativeStatCounter mPB, mPS, eA;

  int ns, nb, cs, cb;


  static final int ITERATIONS = 100;
  static final int MAX_ROUNDS = 1000;
  static final int K = 40;
  static final double R = 0.10;
  static final double E = 0.20;
  static final double S1 = 9;



  public NPTReplicationTest( String name ) {
    super(name);
    generatePRNGseeds();
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(NPTReplicationTest.class);
  }

  public void testRCAP1_2() {
    experimentSetup(3, 3, 20, 10);
    runExperiment();
    assertTrue(mPB.getMean() < 0);
    assertTrue(mPS.getMean() > 0);
    assertTrue(mPB.getStdDev() <= 1);
    assertTrue(mPS.getStdDev() <= 1);
  }

  public void testRCAP_1() {
    experimentSetup(3, 3, 10, 10);
    runExperiment();
    assertTrue(mPB.getMean() < 0);
    assertTrue(mPS.getMean() > 0);
    assertTrue(mPB.getStdDev() <= 1);
    assertTrue(mPS.getStdDev() <= 1);
  }

  public void testRCAP_2() {
    experimentSetup(3, 3, 10, 20);
    runExperiment();
    assertTrue(mPB.getStdDev() <= 1);
    assertTrue(mPS.getStdDev() <= 1);
  }

  public void runExperiment() {
    System.out.println("\nAttempting to replicate NPT results with");
    System.out.println("NS = " + ns + " NB = " + nb + " CS = " + cs + " CB = " + cb);
    System.out.println("R = " + R + " E = " + E + " K = " + K + " S1 = " + S1);
    System.out.println("with " + ITERATIONS + " iterations and " + MAX_ROUNDS
                       + " auction rounds.");
    mPS = new CummulativeStatCounter("MPS");
    mPB = new CummulativeStatCounter("MPB");
    eA = new CummulativeStatCounter("EA");
    for( int i=0; i<ITERATIONS; i++ ) {
      auction.reset();
      setPRNGseeds(i);
      auction.run();
      stats.calculate();
      System.out.println(i + ": " + stats.getEA() + ", " + stats.getMPB() + ", " + stats.getMPS());
      mPS.newData(stats.getMPS());
      mPB.newData(stats.getMPB());
      eA.newData(stats.getEA());
    }
    System.out.println(eA);
    System.out.println(mPS);
    System.out.println(mPB);
    assertTrue(eA.getMean() >= 87.0);
    assertTrue(eA.getStdDev() <= 20);
  }

  public void experimentSetup( int ns, int nb, int cs, int cb ) {
    this.ns = ns;
    this.nb = nb;
    this.cs = cs;
    this.cb = cb;
    auction = new RandomRobinAuction("NPTReplicationTest");
    auctioneer = new DiscrimPriceCDAAuctioneer(auction, 0.5);
    auction.setAuctioneer(auctioneer);
    auction.setMaximumRounds(MAX_ROUNDS);
    registerTraders(auction, true, ns, cs, sellerValues);
    registerTraders(auction, false, nb, cb, buyerValues);
    stats = new ElectricityStats(auction);
  }

  public void registerTraders( RoundRobinAuction auction,
                                     boolean areSellers, int num, int capacity,
                                     int[] values ) {
   for( int i=0; i<num; i++ ) {

     double value = values[i % values.length];

     ElectricityTrader agent =
       new ElectricityTrader(capacity, value, 0, areSellers);

     StimuliResponseStrategy strategy = new StimuliResponseStrategy(agent);
     strategy.setQuantity(capacity);
     NPTRothErevLearner learner = new NPTRothErevLearner(K, R, E, S1);
     strategy.setLearner(learner);
     agent.setStrategy(strategy);

     auction.register(agent);
   }
  }

  public void generatePRNGseeds() {
    if ( seeds != null ) {
      return;
    }
    seeds = new long[ITERATIONS][6];
    generatePRNGseeds(6);
  }

  public void generatePRNGseeds( int numAgents ) {
    System.out.println(this + ": generating PRNG seeds.. ");
    MersenneTwisterFast prng = new MersenneTwisterFast(System.currentTimeMillis());
    for( int i=0; i<ITERATIONS; i++ ) {
      for( int t=0; t<numAgents; t++ ) {
        seeds[i][t] = (long) prng.nextInt();
      }
    }
    System.out.println("done.");
  }

  public void setPRNGseeds( int iteration ) {
    int agentNumber = 0;
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      ElectricityTrader agent = (ElectricityTrader) i.next();
      StimuliResponseStrategy strategy = (StimuliResponseStrategy) agent.getStrategy();
      NPTRothErevLearner learner = (NPTRothErevLearner) strategy.getLearner();
      learner.setSeed(seeds[iteration][agentNumber++]);
    }
  }

}