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
 * @author Steve Phelps
 * @version $Revision$
 */

public class FixedPriceStrategy extends FixedDirectionStrategy implements
    Serializable, Prototypeable {

	protected double price;

	public FixedPriceStrategy(AbstractTradingAgent agent, double price,
	    int quantity) {
		super(agent);
		this.price = price;
		this.quantity = quantity;
	}

	public FixedPriceStrategy() {
		super(null);
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
//		if (agent.isBuyer(auction) && price <= agent.getValuation(auction)
//		    || agent.isSeller(auction) && price >= agent.getValuation(auction)) {
			shout.setPrice(price);
//		} else {
//			shout.setPrice(agent.getValuation(auction));
//		}

		return super.modifyShout(shout);
	}

	public void onRoundClosed(Market auction) {
		// Do nothing
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPrice() {
		return price;
	}

}