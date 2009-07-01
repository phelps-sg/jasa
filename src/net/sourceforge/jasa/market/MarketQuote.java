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

/**
 * A price quote summarising the current state of an market.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class MarketQuote implements Serializable {

	/**
	 * The current ask-quote. Buyers need to beat this in order for their offers
	 * to get matched.
	 */
	protected double ask;

	/**
	 * The current bid-quote. Sellers need to ask less than this in order for
	 * their offers to get matched.
	 */
	protected double bid;

	public MarketQuote(double ask, double bid) {
		this.ask = ask;
		this.bid = bid;
	}

	public MarketQuote(Order ask, Order bid) {
		if (ask == null) {
			this.ask = Double.MAX_VALUE;
		} else {
			this.ask = ask.getPrice();
		}
		if (bid == null) {
			this.bid = 0;
		} else {
			this.bid = bid.getPrice();
		}
	}

	public void setAsk(double ask) {
		this.ask = ask;
	}

	public void setBid(double bid) {
		this.bid = bid;
	}

	public double getAsk() {
		return ask;
	}
	
	public double getBid() {
		return bid;
	}

	public String toString() {
		return "(MarketQuote bid:" + bid + " ask:" + ask + ")";
	}

}