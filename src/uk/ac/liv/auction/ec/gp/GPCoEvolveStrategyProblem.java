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
 * @author Steve Phelps
 */

public class GPCoEvolveStrategyProblem extends GPProblem
                                       implements CoEvolutionaryProblem {

  protected int NS = 3;
  protected int NB = 3;

  protected int NT;

  protected int CS = 10;
  protected int CB = 10;

  protected int maxRounds = 1000;

  boolean randomPrivateValues = false;

  boolean fixedAuctioneer = false;

  boolean verbose = false;

  boolean generateCSV = false;

  double maxPrivateValue = 100;

  int shockInterval = 1;

  int evalIterations = 3;

  String statsFileName;

  protected RoundRobinAuction auction;

  protected List buyers, sellers;

  protected CSVWriter statsOut;

  protected ArrayList allTraders;

  protected StatsMarketDataLogger logger;

  protected ElectricityStats stats;

  protected MersenneTwisterFast randGenerator;

  protected GPContext context = new GPContext();

  protected CummulativeStatCounter[] strategyFitnesses;


  protected CummulativeStatCounter efficiency
                          = new CummulativeStatCounter("efficiency");

  protected CummulativeStatCounter buyerMP
                          = new CummulativeStatCounter("buyerMP");

  protected CummulativeStatCounter sellerMP
                          = new CummulativeStatCounter("sellerMP");

  protected Auctioneer auctioneer;


  static final String P_TRADERS = "numtraders";
  static final String P_ROUNDS = "maxrounds";
  static final String P_RANDOMPRIVATEVALUES = "randomprivatevalues";
  static final String P_MAXPRIVATEVALUE = "maxprivatevalue";
  static final String P_VERBOSE = "verbose";
  static final String P_GENERATECSV ="generatecsv";
  static final String P_SHOCKINTERVAL = "shockinterval";
  static final String P_EVALITERATIONS = "evaliterations";

  static final String[] DEFAULT_PARAMS = new String[] { "-file", "ecj.params/coevolve-gpauctioneer-3-3-10-10.params"};

  static final int buyerValues[] = { 36, 17, 12 };

  static final int sellerValues[] = { 35, 16, 11 };



  public void setup( EvolutionState state, Parameter base ) {

    super.setup(state,base);

    NS = state.parameters.getIntWithDefault(base.push("ns"), null, 3);
    NB = state.parameters.getIntWithDefault(base.push("nb"), null, 3);
    NT = NS + NB;

    CS = state.parameters.getIntWithDefault(base.push("cs"), null, 10);
    CB = state.parameters.getIntWithDefault(base.push("cb"), null, 10);

    maxRounds = state.parameters.getIntWithDefault(base.push(P_ROUNDS), null, 1000);

    randomPrivateValues = state.parameters.getBoolean(base.push(P_RANDOMPRIVATEVALUES), null, randomPrivateValues);
    maxPrivateValue = state.parameters.getDoubleWithDefault(base.push(P_MAXPRIVATEVALUE), null, maxPrivateValue);
    shockInterval = state.parameters.getIntWithDefault(base.push(P_SHOCKINTERVAL), null, shockInterval);
    evalIterations = state.parameters.getIntWithDefault(base.push(P_EVALITERATIONS), null, evalIterations);
    verbose = state.parameters.getBoolean(base.push(P_VERBOSE), null, false);
    generateCSV = state.parameters.getBoolean(base.push(P_GENERATECSV), null, generateCSV);

    System.out.println("NS = " + NS);
    System.out.println("NB = " + NB);
    System.out.println("CS = " + CS);
    System.out.println("CB = " + CB);
    System.out.println("maxRounds = " + maxRounds);
    System.out.println("randomPrivateValues = " + randomPrivateValues);
    System.out.println("maxPrivateValue = " + maxPrivateValue);

    statsFileName = state.parameters.getStringWithDefault(base.push("marketstatsfile"), "coevolve-electricity-stats.csv");

    randGenerator = new MersenneTwisterFast();

    auction = new RandomRobinAuction();
    auction.setMaximumRounds(maxRounds);

    allTraders = new ArrayList(NS+NB);
    sellers = registerTraders(allTraders, auction, true, NS, CS, sellerValues);
    buyers = registerTraders(allTraders, auction, false, NB, CB, buyerValues);

    logger = new StatsMarketDataLogger();
    auction.setMarketDataLogger(logger);

    initialiseFitnesses();

    if ( generateCSV ) {
      initCSVFile();
    }

  }


  public void evaluate( EvolutionState state, Vector[] group, int thread ) {

    context.setState(state);
    context.setThread(thread);
    context.setStack(stack);
    context.setProblem(this);

    if ( generateCSV ) {
      statsOut.newData(state.generation);
    }

    auction.reset();
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

  protected void postEvaluationStats() {
    logStats();
    reportStatus();
  }

  protected void resetStatCounters() {
    buyerMP.reset();
    sellerMP.reset();
    efficiency.reset();
  }

  protected void resetFitnesses() {
    for( int s=0; s<NT; s++ ) {
      strategyFitnesses[s].reset();
    }
  }

  protected void initialiseFitnesses() {
    strategyFitnesses = new CummulativeStatCounter[NT];
    for( int s=0; s<NT; s++ ) {
      strategyFitnesses[s] = new CummulativeStatCounter("strategyFitness");
    }
  }

  protected void setFitnesses( Vector[] group ) {
    setStrategyFitnesses(group);
  }

  protected void computeFitnesses() {
    computeStrategyFitnesses();
  }

  protected void setStrategyFitnesses( Vector[] group ) {
   for( int s=0; s<NT; s++ ) {
      KozaFitness fitness = (KozaFitness) (getStrategy(s, group)).fitness;
      fitness.setStandardizedFitness(context.getState(),
                                      (float) strategyFitnesses[s].getMean());
    }
  }


  protected void randomizePrivateValues() {
    do {
      Iterator traders = allTraders.iterator();
      for( int i=0; traders.hasNext(); i++ ) {
        ElectricityTrader trader = (ElectricityTrader) traders.next();
        trader.setPrivateValue(randGenerator.nextDouble() * maxPrivateValue);
        if ( verbose ) {
          System.out.println("pv " +  i + " = " + trader.getPrivateValue());
        }
      }
      stats.calculate();
      if ( verbose ) {
        System.out.println("Post randomization stats = " + stats);
      }
    } while ( ! stats.standardStats.equilibriaExists() );
  }


  protected void preAuctionProcessing() {

    if ( stats == null ) {
      stats = new ElectricityStats(0, 200, auction);
    }

    if ( randomPrivateValues && ((context.getState().generation % shockInterval)==0)) {
      randomizePrivateValues();
    }

    Iterator traders = allTraders.iterator();
    while ( traders.hasNext() ) {
      ElectricityTrader trader = (ElectricityTrader) traders.next();
      trader.reset();
    }

    auction.reset();
  }


  protected void postAuctionProcessing() {

    stats.calculate();

    efficiency.newData(stats.eA);
    buyerMP.newData(stats.mPB);
    sellerMP.newData(stats.mPS);
  }


  protected Auctioneer assignAuctioneer( Vector[] group ) {
    if ( auctioneer == null ) {
      auctioneer = new ContinuousDoubleAuctioneer(auction, 0.5);
    }
    return auctioneer;
  }


  protected void computeStrategyFitnesses() {

    Iterator traders = allTraders.iterator();

    for( int i=0; traders.hasNext(); i++ ) {

      GPElectricityTrader trader = (GPElectricityTrader) traders.next();
      double profits = trader.getProfits();
      double fitness = Float.MAX_VALUE;

      if ( ! (Double.isNaN(profits)
              || ((GPTradingStrategy) trader.getStrategy()).misbehaved()) ) {
        fitness = 20000 - profits;
      }
      if ( fitness < 0 ) {
        System.err.println("WARNING: trader " + trader + " had negative fitness!");
        fitness = 0;
      }

      strategyFitnesses[i].newData(fitness );
      if ( generateCSV ) {
        statsOut.newData(profits);
      }

    }
  }


  protected LinkedList initialiseTraders( Vector[] group ) {
    Auctioneer auctioneer = auction.getAuctioneer();
    LinkedList strategies = new LinkedList();
    Iterator traders = allTraders.iterator();
    for( int i=0; traders.hasNext(); i++ ) {
      ElectricityTrader trader = (ElectricityTrader) traders.next();
      GPTradingStrategy strategy = getStrategy(i, group);
      strategy.reset();
      strategy.setGPContext(context);
      trader.setStrategy(strategy);
      strategy.setAgent(trader);
      strategy.setQuantity(trader.getCapacity());
      trader.reset();
      strategies.add(strategy);
    }
    return strategies;
  }

  protected GPTradingStrategy getStrategy( int i, Vector[] group ) {
    return (GPTradingStrategy) group[i].get(0);
  }



  public void initCSVFile() {

    try {
      statsOut =
        new CSVWriter(new FileOutputStream(statsFileName), 9 + NS + NB);
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    // Print headings in CSV output file

    statsOut.newData("generation");

    for( int i=1; i<=NS; i++ ) {
      statsOut.newData("s" + i);
    }

    for( int i=1; i<=NB; i++ ) {
      statsOut.newData("b" + i);
    }

    statsOut.newData( new String[] {  "eA", "mPB", "mPS", "transPrice", "bidPrice",
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


  protected List registerTraders( ArrayList allTraders,
                                RoundRobinAuction auction,
                                boolean areSellers, int num, int capacity,
                                int[] values ) {
    ArrayList result = new ArrayList();
    for( int i=0; i<num; i++ ) {

      double value;
      if ( randomPrivateValues ) {
        value = randGenerator.nextDouble() * maxPrivateValue;
      } else {
        value = values[i % values.length];
      }

      GPElectricityTrader trader =
        new GPElectricityTrader(capacity, value, 0, areSellers);

      result.add(trader);
      allTraders.add(trader);
      auction.register(trader);
      System.out.println("Registered " + trader);
    }

    return result;
  }


  public static void main( String[] args ) {
    ec.Evolve.main(DEFAULT_PARAMS);
  }


  public static EvolutionState make() {
    return ec.Evolve.make(DEFAULT_PARAMS);
  }


  public static EvolutionState make( String parameterFile ) {
    return ec.Evolve.make( new String[] { "-file", parameterFile } );
  }


  public Object protoClone() throws CloneNotSupportedException {

    GPCoEvolveStrategyProblem myobj = (GPCoEvolveStrategyProblem) super.protoClone();
    //TODO?
    return myobj;
  }

}

