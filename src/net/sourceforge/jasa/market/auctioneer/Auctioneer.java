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

import java.util.Iterator;
import java.util.List;

import net.sourceforge.jasa.event.MarketEventListener;
import net.sourceforge.jasa.market.Account;
import net.sourceforge.jasa.market.IllegalOrderException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.QuoteProvider;
import net.sourceforge.jasa.market.ShoutsNotVisibleException;

/**
 * Classes implementing this interface define the rules for matching
 * orders in the marketplace and producing the resulting transaction set.
 *  
 * @author Steve Phelps
 * @version $Revision$
 */

public interface Auctioneer extends QuoteProvider, MarketEventListener {

	/**
	 * Perform the clearing operation for the market; match buyers with sellers
	 * and inform the market of any deals.
	 */
	public void clear();

	/**
	 * Code for handling a new order in the market. Subclasses should override
	 * this method if they wish to provide different handling for different
	 * market rules.
	 * 
	 * @param order
	 *          The new shout to be processed
	 * 
	 * @exception IllegalOrderException
	 *              Thrown if the order is invalid in some way.
	 */
	public void newOrder(Order order) throws IllegalOrderException;

	/**
	 * Cancel an existing order.
	 */
	public void removeOrder(Order order);

	/**
	 * Log the current state of the market.
	 */
	public void printState();

	/**
	 * Specify which market we are the auctioneer for.
	 */
	public void setMarket(Market auction);

	/**
	 * Find out which market we are the auctioneer for.
	 */
	public Market getMarket();

	public Iterator<Order> askIterator();

	public Iterator<Order> bidIterator();

	/**
	 * Return true if the shouts of others are visible.
	 */
	public boolean shoutsVisible();

	public boolean orderFilled(Order order) throws ShoutsNotVisibleException;

	public boolean transactionsOccurred() throws ShoutsNotVisibleException;

	public Order getLastAsk() throws ShoutsNotVisibleException;

	public Order getLastBid() throws ShoutsNotVisibleException;

	public Order getLastShout() throws ShoutsNotVisibleException;

	/**
	 * Get the account which holds any budget surplus or deficit for the
	 * auctioneer. This is useful when implementing non-budget-balanced
	 * mechanisms.
	 */
	public Account getAccount();

	public List<Order> getUnmatchedBids();

	public List<Order> getUnmatchedAsks();

}