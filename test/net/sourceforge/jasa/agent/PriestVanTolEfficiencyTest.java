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

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.strategy.PriestVanTolStrategy;
import net.sourceforge.jasa.report.HistoricalDataReport;
import net.sourceforge.jasa.sim.learning.WidrowHoffLearnerWithMomentum;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steve Phelps
 * @version $Revision$
 */
public class PriestVanTolEfficiencyTest extends EfficiencyTest {

	public static final double BENCHMARK_EFFICIENCY = 85.0;

	public static final int TRADE_ENTITLEMENT = 5;

	public PriestVanTolEfficiencyTest(String name) {
		super(name);
	}

	protected void initialiseAuction() {
		super.initialiseAuction();
//		HistoricalDataReport historicalDataReport = new HistoricalDataReport();
//		historicalDataReport.setAuction(auction);
//		auction.setReport(historicalDataReport);
		//TODO
	}

	protected void assignStrategy(AbstractTradingAgent agent) {
		PriestVanTolStrategy strategy = new PriestVanTolStrategy();
		strategy.setLearner(new WidrowHoffLearnerWithMomentum());
		agent.setStrategy(strategy);
		strategy.setAgent(agent);
	}

	protected double getMinMeanEfficiency() {
		return BENCHMARK_EFFICIENCY;
	}

	protected int getInitialTradeEntitlement() {
		return TRADE_ENTITLEMENT;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(PriestVanTolEfficiencyTest.class);
	}
}
