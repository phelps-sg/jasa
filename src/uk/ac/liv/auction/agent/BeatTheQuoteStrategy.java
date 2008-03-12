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

import cern.jet.random.Uniform;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.Shout.MutableShout;
import uk.ac.liv.prng.GlobalPRNG;

public class BeatTheQuoteStrategy extends FixedQuantityStrategyImpl {

	protected double perterb = 0.20;

	public BeatTheQuoteStrategy() {
		super();
	}

	public BeatTheQuoteStrategy(AbstractTradingAgent agent) {
		super(agent);
	}

	public void endOfRound(Auction auction) {
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
