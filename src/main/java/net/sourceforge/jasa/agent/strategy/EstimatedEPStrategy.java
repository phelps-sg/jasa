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
import net.sourceforge.jasa.agent.TokenTradingAgent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.report.HistoricalDataReport;

public class EstimatedEPStrategy extends FixedDirectionStrategy {

	protected HistoricalDataReport historicalDataReport;

	protected double perterb = 0.02;

	protected boolean truthTeller = false;

	protected double truthTellingProbability = 0.6666;
	
	public EstimatedEPStrategy(AbstractTradingAgent agent) {
		super(agent);
	}

	public EstimatedEPStrategy() {
		super();
	}

	public void eventOccurred(MarketEvent event) {		
		super.eventOccurred(event);
	}

	public boolean modifyShout(Order shout) {
		double a = estimatedAskQuote();
		double b = estimatedBidQuote();
		double t = getAgent().getValuation(auction);
		double p = 0;
		if (Double.isInfinite(a) || Double.isInfinite(b)) {
			p = t;
		} else {
			p = (a + b) / 2;
		}
		if (((TokenTradingAgent) agent).isBuyer()) {
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

	public void onRoundClosed(Market auction) {

	}

	protected double estimatedBidQuote() {
		// return Math.max(historicalDataReport.getHighestAcceptedAskPrice(), historicalDataReport
		// .getHighestUnacceptedBidPrice());
		return historicalDataReport.getHighestAcceptedAskPrice();
	}

	protected double estimatedAskQuote() {
		// return Math.min(historicalDataReport.getLowestUnacceptedAskPrice(), historicalDataReport
		// .getLowestAcceptedBidPrice());
		return historicalDataReport.getLowestAcceptedBidPrice();
	}

	public HistoricalDataReport getHistoricalDataReport() {
		return historicalDataReport;
	}

	public void setHistoricalDataReport(HistoricalDataReport historicalDataReport) {
		this.historicalDataReport = historicalDataReport;
	}
	

}
