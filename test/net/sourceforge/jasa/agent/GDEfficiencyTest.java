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

package net.sourceforge.jasa.agent;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jasa.agent.strategy.GDStrategy;
import net.sourceforge.jasa.report.HistoricalDataReport;

/**
 * @author Steve Phelps
 * @version $Revision$
 */
public class GDEfficiencyTest extends EfficiencyTest {

	protected HistoricalDataReport historicalDataReport;
	
	public static final double BENCHMARK_EFFICIENCY = 95.0;

	public GDEfficiencyTest(String name) {
		super(name);
	}

	protected void initialiseAuction() {
		super.initialiseAuction();
		historicalDataReport = new HistoricalDataReport();
		historicalDataReport.setAuction(auction);
		auction.addReport(historicalDataReport);
	}

	protected void assignStrategy(AbstractTradingAgent agent) {
		GDStrategy strategy = new GDStrategy();
		agent.setStrategy(strategy);
		strategy.setAgent(agent);
		strategy.setHistoryStats(historicalDataReport);
	}

	protected double getMinMeanEfficiency() {
		return BENCHMARK_EFFICIENCY;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(GDEfficiencyTest.class);
	}
}
