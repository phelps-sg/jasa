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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.jabm.SimulationTime;
import net.sourceforge.jasa.agent.TradingAgent;

/**
 * <p>
 * A class representing an order in an market. An order may be either a bid
 * (offer to buy) or an ask (offer to sell).
 * </p>
 * 
 * <p>
 * Orders are mutable within this package for performance reasons, hence care
 * should be taken not to rely on, e.g. shouts held in collections remaining
 * constant.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class Order implements Comparable<Order>, Cloneable, Serializable {

	/**
	 * The volume of this order.
	 */
	protected int quantity;

	/**
	 * The price of this offer.
	 */
	protected Price price;

	/**
	 * The agent placing this order.
	 */
	protected TradingAgent agent;

	/**
	 * True if this order is a bid. False if this order is an ask.
	 */
	protected boolean isBid;
	
	/**
	 * The time that this order was placed.
	 */
	protected SimulationTime timeStamp;
	
	/**
	 * The child of this order.  When orders are partially filled 
	 *  they are split into multiple orders.  The complete set of
	 *  orders, both filled and unfilled, can be retrieved
	 *  by recursively following the child references.
	 */
	protected Order child = null;
	
	/**
	 * True if this order has been completely filled
	 * against a matching one.
	 */
	protected boolean filled = false;

	static DecimalFormat currencyFormatter = new DecimalFormat(
	    "+#########0.00;-#########.00");

	public Order(TradingAgent agent, int quantity, double price, boolean isBid) {
		this(agent);
		this.quantity = quantity;
		this.price = new Price(price);
		this.isBid = isBid;
	}
	
	public Order(TradingAgent agent, int quantity, double price, boolean isBid, SimulationTime timeStamp) {
		this(agent, quantity, price, isBid);
		this.timeStamp = timeStamp;
	}

	public Order(Order existing) {
		this(existing.getAgent(), existing.getQuantity(), existing.getPriceAsDouble(),
		    existing.isBid());
	}

	public Order(TradingAgent agent) {
		this();
		this.agent = agent;
	}

	public Order() {
	}

	public int getQuantity() {
		return quantity;
	}

	public double getPriceAsDouble() {
		return price.doubleValue();
	}

	public Price getPrice() {
	    return price;
    }

	public TradingAgent getAgent() {
		return agent;
	}

	public boolean isBid() {
		return isBid;
	}

	public boolean isAsk() {
		return !isBid;
	}

	/**
	 * Check whether two orders "cross"; that is, check whether this order could
	 * potentially be matched against the supplied order resulting in a
	 * transaction.
	 */
	public boolean matches(Order other) {
		if (this.getAgent().equals(other.getAgent())) {
			return false;
		}
		if (this.isBid()) {
			return other.isAsk() && this.getPriceAsDouble() >= other.getPriceAsDouble();
		} else {
			return other.isBid() && other.getPriceAsDouble() >= this.getPriceAsDouble();
		}
	}

	public int compareTo(Order other) {
		int priceComparision = other.price.compareTo(this.price);
		if (priceComparision != 0) {
			return priceComparision;
		} else if (quantity < other.quantity) {
			return 1;
		} else if (quantity > other.quantity) {
			return -1;
		} else if (timeStamp != null && timeStamp.getTicks() < other.timeStamp.getTicks()) {
			return 1;
		} else if (timeStamp != null && timeStamp.getTicks() > other.timeStamp.getTicks()) {
			return -1;
		} else {
			return 0;
		}
	}

	public boolean isValid() {
	    return !price.isNegative() && quantity > 0;
//		if (price.isNegative()) {
//			return false;
//		}
//		if (quantity < 1) {
//			return false;
//		}
//		if (Double.isInfinite(price) || Double.isNaN(price)) {
//			return false;
//		}
//		return true;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String toString() {
		return "(" + getClass() + " id:" + hashCode() + " quantity:" + quantity + " price:"
		    + price + " isBid:" + isBid + " agent:" + agent + ")";
	}

	public String toPrettyString() {
//		double p = price;
//		if (!isBid) {
//			p = -p;
//		}
		String orderSign = isBid ? "+" : "-";
		return orderSign + " " + price.toPrettyString() + "/" + quantity;
	}

	public static double maxPrice(Order s1, Order s2) {
		return Math.max(price(s1, Double.NEGATIVE_INFINITY), price(s2,
		    Double.NEGATIVE_INFINITY));
	}

	public static double minPrice(Order s1, Order s2) {
		return Math.min(price(s1, Double.POSITIVE_INFINITY), price(s2,
		    Double.POSITIVE_INFINITY));
	}

	private static double price(Order s, double alt) {
		if (s == null) {
			return alt;
		} else {
			return s.getPriceAsDouble();
		}
	}

	/**
	 * Get the child of this shout. Shouts have children when they are split().
	 * 
	 * @return The child Shout object, or null if this Shout is childless.
	 */
	public Order getChild() {
		return child;
	}


	// public boolean equals( Object other ) {
	// return id == ((Shout) other).id &&
	// getAgent().equals(((Shout) other).getAgent());
	// }

	//
	// The following methods allow muting of shouts, but only by classes
	// that are part of the net.sourceforge.jasa.market package.
	//

	void makeChildless() {
		if (child != null) {
			child.makeChildless();
			child = null;
		}
	}

	public void copyFrom(Order other) {
		this.price = other.price;
		this.agent = other.getAgent();
		this.quantity = other.getQuantity();
		this.isBid = other.isBid();
//		this.id = other.getId();
		child = null;
	}

	/**
	 * Reduce the quantity of this shout by excess and return a new child shout
	 * containing the excess quantity. After a split, parent shouts keep a
	 * reference to their children.
	 * 
	 * @param excess
	 *          The excess quantity
	 * 
	 */
	Order split(int excess) {
		this.quantity -= excess;
		try {
//			this.child = null;
			this.child = (Order) this.clone();
			this.child.setQuantity(excess);
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		assert this.isValid();
		assert child.isValid();
		return this.child;
	}

	Order splat(int excess) {
		try {
//			this.child = null;
			this.child = (Order) this.clone();
			this.child.setQuantity(this.quantity - excess);
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		this.quantity = excess;
		assert this.isValid();
		assert child.isValid();
		return this.child;
	}

	public void setIsBid(boolean isBid) {
		this.isBid = isBid;
	}

	public void setAgent(TradingAgent agent) {
		this.agent = agent;
	}

	public void setPrice(double price) {
		this.price = new Price(price);
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public SimulationTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(SimulationTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	public boolean isFilled() {
		return filled;
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}
	
	/**
	 * Calculate the aggregate volume of this order,
	 *  which includes both the filled quantities and 
	 *  outstanding quantity.
	 * 
	 * @return  A long representing the aggregate volume.
	 */
	public long aggregateVolume() {
		long result = 0;
		for (Order p = this; p != null; p = p.getChild()) {
			result += p.getQuantity();
		}
		return result;
	}

	/**
	 * Calculate the total filled quantity of this order.
	 * 
	 * @return  A long representing the aggregate filled volume.
	 */
	public long aggregateFilledVolume() {
		long result = 0;
		for (Order p = this; p != null; p = p.getChild()) {
			if (p.isFilled()) result += p.getQuantity();
		}
		return result;
	}

	/**
	 * Calculate the total unfilled quantity of this order.
	 * 
	 * @return A long representing the aggregate outstanding volume.
	 */
	public long aggregateUnfilledVolume() {
		long result = 0;
		for (Order p = this; p != null; p = p.getChild()) {
			if (!p.isFilled()) result += p.getQuantity();
		}
		return result;
	}	
	
	/**
	 * If an order results in a partial fill then it 
	 * will get split into two or more orders.  This
	 * method fetches the fragmented orders which have not yet
	 * been filled.
	 * 
	 * @return A list of the unfilled order fragments.
	 */
	public List<Order> getUnfilledFraction() {
		List<Order> result = new LinkedList<Order>();
		for (Order p = this; p!= null; p = p.getChild()) {
			if (!p.isFilled()) result.add(p);
		}
		return result;
	}

	/**
	 * A utility method to calculate the total
	 * volume of the supplied orders.
	 * 
	 * @param orders  An Iterable collection of orders.
	 * @return  	 	The total quantity of the orders.
	 */
	public static long totalVolume(Iterable<Order> orders) {
		Iterator<Order> i = orders.iterator();
		long qty = 0;
		while (i.hasNext()) {
			Order s = (Order) i.next();
			qty += s.getQuantity();
		}
		return qty;
	}

}
