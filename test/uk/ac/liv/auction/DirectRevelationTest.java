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

package uk.ac.liv.auction;

import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.liv.auction.agent.RandomValuer;
import uk.ac.liv.auction.agent.TruthTellingStrategy;
import uk.ac.liv.auction.electricity.ElectricityTrader;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class DirectRevelationTest extends ElectricityTest {

	public static final double VALUE_MIN = 50;

	public static final double VALUE_MAX = 100;

	public DirectRevelationTest(String name) {
		super(name);
		generatePRNGseeds();
	}

	/*
	 * Test that truth-telling in a DA (CH) always results in 100% efficiency
	 */
	public void testTruthTelling() {
		experimentSetup(3, 3, 10, 10);
		runExperiment();
		assertTrue(eA.getMin() >= 99.99);
		assertTrue(eA.getMean() >= 99.99);
		assertTrue(eA.getMax() <= 100.99);
	}

	public void experimentSetup(int ns, int nb, int cs, int cb) {
		super.experimentSetup(ns, nb, cs, cb);
		auction.setMaximumRounds(1);
	}

	public void assignStrategy(ElectricityTrader agent) {
		TruthTellingStrategy truthTelling = new TruthTellingStrategy();
		agent.setStrategy(truthTelling);
		agent.reset();
	}

	public void assignValuer(ElectricityTrader agent) {
		agent.setValuationPolicy(new RandomValuer(VALUE_MIN, VALUE_MAX));
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(DirectRevelationTest.class);
	}

}