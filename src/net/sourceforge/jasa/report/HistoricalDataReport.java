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

package net.sourceforge.jasa.report;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.RoundClosedEvent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.AuctionRuntimeException;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.ShoutsNotVisibleException;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.util.Resetable;

import org.apache.commons.collections.bag.TreeBag;
import org.apache.commons.collections.list.TreeList;
import org.apache.log4j.Logger;

/**
 * <p>
 * A historicalDataReport that keeps a historical record of the shouts in the market that lead
 * to the last N transactions. This logger is used to keep historical data that
 * is used by various different trading strategies.
 * </p>
 * <p>
 * Since GDStrategy uses this historicalDataReport to compute the number of shouts above or
 * below a certain price, which leads to slow simulation, SortedView and
 * IncreasingQueryAccelerator are introduced to speed up GDStrategy's queries
 * based on the pattern of prices of concern.
 * </p>
 * <p>
 * <b>Parameters </b> <br>
 * <table>
 * <tr>
 * <td valign=top><i>base </i> <tt>.memorysize</tt><br>
 * <font size=-1>int > 0 </font></td>
 * <td valign=top>(the length of most recent historicalDataReport to be recorded)</td>
 * <tr>
 * 
 * </table>
 * 
 * 
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class HistoricalDataReport extends AbstractAuctionReport implements
    Serializable, Resetable {

	protected LinkedList asks = new LinkedList();

	protected LinkedList bids = new LinkedList();

	protected TreeBag sortedShouts = new TreeBag();

	protected HashSet acceptedShouts = new HashSet();


	protected Map shoutMap = Collections.synchronizedMap(new HashMap());

	protected int memorySize = 10;

	protected int currentMemoryCell = 0;

	protected int[] memoryBids = new int[memorySize];

	protected int[] memoryAsks = new int[memorySize];

	protected double lowestAskPrice;

	protected double highestBidPrice;

	protected Order highestUnacceptedBid;

	protected Order lowestUnacceptedAsk;

	public static final String P_DEF_BASE = "historicaldatareport";

	static final String P_MEMORYSIZE = "memorysize";

	static Logger logger = Logger.getLogger(HistoricalDataReport.class);

	protected IncreasingQueryAccelerator accelerator;

	protected SortedView view;

	protected Observable observableProxy;
	
	protected static boolean flag = true;
	
	boolean debug = false;

	public HistoricalDataReport() {
		observableProxy = new Observable() {
			public void notifyObservers() {
				setChanged();
				super.notifyObservers();
			}
		};
		
		if (flag) {
			debug = flag;
			flag = !flag;
		}
	}

	public void addObserver(Observer o) {
		observableProxy.addObserver(o);
	}

	public void deleteObserver(Observer o) {
		observableProxy.deleteObserver(o);
	}
	
	public void checkConsistency() {
		if (debug) {
			if (asks.size() + bids.size() != sortedShouts.size()) {
				logger.info("inconsistency found !");
				logger.info(asks.size() + " " + getAsks() + "\n");
				logger.info(bids.size() + " " + getBids() + "\n");
				logger.info(sortedShouts.size() + " " + sortedShouts.iterator() + "\n");
			}
		}
	}

	protected void removeNShouts(int n, LinkedList shouts) {
		for (int i = 0; i < n; i++) {
			Order shout = (Order) shouts.removeFirst();
			sortedShouts.remove(shout);
			acceptedShouts.remove(shout);
		}
	}

	public void updateTransPriceLog(TransactionExecutedEvent event) {
//		Object o;
		currentMemoryCell = (currentMemoryCell + 1) % memorySize;
		if (memoryAsks[currentMemoryCell] > 0 || memoryBids[currentMemoryCell] > 0) {
			removeNShouts(memoryAsks[currentMemoryCell], asks);
			removeNShouts(memoryBids[currentMemoryCell], bids);
			memoryBids[currentMemoryCell] = 0;
			memoryAsks[currentMemoryCell] = 0;
			markMatched(asks);
			markMatched(bids);
			
//			checkConsistency();
		}

		if (event.getAsk() == lowestUnacceptedAsk) {
			lowestUnacceptedAsk = null;
		}

		if (event.getBid() == highestUnacceptedBid) {
			highestUnacceptedBid = null;
		}
		observableProxy.notifyObservers();
	}

	public void initialise() {
		acceptedShouts.clear();
		bids.clear();
		asks.clear();
		sortedShouts.clear();
		for (int i = 0; i < memorySize; i++) {
			memoryBids[i] = 0;
			memoryAsks[i] = 0;
		}
		initialisePriceRanges();
		observableProxy.notifyObservers();
	}

	public void reset() {
		initialise();
	}

	public void updateShoutLog(OrderPlacedEvent event) {
		Order shout = event.getShout();
//		if (sortedShouts.getCount(shout) > 0) {
//			logger.info(sortedShouts.getCount(shout) + "\n" + shout);
//			logger.info(shoutMap.get(shout.getId()));
//		}
//		
//		shoutMap.put(shout.getId(), shout);
		addToSortedShouts(shout);
		if (shout.isAsk()) {
			asks.add(shout);
			memoryAsks[currentMemoryCell]++;
			if (shout.getPrice() < lowestAskPrice) {
				lowestAskPrice = shout.getPrice();
			}

			if (lowestUnacceptedAsk == null
			    || lowestUnacceptedAsk.getPrice() > shout.getPrice()) {
				lowestUnacceptedAsk = shout;
			}
		} else {
			bids.add(shout);
			memoryBids[currentMemoryCell]++;
			if (shout.getPrice() > highestBidPrice) {
				highestBidPrice = shout.getPrice();
			}

			if (highestUnacceptedBid == null
			    || highestUnacceptedBid.getPrice() < shout.getPrice()) {
				highestUnacceptedBid = shout;
			}

		}

		markMatched(asks);
		markMatched(bids);
		
//		checkConsistency();
		
		observableProxy.notifyObservers();
	}

	public void roundClosed(MarketEvent event) {
		markMatched(asks);
		markMatched(bids);
		// if ( getNumberOfTrades() > memorySize ) {
		// deleteOldShouts();
		// }
		initialisePriceRanges();
		observableProxy.notifyObservers();
	}

	public void eventOccurred(SimEvent event) {
		if (event instanceof RoundClosedEvent) {
			roundClosed((RoundClosedEvent) event);
		} else if (event instanceof OrderPlacedEvent) {
			updateShoutLog((OrderPlacedEvent) event);
		} else if (event instanceof TransactionExecutedEvent) {
			updateTransPriceLog((TransactionExecutedEvent) event);
		}
	}

	public int getNumberOfTrades() {
		return acceptedShouts.size() / 2;
	}

	public double getHighestBidPrice() {
		return highestBidPrice;
	}

	public double getLowestAskPrice() {
		return lowestAskPrice;
	}

	public double getHighestUnacceptedBidPrice() {
		if (highestUnacceptedBid != null) {
			return highestUnacceptedBid.getPrice();
		}

		Iterator i = bids.iterator();
		double highestUnacceptedBidPrice = Double.NEGATIVE_INFINITY;
		while (i.hasNext()) {
			Order s = (Order) i.next();
			if (!accepted(s)) {
				if (s.getPrice() > highestUnacceptedBidPrice) {
					highestUnacceptedBidPrice = s.getPrice();
					highestUnacceptedBid = s;
				}
			}
		}
		return highestUnacceptedBidPrice;
	}

	public double getLowestAcceptedBidPrice() {
		Iterator i = bids.iterator();
		double lowestAcceptedBidPrice = Double.POSITIVE_INFINITY;
		while (i.hasNext()) {
			Order s = (Order) i.next();
			if (accepted(s)) {
				if (s.getPrice() < lowestAcceptedBidPrice) {
					lowestAcceptedBidPrice = s.getPrice();
				}
			}
		}
		return lowestAcceptedBidPrice;

	}

	public double getLowestUnacceptedAskPrice() {
		if (lowestUnacceptedAsk != null) {
			return lowestUnacceptedAsk.getPrice();
		}

		Iterator i = asks.iterator();
		double lowestUnacceptedBidPrice = Double.POSITIVE_INFINITY;
		while (i.hasNext()) {
			Order s = (Order) i.next();
			if (!accepted(s)) {
				if (s.getPrice() < lowestUnacceptedBidPrice) {
					lowestUnacceptedBidPrice = s.getPrice();
					lowestUnacceptedAsk = s;
				}
			}
		}
		return lowestUnacceptedBidPrice;
	}

	public double getHighestAcceptedAskPrice() {
		Iterator i = asks.iterator();
		double highestAcceptedAskPrice = Double.NEGATIVE_INFINITY;
		while (i.hasNext()) {
			Order s = (Order) i.next();
			if (accepted(s)) {
				if (s.getPrice() > highestAcceptedAskPrice) {
					highestAcceptedAskPrice = s.getPrice();
				}
			}
		}
		return highestAcceptedAskPrice;
	}

	public List getBids() {
		return bids;
	}

	public List getAsks() {
		return asks;
	}

	public boolean accepted(Order shout) {
		return acceptedShouts.contains(shout);
	}

	public int getNumberOfAsks(double price, boolean accepted) {
		return getNumberOfShouts(asks, price, accepted);
	}

	public int getNumberOfBids(double price, boolean accepted) {
		return getNumberOfShouts(bids, price, accepted);
	}

	public Iterator sortedShoutIterator() {
		return sortedShouts.iterator();
	}

	public void addToSortedShouts(Order shout) {
		sortedShouts.add(shout);
	}

	/**
	 * 
	 * @param shouts
	 * @param price
	 *          the sign of price controls whether higher shouts or lower shouts
	 *          are needed
	 * @param accepted
	 * @return the number of shouts that meet the specified condition
	 */
	public int getNumberOfShouts(List shouts, double price, boolean accepted) {

		int numShouts = 0;
		Iterator i = shouts.iterator();
		while (i.hasNext()) {
			Order shout = (Order) i.next();
			if ((price >= 0 && shout.getPrice() >= price)
			    || (price < 0 && shout.getPrice() <= -price)) {
				if (accepted) {
					if (acceptedShouts.contains(shout)) {
						numShouts++;
					}
				} else {
					numShouts++;
				}
			}
		}

		return numShouts;
	}

	public void produceUserOutput() {
	}

	public Map getVariables() {
		return new HashMap();
	}

	protected void initialisePriceRanges() {
		highestBidPrice = Double.NEGATIVE_INFINITY;
		lowestAskPrice = Double.POSITIVE_INFINITY;

		highestUnacceptedBid = null;
		lowestUnacceptedAsk = null;
	}

	protected void markMatched(List shouts) {
		try {
			Iterator i = shouts.iterator();
			while (i.hasNext()) {
				Order s = (Order) i.next();
				if (auction.orderAccepted(s)) {
					acceptedShouts.add(s);
				}
			}
		} catch (ShoutsNotVisibleException e) {
			throw new AuctionRuntimeException(e);
		}
	}

	public void finalReport() {
	}

	public String toString() {
		return "(" + getClass() + " market:" + auction + " memorySize:"
		    + memorySize + " bids:" + bids + " asks:" + asks + ")";
	}

	public SortedView getSortedView() {
		if (view == null) {
			view = new SortedView();
		}

		return view;
	}

	public void disableSortedView() {
		disableIncreasingQueryAccelerator();
		view.destroy();
		view = null;
	}

	public IncreasingQueryAccelerator getIncreasingQueryAccelerator() {
		if (accelerator == null) {
			accelerator = new IncreasingQueryAccelerator(getSortedView());
		}

		return accelerator;
	}

	public void disableIncreasingQueryAccelerator() {
		accelerator.destroy();
		accelerator = null;
	}

	/**
	 * a class providing sorted lists of shouts.
	 * 
	 */
	public class SortedView extends Observable implements Observer {

		private TreeList sortedAsks;

		private TreeList sortedBids;

		private TreeList sortedAcceptedAsks;

		private TreeList sortedAcceptedBids;

		private TreeList sortedRejectedAsks;

		private TreeList sortedRejectedBids;

		private boolean toBeReset;

		public SortedView() {
			HistoricalDataReport.this.addObserver(this);
			toBeReset = true;
		}

		public void destroy() {
			HistoricalDataReport.this.deleteObserver(this);
		}

		public void reset() {

			sortedAsks = new SortedTreeList("sortedAsks", asks);
			sortedBids = new SortedTreeList("sortedBids", bids);

			sortedAcceptedAsks = new SortedTreeList("sortedAcceptedAsks");
			sortedAcceptedBids = new SortedTreeList("sortedAcceptedBids");
			sortedRejectedAsks = new SortedTreeList("sortedRejectedAsks");
			sortedRejectedBids = new SortedTreeList("sortedRejectedBids");

			Order s;

			Iterator i = asks.iterator();
			while (i.hasNext()) {
				s = (Order) i.next();
				if (acceptedShouts.contains(s)) {
					sortedAcceptedAsks.add(s);
				} else {
					sortedRejectedAsks.add(s);
				}
			}

			i = bids.iterator();
			while (i.hasNext()) {
				s = (Order) i.next();
				if (acceptedShouts.contains(s)) {
					sortedAcceptedBids.add(s);
				} else {
					sortedRejectedBids.add(s);
				}
			}

			setChanged();
			notifyObservers();
		}

		public void update(Observable o, Object arg) {
			toBeReset = true;
			setChanged();
			notifyObservers();
		}

		public void resetIfNeeded() {
			if (toBeReset) {
				reset();
				toBeReset = false;
			}
		}

		public TreeList getSortedAsks() {
			resetIfNeeded();
			return sortedAsks;
		}

		public TreeList getSortedBids() {
			resetIfNeeded();
			return sortedBids;
		}

		public TreeList getSortedAcceptedAsks() {
			resetIfNeeded();
			return sortedAcceptedAsks;
		}

		public TreeList getSortedAcceptedBids() {
			resetIfNeeded();
			return sortedAcceptedBids;
		}

		public TreeList getSortedRejectedAsks() {
			resetIfNeeded();
			return sortedRejectedAsks;
		}

		public TreeList getSortedRejectedBids() {
			resetIfNeeded();
			return sortedRejectedBids;
		}

	}

	/**
	 * a class to speed up queries from GDStrategy regarding the number of shouts
	 * above or below a certain price. It is designed based on the pattern of
	 * increasing prices queried about.
	 * 
	 */
	public class IncreasingQueryAccelerator implements Observer {

		protected ListIterator asksI;

		protected ListIterator bidsI;

		protected ListIterator acceptedAsksI;

		protected ListIterator acceptedBidsI;

		protected ListIterator rejectedAsksI;

		protected ListIterator rejectedBidsI;

		protected int numOfAsksBelow;

		protected int numOfBidsAbove;

		protected int numOfAcceptedAsksAbove;

		protected int numOfAcceptedBidsBelow;

		protected int numOfRejectedAsksBelow;

		protected int numOfRejectedBidsAbove;

		protected double priceForAsksBelow;

		protected double priceForBidsAbove;

		protected double priceForAcceptedAsksAbove;

		protected double priceForAcceptedBidsBelow;

		protected double priceForRejectedAsksBelow;

		protected double priceForRejectedBidsAbove;

		protected SortedView view;

		private boolean toBeReset;

		public IncreasingQueryAccelerator(SortedView view) {
			this.view = view;
			view.addObserver(this);
			toBeReset = true;
		}

		public IncreasingQueryAccelerator() {
			this(new SortedView());
		}

		public void destroy() {
			if (view != null) {
				view.deleteObserver(this);
			}
		}

		/*
		 * resets all the iterations and counting variables when the underlying
		 * sorted view changes.
		 */
		public void update(Observable o, Object arg) {
			toBeReset = true;
		}

		protected void resetIfNeeded() {
			if (toBeReset) {
				reset();
				toBeReset = false;
			}
		}

		public void reset() {
			resetForAsksBelow();
			resetForBidsAbove();
			resetForAcceptedAsksAbove();
			resetForAcceptedBidsBelow();
			resetForRejectedAsksBelow();
			resetForRejectedBidsAbove();
		}

		protected void resetForAsksBelow() {
			asksI = view.getSortedAsks().listIterator();
			numOfAsksBelow = 0;
			priceForAsksBelow = -1;
		}

		protected void resetForBidsAbove() {
			bidsI = view.getSortedBids().listIterator();
			numOfBidsAbove = view.getSortedBids().size();
			priceForBidsAbove = -1;
		}

		protected void resetForAcceptedAsksAbove() {
			acceptedAsksI = view.getSortedAcceptedAsks().listIterator();
			numOfAcceptedAsksAbove = view.getSortedAcceptedAsks().size();
			priceForAcceptedAsksAbove = -1;
		}

		protected void resetForAcceptedBidsBelow() {
			acceptedBidsI = view.getSortedAcceptedBids().listIterator();
			numOfAcceptedBidsBelow = 0;
			priceForAcceptedBidsBelow = -1;
		}

		protected void resetForRejectedAsksBelow() {
			rejectedAsksI = view.getSortedRejectedAsks().listIterator();
			numOfRejectedAsksBelow = 0;
			priceForRejectedAsksBelow = -1;
		}

		protected void resetForRejectedBidsAbove() {
			rejectedBidsI = view.getSortedRejectedBids().listIterator();
			numOfRejectedBidsAbove = view.getSortedRejectedBids().size();
			priceForRejectedBidsAbove = -1;
		}

		public int getNumOfAsksBelow(double price) {
			resetIfNeeded();

			if (priceForAsksBelow > price)
				resetForAsksBelow();
			priceForAsksBelow = price;

			while (asksI.hasNext())
				if (((Order) asksI.next()).getPrice() <= price) {
					numOfAsksBelow++;
				} else {
					try {
						asksI.previous();
					} catch (Exception e) {
						logger.info(e);
						asksI.previous();
					}
					break;
				}

			return numOfAsksBelow;
		}

		public int getNumOfBidsAbove(double price) {
			resetIfNeeded();

			if (priceForBidsAbove > price)
				resetForBidsAbove();
			priceForBidsAbove = price;

			while (bidsI.hasNext()) {
				if (((Order) bidsI.next()).getPrice() < price) {
					numOfBidsAbove--;
				} else {
					try {
						bidsI.previous();
					} catch (Exception e) {
						logger.info(e);
						bidsI.previous();
					}
					break;
				}
			}

			return numOfBidsAbove;
		}

		public int getNumOfAcceptedAsksAbove(double price) {
			resetIfNeeded();

			if (priceForAcceptedAsksAbove > price)
				resetForAcceptedAsksAbove();
			priceForAcceptedAsksAbove = price;

			while (acceptedAsksI.hasNext())
				if (((Order) acceptedAsksI.next()).getPrice() < price) {
					numOfAcceptedAsksAbove--;
				} else {
					try {
						acceptedAsksI.previous();
					} catch (Exception e) {
						logger.info(e);
						acceptedAsksI.previous();
					}
					break;
				}

			return numOfAcceptedAsksAbove;
		}

		public int getNumOfAcceptedBidsBelow(double price) {
			resetIfNeeded();

			if (priceForAcceptedBidsBelow > price)
				resetForAcceptedBidsBelow();
			priceForAcceptedBidsBelow = price;

			while (acceptedBidsI.hasNext())
				if (((Order) acceptedBidsI.next()).getPrice() <= price) {
					numOfAcceptedBidsBelow++;
				} else {
					// NOTE: due to a possible bug in TreeList,
					// NullPointerException may be
					// thrown. Simply doing it again seems working fine.
					try {
						acceptedBidsI.previous();
					} catch (Exception e) {
						logger.info(e);
						acceptedBidsI.previous();
					}
					break;
				}

			return numOfAcceptedBidsBelow;
		}

		public int getNumOfRejectedAsksBelow(double price) {
			resetIfNeeded();

			if (priceForRejectedAsksBelow > price) {
				resetForRejectedAsksBelow();
			}
			priceForRejectedAsksBelow = price;

			while (rejectedAsksI.hasNext())
				if (((Order) rejectedAsksI.next()).getPrice() <= price) {
					numOfRejectedAsksBelow++;
				} else {
					try {
						rejectedAsksI.previous();
					} catch (Exception e) {
						logger.info(e);
						rejectedAsksI.previous();
					}
					break;
				}

			return numOfRejectedAsksBelow;
		}

		public int getNumOfRejectedBidsAbove(double price) {
			resetIfNeeded();

			if (priceForRejectedBidsAbove > price) {
				resetForRejectedBidsAbove();
			}
			priceForRejectedBidsAbove = price;

			while (rejectedBidsI.hasNext())
				if (((Order) rejectedBidsI.next()).getPrice() < price) {
					numOfRejectedBidsAbove--;
				} else {
					try {
						rejectedBidsI.previous();
					} catch (Exception e) {
						logger.info(e);
						rejectedBidsI.previous();
					}
					break;
				}

			return numOfRejectedBidsAbove;
		}
	}

	/**
	 * a tree-based sorted list, which can enable increasing queries about shout
	 * counting.
	 */
	static class SortedTreeList extends TreeList {
		private String name;

		public SortedTreeList(String name) {
			this.name = name;
		}

		public SortedTreeList(String name, Collection c) {
			super(c);
			this.name = name;
		}

		/**
		 * adds <code>o</code> into the list maintaining its sorted nature.
		 * 
		 * @param o
		 * @return always returns true
		 */
		public boolean add(Object o) {
			insert(0, size() - 1, o);
			return true;
		}

		/**
		 * inserts <code>o</code> into the segment from <code>b</code> to
		 * <code>e</code> inclusively at both ends.
		 * 
		 * @param b
		 * @param e
		 * @param o
		 */
		private void insert(int b, int e, Object o) {
			if (b > e)
				add(b, o);
			else {
				int c = ((Comparable) o).compareTo(get((b + e) / 2));

				if (c == 1) {
					// o has higher price
					insert(1 + ((b + e) / 2), e, o);
				} else if (c == -1) {
					// o has lower price
					insert(b, ((b + e) / 2) - 1, o);
				} else {
					add((b + e) / 2, o);
				}
			}

		}

		public String toString() {
			String s = "[";
			ListIterator iterator = listIterator();
			while (iterator.hasNext()) {
				s += ((Order) iterator.next()).getPrice() + " ";
			}
			s += "] ";
			return "(" + getClass() + s + "name: " + name + " size: " + size() + ")";
		}
	}

}