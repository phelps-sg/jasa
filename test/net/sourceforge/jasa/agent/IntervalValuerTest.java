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

import cern.jet.random.engine.MersenneTwister64;
import net.sourceforge.jasa.agent.valuation.IntervalValuer;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import junit.framework.TestCase;

/**
 * @author Steve Phelps
 * @version $Revision$
 */
public abstract class IntervalValuerTest extends TestCase {

	protected IntervalValuer valuer;

	protected MarketFacade auction;

	public static final double MIN_VALUE = 10;

	public static final double STEP = 5;

	public IntervalValuerTest(String name) {
		super(name);
	}

	public void setUp() {
		auction = new MarketFacade(new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED));
	}

	public void testStep() {
		double value;

		valuer = assignValuer(MIN_VALUE, STEP);
		value = valuer.determineValue(auction);
		System.out.println("value = " + value);
		assertTrue(value == MIN_VALUE);

		valuer = assignValuer(MIN_VALUE, STEP);
		value = valuer.determineValue(auction);
		System.out.println("value = " + value);
		assertTrue(value == MIN_VALUE + STEP);
	}

	public abstract IntervalValuer assignValuer(double minValue, double step);

}
