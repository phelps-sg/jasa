/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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

package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.util.Prototypeable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.io.Serializable;

/**
 * This strategy bids at the specified percentage markup over the agent's
 * current valuation.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class ProportionalMarkupStrategy extends FixedQuantityStrategyImpl
    implements Serializable, Prototypeable {

	protected double markup;

	public static final String P_MARKUP = "markup";

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

	public void setup(ParameterDatabase parameters, Parameter base) {
		super.setup(parameters, base);
		markup = parameters.getDoubleWithDefault(base.push(P_MARKUP), null, 0);
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

	public boolean modifyShout(Shout.MutableShout shout) {
		double delta;
		if (agent.isSeller(auction)) {
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

	public void endOfRound(Auction auction) {
		// Do nothing
	}

}