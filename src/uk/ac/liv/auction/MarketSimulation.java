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

package uk.ac.liv.auction;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.*;

import uk.ac.liv.util.io.CSVReader;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import java.io.FileOutputStream;
import java.io.FileInputStream;


public class MarketSimulation {

  static final int NUM_TRADERS = 5;
  static final int MAX_ROUNDS = 20;

  static Random randGenerator = new Random();

  public MarketSimulation() {
  }

  public static void main( String[] args ) {

    try {

      RoundRobinAuction auction = new RoundRobinAuction("Apples");
      //InteractiveTraderAgent seller = new InteractiveTraderAgent(100,100);
      ContinuousDoubleAuctioneer auctioneer =
        new ContinuousDoubleAuctioneer(auction);
      auctioneer.setK(0.0);
      auction.setAuctioneer(auctioneer);

      StatsMarketDataLogger logger = new StatsMarketDataLogger();
      auction.setMarketDataLogger(logger);

      auction.activateGUIConsole();

      auction.setMaximumRounds(MAX_ROUNDS);

      for(int i=0; i<NUM_TRADERS; i++ ) {
        // auction.register( new ZICTraderAgent(limit,DAILY_ENTITLEMENT,seller) );
        auction.register(new InteractiveTraderAgent(100,1000));
      }

      auction.run();

      System.out.println("Stats");
      System.out.println("-----");
      System.out.println(logger.getTransPriceStats());
      System.out.println(logger.getBidPriceStats());
      System.out.println(logger.getAskPriceStats());

    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }
}