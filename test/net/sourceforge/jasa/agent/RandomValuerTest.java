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

import net.sourceforge.jasa.agent.valuation.RandomValuer;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import net.sourceforge.jasa.sim.prng.GlobalPRNG;
import net.sourceforge.jasa.sim.util.MathUtil;
import net.sourceforge.jasa.sim.util.SummaryStats;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Steve Phelps
 * @version $Revision$
 */
public class RandomValuerTest extends TestCase {

	/**
	 * @uml.property name="valuer"
	 * @uml.associationEnd
	 */
	protected RandomValuer valuer;

	public static final double MIN_VALUE = 0;

	public static final double MAX_VALUE = 100;

	public static final int SAMPLES = (int) 10E7;

	public RandomValuerTest(String name) {
		super(name);
	}

	public void setUp() {
		GlobalPRNG.initialiseWithSeed(PRNGTestSeeds.UNIT_TEST_SEED);
		valuer = new RandomValuer(MIN_VALUE, MAX_VALUE);
	}

	public void testDistribution() {
		System.out.println("testDistribution()");
		System.out.println("Taking " + SAMPLES + " samples...");
		SummaryStats values = new SummaryStats();
		for (int i = 0; i < SAMPLES; i++) {
			valuer.drawRandomValue();
			double value = valuer.getCurrentValuation();
			values.newData(value);
		}
		System.out.println("min = " + values.getMin());
		System.out.println("max = " + values.getMax());
		System.out.println("mean = " + values.getMean());
		System.out.println("stdev = " + values.getStdDev());
		assertTrue(MathUtil.approxEqual(values.getMin(), MIN_VALUE, 10E-3));
		assertTrue(MathUtil.approxEqual(values.getMax(), MAX_VALUE, 10E-3));
		assertTrue(MathUtil.approxEqual(values.getMean(), MAX_VALUE / 2, 10E-3));
		assertTrue(MathUtil.approxEqual(values.getStdDev(), 28.86, 10E-3));
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(RandomValuerTest.class);
	}

}
