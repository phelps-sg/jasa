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

import uk.ac.liv.auction.agent.ZPTraderAgent;

import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.core.ContinuousDoubleAuctioneer;

import java.util.Random;


public class SupplyRingSimulation {

  static final int NUM_TRADERS = 2000;

  public static void main( String[] args ) {

    Random rand = new Random();

    RoundRobinAuction auction = new RoundRobinAuction("SupplyRing");
    auction.setAuctioneer(new ContinuousDoubleAuctioneer(auction));

    for( int i=0; i<NUM_TRADERS; i++ ) {
      int stock = rand.nextInt(100);
      auction.register( new ZPTraderAgent(stock) );
    }

    auction.activateGUIConsole();
    auction.run();
  }
}