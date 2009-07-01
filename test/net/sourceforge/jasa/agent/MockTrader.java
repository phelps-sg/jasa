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

package net.sourceforge.jasa.agent;

import junit.framework.TestCase;

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.RandomRobinAuction;

import org.apache.log4j.Logger;


public class MockTrader extends AbstractTradingAgent {

	/**
	 * @uml.property name="lastWinningShout"
	 * @uml.associationEnd
	 */
	public Order lastWinningShout = null;

	/**
	 * @uml.property name="lastWinningPrice"
	 */
	public double lastWinningPrice = 0;

	/**
	 * @uml.property name="lastWinningQuantity"
	 */
	public int lastWinningQuantity;

	public int requestShoutDay = -1;

	/**
	 * @uml.property name="receivedAuctionOpen"
	 */
	public boolean receivedAuctionOpen = false;

	/**
	 * @uml.property name="receivedAuctionClosed"
	 */
	public boolean receivedAuctionClosed = false;

	/**
	 * @uml.property name="receivedAuctionClosedAfterAuctionOpen"
	 */
	public boolean receivedAuctionClosedAfterAuctionOpen = false;

	public boolean receivedEndOfDayAfterRequestShout = true;

	/**
	 * @uml.property name="receivedRoundClosed"
	 */
	public int receivedRoundClosed = 0;

	/**
	 * @uml.property name="receivedRequestShout"
	 */
	public int receivedRequestShout = 0;

	/**
	 * @uml.property name="test"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	TestCase test;

	static Logger logger = Logger.getLogger(AbstractTradingAgent.class);

	public MockTrader(TestCase test, int stock, long funds) {
		super(stock, funds);
		this.test = test;
	}

	public MockTrader(TestCase test, int stock, double funds,
	    double privateValue, boolean isSeller) {
		super(stock, funds, privateValue, isSeller);
		this.test = test;
	}

	// public void informOfSeller( Auction market, Shout winningShout,
	// TradingAgent seller, double price, int quantity ) {
	// super.informOfSeller(market, winningShout, seller, price, quantity);
	// test.assertTrue(((AbstractTradingAgent) seller).isSeller());
	// System.out.println(this + ": winning shout " + winningShout + " at price "
	// + price + " and quantity " + quantity + " and seller: " + seller);
	// lastWinningShout = winningShout;
	// lastWinningPrice = price;
	// purchaseFrom(market, (AbstractTradingAgent) seller, quantity, price);
	// }
	//
	// public void informOfBuyer( Auction market, TradingAgent buyer, double
	// price,
	// int quantity ) {
	// super.informOfBuyer(market, buyer, price, quantity);
	// test.assertTrue(((AbstractTradingAgent) buyer).isBuyer());
	// lastWinningPrice = price;
	// lastWinningShout = getCurrentShout();
	// }

	public void shoutAccepted(Market auction, Order shout, double price,
	    int quantity) {
		super.shoutAccepted(auction, shout, price, quantity);
		System.out.println("order accepted " + shout + " at price " + price
		    + " and quantity " + quantity);
		lastWinningShout = shout;
		lastWinningPrice = price;
	}

	public int determineQuantity(Market auction) {
		return 1;
	}

	public void requestShout(Market auction) {
		super.requestShout(auction);
		System.out.println(this + ": placed " + currentOrder);
		receivedRequestShout++;
		requestShoutDay = auction.getDay();
	}

	public void auctionOpen(MarketEvent event) {
		super.auctionOpen(event);
		receivedAuctionOpen = true;
	}

	public void auctionClosed(MarketEvent event) {
		super.auctionClosed(event);
		logger.debug(this + ": recieved auctionClosed()");
		((RandomRobinAuction) event.getAuction()).remove(this);
		receivedAuctionClosed = true;
		receivedAuctionClosedAfterAuctionOpen = receivedAuctionOpen;
	}

	public void roundClosed(MarketEvent event) {
		super.roundClosed(event);
		receivedRoundClosed++;
	}

	public void endOfDay(MarketEvent event) {
		int day = event.getAuction().getDay();
		receivedEndOfDayAfterRequestShout = day <= requestShoutDay;
	}

	public boolean active() {
		return true;
	}

	public double equilibriumProfits(Market auction, double equilibriumPrice,
	    int quantity) {
		// TODO
		return -1;
	}

	public String toString() {
		return "(" + getClass() + " id:" + id + " isSeller:" + isSeller
		    + " valuer:" + valuer + " lastProfit:" + getLastProfit() + " funds:"
		    + account + " account:" + account + ")";
	}

}