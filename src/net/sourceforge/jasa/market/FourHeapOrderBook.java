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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

import org.apache.commons.collections.iterators.CollatingIterator;
import org.apache.log4j.Logger;

/**
 * <p>
 * This class provides market order-matching services using the 4-Heap
 * algorithm. See:
 * </p>
 * 
 * <p>
 * "Flexible Double Auctions for Electronic Commerce: Theory and Implementation"
 * by Wurman, Walsh and Wellman 1998.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class FourHeapOrderBook implements OrderBook, Serializable {

	/**
	 * Matched bids in ascending order
	 */
	protected PriorityQueue<Order> bIn = new PriorityQueue<Order>(
			new TreeSet<Order>(greaterThan));

	/**
	 * Unmatched bids in descending order
	 */
	protected PriorityQueue<Order> bOut = new PriorityQueue<Order>(
			new TreeSet<Order>(lessThan));

	/**
	 * Matched asks in descending order
	 */
	protected PriorityQueue<Order> sIn = new PriorityQueue<Order>(
			new TreeSet<Order>(lessThan));

	/**
	 * Unmatched asks in ascending order
	 */
	protected PriorityQueue<Order> sOut = new PriorityQueue<Order>(
			new TreeSet<Order>(greaterThan));

	protected static AscendingOrderComparator greaterThan = 
			new AscendingOrderComparator();

	protected static DescendingOrderComparator lessThan = 
			new DescendingOrderComparator();

	static Logger logger = Logger.getLogger(FourHeapOrderBook.class);

	public FourHeapOrderBook() {
		initialise();
	}

	public void remove(Order shout) {
		if (shout.isAsk()) {
			removeAsk(shout);
		} else {
			removeBid(shout);
		}
		checkIntegrity();
	}

	protected void removeAsk(Order shout) {
		if (sIn.remove(shout)) {
			reinsert(bIn, shout.getQuantity());
		} else {
			sOut.remove(shout);
		}
	}

	protected void removeBid(Order shout) {
		if (bIn.remove(shout)) {
			reinsert(sIn, shout.getQuantity());
		} else {
			bOut.remove(shout);
		}
	}

	public String toString() {
		return "sIn = " + sIn + "\nbIn = " + bIn + "\nsOut = " + sOut + "\nbOut = "
		    + bOut;
	}

	/**
	 * Log the current state of the market.
	 */
	public void printState() {
		logger.info("Auction state:\n");
		prettyPrint("Matched bids", bIn);
		prettyPrint("Matched asks", sIn);
		prettyPrint("Runner-up bids", bOut);
		prettyPrint("Runner-up asks", sOut);
	}

	@SuppressWarnings("all")
	public void prettyPrint(String title, PriorityQueue shouts) {
		logger.info(title);
		logger.info("--------------");		
		Iterator i = shouts.iterator();
		while (i.hasNext()) {
			Order shout = (Order) i.next();
			logger.info(shout.toPrettyString());
		}
		logger.info("");
	}

	/**
	 * Insert a shout into a binary heap.
	 * 
	 * @param heap
	 *          The heap to insert into
	 * @param shout
	 *          The shout to insert
	 * 
	 */
	private static void insertShout(PriorityQueue<Order> heap, Order shout)
	    throws DuplicateShoutException {
		try {
			heap.add(shout);
		} catch (IllegalArgumentException e) {
			logger.error(e);
			e.printStackTrace();
			throw new DuplicateShoutException("Duplicate shout: " + shout.toString());
		}
	}

	/**
	 * Insert an unmatched ask into the appropriate heap.
	 */
	public void insertUnmatchedAsk(Order ask) throws DuplicateShoutException {
		assert ask.isAsk();
		insertShout(sOut, ask);
	}

	/**
	 * Insert an unmatched bid into the appropriate heap.
	 */
	public void insertUnmatchedBid(Order bid) throws DuplicateShoutException {
		assert bid.isBid();
		insertShout(bOut, bid);
	}

	/**
	 * Get the highest unmatched bid.
	 */
	public Order getHighestUnmatchedBid() {
		if (bOut.isEmpty()) {
			return null;
		}
		return (Order) bOut.peek();
	}

	/**
	 * Get the lowest matched bid
	 */
	public Order getLowestMatchedBid() {
		if (bIn.isEmpty()) {
			return null;
		}
		return (Order) bIn.peek();
	}

	/**
	 * Get the lowest unmatched ask.
	 */
	public Order getLowestUnmatchedAsk() {
		if (sOut.isEmpty()) {
			return null;
		}
		return (Order) sOut.peek();
	}

	/**
	 * Get the highest matched ask.
	 */
	public Order getHighestMatchedAsk() {
		if (sIn.isEmpty()) {
			return null;
		}
		return (Order) sIn.peek();
	}

	/**
	 * Unify the shout at the top of the heap with the supplied shout, so that
	 * quantity(shout) = quantity(top(heap)). This is achieved by splitting the
	 * supplied shout or the shout at the top of the heap.
	 * 
	 * @param shout
	 *          The shout.
	 * @param heap
	 *          The heap.
	 * 
	 * @return A reference to the, possibly modified, shout.
	 * 
	 */
	protected static Order unifyShout(Order shout, PriorityQueue<Order> heap) {

		Order top = (Order) heap.peek();

		if (shout.getQuantity() > top.getQuantity()) {
			shout = shout.splat(shout.getQuantity() - top.getQuantity());
		} else {
			if (top.getQuantity() > shout.getQuantity()) {
				Order remainder = top.split(top.getQuantity() - shout.getQuantity());
				heap.add(remainder);
			}
		}

		return shout;
	}

	protected int displaceShout(Order shout, PriorityQueue<Order> from,
			PriorityQueue<Order> to) throws DuplicateShoutException {
		shout = unifyShout(shout, from);
		to.add(from.remove());
		insertShout(from, shout);
		return shout.getQuantity();
	}

	public int promoteShout(Order shout, PriorityQueue<Order> from, PriorityQueue<Order> to,
			PriorityQueue<Order> matched) throws DuplicateShoutException {
		shout = unifyShout(shout, from);
		insertShout(matched, shout);
		to.add(from.remove());
		return shout.getQuantity();
	}

	public int displaceHighestMatchedAsk(Order ask)
	    throws DuplicateShoutException {
		assert ask.isAsk();
		return displaceShout(ask, sIn, sOut);
	}

	public int displaceLowestMatchedBid(Order bid) throws DuplicateShoutException {
		assert bid.isBid();
		return displaceShout(bid, bIn, bOut);
	}

	public int promoteHighestUnmatchedBid(Order ask)
	    throws DuplicateShoutException {
		assert ask.isAsk();
		return promoteShout(ask, bOut, bIn, sIn);
	}

	public int promoteLowestUnmatchedAsk(Order bid)
	    throws DuplicateShoutException {
		assert bid.isBid();
		return promoteShout(bid, sOut, sIn, bIn);
	}

	public void add(Order shout) throws DuplicateShoutException {
		if (shout.isBid()) {
			addBid(shout);
		} else {
			addAsk(shout);
		}
		checkIntegrity();
	}

	protected void addBid(Order bid) throws DuplicateShoutException {

		double bidVal = bid.getPrice();
		int uninsertedUnits = bid.getQuantity();

		while (uninsertedUnits > 0) {

			Order sOutTop = getLowestUnmatchedAsk();
			Order bInTop = getLowestMatchedBid();

			if (sOutTop != null && sOutTop.matches(bid)
			    && (bInTop == null || bInTop.getPrice() >= sOutTop.getPrice())) {

				// found match
				uninsertedUnits -= promoteLowestUnmatchedAsk(bid);

			} else if (bInTop != null && bidVal > bInTop.getPrice()) {

				uninsertedUnits -= displaceLowestMatchedBid(bid);

			} else {
				insertUnmatchedBid(bid);
				uninsertedUnits -= bid.getQuantity();
			}

		}
	}

	protected void addAsk(Order ask) throws DuplicateShoutException {

		int uninsertedUnits = ask.getQuantity();

		while (uninsertedUnits > 0) {

			Order sInTop = getHighestMatchedAsk();
			Order bOutTop = getHighestUnmatchedBid();

			if (bOutTop != null && bOutTop.matches(ask)
			    && (sInTop == null || sInTop.matches(bOutTop))) {
				//TODO alter logic so that this maintains heap constraints
				uninsertedUnits -= promoteHighestUnmatchedBid(ask);

			} else if (sInTop != null && ask.getPrice() <= sInTop.getPrice()) {

				uninsertedUnits -= displaceHighestMatchedAsk(ask);

			} else {

				insertUnmatchedAsk(ask);
				uninsertedUnits -= ask.getQuantity();

			}
		}

	}

	@SuppressWarnings("unchecked")
	public Iterator<Order> askIterator() {
		return new CollatingIterator(greaterThan, sIn.iterator(),
				sOut.iterator());
	}

	@SuppressWarnings("unchecked")
	public Iterator<Order> bidIterator() {
		return new CollatingIterator(lessThan, bIn.iterator(), bOut.iterator());
	}

	/**
	 * <p>
	 * Return a list of matched bids and asks. The list is of the form
	 * </p>
	 * <br> ( b0, a0, b1, a1 .. bn, an )<br>
	 * 
	 * <p>
	 * where bi is the ith bid and a0 is the ith ask. A typical auctioneer would
	 * clear by matching bi with ai for all i at some price.
	 * </p>
	 */
	public List<Order> matchOrders() {
		ArrayList<Order> result = 
				new ArrayList<Order>(sIn.size() + bIn.size());
		while (!sIn.isEmpty()) {
			Order sInTop = (Order) sIn.remove();
			Order bInTop = (Order) bIn.remove();
			int nS = sInTop.getQuantity();
			int nB = bInTop.getQuantity();
			if (nS < nB) {
				// split the bid
				Order remainder = bInTop.split(nB - nS);
				bIn.add(remainder);
			} else if (nB < nS) {
				// split the ask
				Order remainder = sInTop.split(nS - nB);
				sIn.add(remainder);
			}
			assert bInTop.getAgent() != sInTop.getAgent();
			result.add(bInTop);
			result.add(sInTop);
		}
		assert bIn.isEmpty();
		checkIntegrity();
		return result;
	}

	protected void initialise() {
		bIn.clear();
		bOut.clear();
		sIn.clear();
		sOut.clear();
	}

	public synchronized void reset() {
		initialise();
	}

	/**
	 * Remove, possibly several, shouts from heap such that quantity(heap) is
	 * reduced by the supplied quantity and reinsert the shouts using the
	 * standard insertion logic. quantity(heap) is defined as the total quantity
	 * of every shout in the heap.
	 * 
	 * @param heap
	 *            The heap to remove shouts from.
	 * @param quantity
	 *            The total quantity to remove.
	 */
	protected void reinsert(PriorityQueue<Order> heap, int quantity) {

		while (quantity > 0) {

			Order top = (Order) heap.remove();

			if (top.getQuantity() > quantity) {
				heap.add(top.split(top.getQuantity() - quantity));
			}

			quantity -= top.getQuantity();

			try {
				if (top.isBid()) {
					addBid(top);
				} else {
					addAsk(top);
				}
			} catch (DuplicateShoutException e) {
				throw new AuctionRuntimeException("Invalid market state");
			}
		}
		
	}

	/**
	 * Compute the total number of orders in the book.
	 */
	public int size() {
		return bIn.size() + bOut.size() + sIn.size() + sOut.size();
	}
	
	@Override
	public boolean isEmpty() {
		return bIn.isEmpty() && sIn.isEmpty() && 
					bOut.isEmpty() && bIn.isEmpty();
	}

	@Override
	public int getDepth() {
		return Math.max(bOut.size(), sOut.size());
	}
	
	@Override
	public List<Order> getUnmatchedBids() {
		ArrayList<Order> bids = new ArrayList<Order>(bOut);
		Collections.sort(bids, lessThan);
		return bids;
	}

	@Override
	public List<Order> getUnmatchedAsks() {
		ArrayList<Order> asks = new ArrayList<Order>(sOut);
		Collections.sort(asks, greaterThan);
		return asks;
	}

	public void checkIntegrity() {
		// These conditions are violated when we have unmatched orders from 
		//	the same trader on both sides of the book.  However this does not 
		//  appear to violate the integrity of the auction state.  See
		//  FourHeapTest.testSameSide().
		
		//  TODO: Prove this and then update integrity checks to take into
		//       	account unmatched orders from the same trader.
		
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
		if (!((s1 == null || s2 == null) || s1.getPrice() >= (s2.getPrice()))) {
			throw new RuntimeException("Heaps not balanced! - " + condition);
		}
	}

}
