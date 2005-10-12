/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import uk.ac.liv.auction.agent.TruthTellingStrategy;
import uk.ac.liv.auction.core.AbstractAuctioneer;
import uk.ac.liv.auction.core.AuctionClosedException;
import uk.ac.liv.auction.core.Auctioneer;
import uk.ac.liv.auction.core.ClearingHouseAuctioneer;
import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.core.UniformPricingPolicy;

import uk.ac.liv.auction.electricity.ElectricityTrader;
import uk.ac.liv.auction.zi.ZITraderAgent;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class SerializationTests extends TestCase {

  /**
   * @uml.property   name="auction"
   * @uml.associationEnd   
   */
  RoundRobinAuction auction;

  public SerializationTests( String name ) {
    super(name);
  }

  public void setUp() {

    auction = new RoundRobinAuction("serialized auction");
    Auctioneer auctioneer = new ClearingHouseAuctioneer(auction);
    ((AbstractAuctioneer) auctioneer)
        .setPricingPolicy(new UniformPricingPolicy(0.5));
    auction.setAuctioneer(auctioneer);

    ZITraderAgent seller1 = new ZITraderAgent(10, 1, true);
    seller1.setStrategy(new TruthTellingStrategy(seller1));

    ZITraderAgent buyer1 = new ZITraderAgent(5, 1, false);
    buyer1.setStrategy(new TruthTellingStrategy(buyer1));

    ElectricityTrader buyer2 = new ElectricityTrader(10, 5, 0, true);
    buyer2.setStrategy(new TruthTellingStrategy(buyer2));

    auction.register(buyer1);
    auction.register(buyer2);
    auction.register(seller1);
  }

  /**
   * Test whether we can serialize an auction without resulting in any
   * NotSerializableExceptions. This simply checks that we have correctly
   * declared our classes and subclasses to implement Serializable.
   *  
   */
  public void testCanSerializeAuction() {
    System.out.println("\ntestAuctionSerialization()\n");

    System.out.print("Testing serialization in initial state.. ");
    if ( !canSerialize(auction) ) {
      fail("cannot serialize auction in initial state");
    }
    System.out.println("done.");

    System.out.print("Testing serialization after a single step.. ");
    auction.begin();
    try {
      auction.step();
    } catch ( AuctionClosedException e ) {
      fail("tried to step through a closed auction");
    }
    if ( !canSerialize(auction) ) {
      fail("cannot serialize auction in initial state");
    }
    System.out.println("done.");

    System.out.print("Testing serialization of closed auction.. ");
    auction.close();
    if ( !canSerialize(auction) ) {
      fail("cannot serialize auction in initial state");
    }
    System.out.println("done.");

  }

  //TODO create unit test to check state after reading object back

  public boolean canSerialize( Object o ) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream objectStream = new ObjectOutputStream(out);
      objectStream.writeObject(o);
      objectStream.close();
      out.close();
      System.out.print(" wrote " + out.size() + " bytes.. ");
    } catch ( IOException e ) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(SerializationTests.class);
  }

}
