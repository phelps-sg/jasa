/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout.MutableShout;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionOpenEvent;
import uk.ac.liv.auction.stats.HistoricalDataReport;

public class EstimatedEPStrategy extends FixedQuantityStrategyImpl {

	protected HistoricalDataReport history;

	protected double perterb = 0.02;

	protected boolean truthTeller = false;

	protected double truthTellingProbability = 0.6666;

	public EstimatedEPStrategy(AbstractTradingAgent agent) {
		super(agent);
	}

	public EstimatedEPStrategy() {
		super();
	}

	public void eventOccurred(AuctionEvent event) {
		if (event instanceof AuctionOpenEvent) {
			history = (HistoricalDataReport) event.getAuction().getReport(
			    HistoricalDataReport.class);

		}
		super.eventOccurred(event);
	}

	public boolean modifyShout(MutableShout shout) {
		double a = estimatedAskQuote();
		double b = estimatedBidQuote();
		double t = agent.getValuation(auction);
		double p = 0;
		if (Double.isInfinite(a) || Double.isInfinite(b)) {
			p = t;
		} else {
			p = (a + b) / 2;
		}
		if (agent.isBuyer(auction)) {
			// p *= 1 - GlobalPRNG.getInstance().uniform(0, perterb);
			if (p < t) {
				shout.setPrice(p);
			} else {
				shout.setPrice(t);
			}
		} else {
			// p *= 1 + GlobalPRNG.getInstance().uniform(0, perterb);
			if (p > t) {
				shout.setPrice(p);
			} else {
				shout.setPrice(t);
			}
		}

		return super.modifyShout(shout);
	}

	public void endOfRound(Auction auction) {

	}

	protected double estimatedBidQuote() {
		// return Math.max(history.getHighestAcceptedAskPrice(), history
		// .getHighestUnacceptedBidPrice());
		return history.getHighestAcceptedAskPrice();
	}

	protected double estimatedAskQuote() {
		// return Math.min(history.getLowestUnacceptedAskPrice(), history
		// .getLowestAcceptedBidPrice());
		return history.getLowestAcceptedBidPrice();
	}

}
