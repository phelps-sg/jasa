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

import java.io.Serializable;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.AgentArrivalEvent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;

/**
 * <p>
 * Agents of this type have a finite trade entitlement, which determines how
 * many units or "tokens" they are able to trade in a given trading period.
 * Agents become inactive once their intitial trade entitlement is used up, and
 * their trade entitlement is restored at the end of each day.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public class TokenTradingAgent extends FixedDirectionTradingAgent implements
		Serializable {

	/**
	 * The number of units this agent is entitlted to trade in this trading
	 * period.
	 */
	protected int tradeEntitlement;

	/**
	 * The initial value of tradeEntitlement
	 */
	protected int initialTradeEntitlement;

	/**
	 * The number of units traded to date
	 */
	protected int quantityTraded = 0;

	protected boolean isActive = true;

	static Logger logger = Logger.getLogger(TokenTradingAgent.class);

	public TokenTradingAgent(EventScheduler scheduler) {
		super(scheduler);
	}
	
	public TokenTradingAgent() {
		super(null);
	}

	public TokenTradingAgent(int stock, double funds, double privateValue,
			int tradeEntitlement, EventScheduler scheduler) {
		super(stock, funds, privateValue, scheduler);
		this.initialTradeEntitlement = tradeEntitlement;
		initialise();
	}

	public TokenTradingAgent(double privateValue, int tradeEntitlement,
			EventScheduler scheduler) {
		this(0, 0, privateValue, tradeEntitlement, scheduler);
	}
	

//	public void setup(ParameterDatabase parameters, Parameter base) {
//
//		initialTradeEntitlement = parameters.getInt(base
//		    .push(P_INITIAL_TRADE_ENTITLEMENT), null);
//
//		super.setup(parameters, base);
//	}

	public Object protoClone() {
		try {
			TokenTradingAgent clone = (TokenTradingAgent) clone();
			clone.initialise();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	public void onAgentArrival(Market auction, AgentArrivalEvent event) {
		if (tradeEntitlement <= 0) {
			isActive = false;
		}
		super.onAgentArrival(auction, event);
	}

	public void initialise() {
		super.initialise();
		lastOrderFilled = false;
		tradeEntitlement = initialTradeEntitlement;
		quantityTraded = 0;
		isActive = true;
		logger.debug(this + ": initialised.");
	}

	public void onEndOfDay(MarketEvent event) {
		logger.debug("Performing end-of-day processing..");
		super.onEndOfDay(event);
		tradeEntitlement = initialTradeEntitlement;
		isActive = true;
		// quantityTraded = 0;
		lastOrderFilled = false;
		logger.debug("done.");
	}

	public boolean active() {
		return isActive;
	}

	public void orderFilled(Market auction, Order shout, double price,
	    int quantity) {
		super.orderFilled(auction, shout, price, quantity);
//		if ((isBuyer(auction) && price > valuer.determineValue(auction))
//		    || (isSeller(auction) && price < valuer.determineValue(auction))) {
//			logger.debug("Unprofitable transaction");
//		}
		quantityTraded += quantity;
		tradeEntitlement -= quantity;
	}

	public double equilibriumProfits(Market auction, double equilibriumPrice,
	    int quantity) {
		double surplus = 0;
		if (isSeller()) {
			surplus = equilibriumPrice - getValuation(auction);
		} else {
			surplus = getValuation(auction) - equilibriumPrice;
		}
		return auction.getDay() * initialTradeEntitlement * surplus;
	}

	public double equilibriumProfitsEachDay(Market auction,
	    double equilibriumPrice, int quantity) {
		double surplus = 0;
		if (isSeller()) {
			surplus = equilibriumPrice - getValuation(auction);
		} else {
			surplus = getValuation(auction) - equilibriumPrice;
		}
		// TODO
		if (surplus < 0) {
			surplus = 0;
		}
		return initialTradeEntitlement * surplus;
	}

	public int getQuantityTraded() {
		return quantityTraded;
	}

	public int determineQuantity(Market auction) {
		return getTradingStrategy().determineQuantity(auction);
	}
	

	public int getTradeEntitlement() {
		return tradeEntitlement;
	}

	public void setTradeEntitlement(int tradeEntitlement) {
		this.tradeEntitlement = tradeEntitlement;
	}

	public int getInitialTradeEntitlement() {
		return initialTradeEntitlement;
	}

	public void setInitialTradeEntitlement(int initialTradeEntitlement) {
		this.initialTradeEntitlement = initialTradeEntitlement;
	}

	public String toString() {
		return "(" + getClass() + " id:" + hashCode()
		    + " valuer:" + valuer + " strategy:" + strategy + " tradeEntitlement:"
		    + tradeEntitlement + " quantityTraded:" + quantityTraded + ")";
	}
	
	
	


}