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

import junit.framework.TestCase;
import uk.ac.liv.auction.core.RandomRobinAuction;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author Steve Phelps
 * @version $Revision$
 */
public abstract class IntervalValuerTest extends TestCase {

	/**
	 * @uml.property name="valuer"
	 * @uml.associationEnd
	 */
	protected IntervalValuer valuer;

	/**
	 * @uml.property name="paramDb"
	 * @uml.associationEnd
	 */
	protected ParameterDatabase paramDb;

	/**
	 * @uml.property name="base"
	 * @uml.associationEnd
	 */
	protected Parameter base;

	/**
	 * @uml.property name="auction"
	 * @uml.associationEnd
	 */
	protected RandomRobinAuction auction;

	public static final double MIN_VALUE = 10;

	public static final double STEP = 5;

	public IntervalValuerTest(String name) {
		super(name);
	}

	public void setUp() {
		valuer = assignValuer();
		paramDb = new ParameterDatabase();
		base = new Parameter("test");
		paramDb.set(base.push(IntervalValuer.P_MINVALUE), MIN_VALUE + "");
		paramDb.set(base.push(IntervalValuer.P_STEP), STEP + "");
		auction = new RandomRobinAuction("test");
	}

	public void testStep() {
		double value;

		valuer.setup(paramDb, base);

		value = valuer.determineValue(auction);
		System.out.println("value = " + value);
		assertTrue(value == MIN_VALUE);

		valuer.setup(paramDb, base);

		value = valuer.determineValue(auction);
		System.out.println("value = " + value);
		assertTrue(value == MIN_VALUE + STEP);
	}

	public abstract IntervalValuer assignValuer();

}
