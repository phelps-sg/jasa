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

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

/**
 * 
 * An abstract implementation of FixedQuantityStrategy.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public abstract class FixedQuantityStrategyImpl extends AbstractTradingStrategy
		implements FixedQuantityStrategy, Serializable {

	int quantity = 1;

	public FixedQuantityStrategyImpl(AbstractTradingAgent agent) {
		super(agent);
	}

	public FixedQuantityStrategyImpl() {
		this(null);
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	public int determineQuantity(Market auction) {
		return quantity;
	}

	public boolean modifyShout(Order shout) {
		shout.setQuantity(quantity);
		return super.modifyShout(shout);
	}

}