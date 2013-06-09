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

import junit.framework.TestCase;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.AgentArrivalEvent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketSimulation;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;

public class MockTrader extends SimpleTradingAgent {

	public Order lastWinningShout = null;

	public double lastWinningPrice = 0;

	public int lastWinningQuantity;

	public int requestShoutDay = -1;

	public boolean receivedAuctionOpen = false;

	public boolean receivedAuctionClosed = false;

	public boolean receivedAuctionClosedAfterAuctionOpen = false;

	public boolean receivedEndOfDayAfterRequestShout = true;

	public int receivedRequestShout = 0;

	TestCase test;

	static Logger logger = Logger.getLogger(AbstractTradingAgent.class);

	public MockTrader(TestCase test, int stock, long funds,
			EventScheduler scheduler) {
		super(stock, funds, scheduler);
		this.test = test;
	}

	public MockTrader(TestCase test, int stock, double funds,
	    double privateValue, EventScheduler scheduler) {
		super(stock, funds, privateValue, scheduler);
		this.test = test;
	}

	public MockTrader(TestCase test, int stock, double funds, double privateValue,
			TradingStrategy strategy, EventScheduler scheduler) {
		super(stock, funds, privateValue, strategy, scheduler);
		this.test = test;
	}

	public MockTrader(TestCase test,
			int stock, double funds, double privateValue,
			MockStrategy strategy1, MarketSimulation auction) {
		this(test, stock, funds, privateValue, strategy1, auction.getSimulationController());
	}

	public MockTrader(TestCase test, int stock, double funds, double privateValue,
			MarketSimulation auction) {
		this(test, stock, funds, privateValue, auction.getSimulationController());
	}

	public MockTrader(TestCase test, int stock, int funds,
			MarketSimulation auction) {
		this(test, stock, funds, auction.getSimulationController());
	}

	@Override
	public void orderFilled(Market auction, Order shout, double price,
	    int quantity) {
		super.orderFilled(auction, shout, price, quantity);
		System.out.println("order accepted " + shout + " at price " + price
		    + " and quantity " + quantity);
		lastWinningShout = shout;
		lastWinningPrice = price;
	}

	public int determineQuantity(Market auction) {
		return 1;
	}

	@Override
	public void onAgentArrival(Market auction, AgentArrivalEvent event) {
		super.onAgentArrival(auction, event);
		System.out.println(this + ": placed " + currentOrder);
		receivedRequestShout++;
		requestShoutDay = auction.getDay();
	}

	@Override
	public void onMarketOpen(MarketEvent event) {
		super.onMarketOpen(event);
		receivedAuctionOpen = true;
	}

	@Override
	public void onMarketClosed(MarketEvent event) {
		super.onMarketClosed(event);
		logger.debug(this + ": recieved auctionClosed()");
		event.getAuction().remove(this);
		receivedAuctionClosed = true;
		receivedAuctionClosedAfterAuctionOpen = receivedAuctionOpen;
	}

	@Override
	public void onEndOfDay(MarketEvent event) {
		int day = event.getAuction().getDay();
		receivedEndOfDayAfterRequestShout = day <= requestShoutDay;
	}

	public boolean active() {
		return true;
	}

//	public double equilibriumProfits(Market auction, double equilibriumPrice,
//	    int quantity) {
//		// TODO
//		return -1;
//	}
	
	

	public String toString() {
		return "(" + getClass() + " id:" + hashCode() 
		    + " valuer:" + valuer + " lastProfit:" + getLastPayoff() + " funds:"
		    + account + " account:" + account + ")";
	}


}