/*
 * JASA Java Auction Simulator API
 * Copyright (C) Steve Phelps
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

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.electricity.*;
import uk.ac.liv.auction.ec.gp.func.*;

import uk.ac.liv.ai.learning.*;

import uk.ac.liv.util.io.CSVReader;
import uk.ac.liv.util.*;

import ec.util.*;
import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;

import java.io.*;
import java.util.*;


public class AuctionProblem extends GPProblem implements SimpleProblemForm {

  static final String P_TRADERS = "numtraders";
  static final String P_ROUNDS = "maxrounds";
  static final String P_ITERATIONS = "iterations";

  static Random rand = new Random();

  static final String BUYER_CONF  = "auction-buyers.csv";
  static final String SELLER_CONF = "auction-sellers.csv";

  public int numTraders;
  public int maxRounds;
  public int iterations;

  // We'll deep clone this anyway, even though we don't
  // need it by default!
  public GPAuctionData input;

  RoundRobinAuction auction;


  public Object protoClone() throws CloneNotSupportedException {

    AuctionProblem myobj = (AuctionProblem) super.protoClone();
    myobj.numTraders = numTraders;
    return myobj;
  }

  public void setup( EvolutionState state, Parameter base ) {

    super.setup(state,base);

    String buyerConfig = state.parameters.getString(base.push("buyerconf"), null);
    String sellerConfig = state.parameters.getString(base.push("sellerconf"), null);

    // numTraders = state.parameters.getInt(base.push(P_TRADERS),null,1);
    maxRounds = state.parameters.getInt(base.push(P_ROUNDS),null,1);

    iterations = state.parameters.getInt(base.push(P_ITERATIONS),null,1);

    System.out.println("Using parameters");
    System.out.println("maxRounds = " + maxRounds);
    System.out.println("iterations = " + iterations);

    auction = new RoundRobinAuction();
    auction.setMaximumRounds(maxRounds);

    try {
      registerTraders(auction, buyerConfig, false);
      registerTraders(auction, sellerConfig, true);
    } catch ( Exception e ) {
      e.printStackTrace();
    }

  }

  public void evaluate( EvolutionState state,
			 Individual individual,
			 int thread ) {

    if ( individual.evaluated ) {
      return;
    }

    GPAuctioneer auctioneer = (GPAuctioneer) individual;

    auctioneer.setGPContext(state, thread, stack, this);
    auction.setAuctioneer(auctioneer);
    auctioneer.setAuction(auction);


    CummulativeStatCounter mpStats = new CummulativeStatCounter("relMarketPower");
    CummulativeStatCounter efficiencyStats = new CummulativeStatCounter("efficiency");

    for( int i=0; i<iterations; i++ ) {

      // Reset the agents
      Iterator traders = auction.getTraderIterator();
      while ( traders.hasNext() ) {
        MREElectricityTrader agent = (MREElectricityTrader) traders.next();
        NPTRothErevLearner learner = (NPTRothErevLearner) agent.getLearner();
        agent.reset();
        learner.setSeed( System.currentTimeMillis() );
      }
      // Reset the auction
      auction.reset();

      // Run the market!
      auction.run();

      // Calculate and maintain stats
      ElectricityStats stats = new ElectricityStats(0, 200, auction);
      double relMarketPower = (double) (stats.mPB + stats.mPS) / 2.0f;
      mpStats.newData(relMarketPower);
      efficiencyStats.newData(stats.eA);
    }

    float fitness = ((float) Math.abs(mpStats.getMean())
                      + 1-((float) efficiencyStats.getMean()/100))/2;
    if ( Float.isNaN(fitness) ) {
      fitness = 100;
    }

    KozaFitness f = ((KozaFitness)individual.fitness);
    f.setStandardizedFitness(state, fitness);

    individual.evaluated = true;


    System.out.println("\nFitness = " + fitness);
    System.out.println(efficiencyStats);
    System.out.println(mpStats);
  }

  public static void registerTraders( RoundRobinAuction auction, String traderConfigFile,
                                      boolean areSellers )
                    throws IOException, FileNotFoundException, ClassNotFoundException {

    // Set up the parser for the trader config file

    ArrayList traderFormat = new ArrayList(2);  // Each record has 2 fields
    traderFormat.add(0, Double.class);
    traderFormat.add(1, Integer.class);
    CSVReader traderConfig =
      new CSVReader(new FileInputStream(traderConfigFile), traderFormat);

    // Iterate over all the traders in the trader config file
    List traderRecord;
    while ( (traderRecord = traderConfig.nextRecord()) != null ) {

      // Get the fields from this trader's record
      Double privateValue = (Double) traderRecord.get(0);
      Integer capacity = (Integer) traderRecord.get(1);

      // Construct a trader for this record
      MREElectricityTrader trader =
        new MREElectricityTrader(capacity.intValue(), privateValue.doubleValue(), 0, areSellers);

      // Register it in the auction
      auction.register(trader);
    }
  }

  public static void main( String[] args ) {
    ec.Evolve.main( new String[] { "-file", "ecj.params/gpauctioneer.params" } );
  }

}
