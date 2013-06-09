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

package net.sourceforge.jasa.agent;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SpringSimulationController;
import net.sourceforge.jabm.event.EventListener;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.init.BasicAgentInitialiser;
import net.sourceforge.jabm.mixing.RandomRobinAgentMixer;
import net.sourceforge.jasa.market.AuctionClosedException;
import net.sourceforge.jasa.market.MarketSimulation;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.auctioneer.AbstractAuctioneer;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.market.rules.UniformPricingPolicy;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

public class AbstractTraderAgentTest extends TestCase implements EventListener {

	MockTrader trader1;

	MockTrader trader2;

	MarketSimulation auction;	

	public static final int TRADER1_STOCK = 0;

	public static final int TRADER2_STOCK = 5;

	public static final double TRADER1_FUNDS = 10000;

	public static final double TRADER2_FUNDS = 20000;

	public static final double TRADER1_VALUE = 250;

	public static final double TRADER2_VALUE = 140;

	public AbstractTraderAgentTest(String name) {
		super(name);
	}

	public void setUp() {
		RandomEngine prng = new MersenneTwister64();		
		auction = new MarketSimulation();
		auction.setSimulationController(new SpringSimulationController());
		auction.setPopulation(new Population());
		auction.setAgentMixer(new RandomRobinAgentMixer(prng));
		auction.setAgentInitialiser(new BasicAgentInitialiser());
		AbstractAuctioneer auctioneer = new ClearingHouseAuctioneer(auction);
		auctioneer.setPricingPolicy(new UniformPricingPolicy(0.5));
		auction.setAuctioneer(auctioneer);
		
		MockStrategy strategy1 = new MockStrategy(new Order[] {
			    new Order(trader1, 1, TRADER1_VALUE - 100, true),
			    new Order(trader1, 1, TRADER1_VALUE - 50, true),
			    new Order(trader1, 1, TRADER1_VALUE, true),
			    new Order(trader1, 1, TRADER1_VALUE - 100, true) }, auction);
		
		trader1 = new MockTrader(this, TRADER1_STOCK, TRADER1_FUNDS,
				TRADER1_VALUE, strategy1, auction);

		MockStrategy strategy2 = new MockStrategy(new Order[] {
			    new Order(trader2, 1, TRADER2_VALUE + 100, false),
			    new Order(trader2, 1, TRADER2_VALUE + 50, false),
			    new Order(trader2, 1, TRADER2_VALUE, false),
			    new Order(trader2, 1, TRADER2_VALUE + 100, false) }, auction);

		trader2 = new MockTrader(this, TRADER2_STOCK, TRADER2_FUNDS,
				TRADER2_VALUE, strategy2, auction);
		
		auction.register(trader1);
		auction.register(trader2);
	
		auction.initialise();
	}

	public void testPurchase() {
		System.out.println("trader1 = " + trader1);
		System.out.println("trader2 = " + trader2);
		trader1.getAccount().transfer(trader2.getAccount(), 5000);
		trader2.getCommodityHolding().transfer(trader1.getCommodityHolding(), 5);
		System.out.println("after purchase");
		System.out.println("trader1 = " + trader1);
		System.out.println("trader2 = " + trader2);
		assertTrue(trader1.getStock() == TRADER1_STOCK + 5);
		assertTrue(trader2.getStock() == TRADER2_STOCK - 5);
		assertTrue(trader1.getFunds() == TRADER1_FUNDS - 5000);
		assertTrue(trader2.getFunds() == TRADER2_FUNDS + 5000);
		trader1.initialise();
		assertTrue(trader1.getFunds() == TRADER1_FUNDS);
		assertTrue(trader1.getStock() == TRADER1_STOCK);
		assertTrue(trader1.getLastPayoff() == 0);
		assertTrue(trader1.getTotalPayoff() == 0);
		trader2.initialise();
		assertTrue(trader2.getFunds() == TRADER2_FUNDS);
		assertTrue(trader2.getStock() == TRADER2_STOCK);
		assertTrue(trader2.getLastPayoff() == 0);
		assertTrue(trader2.getTotalPayoff() == 0);

	}

	public void testLastOrderFilled() {

		assertTrue(!trader1.lastOrderFilled());
		assertTrue(!trader2.lastOrderFilled());

		auction.begin();

		try {

			auction.step();
			assertTrue(!trader1.lastOrderFilled());
			assertTrue(!trader2.lastOrderFilled());

			auction.step();
			assertTrue(trader1.lastOrderFilled());
			assertTrue(trader2.lastOrderFilled());

			auction.step();
			assertTrue(trader1.lastOrderFilled());
			assertTrue(trader2.lastOrderFilled());

			auction.step();
			assertTrue(!trader1.lastOrderFilled());
			assertTrue(!trader2.lastOrderFilled());

		} catch (AuctionClosedException e) {
			fail("we tried to step through a closed market.");
		}

	}

	public void testLastProfit() {

		assertTrue(trader1.getLastPayoff() == 0);
		assertTrue(trader2.getLastPayoff() == 0);

		auction.begin();

		try {

			auction.step();
			assertTrue(trader1.getLastPayoff() == 0);
			assertTrue(trader2.getLastPayoff() == 0);

			auction.step();
			assertTrue(trader1.getLastPayoff() == 55);
			assertTrue(trader2.getLastPayoff() == 55);

			auction.step();
			assertTrue(trader1.getLastPayoff() == 55);
			assertTrue(trader2.getLastPayoff() == 55);

			auction.step();
			assertTrue(trader1.getLastPayoff() == 0);
			assertTrue(trader2.getLastPayoff() == 0);

		} catch (AuctionClosedException e) {
			fail("we tried to step through a closed market.");
		}

	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(AbstractTraderAgentTest.class);
	}

	@Override
	public void eventOccurred(SimEvent event) {
		System.out.println(event);
	}
}
