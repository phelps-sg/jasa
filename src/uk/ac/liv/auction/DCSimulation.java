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
import uk.ac.liv.auction.stats.*;

import uk.ac.liv.util.io.CSVReader;
import uk.ac.liv.util.CummulativeStatCounter;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class DCSimulation {

  static final String QUOTE_LOG   = "/tmp/auction-quotes.csv";
  static final String TRANS_LOG   = "/tmp/auction-transprice.csv";
  static final String SHOUT_LOG   = "/tmp/auction-shouts.csv";

  static final String BUYER_CONF  = "auction-buyers.csv";
  static final String SELLER_CONF = "auction-sellers.csv";

  static final int MAX_ROUNDS     = 4000;


  public static void main( String[] args ) {

    int k0Wins = 0;
    int k1Wins = 0;

    try {

      for( int round=0; round<5000; round++ ) {

        RoundRobinAuction auction = new RandomRobinAuction();
        ContinuousDoubleAuctioneer auctioneer = new ContinuousDoubleAuctioneer(auction,0.0);
        auction.setAuctioneer(auctioneer);

        Random randGenerator = new Random();

        auction.setMaximumRounds(MAX_ROUNDS);

        // Set up data logs
        /*
        CSVMarketDataLogger logger = new CSVMarketDataLogger();
        logger.setCSVQuoteLog(new FileOutputStream(QUOTE_LOG));
        logger.setCSVTransPriceLog(new FileOutputStream(TRANS_LOG));
        logger.setCSVShoutLog(new FileOutputStream(SHOUT_LOG)); */
        StatsMarketDataLogger logger = new StatsMarketDataLogger();
        auction.setMarketDataLogger(logger);
  /*
        registerTraders(auction,BUYER_CONF,false);
        registerTraders(auction,SELLER_CONF,true);
  */
        for( int i=0; i<10; i++ ) {
          long limitPrice = (long) (randGenerator.nextDouble()*200);
          boolean isSeller = randGenerator.nextBoolean();
          ZISTraderAgent trader = new ZISTraderAgent(limitPrice, 100, isSeller);
          auction.register(trader);
        }

        System.out.println("k = 0");
        auction.run();
        double k0Result = doStats(auction, logger);

        // Now try it with a 1st price auction
        System.out.println("k = 1");
        auctioneer.setK(1.0);

        auction.reset();
        Iterator i = auction.getTraderIterator();
        while ( i.hasNext() ) {
          AbstractTraderAgent trader = (AbstractTraderAgent) i.next();
          trader.reset();
        }

        auction.run();
        double k1Result = doStats(auction, logger);

        if ( k0Result < k1Result ) {
          System.out.println("*** k=0 WINS ***");
          k0Wins++;
        } else {
          System.out.println("*** k=1 WINS ***");
          k1Wins++;
        }

      }

      System.out.println("k0 wins: " + k0Wins);
      System.out.println("k1 wins: " + k1Wins);


    } catch ( Exception e ) {
      e.printStackTrace();
    }

    // System.exit(0);
  }

  public static double doStats( RoundRobinAuction auction, StatsMarketDataLogger logger ) {
    // Print out stats
    System.out.println("Stats");
    System.out.println("--------------------------");
    System.out.println(logger.getTransPriceStats());
    System.out.println(logger.getAskPriceStats());
    System.out.println(logger.getBidPriceStats());
    System.out.println(logger.getBidQuoteStats());
    System.out.println(logger.getAskQuoteStats());
    MetaMarketStats marketStats = new MetaMarketStats(0,200,auction.getTraderList());
    marketStats.calculateSupplyAndDemand();
    List equilibria = marketStats.getMarketEquilibria();
    Iterator i = equilibria.iterator();
    CummulativeStatCounter efficiencyStats =
        new CummulativeStatCounter("efficiency");
    while ( i.hasNext() ) {
      Long price = (Long) i.next();
      Integer qty = (Integer) i.next();
      System.out.println("market equilibria at " + price + " / " + qty);
      double efficiencyCoef = logger.getTransPriceStats().getVarCoef(price.doubleValue());
      System.out.println("efficiency coef = " + efficiencyCoef);
      if ( ! Double.isNaN(efficiencyCoef) ) {
        efficiencyStats.newData(efficiencyCoef);
      }
    }
    System.out.println(efficiencyStats);
    return efficiencyStats.getMin();
  }

  public static void registerTraders( RoundRobinAuction auction, String traderConfigFile,
                                      boolean areSellers )
                    throws IOException, FileNotFoundException, ClassNotFoundException {

    // Set up the parser for the trader config file

    ArrayList traderFormat = new ArrayList(2);  // Each record has 2 fields
    traderFormat.add(0, Long.class);            // First field is Long
    traderFormat.add(1, Integer.class);         // Second field is Integer
    CSVReader traderConfig =
      new CSVReader(new FileInputStream(traderConfigFile), traderFormat);

    // Iterate over all the traders in the trader config file
    List traderRecord;
    while ( (traderRecord = traderConfig.nextRecord()) != null ) {

      // Get the fields from this trader's record
      Long limitPrice = (Long) traderRecord.get(0);
      Integer tradeEntitlement = (Integer) traderRecord.get(1);

      // Construct a ZI-C trader for this record
      ZISTraderAgent trader =
        new ZISTraderAgent(limitPrice.longValue(), tradeEntitlement.intValue(),
                            areSellers);

      // Register it in the auction
      auction.register(trader);
    }
  }
}