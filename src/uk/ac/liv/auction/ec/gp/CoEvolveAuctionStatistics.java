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
import uk.ac.liv.ec.gp.GPBestStatistics;


/**
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 *
 */

public class CoEvolveAuctionStatistics extends CoEvolveStrategyStatistics {


  public void postEvaluationStatistics( EvolutionState state ) {

    //TODO super.postEvaluationStatistics(state);

    findBestOfGeneration();
    if ( best[0] instanceof GPAuctioneer ) {

      GPAuctioneer auctioneer = (GPAuctioneer) best[0];

      println("");
      println("Best auctioneer of generation " + state.generation );
      println("=====================================");

      println("Pricing rule:");
      printIndividual(auctioneer);

      println("\nMarket statistics:");
      println(auctioneer.getMarketStats().toString());
      println();

      StatsMarketDataLogger logger = auctioneer.getLogStats();
      println("Bid price: " + logger.getBidPriceStats());
      println();
      println("Ask price: " + logger.getAskPriceStats());
      println();
      println("Bid quote: " + logger.getBidQuoteStats());
      println();
      println("Ask quote: " + logger.getAskQuoteStats());
      println();
      println("Trans price: " + logger.getTransPriceStats());

      println();
      println("Strategies participating in best auction of generation " + state.generation);
      println("========================================================");
      println();

      Iterator strategies = auctioneer.getStrategies().iterator();
      while ( strategies.hasNext() ) {
        Object s = strategies.next();
        if ( s instanceof GPTradingStrategy ) {
          GPTradingStrategy strategy = (GPTradingStrategy) s;
          printIndividual(strategy);
          println();
          println("Price stats for trader: " + strategy.getPriceStats());
          println();
          println("Misbehaved? " + strategy.misbehaved());
          println();
        } //else {
          println(s.toString());
//        }
        println("-------------");
      }

    }
  }



}