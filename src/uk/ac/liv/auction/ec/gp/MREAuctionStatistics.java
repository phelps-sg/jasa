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

import java.io.*;

import java.util.*;

import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.util.*;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.stats.*;
import uk.ac.liv.auction.electricity.ElectricityTrader;
import uk.ac.liv.auction.ec.gp.func.*;


/**
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 *
 */

public class MREAuctionStatistics extends KozaStatistics {

  static final String P_PREFIX = "serfilenameprefix";
  static final String DEFAULT_PREFIX = "/tmp/auction";

  protected Individual best[];

  String fileNamePrefix;

  EvolutionState state;

  public void setup( final EvolutionState state, final Parameter base ) {
    super.setup(state, base);
    this.state = state;
    fileNamePrefix = state.parameters.getStringWithDefault(base.push(P_PREFIX), DEFAULT_PREFIX);
  }

  public void finalStatistics( final EvolutionState state, final int result ) {
    //super.finalStatistics(state, result);
    /*findBestOfGeneration();
    for( int i=0; i<state.population.subpops.length; i++ ) {
      System.out.println("Serializing " + best[i]);
      writeObject(best[i], fileNamePrefix + "." + i);
    } */
    writeObject(state, fileNamePrefix + ".state");
  }

  public void postEvaluationStatistics( final EvolutionState state ) {

    // super.postEvaluationStatistics(state);

    findBestOfGeneration(1);
    GPAuctioneer auctioneer = (GPAuctioneer) best[0];

    println("");
    println("Best auctioneer of generation " + state.generation );
    println("=====================================");

    println("Pricing rule:");
    printIndividual(auctioneer);

    println("\nMarket statistics:");
    println(auctioneer.getMarketStats().toString());

    StatsMarketDataLogger logger = auctioneer.getLogStats();
    println("Bid price: " + logger.getBidPriceStats());
    println("");
    println("Ask price: " + logger.getAskPriceStats());
    println("");
    println("Bid quote: " + logger.getBidQuoteStats());
    println("");
    println("Ask quote: " + logger.getAskQuoteStats());
    println("");
    println("Trans price: " + logger.getTransPriceStats());

    println("");
    println("Strategies participating in best auction of generation " + state.generation);
    println("========================================================");
    println("");

    /*
    Iterator strategies = auctioneer.getStrategies().iterator();
    while ( strategies.hasNext() ) {
      GPTradingStrategy strategy = (GPTradingStrategy) strategies.next();
      printIndividual(strategy);
      println("");
      println("Price stats for trader: " + strategy.getPriceStats());
      println("");
      println("-------------");
      println("");
    }
    */
    //TODO
  }

  public void println( String message ) {
    state.output.println(message, Output.V_NO_GENERAL, statisticslog);
  }

  public void printIndividual( GPIndividual individual ) {
    individual.printIndividualForHumans(state, statisticslog, Output.V_NO_GENERAL);
  }


  public void writeObject( Object individual, String fileName ) {
    try {
      FileOutputStream file = new FileOutputStream(fileName);
      ObjectOutputStream out = new ObjectOutputStream(file);
      out.writeObject(individual);
      out.close();
    } catch ( IOException e ) {
      System.err.println("Caught IO Exception: " + e);
      e.printStackTrace();
    }
  }

  public void findBestOfGeneration() {
    findBestOfGeneration(state.population.subpops.length);
  }

  public void findBestOfGeneration(int numSubpops ) {
    best = new Individual[state.population.subpops.length];
    for( int x=0; x<state.population.subpops.length; x++ ) {
      best[x] = state.population.subpops[x].individuals[0];
      for( int y=0; y<state.population.subpops[x].individuals.length; y++ ) {
        if ( state.population.subpops[x].individuals[y].fitness.betterThan(best[x].fitness) ) {
          best[x] = state.population.subpops[x].individuals[y];
        }
      }
    }
  }


}
