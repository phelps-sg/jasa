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

package test.uk.ac.liv.auction.core;

import junit.framework.*;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.StatsMarketDataLogger;
import uk.ac.liv.auction.electricity.*;

import ec.util.MersenneTwisterFast;

import java.util.*;


public class ElectricityAuctionTest extends TestCase {

  RandomRobinAuction auction;
  HashMap gridGraph;
  ElectricityTrader traders[];
  StatsMarketDataLogger logger;
  Auctioneer auctioneer;

  public ElectricityAuctionTest( String name ) {
    super(name);
  }

  public void setUp() {
    gridGraph = new HashMap();
    traders = new ElectricityTrader[3];

    traders[0] = new MREElectricityTrader(20, 50, 0, true);
    traders[1] = new MREElectricityTrader(8, 70, 0, false);
    traders[2] = new MREElectricityTrader(3, 80, 0, false);

    HashMap trader0ATC = new HashMap();
    trader0ATC.put(traders[1], new Integer(5));
    trader0ATC.put(traders[2], new Integer(4));
    gridGraph.put(traders[0], trader0ATC);

    HashMap trader1ATC = new HashMap();
    trader1ATC.put(traders[0], new Integer(5));
    trader1ATC.put(traders[2], new Integer(10));
    gridGraph.put(traders[1], trader1ATC);

    HashMap trader2ATC = new HashMap();
    trader2ATC.put(traders[0], new Integer(4));
    trader2ATC.put(traders[1], new Integer(3));
    gridGraph.put(traders[2], trader2ATC);

    TransmissionGrid grid = new TransmissionGrid(gridGraph);

    auction = new RandomRobinAuction("Electricity Auction Test");
    auctioneer = new ElectricityAuctioneer(grid, auction);
    auction.setAuctioneer(auctioneer);

    for( int i=0; i<3; i++ ) {
      auction.register(traders[i]);
    }

    logger = new StatsMarketDataLogger();
    auction.setMarketDataLogger(logger);

    auction.setMaximumRounds(1000);

    auction.activateGUIConsole();
  }


  public void testRun() {
    auction.reset();
    auction.run();
    System.out.println(logger.getTransPriceStats());
    System.out.println(logger.getAskPriceStats());
    System.out.println(logger.getBidPriceStats());
    long totalProfits = 0;
    for( int i=0; i<3; i++ ) {
      System.out.println(traders[i]);
      totalProfits += traders[i].getProfits();
    }
    System.out.println("Total profits = " + totalProfits);
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(ElectricityAuctionTest.class);
  }

}

