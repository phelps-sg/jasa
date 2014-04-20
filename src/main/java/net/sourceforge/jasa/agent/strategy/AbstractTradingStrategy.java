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

package net.sourceforge.jasa.agent.strategy;

import java.io.Serializable;

import net.sourceforge.jabm.strategy.AbstractStrategy;
import net.sourceforge.jabm.util.Resetable;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.TradingStrategy;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

import org.springframework.beans.factory.annotation.Required;

/**
 * <p>
 * An abstract implementation of the Strategy interface that provides skeleton
 * functionality for making trading decisions.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public abstract class AbstractTradingStrategy extends AbstractStrategy
		implements Serializable, TradingStrategy, Resetable, Cloneable {

	protected Market auction;
	
	protected TradeDirectionPolicy tradeDirectionPolicy;

	public AbstractTradingStrategy() {
		initialise();
	}

	public AbstractTradingStrategy(AbstractTradingAgent agent) {
		this();
		this.agent = agent;
	}

	public void reset() {
		initialise();
	}

	public Object protoClone() {
		try {
			AbstractTradingStrategy copy = (AbstractTradingStrategy) clone();
			copy.reset();
			return copy;
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	/**
	 * Modify the price and quantity of the given shout according to this
	 * strategy.
	 * 
	 * @return false if no shout is to be placed at this time
	 */
	@Override
	public Order modifyOrder(Order currentShout, Market auction) {
		this.auction = auction;
		if (currentShout == null) {
			currentShout = new Order();
		}
		if (modifyShout(currentShout)) {
			return new Order(currentShout.getAgent(), currentShout.getQuantity(),
			    currentShout.getPrice(), currentShout.isBid());
		} else {
			return null;
		}
	}
	
	public boolean modifyShout(Order shout) {
		shout.setAgent(getAgent());
		shout.setIsBid(this.isBuy(this.auction));
		return true;
	}

	public void initialise() {
	}

	@Override
	public AbstractTradingAgent getAgent() {
		return (AbstractTradingAgent) agent;
	}

	@Override
	public void setAgent(AbstractTradingAgent agent) {
		this.agent = agent;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean isBuy(Market market) {
		return tradeDirectionPolicy.isBuy(market, getAgent());
	}

	public TradeDirectionPolicy getTradeDirectionPolicy() {
		return tradeDirectionPolicy;
	}

	@Required
	public void setTradeDirectionPolicy(TradeDirectionPolicy tradeDirectionPolicy) {
		this.tradeDirectionPolicy = tradeDirectionPolicy;
	}
	
}