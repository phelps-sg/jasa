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
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 *
 */

public class GPCoEvolveAuctionProblem extends GPProblem implements CoEvolutionaryProblem {

  static int NS = 3;
  static int NB = 3;

  static int CS = 10;
  static int CB = 10;

  static int MAX_ROUNDS = 1000;

  static boolean randomPrivateValues = false;

  static boolean fixedAuctioneer = false;

  static boolean verbose = true;

  static double maxPrivateValue = 10;

  static int shockInterval = 10;

  static final String P_TRADERS = "numtraders";
  static final String P_ROUNDS = "maxrounds";
  static final String P_ITERATIONS = "iterations";
  static final String P_RANDOMPRIVATEVALUES = "randomprivatevalues";
  static final String P_MAXPRIVATEVALUE = "maxprivatevalue";
  static final String P_FIXEDAUCTIONEER = "fixedauctioneer";
  static final String P_VERBOSE = "verbose";
  static final String P_SHOCKINTERVAL = "shockinterval";

  static final String[] DEFAULT_PARAMS = new String[] { "-file", "ecj.params/coevolve-gpauctioneer-30-30-10-10.params"};

  static final int buyerValues[] = { 36, 17, 12 };

  static final int sellerValues[] = { 35, 16, 11 };

  protected RoundRobinAuction auction;

  protected List buyers, sellers;

  protected CSVWriter statsOut;

  protected ArrayList allTraders;

  protected StatsMarketDataLogger logger;

  protected ElectricityStats stats;

  protected MersenneTwisterFast randGenerator;

  protected GPContext context = new GPContext();



  public Object protoClone() throws CloneNotSupportedException {

    GPCoEvolveAuctionProblem myobj = (GPCoEvolveAuctionProblem) super.protoClone();
    //TODO?
    return myobj;
  }


  public void setup( EvolutionState state, Parameter base ) {

    super.setup(state,base);

    NS = state.parameters.getIntWithDefault(base.push("ns"), null, 3);
    NB = state.parameters.getIntWithDefault(base.push("nb"), null, 3);

    CS = state.parameters.getIntWithDefault(base.push("cs"), null, 10);
    CB = state.parameters.getIntWithDefault(base.push("cb"), null, 10);

    MAX_ROUNDS = state.parameters.getIntWithDefault(base.push(P_ROUNDS), null, 1000);

    randomPrivateValues = state.parameters.getBoolean(base.push(P_RANDOMPRIVATEVALUES), null, false);
    maxPrivateValue = state.parameters.getDoubleWithDefault(base.push(P_MAXPRIVATEVALUE), null, 100.0);
    shockInterval = state.parameters.getIntWithDefault(base.push(P_SHOCKINTERVAL), null, 10);
    fixedAuctioneer = state.parameters.getBoolean(base.push(P_FIXEDAUCTIONEER), null, false);
    verbose = state.parameters.getBoolean(base.push(P_VERBOSE), null, true);

    System.out.println("NS = " + NS);
    System.out.println("NB = " + NB);
    System.out.println("CS = " + CS);
    System.out.println("CB = " + CB);
    System.out.println("MAX_ROUNDS = " + MAX_ROUNDS);
    System.out.println("randomPrivateValues = " + randomPrivateValues);
    System.out.println("maxPrivateValue = " + maxPrivateValue);

    String statsFileName = state.parameters.getStringWithDefault(base.push("marketstatsfile"), "coevolve-electricity-stats.csv");

    randGenerator = new MersenneTwisterFast();

    auction = new RandomRobinAuction();
    auction.setMaximumRounds(MAX_ROUNDS);

    allTraders = new ArrayList(NS+NB);
    sellers = registerTraders(allTraders, auction, true, NS, CS, sellerValues);
    buyers = registerTraders(allTraders, auction, false, NB, CB, buyerValues);

    logger = new StatsMarketDataLogger();
    auction.setMarketDataLogger(logger);

    try {
      statsOut = new CSVWriter(new FileOutputStream(statsFileName), 10 + NS + NB);//13);
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
                                      "fitness" });
  }


  public void evaluate( EvolutionState state, Vector[] group, int thread ) {

    context.setState(state);
    context.setThread(thread);
    context.setStack(stack);
    context.setProblem(this);

    statsOut.newData(state.generation);

    auction.reset();
    Auctioneer auctioneer = assignAuctioneer((GPAuctioneer) group[0].get(0));
    auctioneer.setAuction(auction);
    auction.setAuctioneer(auctioneer);

    initialiseTraders(group);

    preAuctionProcessing();
    auction.run();
    postAuctionProcessing();

    computeStrategyFitnesses(group);
    float auctioneerFitness = computeAuctioneerFitness((GPIndividual) group[0].get(0));

    logStats(auctioneerFitness);
    reportStatus();
  }


  protected void randomizePrivateValues( Auctioneer auctioneer ) {
    Iterator traders = allTraders.iterator();
    for( int i=0; traders.hasNext(); i++ ) {
      ElectricityTrader trader = (ElectricityTrader) traders.next();
      trader.setPrivateValue(randGenerator.nextDouble() * maxPrivateValue);
      if ( verbose ) {
        System.out.println("pv " +  i + " = " + trader.getPrivateValue());
      }
    }
  }


  protected void preAuctionProcessing() {

    if ( stats == null ) {
      stats = new ElectricityStats(0, 200, auction);
    }

    if ( randomPrivateValues && ((context.getState().generation % shockInterval)==0)) {
      do {
        randomizePrivateValues(auction.getAuctioneer());
        stats.calculate();
        if ( verbose ) {
          System.out.println("Post randomization stats = " + stats);
        }
      } while ( Double.isNaN(stats.standardStats.getEquilibriaPriceStats().getMean()) );
    }

  }


  protected void postAuctionProcessing() {

    stats.calculate();
  /*
    if ( randomPrivateValues ) {
      stats.calculate();
    } else {
      stats.recalculate();
    } */

    // Save a copy of the stats for posterity
    if ( ! fixedAuctioneer ) {
      GPAuctioneer auctioneer = (GPAuctioneer) auction.getAuctioneer();
      auctioneer.setMarketStats(stats.newCopy());
      auctioneer.setLogStats(logger.newCopy());
    }
  }


  protected Auctioneer assignAuctioneer( GPAuctioneer individual ) {
    Auctioneer auctioneer = null;
    if ( fixedAuctioneer ) {
      auctioneer = new ContinuousDoubleAuctioneer(auction, 0.5);
    } else {
      auctioneer = individual;
      ((GPAuctioneer) auctioneer).setGPContext(context);
    }
    return auctioneer;
  }


  protected void computeStrategyFitnesses( Vector[] group ) {
    Iterator traders = allTraders.iterator();
    for( int i=0; traders.hasNext(); i++ ) {
      GPElectricityTrader trader = (GPElectricityTrader) traders.next();
      double profits = trader.getProfits();
      float fitness = Float.MAX_VALUE;
      if ( (!Double.isNaN(profits)) ) {
        fitness = 20000f - (float) profits;
      }
      if ( fitness < 0 ) {
        System.err.println("WARNING: trader " + trader + " had negative fitness!");
        fitness = 0;
      }
      GPTradingStrategy individual = (GPTradingStrategy) group[i+1].get(0);
      ((KozaFitness) individual.fitness).setStandardizedFitness(context.getState(), fitness);
      individual.evaluated = true;
      statsOut.newData(profits);
    }
  }


  protected void initialiseTraders( Vector[] group ) {
    Auctioneer auctioneer = auction.getAuctioneer();
    LinkedList strategies = new LinkedList();
    Iterator traders = allTraders.iterator();
    for( int i=0; traders.hasNext(); i++ ) {
      ElectricityTrader trader = (ElectricityTrader) traders.next();
      GPTradingStrategy strategy = (GPTradingStrategy) group[i+1].get(0);
      strategy.reset();
      strategy.setGPContext(context);
      trader.setStrategy(strategy);
      strategy.setAgent(trader);
      strategy.setQuantity(trader.getCapacity());
      trader.reset();
      strategies.add(strategy);
    }

    // Save the strategies used in this auction for posterity
    if ( ! fixedAuctioneer ) {
      ((GPAuctioneer) auctioneer).setStrategies(strategies);
    }
  }


  protected float computeAuctioneerFitness( GPIndividual auctioneer ) {

    float relMarketPower = (float) (Math.abs(stats.mPB) + Math.abs(stats.mPS)) / 2.0f;
    if ( stats.eA > 100 ) {
      System.err.println("eA > 100% !!");
      System.err.println(stats);
    }
    float fitness = Float.MAX_VALUE;

    if ( !Float.isNaN(relMarketPower) && !Float.isInfinite(relMarketPower)
           && !Double.isNaN(stats.eA) ) {
      fitness = 1-((float) stats.eA/100); //TODO + relMarketPower;
    }

    ((KozaFitness) auctioneer.fitness).setStandardizedFitness(context.getState(), fitness);
    auctioneer.evaluated = true;

    return fitness;
  }

  protected void logStats( float auctioneerFitness ) {
    // Log market stats to CSV file
    statsOut.newData(stats.eA);
    statsOut.newData(stats.mPB);
    statsOut.newData(stats.mPS);
    statsOut.newData(logger.getTransPriceStats().getMean());
    statsOut.newData(logger.getAskPriceStats().getMean());
    statsOut.newData(logger.getBidPriceStats().getMean());
    statsOut.newData(logger.getAskQuoteStats().getMean());
    statsOut.newData(logger.getBidQuoteStats().getMean());
    statsOut.newData(auctioneerFitness);
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

}

/**
 * GPElectricityTrader only makes deals that result in +ve profits.
 */
class GPElectricityTrader extends ElectricityTrader {

  public GPElectricityTrader( int capacity, double privateValue,
                              double fixedCosts, boolean isSeller ) {
    super(capacity, privateValue, fixedCosts, isSeller);
  }

  public void informOfSeller( Shout winningShout, RoundRobinTrader seller,
                               double price, int quantity) {

    if ( price < privateValue ) {

      GPElectricityTrader trader = (GPElectricityTrader) seller;
      trader.informOfBuyer(this, price, quantity);
    }
  }

  public void trade( double price, int quantity ) {
    double profit = quantity * (privateValue - price);
    profits += profit;
  }

  public void informOfBuyer( GPElectricityTrader buyer, double price, int quantity ) {

    if ( price > privateValue ) {

      buyer.trade(price, quantity);

      double profit = quantity * (price - privateValue);
      profits += profit;
    }
  }



}
