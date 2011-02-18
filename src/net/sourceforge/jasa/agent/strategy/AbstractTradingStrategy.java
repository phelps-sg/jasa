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

package net.sourceforge.jasa.agent.strategy;

import java.io.Serializable;
import java.util.List;

import net.sourceforge.jabm.Agent;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.StrategyExecutedEvent;
import net.sourceforge.jabm.strategy.AbstractStrategy;
import net.sourceforge.jabm.util.Resetable;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.TradingStrategy;
import net.sourceforge.jasa.event.RoundClosedEvent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

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

	protected AbstractTradingAgent agent;
//
//	protected Order currentShout;

	protected Market auction;

	public AbstractTradingStrategy() {
		initialise();
	}

	public AbstractTradingStrategy(AbstractTradingAgent agent) {
		this();
		this.agent = agent;
	}

	public void setAgent(AbstractTradingAgent agent) {
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

	/**
	 * Modify the price and quantity of the given shout according to this
	 * strategy.
	 * 
	 * @return false if no shout is to be placed at this time
	 */
	public boolean modifyShout(Order shout) {
		//TODO Introduce a new class FixedDirectionStrategy
//		shout.setIsBid(agent.isBuyer(auction));
		shout.setAgent(agent);
		return true;
	}

	public void initialise() {
//		currentShout = new Order();
	}

	@Override
	public void eventOccurred(SimEvent event) {
		if (event instanceof RoundClosedEvent) {
			onRoundClosed(((RoundClosedEvent) event).getAuction());
		}
	}
	
	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		scheduler.addListener(RoundClosedEvent.class, this);
	}

	public AbstractTradingAgent getAgent() {
		return agent;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

//	@Override
//	public void execute(List<Agent> otherAgents) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void setAgent(Agent agent) {
		this.agent = (AbstractTradingAgent) agent;
	}

//	@Override
//	public Strategy transfer(Agent newAgent) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public abstract void onRoundClosed(Market auction);
}