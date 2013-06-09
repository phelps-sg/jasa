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
package net.sourceforge.jasa.report;

import java.util.Map;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.util.SummaryStats;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.market.Order;

public class VeracityReport extends AbstractAuctionReport {

	protected SummaryStats veracity;

	public static final ReportVariable VAR_VERACITY_MEAN = new ReportVariable(
	    "veracity.mean", "mean value of veracity");

	public static final ReportVariable VAR_VERACITY_STDEV = new ReportVariable(
	    "veracity.stdev", "stdev of veracity");

	public VeracityReport() {
		super();
		veracity = new SummaryStats("Veracity");
	}

	public void produceUserOutput() {
		logger.info("");
		logger.info("Veracity historicalDataReport");
		logger.info("---------------");
		logger.info("");
		veracity.log();
		logger.info("");
	}

	@Override
	public Map<Object,Number> getVariableBindings() {
		Map<Object,Number> vars = super.getVariableBindings();
		vars.put(VAR_VERACITY_MEAN, new Double(veracity.getMean()));
		vars.put(VAR_VERACITY_STDEV, new Double(veracity.getStdDev()));
		return vars;
	}

	public void eventOccurred(SimEvent event) {
		if (event instanceof OrderPlacedEvent) {
			recordVeracity(((OrderPlacedEvent) event).getOrder());
		}
	}

	public void recordVeracity(Order shout) {
		double shoutPrice = shout.getPrice();
		AbstractTradingAgent agent = (AbstractTradingAgent) shout.getAgent();
		double value = agent.getValuation(auction);
		double diff = value - shoutPrice;
		veracity.newData(diff*diff);
	}

	public void reset() {
		veracity.reset();
	}

}
