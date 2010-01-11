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
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.sim.util.Prototypeable;

/**
 * This strategy bids at the specified percentage markup over the agent's
 * current valuation.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class ProportionalMarkupStrategy extends FixedDirectionStrategy
    implements Serializable, Prototypeable {

	protected double markup;

	public ProportionalMarkupStrategy(AbstractTradingAgent agent, double markup,
	    int quantity) {
		super(agent);
		this.markup = markup;
		this.quantity = quantity;
	}

	public ProportionalMarkupStrategy() {
		super(null);
		markup = 0;
	}

	public Object protoClone() {
		Object clonedStrategy;
		try {
			clonedStrategy = this.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
		return clonedStrategy;
	}

	public boolean modifyShout(Order shout) {
		double delta;
		if (isSell()) {
			delta = markup * agent.getValuation(auction);
		} else {
			delta = -markup * agent.getValuation(auction);
		}
		shout.setPrice(agent.getValuation(auction) + delta);
		shout.setQuantity(quantity);
		if (shout.getPrice() < 0) {
			shout.setPrice(0);
		}
		return super.modifyShout(shout);
	}

	public void endOfRound(Market auction) {
		// Do nothing
	}

	public double getMarkup() {
		return markup;
	}

	public void setMarkup(double markup) {
		this.markup = markup;
	}

	
}