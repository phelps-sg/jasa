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

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order.MutableShout;
import net.sourceforge.jasa.sim.prng.GlobalPRNG;
import cern.jet.random.Uniform;

public class BeatTheQuoteStrategy extends FixedQuantityStrategyImpl {

	protected double perterb = 0.20;

	public BeatTheQuoteStrategy() {
		super();
	}

	public BeatTheQuoteStrategy(AbstractTradingAgent agent) {
		super(agent);
	}

	public void endOfRound(Market auction) {
	}

	public boolean modifyShout(MutableShout shout) {
		MarketQuote quote = auction.getQuote();
		if (agent.isBuyer(auction)) {
			double p = quote.getAsk();
			if (!Double.isInfinite(p) && p < agent.getValuation(auction)) {
				// shout.setPrice(p + p * GlobalPRNG.getInstance().uniform(0,
				// perterb));
				shout.setPrice(p + p
				    * new Uniform(0, perterb, GlobalPRNG.getInstance()).nextDouble());
			} else {
				shout.setPrice(agent.getValuation(auction));
			}
		} else {
			double p = quote.getBid();
			if (!Double.isInfinite(p) && p > agent.getValuation(auction)) {
				// shout.setPrice(p - p * GlobalPRNG.getInstance().uniform(0,
				// perterb));
				shout.setPrice(p - p
				    * new Uniform(0, perterb, GlobalPRNG.getInstance()).nextDouble());
			} else {
				shout.setPrice(agent.getValuation(auction));
			}
		}
		return super.modifyShout(shout);
	}

}
