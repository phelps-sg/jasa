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

package net.sourceforge.jasa.event;

import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

/**
 * An event that is fired every time an order is fulfilled.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class TransactionExecutedEvent extends MarketEvent {

	/**
	 * The offers that led to this transaction.
	 */
	protected Order ask;

	/**
	 * The offers that led to this transaction.
	 */
	protected Order bid;

	/**
	 * The price at which the good was sold for.
	 */
	protected double price;

	/**
	 * The quantity of the good that was sold.
	 */
	protected int quantity;

	public TransactionExecutedEvent(Market auction, int time, Order ask,
	    Order bid, double price, int quantity) {
		super(auction, time);
		this.ask = ask;
		this.bid = bid;
		this.price = price;
		this.quantity = quantity;
	}
	
	public TransactionExecutedEvent() {
		super(null, 0);
	}

	public Order getAsk() {
		return ask;
	}

	public Order getBid() {
		return bid;
	}

	public double getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}
	
	public String toString() {
		return "(" + getClass() + " price:" + price + " quantity:" + quantity
				+ " bid:" + bid + " ask:" + ask + ")";
	}
}
