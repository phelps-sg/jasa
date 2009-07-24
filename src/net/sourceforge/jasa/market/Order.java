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



import java.text.DecimalFormat;

import java.io.Serializable;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.sim.util.IdAllocator;

/**
 * <p>
 * A class representing a shout in an market. A shout may be either a bid
 * (offer to buy) or an ask (offer to sell).
 * </p>
 * 
 * <p>
 * Shouts are mutable within this package for performance reasons, hence care
 * should be taken not to rely on, e.g. shouts held in collections remaining
 * constant.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class Order implements Comparable<Order>, Cloneable, Serializable {

	/**
	 * The number of items offered/wanted.
	 */
	protected int quantity;

	/**
	 * The price of this offer
	 */
	protected double price;

	/**
	 * The agent placing this offer
	 */
	protected TradingAgent agent;

	/**
	 * True if this shout is a bid. False if this shout is an ask.
	 */
	protected boolean isBid;

	/**
	 * The unique id of this shout
	 */
//	protected long id = -1;

	/**
	 * The child of this shout.
	 */
	protected Order child = null;

	/**
	 * Used to allocate each agent with a unique id.
	 */
//	static IdAllocator idAllocator = new IdAllocator();

	static DecimalFormat currencyFormatter = new DecimalFormat(
	    "+#########0.00;-#########.00");

	public Order(TradingAgent agent, int quantity, double price, boolean isBid) {
		this(agent);
		this.quantity = quantity;
		this.price = price;
		this.isBid = isBid;
	}

	public Order(Order existing) {
		this(existing.getAgent(), existing.getQuantity(), existing.getPrice(),
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

	public double getPrice() {
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

	public boolean matches(Order other) {
		if (this.getAgent() == other.getAgent()) {
			return false;
		}
		if (this.isBid()) {
			return other.isAsk() && this.getPrice() >= other.getPrice();
		} else {
			return other.isBid() && other.getPrice() >= this.getPrice();
		}
	}

	public int compareTo(Order other) {
		if (price > other.price) {
			return 1;
		} else if (price < other.price) {
			return -1;
		} else {
			return 0;
		}
		// return new Long(this.price).compareTo(new Long(other.getPrice()));
	}

	public boolean isValid() {
		if (price < 0) {
			return false;
		}
		if (quantity < 1) {
			return false;
		}
		return true;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String toString() {
		return "(" + getClass() + " id:" + hashCode() + " quantity:" + quantity + " price:"
		    + price + " isBid:" + isBid + " agent:" + agent + ")";
	}

	public String toPrettyString() {
		double p = price;
		if (!isBid) {
			p = -p;
		}
		return currencyFormatter.format(p) + "/" + quantity;
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
			return s.getPrice();
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

	void copyFrom(Order other) {
		this.price = other.getPrice();
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
		quantity -= excess;
		Order newShout = new Order(agent, excess, price, isBid);
		child = newShout;
		assert isValid();
		assert newShout.isValid();
		return newShout;
	}

	Order splat(int excess) {
		Order newShout = new Order(agent, quantity - excess, price, isBid);
		quantity = excess;
		child = newShout;
		assert isValid();
		assert newShout.isValid();
		return newShout;
	}

	void setIsBid(boolean isBid) {
		this.isBid = isBid;
	}

	void setAgent(TradingAgent agent) {
		this.agent = agent;
	}

	void setPrice(double price) {
		this.price = price;
	}

	void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * A Shout that is publically mutable.
	 * 
	 * @author Steve Phelps
	 */
	public static class MutableShout extends Order {

		public MutableShout() {
			super();
		}

		public MutableShout(Order existing) {
			super(existing);
		}

		public void setPrice(double price) {
			super.setPrice(price);
		}

		public void setAgent(TradingAgent agent) {
			super.setAgent(agent);
		}

		public void setQuantity(int quantity) {
			super.setQuantity(quantity);
		}

		public void setIsBid(boolean isBid) {
			super.setIsBid(isBid);
		}

		public void copyFrom(Order other) {
			super.copyFrom(other);
		}
	}

}
