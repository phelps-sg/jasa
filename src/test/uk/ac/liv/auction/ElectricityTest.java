/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

import test.uk.ac.liv.PRNGTestSeeds;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.electricity.*;

import uk.ac.liv.ai.learning.NPTRothErevLearner;

import uk.ac.liv.util.CummulativeStatCounter;

import uk.ac.liv.prng.*;

import edu.cornell.lassp.houle.RngPack.RandomElement;

import java.util.*;

import org.apache.log4j.*;

/**
 *
 * Superclass for tests based on
 *
 * "Market Power and Efficiency in a Computational Electricity Market
 * with Discriminatory Double-Auction Pricing"
 * <br>
 * Nicolaisen, Petrov, and Tesfatsion
 * <i>IEEE Transactions on Evolutionary Computation, Vol. 5, No. 5. 2001</I>
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class ElectricityTest extends TestCase {

  protected Auctioneer auctioneer;

  protected RandomRobinAuction auction;

  protected ElectricityStats stats;

  protected static double buyerValues[] = { 37, 17, 12 };

  protected static double sellerValues[] = { 35, 16, 11 };

  protected static long seeds[] = null;

  protected CummulativeStatCounter mPB, mPS, eA;

  protected int ns, nb, cs, cb;

  static final int ITERATIONS = 100;
  static final int MAX_ROUNDS = 1000;
  static final int K = 40;
  static final double R = 0.10;
  static final double E = 0.20;
  static final double S1 = 9;
  static final double MIN_PRIVATE_VALUE = 30;
  static final double MAX_PRIVATE_VALUE = 100;

  static Logger logger = Logger.getLogger(ElectricityTest.class);


  public ElectricityTest( String name ) {
    super(name);
    generatePRNGseeds();
  }

  public void runExperiment() {
    System.out.println("\nAttempting to replicate NPT results with");
    System.out.println("NS = " + ns + " NB = " + nb + " CS = " + cs + " CB = " + cb);
    System.out.println("R = " + R + " E = " + E + " K = " + K + " S1 = " + S1);
    System.out.println("with " + ITERATIONS + " iterations and " + MAX_ROUNDS
                       + " auction rounds.");
    initStats();
    for( int i=0; i<ITERATIONS; i++ ) {
      System.out.println("Iteration " + i);
      auction.reset();
      GlobalPRNG.initialiseWithSeed(seeds[i]);
      auction.run();
      stats.calculate();
      updateStats();
      System.out.println("EA = " + stats.getEA());
    }
    System.out.println(eA);
    System.out.println(mPS);
    System.out.println(mPB);
    traderReport();
  }

  public void updateStats() {
    mPS.newData(stats.getMPS());
    mPB.newData(stats.getMPB());
    eA.newData(stats.getEA());
  }

  public void initStats() {
    mPS = new CummulativeStatCounter("MPS");
    mPB = new CummulativeStatCounter("MPB");
    eA = new CummulativeStatCounter("EA");
  }

  public void experimentSetup( int ns, int nb, int cs, int cb ) {
    this.ns = ns;
    this.nb = nb;
    this.cs = cs;
    this.cb = cb;
    auction = new RandomRobinAuction("NPTReplicationTest");
    auctioneer =
         new KDoubleAuctioneer(auction,
                                          new DiscriminatoryPricingPolicy(0.5));
    auction.setAuctioneer(auctioneer);
    auction.setMaximumRounds(MAX_ROUNDS);
    registerTraders(auction, true, ns, cs, sellerValues);
    registerTraders(auction, false, nb, cb, buyerValues);
    stats = new ElectricityStats(auction);
  }

  public void registerTraders( RoundRobinAuction auction,
                                     boolean areSellers, int num, int capacity,
                                     double[] values ) {
   for( int i=0; i<num; i++ ) {
     double value = values[i % values.length];
     ElectricityTrader agent =
       new ElectricityTrader(capacity, value, 0, areSellers);
     assignStrategy(agent);
     auction.register(agent);
   }
  }

  public void generatePRNGseeds() {

    if ( seeds != null ) {
      return;
    }

    GlobalPRNG.initialiseWithSeed(PRNGTestSeeds.UNIT_TEST_SEED);
    logger.info(this + ": generating PRNG seeds using default seed.. ");

    seeds = new long[ITERATIONS];
    
    RandomElement prng = GlobalPRNG.getInstance();
    for( int i=0; i<ITERATIONS; i++ ) {      
    	seeds[i] = (long) prng.choose(0, Integer.MAX_VALUE);      
    }
    logger.info("done.");
  }

  public void assignStrategy( ElectricityTrader agent ) {
    StimuliResponseStrategy strategy = new StimuliResponseStrategy(agent);
    strategy.setQuantity(agent.getCapacity());
    NPTRothErevLearner learner = new NPTRothErevLearner(K, R, E, S1);
    strategy.setLearner(learner);
    agent.setStrategy(strategy);
    agent.reset();
  }

  public void randomizePrivateValues( double[] values ) {
    for( int i=0; i<values.length; i++ ) {
      values[i] = generateRandomPrivateValue();
    }
  }

  public void randomizePrivateValues() {
    randomizePrivateValues(sellerValues);
    randomizePrivateValues(buyerValues);
  }

  public double generateRandomPrivateValue() {
    return GlobalPRNG.getInstance().uniform(MIN_PRIVATE_VALUE, MAX_PRIVATE_VALUE);
  }

  public void traderReport() {
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      ElectricityTrader agent = (ElectricityTrader) i.next();
      System.out.println(agent);
    }
  }

}