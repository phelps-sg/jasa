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

package uk.ac.liv.auction.ec.gp;


import java.util.*;

import java.io.*;

import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.util.*;

import uk.ac.liv.ec.coevolve.*;

import uk.ac.liv.util.*;
import uk.ac.liv.util.io.*;

import uk.ac.liv.ec.gp.*;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.electricity.*;
import uk.ac.liv.auction.stats.*;
import uk.ac.liv.auction.ec.gp.func.*;

/**
 * Abstract super-class for electricity-trading scenarios.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class GPElectricityTradingProblem extends GPProblem {

  protected int numSellers = 3;
  protected int numBuyers = 3;

  protected int numTraders;

  protected int sellerCapacity = 10;
  protected int buyerCapacity = 10;

  protected int maxRounds = 1000;

  protected boolean randomPrivateValues = false;

  protected boolean verbose = false;

  protected boolean generateCSV = false;

  protected double maxSellerPrivateValue = 100;

  protected double maxBuyerPrivateValue = 100;

  protected double minSellerPrivateValue = 50;

  protected double minBuyerPrivateValue = 50;

  protected int shockInterval = 1;

  protected int evalIterations = 3;

  protected String statsFileName;

  protected RoundRobinAuction auction;

  protected List buyers, sellers;

  protected CSVWriter statsOut;

  protected ArrayList allTraders;

  protected StatsMarketDataLogger logger;

  protected ElectricityStats stats;

  protected MersenneTwisterFast randGenerator;

  protected GPContext context = new GPContext();

  protected CummulativeStatCounter efficiency
                          = new CummulativeStatCounter("efficiency");

  protected CummulativeStatCounter buyerMP
                          = new CummulativeStatCounter("buyerMP");

  protected CummulativeStatCounter sellerMP
                          = new CummulativeStatCounter("sellerMP");

  protected Auctioneer auctioneer;

  protected StrategyMixer strategyMixer;


  static final String P_TRADERS = "numtraders";
  static final String P_ROUNDS = "maxrounds";
  static final String P_RANDOMPRIVATEVALUES = "randomprivatevalues";
  static final String P_MAXSELLERPRIVATEVALUE = "maxsellerprivatevalue";
  static final String P_MINSELLERPRIVATEVALUE = "minsellerprivatevalue";
  static final String P_MAXBUYERPRIVATEVALUE = "maxbuyerprivatevalue";
  static final String P_MINBUYERPRIVATEVALUE = "minbuyerprivatevalue";
  static final String P_VERBOSE = "verbose";
  static final String P_GENERATECSV ="generatecsv";
  static final String P_SHOCKINTERVAL = "shockinterval";
  static final String P_EVALITERATIONS = "evaliterations";
  static final String P_NUMSELLERS = "ns";
  static final String P_NUMBUYERS = "nb";
  static final String P_SELLERCAPACITY = "cs";
  static final String P_BUYERCAPACITY = "cb";
  static final String P_STRATEGYMIXER = "strategymixer";
  static final String P_STATS = "stats";
  static final String P_MEMORYSIZE = "memorysize";

  static final String DEFAULT_STATS_FILE = "coevolve-electricity-stats.csv";

  static final String DEFAULT_PARAM_FILE
                    = "ecj.params/test.params";

  static final int buyerValues[] = { 36, 17, 12 };

  static final int sellerValues[] = { 35, 16, 11 };



  public void setup( EvolutionState state, Parameter base ) {

    super.setup(state, base);

    numSellers =
      state.parameters.getIntWithDefault(base.push(P_NUMSELLERS), null, 3);

    numBuyers =
      state.parameters.getIntWithDefault(base.push(P_NUMBUYERS), null, 3);

    numTraders = numSellers + numBuyers;

    sellerCapacity =
      state.parameters.getIntWithDefault(base.push(P_SELLERCAPACITY), null,
                                         10);

    buyerCapacity =
      state.parameters.getIntWithDefault(base.push(P_BUYERCAPACITY), null,
                                         10);

    maxRounds =
      state.parameters.getIntWithDefault(base.push(P_ROUNDS), null, 1000);

    randomPrivateValues =
      state.parameters.getBoolean(base.push(P_RANDOMPRIVATEVALUES), null,
                                    randomPrivateValues);

    maxSellerPrivateValue =
      state.parameters.getDoubleWithDefault(base.push(P_MAXSELLERPRIVATEVALUE),
                                              null, maxSellerPrivateValue);

    minSellerPrivateValue =
      state.parameters.getDoubleWithDefault(base.push(P_MINSELLERPRIVATEVALUE),
                                              null, minSellerPrivateValue);


    maxBuyerPrivateValue =
      state.parameters.getDoubleWithDefault(base.push(P_MAXSELLERPRIVATEVALUE),
                                              null, maxSellerPrivateValue);

    minBuyerPrivateValue =
      state.parameters.getDoubleWithDefault(base.push(P_MINSELLERPRIVATEVALUE),
                                              null, minSellerPrivateValue);

    shockInterval =
      state.parameters.getIntWithDefault(base.push(P_SHOCKINTERVAL), null,
        shockInterval);

    evalIterations =
      state.parameters.getIntWithDefault(base.push(P_EVALITERATIONS), null,
                                          evalIterations);

    verbose =
      state.parameters.getBoolean(base.push(P_VERBOSE), null, false);

    generateCSV =
      state.parameters.getBoolean(base.push(P_GENERATECSV), null,
                                    generateCSV);

    statsFileName =
      state.parameters.getStringWithDefault(base.push("marketstatsfile"),
                                              DEFAULT_STATS_FILE);

    strategyMixer = (StrategyMixer)
      state.parameters.getInstanceForParameter(base.push(P_STRATEGYMIXER), null,
                                                StrategyMixer.class);
    strategyMixer.setProblem(this);
    ((Parameterizable) strategyMixer).setup(state.parameters,
                                              base.push(P_STRATEGYMIXER));

    stats = (ElectricityStats)
      state.parameters.getInstanceForParameterEq(base.push(P_STATS), null,
                                                ElectricityStats.class);
    stats.setup(state.parameters, base.push(P_STATS));

    randGenerator = new MersenneTwisterFast();

    auction = new RandomRobinAuction();
    auction.setMaximumRounds(maxRounds);

    stats.setAuction(auction);
    stats.calculate();

    logger = new StatsMarketDataLogger();
    auction.setMarketDataLogger(logger);

    initialiseFitnesses();

    registerTraders();

    if ( generateCSV ) {
      initCSVFile();
    }

    System.out.println("numSellers = " + numSellers);
    System.out.println("numBuyers = " + numBuyers);
    System.out.println("sellerCapacity = " + sellerCapacity);
    System.out.println("buyerCapacity = " + buyerCapacity);
    System.out.println("maxRounds = " + maxRounds);
    System.out.println("randomPrivateValues = " + randomPrivateValues);
    System.out.println("minSellerPrivateValue = " + minSellerPrivateValue);
    System.out.println("maxSellerPrivateValue = " + maxSellerPrivateValue);
    System.out.println("minBuyerPrivateValue = " + minBuyerPrivateValue);
    System.out.println("maxBuyerPrivateValue = " + maxBuyerPrivateValue);
  }



  public void evaluate( EvolutionState state, Vector[] group, int thread ) {

    context.setState(state);
    context.setThread(thread);
    context.setStack(stack);
    context.setProblem(this);

    if ( generateCSV ) {
      statsOut.newData(state.generation);
    }

    auctioneer = assignAuctioneer(group);
    auctioneer.setAuction(auction);
    auction.setAuctioneer(auctioneer);

    initialiseTraders(group);

    resetStatCounters();
    resetFitnesses();

    for( int i=0; i<evalIterations; i++ ) {
      preAuctionProcessing();
      auction.run();
      postAuctionProcessing();
      computeFitnesses();
    }

    setFitnesses(group);

    postEvaluationStats();
  }


  public int getNumBuyers() {
    return numBuyers;
  }


  public int getNumSellers() {
    return numSellers;
  }


  public GPContext getGPContext() {
    return context;
  }


  protected void postEvaluationStats() {
    logStats();
    reportStatus();
  }

  protected void resetStatCounters() {
    buyerMP.reset();
    sellerMP.reset();
    efficiency.reset();
  }


  protected void randomizePrivateValues() {
    do {

      Iterator traders = allTraders.iterator();

      for( int i=0; traders.hasNext(); i++ ) {

        ElectricityTrader trader = (ElectricityTrader) traders.next();

        double value;
        if ( trader.isBuyer() ) {
          value =
            nextRandomDouble(minBuyerPrivateValue, maxBuyerPrivateValue);
        } else {
          value =
            nextRandomDouble(minSellerPrivateValue, maxSellerPrivateValue);
        }

        trader.setPrivateValue(value);

        if ( verbose ) {
          System.out.println("pv " +  i + " = " + trader.getPrivateValue(auction));
        }
      }
      stats.calculate();
      if ( verbose ) {
        System.out.println("Post randomization stats = " + stats);
      }
    } while ( ! stats.getEquilibriaStats().equilibriaExists() );
  }


  protected double nextRandomDouble( double min, double max ) {
    return min + randGenerator.nextDouble() * (max - min);
  }


  protected void preAuctionProcessing() {

    if ( stats == null ) {
      stats = new ElectricityStats(auction);
    }

    if ( randomPrivateValues &&
          ((context.getState().generation % shockInterval)==0)) {
      randomizePrivateValues();
    }

    resetTraders();
    auction.reset();
  }


  protected void resetTraders() {
    Iterator traders = allTraders.iterator();
    while ( traders.hasNext() ) {
      ElectricityTrader trader = (ElectricityTrader) traders.next();
      trader.reset();
    }
  }


  protected void postAuctionProcessing() {

    stats.calculate();

    efficiency.newData(stats.getEA());
    buyerMP.newData(stats.getMPB());
    sellerMP.newData(stats.getMPS());
  }


  protected Auctioneer assignAuctioneer( Vector[] group ) {
    if ( auctioneer == null ) {
      auctioneer = new KDoubleAuctioneer(auction, 0.5);
    }
    return auctioneer;
  }




  protected void initCSVFile() {

    try {

      statsOut =
        new CSVWriter(new FileOutputStream(statsFileName),
                        9 + numSellers + numBuyers);

    } catch ( IOException e ) {
      e.printStackTrace();
    }

    // Print headings in CSV output file

    statsOut.newData("generation");

    for( int i=1; i<=numSellers; i++ ) {
      statsOut.newData("s" + i);
    }

    for( int i=1; i<=numBuyers; i++ ) {
      statsOut.newData("b" + i);
    }

    statsOut.newData( new String[] { "eA", "mPB", "mPS", "transPrice", "bidPrice",
                                      "askPrice", "quoteBidPrice", "quoteAskPrice",
                                  }
                    );
  }


  protected void logStats() {
    // Log market stats to CSV file
    if ( generateCSV ) {
      statsOut.newData(efficiency.getMean());
      statsOut.newData(buyerMP.getMean());
      statsOut.newData(sellerMP.getMean());
      statsOut.newData(logger.getTransPriceStats().getMean());
      statsOut.newData(logger.getAskPriceStats().getMean());
      statsOut.newData(logger.getBidPriceStats().getMean());
      statsOut.newData(logger.getAskQuoteStats().getMean());
      statsOut.newData(logger.getBidQuoteStats().getMean());
    }
  }


  protected void reportStatus() {
    if ( verbose ) {
      System.out.println(stats);
    }
  }


  protected LinkedList initialiseTraders( Vector[] group ) {
    Auctioneer auctioneer = auction.getAuctioneer();
    LinkedList strategies = new LinkedList();
    Iterator traders = allTraders.iterator();
    for( int i=0; traders.hasNext(); i++ ) {
      ElectricityTrader trader = (ElectricityTrader) traders.next();
      Strategy strategy = getStrategy(i, group);
      ((Resetable) strategy).reset();
      trader.setStrategy(strategy);
      strategy.setAgent(trader);
      ((FixedQuantityStrategy) strategy).setQuantity(trader.getCapacity());
      trader.reset();
      strategies.add(strategy);
    }
    return strategies;
  }


  protected void registerTraders() {

    allTraders = new ArrayList(numSellers+numBuyers);

    sellers =
      registerTraders(allTraders, auction, true, numSellers, sellerCapacity,
                        sellerValues);

    buyers =
      registerTraders(allTraders, auction, false, numBuyers, buyerCapacity,
                        buyerValues);

  }


  protected List registerTraders( ArrayList allTraders,
                                RoundRobinAuction auction,
                                boolean areSellers, int num, int capacity,
                                int[] values ) {
    ArrayList result = new ArrayList();
    for( int i=0; i<num; i++ ) {

      double value = values[i % values.length];

      GPElectricityTrader trader =
        new GPElectricityTrader(capacity, value, 0, areSellers);

      result.add(trader);
      allTraders.add(trader);
      auction.register(trader);
      System.out.println("Registered " + trader);
    }

    return result;
  }

  protected Strategy getStrategy( int i, Vector[] group ) {
    return strategyMixer.getStrategy(i, group);
  }

  public static void main( String[] args ) {
    ec.Evolve.main(new String[] {"-file", DEFAULT_PARAM_FILE});
  }


  public static EvolutionState make() {
    return make(DEFAULT_PARAM_FILE);
  }


  public static EvolutionState make( String parameterFile ) {
    return ec.Evolve.make( new String[] { "-file", parameterFile } );
  }


  protected abstract void resetFitnesses();

  protected abstract void initialiseFitnesses();

  protected abstract void setFitnesses( Vector[] group );

  protected abstract void computeFitnesses();

  public abstract int getFirstStrategySubpop();

}

