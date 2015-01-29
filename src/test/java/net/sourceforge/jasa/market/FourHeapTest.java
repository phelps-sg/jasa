/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2013 Steve Phelps
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
import net.sourceforge.jasa.test.PRNGTestSeeds;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class FourHeapTest extends TestCase {

	TestShoutEngine book;
	
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
		book = new TestShoutEngine();
		randGenerator = new Random(PRNGTestSeeds.UNIT_TEST_SEED);
		initialiseAuction();
//		org.apache.log4j.BasicConfigurator.configure();
	}

	public Order randomShout(MockTrader trader) {
		int quantity = randGenerator.nextInt(50);
		double price = Math.round(randGenerator.nextDouble() * 10000) / 100.0;
		boolean isBid = randGenerator.nextBoolean();
		return new Order(trader, quantity, price, isBid);
	}
	
	public void testHeapOrdering() {
		TradingAgent trader1 = new MockTrader(this, 10, 0, auction);
		Order buy1 = new Order(trader1, 1, 2.0, true);
		Order buy2 = new Order(trader1, 1, 3.0, true);
		
		book.bOut.add(buy1);
		book.bOut.add(buy2);
		
		assertTrue(book.bOut.peek() == buy2);
		
		Order sell1 = new Order(trader1, 1, 5.0, true);
		Order sell2 = new Order(trader1, 1, 6.0, true);
		
		book.sOut.add(sell1);
		book.sOut.add(sell2);
		
		assertTrue(book.sOut.peek() == sell1);
	}
	
	public void testHeapOrderingReverseInsertion() {
		TradingAgent trader1 = new MockTrader(this, 10, 0, auction);
		Order buy1 = new Order(trader1, 1, 2.0, true);
		Order buy2 = new Order(trader1, 1, 3.0, true);
		
		book.bOut.add(buy2);
		book.bOut.add(buy1);
		
		assertTrue(book.bOut.peek() == buy2);
		
		Order sell1 = new Order(trader1, 1, 5.0, true);
		Order sell2 = new Order(trader1, 1, 6.0, true);
		
		book.sOut.add(sell2);
		book.sOut.add(sell1);
		
		assertTrue(book.sOut.peek() == sell1);
	}
	
	/**
	 * Test for bug #2803011 and #3523823
	 */
	public void testSameSide() {
		try {
			
			TradingAgent trader1 = new MockTrader(this, 10, 0, auction);
			
			Order buy1 = new Order(trader1, 1, 10.0, true);
			Order sell1 = new Order(trader1, 1, 5.0, false);
			book.add(buy1);
			book.add(sell1);
			
			assertNoMatches();
			
			// Test for bug #3523823
			book.add(sell1);
			book.add(buy1);
			
			assertNoMatches();
			
			TradingAgent trader2 = new MockTrader(this, 10, 0, auction);
			Order buy2 = new Order(trader2, 1, 6.0, true);
			assertMatched(buy2, true);
			
			Order sell2Out = new Order(trader2, 1, 20, false);
			assertMatched(sell2Out, false);
			
			Order sell2 = new Order(trader2, 1, 9, false);
			assertMatched(sell2, true);
			
		} catch (DuplicateShoutException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void assertMatched(Order order, boolean matched) throws DuplicateShoutException {
		book.add(order);
		List<Order> matches = book.matchOrders();
		System.out.println(matches);
		assertEquals("bid from different trader " + 
							(matched ? "" : "not") + " matched",
						matches.contains(order), matched);
		assertNoSameSideTrades(matches);
	}
	
	public void assertNoSameSideTrades(List<Order> matches) {
		Iterator<Order> i = matches.iterator();
		while (i.hasNext()) {
			Order buy = i.next();
			Order sell = i.next();
			assertTrue("Matching orders from the same trader",
							buy.getAgent() != sell.getAgent());
		}
	}
	
	public void assertNoMatches() {
		// No match should result because the orders are from the same
		// trader
		List<Order> matched = book.matchOrders();
		assertTrue("Matching shouts from the same agent", 
				matched.isEmpty());
	}
	
	public void testSimpleMatch() {
		try {
			TradingAgent trader1 = new MockTrader(this, 0, 0, auction);
			TradingAgent trader2 = new MockTrader(this, 0, 0, auction);
			Order buy = new Order(trader1, 1, 10.0, true);
			Order sell = new Order(trader2, 1, 5.0, false);
			book.add(buy);
			book.add(sell);		
			List<Order> matched = book.matchOrders();
			assertTrue(matched.contains(buy));
			assertTrue(matched.contains(sell));
		} catch (DuplicateShoutException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test that a buy order can be matched
	 * with a sell order of lower volume, resulting
	 * in the remaining volume being retained on the book.
	 */
	public void testPartialBuyFills() {
		try {

			TradingAgent trader1 = new MockTrader(this, 0, 0, auction);
			TradingAgent trader2 = new MockTrader(this, 0, 0, auction);
			Order buy = new Order(trader1, 10, 10.0, true);
			Order sell = new Order(trader2, 5, 5.0, false);
			book.add(buy);
			book.add(sell);

			List<Order> matched = book.matchOrders();
			System.out.println(matched);

			assertTrue(matched.contains(sell));

			// Order should remain on the book with the outstanding volume
			assertTrue(buy.getQuantity() == 5);
			assertTrue(book.bOut.contains(buy.getChild()));

		} catch (DuplicateShoutException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test that a sell order can be matched
	 * with a buy order of lower volume, resulting
	 * in the remaining volume being retained on the book.
	 */
	public void testPartialSellFills() {
		try {
			TradingAgent trader1 = new MockTrader(this, 0, 0, auction);
			TradingAgent trader2 = new MockTrader(this, 0, 0, auction);
			Order buy = new Order(trader1, 5, 10.0, true);
			Order sell = new Order(trader2, 10, 5.0, false);
			book.add(buy);
			book.add(sell);
			List<Order> matched = book.matchOrders();
			System.out.println(matched);
			assertTrue(matched.contains(buy));
//			assertTrue(matched.contains(sell));
			
			// Order should remain on the book with the oustanding volume
			assertTrue(sell.getQuantity() == 5);
//			assertTrue(book.sOut.contains(sell.getChild().getChild()));

		} catch (DuplicateShoutException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testWalkingBookBuy() {
		try {
			// Create two test traders
			TradingAgent trader1 = new MockTrader(this, 0, 0, auction);
			TradingAgent trader2 = new MockTrader(this, 0, 0, auction);
			
			// The total volume of buy orders
			int buyQuantity = 20;
			
			// The volume of the sell orders
			int sell1Quantity = 10;
			int sell2Quantity = 8;
			
			// The total tradable volume and remaining volumes
			int tradableVolume = sell1Quantity + sell2Quantity;
			int untradedVolume = buyQuantity - tradableVolume;

			// Create the limit-orders
			Order buy = new Order(trader1, buyQuantity, 10.0, true);
			Order sell1 = new Order(trader2, sell1Quantity, 5.0, false);
			Order sell2 = new Order(trader2, sell2Quantity, 5.0, false);

			// Submit them to the exchange
			book.add(buy);
			book.add(sell1);
			book.add(sell2);
			
			// Uncross the market
			List<Order> matched = book.matchOrders();
			System.out.println(matched);

			// The total volume of uncrossed orders should be 18.
			// We multiply by two because we count both bids and asks.
			assertTrue(Order.totalVolume(matched) == tradableVolume * 2);
			
			// The untraded volume is held in the child of of the child
			// the original order, since we have split it three times.
			Order remainingOrder = buy.getUnfilledFraction().get(0);
			assertTrue(remainingOrder.getQuantity() == untradedVolume);

			// An order with this remaining volume should remain on the book.
			assertTrue(book.bOut.contains(remainingOrder));

			// There should be no remaining volume on the ask side.
			assertTrue(book.sOut.isEmpty());

		} catch (DuplicateShoutException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testWalkingBookDifferentPrices() {

		try {

			// Create two test traders
			TradingAgent trader1 = new MockTrader(this, 0, 0, auction);
			TradingAgent trader2 = new MockTrader(this, 0, 0, auction);

			// The total volume of buy orders
			int buyQuantity = 20;

			// The volume of the sell orders
			int sell1Quantity = 10;
			int sell2Quantity = 8;

			// The total tradable volume and remaining volumes
			int tradableVolume = sell1Quantity + sell2Quantity;
			int untradedVolume = buyQuantity - tradableVolume;

			// Create the limit-orders
			Order buy = new Order(trader1, buyQuantity, 10.0, true);
			Order sell1 = new Order(trader2, sell1Quantity, 5.0, false);
			Order sell2 = new Order(trader2, sell2Quantity, 6.0, false);

			// Submit them to the exchange
			book.add(buy);
			book.add(sell1);
			book.add(sell2);

			// Uncross the market
			List<Order> matched = book.matchOrders();
			System.out.println(matched);

			// The total volume of uncrossed orders should be 18.
			// We multiply by two because we count both bids and asks.
			assertTrue(Order.totalVolume(matched) == tradableVolume * 2);

			// The untraded volume is held in the child of of the child
			// the original order, since we have split it three times.
			Order remainingOrder = buy.getUnfilledFraction().get(0);
			assertTrue(remainingOrder.getQuantity() == untradedVolume);

			// An order with this remaining volume should remain on the book.
			assertTrue(book.bOut.contains(remainingOrder));

			// There should be no remaining volume on the ask side.
			assertTrue(book.sOut.isEmpty());

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
					book.remove(testRemoveShout);
					book.remove(testRemoveShout2);
				}
				
				System.out.println("Placing " + numOrders + " random orders.. ");
				long t0 = System.currentTimeMillis();
				for (int i = 0; i < numOrders; i++) {
					Order randomShout = randomShout(traders[i]);
					System.out.println(randomShout);
					book.add(randomShout);
					book.checkBalanced();
				}
				long t1 = System.currentTimeMillis();
				long elapsed = t1 - t0;
				System.out.println("completed. (" + elapsed + "ms)");

				book.add(testRemoveShout = randomShout(cancellingAgent));
				testRemoveShout2 = new Order(testRemoveShout.getAgent(),
				    testRemoveShout.getQuantity(), testRemoveShout.getPrice(),
				    !testRemoveShout.isBid());
				book.add(testRemoveShout2);
				
				int size = book.size();
				System.out.println("order book size = " + size);
				
				if ((round % 16) == 0) {
					System.out.println("Clearing the market.. ");
					List<Order> matched = book.matchOrders();
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
					assertTrue(book.sIn.isEmpty());
					assertTrue(book.bIn.isEmpty());
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

	@Override
	public void checkIntegrity() {
		checkBalanced();
	}

	protected void checkBalanced() {
		int nS = Order.totalVolume(sIn);
		int nB = Order.totalVolume(bIn);
		if (nS != nB) {
//			printState();
			throw new Error("shout heaps not balanced nS=" + nS + " nB=" + nB);
		}

//		Order bInTop = getLowestMatchedBid();
//		Order sInTop = getHighestMatchedAsk();
//		Order bOutTop = getHighestUnmatchedBid();
//		Order sOutTop = getLowestUnmatchedAsk();
//
//		checkBalanced(bInTop, bOutTop, "bIn >= bOut");
//		checkBalanced(sOutTop, sInTop, "sOut >= sIn");
//		checkBalanced(sOutTop, bOutTop, "sOut >= bOut");
//		checkBalanced(bInTop, sInTop, "bIn >= sIn");
	}

	protected void checkBalanced(Order s1, Order s2, String condition) {
		if (!((s1 == null || s2 == null) || s1.getPrice() >= (s2.getPrice() - TOLERANCE))) {
//			printState();
			System.out.println("shout1 = " + s1);
			System.out.println("shout2 = " + s2);
			throw new RuntimeException("Heaps not balanced! - " + condition);
		}
	}

	public void add(Order shout) throws DuplicateShoutException {
		if (shout.isAsk()) {
			addAsk(shout);
		} else {
			addBid(shout);
		}
	}

}