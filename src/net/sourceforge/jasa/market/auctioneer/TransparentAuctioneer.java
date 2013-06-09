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

import java.util.HashSet;

import net.sourceforge.jasa.market.DuplicateShoutException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.ShoutsNotVisibleException;

public abstract class TransparentAuctioneer extends AbstractAuctioneer {

	/**
	 * The set of shouts that have been matched in the current round.
	 * 
	 */
	protected HashSet<Order> acceptedShouts = new HashSet<Order>();

	protected Order lastAsk = new Order();

	protected Order lastBid = new Order();

	protected Order lastShout;


	public TransparentAuctioneer(Market auction) {
		super(auction);
	}

	public boolean shoutsVisible() {
		return true;
	}

	public void recordMatch(Order ask, Order bid) {
		assert ask.isAsk();
		assert bid.isBid();
		acceptedShouts.add(ask);
		acceptedShouts.add(bid);
	}

	protected void newShoutInternal(Order shout) throws DuplicateShoutException {
		super.newShoutInternal(shout);
		if (shout.isAsk()) {
			lastAsk.copyFrom(shout);
		} else {
			lastBid.copyFrom(shout);
		}
		lastShout = shout;
	}

	public boolean orderFilled(Order shout) throws ShoutsNotVisibleException {
		return acceptedShouts.contains(shout);
	}

	public boolean transactionsOccurred() throws ShoutsNotVisibleException {
		return !acceptedShouts.isEmpty();
	}

	public void onRoundClosed() {
		acceptedShouts.clear();
	}

	public void reset() {
		super.reset();
		acceptedShouts.clear();
		lastShout = null;
	}

	public Order getLastAsk() {
		return lastAsk;
	}

	public Order getLastBid() {
		return lastBid;
	}

	public Order getLastShout() {
		return lastShout;
	}

}
