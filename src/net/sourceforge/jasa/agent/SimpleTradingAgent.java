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

package net.sourceforge.jasa.agent;

import net.sourceforge.jasa.agent.strategy.FixedQuantityStrategy;

import net.sourceforge.jasa.event.MarketEvent;

import net.sourceforge.jasa.market.Market;

/**
 * <p>
 * Agents of this type have a fixed volume, and they trade units equal to their
 * volume in each round of the market.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class SimpleTradingAgent extends AbstractTradingAgent {


	public SimpleTradingAgent(double privateValue,
			boolean isSeller, TradingStrategy strategy) {
		super(0, 0, privateValue, isSeller, strategy);
	}

	public SimpleTradingAgent(double privateValue, boolean isSeller) {
		super(0, 0, privateValue, isSeller);
	}

	public SimpleTradingAgent() {
		this(0, false);
	}

	
	public void requestShout(Market auction) {
		super.requestShout(auction);
		lastPayoff = 0;
	}

	public boolean acceptDeal(Market auction, double price, int quantity) {
		assert isSeller;
		return price >= valuer.determineValue(auction);
	}

	public int getVolume() {
		return ((FixedQuantityStrategy) strategy).getQuantity();
	}
//	
//	public void setVolume(int volume) {
//		this.volume = volume;
//	}

	public double getLastPayoff() {
		return lastPayoff;
	}

	public double equilibriumProfits(Market auction, double equilibriumPrice,
	    int quantity) {
		double surplus = 0;
		if (isSeller) {
			surplus = equilibriumPrice - getValuation(auction);
		} else {
			surplus = getValuation(auction) - equilibriumPrice;
		}
		// TODO
		if (surplus < 0) {
			surplus = 0;
		}
		return auction.getAge() * quantity * surplus;
	}

	public boolean active() {
		return true;
	}

	public void endOfDay(MarketEvent event) {
		// reset();
	}

	public String toString() {
		return "(" + getClass() + " id:" + hashCode() 
		    + " valuer:" + valuer + 
		    + totalPayoff + " isSeller:" + isSeller + " lastProfit:" + lastPayoff
		    + " strategy:" + strategy + ")";
	}

}
