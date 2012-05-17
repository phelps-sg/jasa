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

package net.sourceforge.jasa.market.auctioneer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jabm.util.Parameterizable;
import net.sourceforge.jabm.util.Prototypeable;
import net.sourceforge.jabm.util.Resetable;
import net.sourceforge.jasa.event.EndOfDayEvent;
import net.sourceforge.jasa.market.DuplicateShoutException;
import net.sourceforge.jasa.market.FourHeapOrderBook;
import net.sourceforge.jasa.market.IllegalOrderException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.OrderBook;
import net.sourceforge.jasa.market.rules.ClearingPolicy;
import net.sourceforge.jasa.market.rules.EquilibriumClearingPolicy;
import net.sourceforge.jasa.market.rules.PricingPolicy;

import org.apache.log4j.Logger;

/**
 * An abstract class representing an auctioneer managing shouts in an market.
 * Different market rules should be encapsulated in different Auctioneer
 * classes.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AbstractAuctioneer implements Serializable, Auctioneer,
    Resetable, Prototypeable, Cloneable, Parameterizable {

	protected Market market;

	protected OrderBook orderBook = new FourHeapOrderBook();
	
	protected MarketQuote currentQuote = null;

	protected MarketQuote clearingQuote;

	protected PricingPolicy pricingPolicy;
	
	protected ClearingPolicy clearingPolicy = new EquilibriumClearingPolicy(this);

	static Logger logger = Logger.getLogger(AbstractAuctioneer.class);
	
	public AbstractAuctioneer(Market auction) {
		this.market = auction;
	}
	
	public AbstractAuctioneer() {
	}

	public Object protoClone() {
		try {
			AbstractAuctioneer clone = (AbstractAuctioneer) clone();
			clone.orderBook = new FourHeapOrderBook();
			clone.reset();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

//	public void setup(ParameterDatabase parameters, Parameter base) {
//
//		pricingPolicy = (PricingPolicy) parameters.getInstanceForParameterEq(base
//		    .push(P_PRICING), null, PricingPolicy.class);
//
//		if (pricingPolicy instanceof Parameterizable) {
//			((Parameterizable) pricingPolicy).setup(parameters, base.push(P_PRICING));
//		}
//
//	}

	public PricingPolicy getPricingPolicy() {
		return pricingPolicy;
	}

	public void setPricingPolicy(PricingPolicy pricingPolicy) {
		this.pricingPolicy = pricingPolicy;
	}

	/**
	 * Code for handling a new shout in the market. Subclasses should override
	 * this method if they wish to provide different handling for different
	 * market rules.
	 * 
	 * @param shout
	 *          The new shout to be processed
	 * 
	 * @exception IllegalOrderException
	 *              Thrown if the shout is invalid in some way.
	 */
	public void newOrder(Order shout) throws IllegalOrderException,
	    DuplicateShoutException {
		checkShoutValidity(shout);
		newShoutInternal(shout);
	}

	protected void newShoutInternal(Order shout) throws DuplicateShoutException {
		orderBook.add(shout);
	}

	/**
	 * 
	 * @param shout
	 *          The new shout to be processed
	 * @throws IllegalOrderException
	 *           Thrown if the shout is invalid in some way.
	 */
	protected void checkShoutValidity(Order shout) throws IllegalOrderException {
		if (!shout.isValid()) {
			logger.error("malformed shout: " + shout);
			throw new IllegalOrderException("Malformed shout");
		}
	}

	/**
	 * Handle a request to retract a shout.
	 */
	public void removeShout(Order shout) {
		orderBook.remove(shout);
	}

	/**
	 * Log the current state of the market.
	 */
	public void printState() {
		orderBook.printState();
	}

	public void reset() {
		orderBook.reset();

		if (pricingPolicy instanceof Resetable) {
			((Resetable) pricingPolicy).reset();
		}

		initialise();
	}

	protected void initialise() {
		currentQuote = null;
	}

	public MarketQuote getQuote() {
		if (currentQuote == null) {
			generateQuote();
		}
		return currentQuote;
	}

	public Iterator<Order> askIterator() {
		return orderBook.askIterator();
	}

	public Iterator<Order> bidIterator() {
		return orderBook.bidIterator();
	}

	public abstract void generateQuote();

	public void setMarket(Market auction) {
		this.market = auction;
	}

	/**
	 * Find out which market we are the auctioneer for.
	 */
	public Market getMarket() {
		return market;
	}

	public void onEndOfDay() {
	}

	public void clear() {
		clearingPolicy.clear();
	}

	public void clear(Order ask, Order bid, double price) {
		assert ask.isAsk();
		assert bid.isBid();
		recordMatch(ask, bid);
		market.clear(ask, bid, price);
	}

	public void clear(Order ask, Order bid, double buyerCharge,
	    double sellerPayment, int quantity) {
		assert ask.isAsk();
		assert bid.isBid();
		recordMatch(ask, bid);
		market.clear(ask, bid, buyerCharge, sellerPayment, quantity);
	}

	public double determineClearingPrice(Order bid, Order ask) {
		return pricingPolicy.determineClearingPrice(bid, ask, clearingQuote);
	}

	public double bidQuote() {
		return Order.maxPrice(orderBook.getHighestMatchedAsk(), orderBook
		    .getHighestUnmatchedBid());
	}

	public double askQuote() {
		return Order.minPrice(orderBook.getLowestUnmatchedAsk(), orderBook
		    .getLowestMatchedBid());
	}

	public void eventOccurred(SimEvent event) {
		if (event instanceof EndOfDayEvent) {
			onEndOfDay();
		} else if (event instanceof RoundFinishedEvent) {
			onRoundClosed();
		}
		if (event instanceof SimulationStartingEvent) {
			reset();
		}
	}
	
	public abstract void onRoundClosed();

	public void recordMatch(Order ask, Order bid) {
		// default is do nothing
	}

	public OrderBook getOrderBook() {
		return orderBook;
	}

	public void setOrderBook(OrderBook orderBook) {
		this.orderBook = orderBook;
	}
	
	public List<Order> getUnmatchedAsks() {
		return orderBook.getUnmatchedAsks();
	}
	
	public List<Order> getUnmatchedBids() {
		return orderBook.getUnmatchedBids();
	}

	public MarketQuote getClearingQuote() {
		return clearingQuote;
	}

	public void setClearingQuote(MarketQuote clearingQuote) {
		this.clearingQuote = clearingQuote;
	}

	public ClearingPolicy getClearingPolicy() {
		return clearingPolicy;
	}

	public void setClearingPolicy(ClearingPolicy clearingPolicy) {
		this.clearingPolicy = clearingPolicy;
	}
	
	public String toString() {
		return "(" + getClass() + ")";
	}

}
