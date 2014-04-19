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

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public interface OrderBook extends net.sourceforge.jabm.util.Resetable {

	public void add(Order shout) throws DuplicateShoutException;

	public void remove(Order shout);
	
	/**
	 * Log the current state of the market.
	 */
	public void printState();

	/**
	 * <p>
	 * Destructively fetch the list of matched bids and asks. The list is of the
	 * form
	 * </p>
	 * <br> ( b0, a0, b1, a1 .. bn, an )<br>
	 * <p>
	 * where bi is the ith bid and ai is the ith ask. A typical auctioneer would
	 * clear by matching bi with ai for all i at some price.
	 * </p>
	 * <p>
	 * Note that the engine's set of matched shouts will become empty as a result
	 * of invoking this method.
	 * </p>
	 */
	public List<Order> matchOrders();

	/**
	 * Get the highest unmatched bid in the market.
	 */
	public Order getHighestUnmatchedBid();

	/**
	 * Get the lowest matched bid in the market.
	 */
	public Order getLowestMatchedBid();

	/**
	 * Get the lowest unmatched ask.
	 */
	public Order getLowestUnmatchedAsk();

	/**
	 * Get the highest matched ask.
	 */
	public Order getHighestMatchedAsk();

	/**
	 * Return an iterator that non-destructively iterates over every ask in the
	 * market (both matched and unmatched).
	 */
	public Iterator<Order> askIterator();

	/**
	 * Return an iterator that non-destructively iterates over every bid in the
	 * market (both matched and unmatched).
	 */
	public Iterator<Order> bidIterator();

	public boolean isEmpty();

	public int getDepth();

	public List<Order> getUnmatchedBids();

	public List<Order> getUnmatchedAsks();

}
