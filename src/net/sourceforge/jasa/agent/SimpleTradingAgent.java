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

package net.sourceforge.jasa.agent;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.AgentArrivalEvent;
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
	
	public SimpleTradingAgent(int stock, double funds, double privateValue,
			EventScheduler scheduler) {
		super(stock, funds, privateValue, scheduler);
	}

	public SimpleTradingAgent(int stock, double funds, double privateValue,
			TradingStrategy strategy, EventScheduler scheduler) {
		super(stock, funds, privateValue, strategy, scheduler);
	}

	public SimpleTradingAgent(int stock, double funds, EventScheduler scheduler) {
		super(stock, funds, scheduler);
	}

	public SimpleTradingAgent(double privateValue,
			boolean isSeller, TradingStrategy strategy, EventScheduler scheduler) {
		super(0, 0, privateValue, strategy, scheduler);
	}

	public SimpleTradingAgent(double privateValue, EventScheduler scheduler) {
		super(0, 0, privateValue, scheduler);
	}

	public SimpleTradingAgent(EventScheduler scheduler) {
		this(0, scheduler);
	}
	
	public SimpleTradingAgent() {
		this(null);
	}
	
	public void onAgentArrival(Market auction, AgentArrivalEvent event) {
		super.onAgentArrival(auction, event);
		lastPayoff = 0;
	}

	public boolean acceptDeal(Market auction, double price, int quantity) {
		return price >= valuer.determineValue(auction);
	}
//	
//	public void setVolume(int volume) {
//		this.volume = volume;
//	}

	public double getLastPayoff() {
		return lastPayoff;
	}

	public boolean active() {
		return true;
	}

	public void onEndOfDay(MarketEvent event) {
		// reset();
	}

	public String toString() {
		return "(" + getClass() + " id:" + hashCode() 
		    + " valuer:" + valuer + 
		    + totalPayoff + " lastProfit:" + lastPayoff
		    + " strategy:" + strategy + ")";
	}

	
	@Override
	public double calculateProfit(Market auction, int quantity, double price) {
		if (currentOrder == null) {
			return 0;
		}
		return super.calculateProfit(auction, quantity, price);
	}
	
//	@Override
//	public double calculateProfit(Market auction, int quantity, double price) {
//		if (isBuyer()) {
//			return (getValuation(auction) - price) * quantity;
//		} else {
//			return  (price - getValuation(auction)) * quantity;
//		}
//	}
	
}
