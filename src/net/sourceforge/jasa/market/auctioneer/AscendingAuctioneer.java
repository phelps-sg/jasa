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

package net.sourceforge.jasa.market.auctioneer;

import java.io.Serializable;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.Account;
import net.sourceforge.jasa.market.AuctionRuntimeException;
import net.sourceforge.jasa.market.DuplicateShoutException;
import net.sourceforge.jasa.market.IllegalOrderException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.rules.UniformPricingPolicy;

import org.apache.log4j.Logger;

/**
 * Auctioneer for standard multi-unit english ascending market.
 */

public class AscendingAuctioneer extends TransparentAuctioneer implements
    Serializable {

	/**
	 * The reservation price.
	 */
	protected double reservePrice;

	/**
	 * The seller.
	 */
	protected TradingAgent seller;

	protected Account account;

	/**
	 * The number of items for sale.
	 */
	int quantity;

	public static final String P_RESERVEPRICE = "reserveprice";

	public static final String P_QUANTITY = "quantity";

	public static final String P_SELLER = "seller";

	static Logger logger = Logger.getLogger(AscendingAuctioneer.class);

	public AscendingAuctioneer(Market auction, TradingAgent seller,
	    int quantity, double reservePrice) {
		super(auction);

		this.reservePrice = reservePrice;
		this.quantity = quantity;
		this.seller = seller;

		setPricingPolicy(new UniformPricingPolicy(0));
		account = new Account(this, 0);

		initialise();
	}


	public void initialise() {
		super.initialise();
		try {
			newOrder(new Order(seller, quantity, 0, false));
		} catch (DuplicateShoutException e) {
			throw new AuctionRuntimeException(
			    "Fatal error: invalid market state on initialisation!");
		} catch (IllegalOrderException e) {
			throw new AuctionRuntimeException(
			    "Fatal error: invalid market state on initialisation!");
		}
	}

//	public void setup(ParameterDatabase parameters, Parameter base) {
//
//		super.setup(parameters, base);
//
//		quantity = parameters.getInt(base.push(P_QUANTITY), null, 1);
//
//		reservePrice = parameters.getDouble(base.push(P_RESERVEPRICE), null, 0);
//
//		seller = (TradingAgent) parameters.getInstanceForParameterEq(base
//		    .push(P_SELLER), null, TradingAgent.class);
//
//		if (seller instanceof Parameterizable) {
//			((Parameterizable) seller).setup(parameters, base.push(P_SELLER));
//		}
//
//		initialise();
//	}

	public void onRoundClosed() {
		super.onRoundClosed();
		generateQuote();
	}

//	public void endOfAuctionProcessing() {
//		super.endOfAuctionProcessing();
//		logger.debug("Clearing at end of market..");
//		orderBook.printState();
//		clear();
//		logger.debug("clearing done.");
//	}

	public void generateQuote() {
		currentQuote = new MarketQuote(null, orderBook.getLowestMatchedBid());
	}

	protected void checkShoutValidity(Order shout) throws IllegalOrderException {
		super.checkShoutValidity(shout);
		if (shout.isAsk()) {
			throw new IllegalOrderException(
			    "asks are not allowed in an ascending market");
		}
		// TODO: Additional logic to enforce bid amounts at round nos and/or
		// beat existing bids by certain amount?
	}

	public boolean shoutsVisible() {
		return true;
	}

	public Account getAccount() {
		return account;
	}

}