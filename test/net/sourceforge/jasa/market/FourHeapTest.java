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

package net.sourceforge.jasa.market;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.jabm.SpringSimulationController;
import net.sourceforge.jasa.agent.MockTrader;
import net.sourceforge.jasa.agent.TradingAgent;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class FourHeapTest extends TestCase {

	TestShoutEngine shoutEngine;
	
	MarketSimulation auction;

	Random randGenerator;

	public FourHeapTest(String name) {
		super(name);
	}
	
	public void initialiseAuction() {
		auction = new MarketSimulation();
		auction.setSimulationController(new SpringSimulationController());
	}

	public void setUp() {
		shoutEngine = new TestShoutEngine();
		randGenerator = new Random();
		initialiseAuction();
//		org.apache.log4j.BasicConfigurator.configure();
	}

	public Order randomShout(MockTrader trader) {
		int quantity = randGenerator.nextInt(50);
		double price = Math.round(randGenerator.nextDouble() * 10000) / 100.0;
		boolean isBid = randGenerator.nextBoolean();
		return new Order(trader, quantity, price, isBid);
	}
	
	/**
	 * Test for bug #2803011 and #3523823
	 */
	public void testSameSide() {
		try {
			
			TradingAgent trader1 = new MockTrader(this, 10, 0, auction);
			
			Order buy = new Order(trader1, 1, 10.0, true);
			Order sell = new Order(trader1, 1, 5.0, false);
			shoutEngine.add(buy);
			shoutEngine.add(sell);
			
			assertNoMatches();
			
			// Test for bug #3523823
			shoutEngine.add(sell);
			shoutEngine.add(buy);
			
			assertNoMatches();
			
		} catch (DuplicateShoutException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void assertNoMatches() {
		// No match should result because the orders are from the same
		// trader
		List<Order> matched = shoutEngine.matchOrders();
		assertTrue("Matching shouts from the same agent", 
				matched.isEmpty());
	}
	
	public void testSimpleMatch() {
		try {
			TradingAgent trader1 = new MockTrader(this, 0, 0, auction);
			TradingAgent trader2 = new MockTrader(this, 0, 0, auction);
			Order buy = new Order(trader1, 1, 10.0, true);
			Order sell = new Order(trader2, 1, 5.0, false);
			shoutEngine.add(buy);
			shoutEngine.add(sell);		
			List<Order> matched = shoutEngine.matchOrders();
			assertTrue(matched.contains(buy));
			assertTrue(matched.contains(sell));
		} catch (DuplicateShoutException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testRandom() {

		int matches = 0;

		try {

			int numOrders = 200;
			MockTrader[] traders = new MockTrader[numOrders];
			for(int i=0; i<numOrders; i++) {
				traders[i] = new MockTrader(this, 0, 0, auction);
			}
			MockTrader cancellingAgent = new MockTrader(this, 0, 0, auction);
			
			Order testRemoveShout = null, testRemoveShout2 = null;

			for (int round = 0; round < 700; round++) {

				System.out.println("Iteration " + round + ".. ");
				
				if (testRemoveShout != null) {
					shoutEngine.remove(testRemoveShout);
					shoutEngine.remove(testRemoveShout2);
				}
				
				System.out.println("Placing " + numOrders + " random orders.. ");
				long t0 = System.currentTimeMillis();
				for (int i = 0; i < numOrders; i++) {
					Order randomShout = randomShout(traders[i]);
					shoutEngine.add(randomShout);
					shoutEngine.checkBalanced();
				}
				long t1 = System.currentTimeMillis();
				long elapsed = t1 - t0;
				System.out.println("completed. (" + elapsed + "ms)");

				shoutEngine.add(testRemoveShout = randomShout(cancellingAgent));
				testRemoveShout2 = new Order(testRemoveShout.getAgent(),
				    testRemoveShout.getQuantity(), testRemoveShout.getPrice(),
				    !testRemoveShout.isBid());
				shoutEngine.add(testRemoveShout2);
				
				int size = shoutEngine.size();
				System.out.println("order book size = " + size);
				
				if ((round % 16) == 0) {
					System.out.println("Clearing the market.. ");
					List<Order> matched = shoutEngine.matchOrders();
					Iterator<Order> i = matched.iterator();
					while (i.hasNext()) {
						matches++;
						Order bid = i.next();
						Order ask = i.next();
						assertTrue(bid.isBid());
						assertTrue(ask.isAsk());
						assertTrue(bid.getPrice() >= ask.getPrice());
						// System.out.print(bid + "/" + ask + " ");
					}
					System.out.println("clearing complete.");
					assertTrue(shoutEngine.sIn.isEmpty());
					assertTrue(shoutEngine.bIn.isEmpty());
//					System.out.println("Removing remaining orders from book");
//					shoutEngine.sOut.clear();
//					shoutEngine.bOut.clear();
				}

				System.out.println("iteration complete.");
			}

		} catch (Exception e) {
//			shoutEngine.printState();
			e.printStackTrace();
			fail();
		}

		System.out.println("Matches = " + matches);

	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(FourHeapTest.class);
	}

}

class TestShoutEngine extends FourHeapOrderBook {

	private static final double TOLERANCE = 0.01;

	protected void preRemovalProcessing() {
		checkBalanced();
	}

	protected void postRemovalProcessing() {
		checkBalanced();
	}

	protected void checkBalanced() {
		int nS = countQty(sIn);
		int nB = countQty(bIn);
		if (nS != nB) {
//			printState();
			throw new Error("shout heaps not balanced nS=" + nS + " nB=" + nB);
		}

		Order bInTop = getLowestMatchedBid();
		Order sInTop = getHighestMatchedAsk();
		Order bOutTop = getHighestUnmatchedBid();
		Order sOutTop = getLowestUnmatchedAsk();

		checkBalanced(bInTop, bOutTop, "bIn >= bOut");
		checkBalanced(sOutTop, sInTop, "sOut >= sIn");
		checkBalanced(sOutTop, bOutTop, "sOut >= bOut");
		checkBalanced(bInTop, sInTop, "bIn >= sIn");
	}

	protected void checkBalanced(Order s1, Order s2, String condition) {
		if (!((s1 == null || s2 == null) || s1.getPrice() >= (s2.getPrice() - TOLERANCE))) {
//			printState();
			System.out.println("shout1 = " + s1);
			System.out.println("shout2 = " + s2);
			throw new RuntimeException("Heaps not balanced! - " + condition);
		}
	}

	public static int countQty(java.util.PriorityQueue<Order> heap) {
		Iterator<Order> i = heap.iterator();
		int qty = 0;
		while (i.hasNext()) {
			Order s = (Order) i.next();
			qty += s.getQuantity();
		}
		return qty;
	}

	public void add(Order shout) throws DuplicateShoutException {
		if (shout.isAsk()) {
			addAsk(shout);
		} else {
			addBid(shout);
		}
	}

}