/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2009 Steve Phelps
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

package net.sourceforge.jasa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import cern.jet.random.engine.MersenneTwister64;

import net.sourceforge.jasa.agent.SimpleTradingAgent;
import net.sourceforge.jasa.agent.TokenTradingAgent;
import net.sourceforge.jasa.agent.strategy.TruthTellingStrategy;
import net.sourceforge.jasa.market.AuctionClosedException;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.market.auctioneer.AbstractAuctioneer;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.market.rules.UniformPricingPolicy;
import net.sourceforge.jasa.sim.PRNGTestSeeds;



import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class SerializationTests extends TestCase {

	/**
	 * @uml.property name="market"
	 * @uml.associationEnd
	 */
	MarketFacade auction;

	public SerializationTests(String name) {
		super(name);
	}

	public void setUp() {
		auction = new MarketFacade(
				new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED));
		Auctioneer auctioneer = new ClearingHouseAuctioneer(auction);
		((AbstractAuctioneer) auctioneer)
		    .setPricingPolicy(new UniformPricingPolicy(0.5));
		auction.setAuctioneer(auctioneer);

		TokenTradingAgent seller1 = new TokenTradingAgent(10, 1, true, auction);
		seller1.setStrategy(new TruthTellingStrategy(seller1));

		TokenTradingAgent buyer1 = new TokenTradingAgent(5, 1, false, auction);
		buyer1.setStrategy(new TruthTellingStrategy(buyer1));

		SimpleTradingAgent buyer2 = new SimpleTradingAgent(5, true, auction);
		buyer2.setStrategy(new TruthTellingStrategy(buyer2));

		auction.register(buyer1);
		auction.register(buyer2);
		auction.register(seller1);
	}

	/**
	 * Test whether we can serialize an market without resulting in any
	 * NotSerializableExceptions. This simply checks that we have correctly
	 * declared our classes and subclasses to implement Serializable.
	 * 
	 */
	public void testCanSerializeAuction() {
		System.out.println("\ntestAuctionSerialization()\n");

		System.out.print("Testing serialization in initial state.. ");
		if (!canSerialize(auction)) {
			fail("cannot serialize market in initial state");
		}
		System.out.println("done.");

		System.out.print("Testing serialization after a single step.. ");
		auction.begin();
		try {
			auction.step();
		} catch (AuctionClosedException e) {
			fail("tried to step through a closed market");
		}
		if (!canSerialize(auction)) {
			fail("cannot serialize market in initial state");
		}
		System.out.println("done.");

		System.out.print("Testing serialization of closed market.. ");
		auction.close();
		if (!canSerialize(auction)) {
			fail("cannot serialize market in initial state");
		}
		System.out.println("done.");

	}

	// TODO create unit test to check state after reading object back

	public boolean canSerialize(Object o) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(out);
			objectStream.writeObject(o);
			objectStream.close();
			out.close();
			System.out.print(" wrote " + out.size() + " bytes.. ");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(SerializationTests.class);
	}

}
