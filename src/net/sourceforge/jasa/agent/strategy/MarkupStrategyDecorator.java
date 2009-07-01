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

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.TradingStrategy;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.sim.util.Prototypeable;

/**
 * This strategy decorates a component strategy by bidding a fixed proportional
 * markup over the price specified by the underlying component strategy.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class MarkupStrategyDecorator extends FixedQuantityStrategyImpl
    implements Serializable, Prototypeable {

	/**
	 * The component strategy to decorate.
	 */
	protected TradingStrategy subStrategy;

	/**
	 * The proportional markup on the sub strategy.
	 */
	protected double markup;

	public static final String P_SUBSTRATEGY = "substrategy";

	public static final String P_MARKUP = "markup";

	public MarkupStrategyDecorator() {
		super(null);
	}

	public boolean modifyShout(Order.MutableShout shout) {
		assert agent.equals(((AbstractStrategy) subStrategy).getAgent());
		double delta;
		Order strategicShout = subStrategy.modifyOrder(shout, auction);
		double strategicPrice = strategicShout.getPrice();
		if (strategicShout != null) {
			if (agent.isSeller(auction)) {
				delta = markup * strategicPrice;
			} else {
				delta = -markup * strategicPrice;
			}
			shout.setPrice(strategicPrice + delta);
			shout.setQuantity(quantity);
			if (shout.getPrice() < 0) {
				shout.setPrice(0);
			}
			return super.modifyShout(shout);
		} else {
			return false;
		}
	}

	public void endOfRound(Market auction) {

	}

	public void eventOccurred(MarketEvent event) {
		super.eventOccurred(event);
		subStrategy.eventOccurred(event);
	}

	public void setAgent(AbstractTradingAgent agent) {
		super.setAgent(agent);
		subStrategy.setAgent(agent);
	}

	public Object protoClone() {
		Object clonedStrategy;
		try {
			clonedStrategy = this.clone();
			((MarkupStrategyDecorator) clonedStrategy).subStrategy = (TradingStrategy) subStrategy
			    .protoClone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
		return clonedStrategy;
	}

}