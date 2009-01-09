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

package uk.ac.liv.auction.agent;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steve Phelps
 * @version $Revision$
 */
public class EPEfficiencyTest extends EfficiencyTest {

	public EPEfficiencyTest(String name) {
		super(name);
	}

	protected void assignStrategy(AbstractTradingAgent agent) {
		EquilibriumPriceStrategy strategy = new EquilibriumPriceStrategy();
		agent.setStrategy(strategy);
		strategy.setAgent(agent);
	}

	protected double getMinMeanEfficiency() {
		return 99.99;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(EPEfficiencyTest.class);
	}
}
