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

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

public class BeatTheQuoteStrategy extends FixedDirectionStrategy {

	protected double perterb = 0.20;
	
	protected RandomEngine prng;

	public BeatTheQuoteStrategy(RandomEngine prng) {
		this(null, prng);
	}

	public BeatTheQuoteStrategy(AbstractTradingAgent agent, RandomEngine prng) {
		super(agent);
		this.prng = prng;
	}

	public void onRoundClosed(Market auction) {
	}

	public boolean modifyShout(Order shout) {
		MarketQuote quote = auction.getQuote();
		if (isBuy()) {
			double p = quote.getAsk();
			if (!Double.isInfinite(p) && p < getAgent().getValuation(auction)) {
				// shout.setPrice(p + p * GlobalPRNG.getInstance().uniform(0,
				// perterb));
				shout.setPrice(p + p
				    * new Uniform(0, perterb, prng).nextDouble());
			} else {
				shout.setPrice(getAgent().getValuation(auction));
			}
		} else {
			double p = quote.getBid();
			if (!Double.isInfinite(p) && p > getAgent().getValuation(auction)) {
				// shout.setPrice(p - p * GlobalPRNG.getInstance().uniform(0,
				// perterb));
				shout.setPrice(p - p
				    * new Uniform(0, perterb, prng).nextDouble());
			} else {
				shout.setPrice(getAgent().getValuation(auction));
			}
		}
		return super.modifyShout(shout);
	}

}
