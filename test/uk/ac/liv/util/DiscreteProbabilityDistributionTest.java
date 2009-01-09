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

package uk.ac.liv.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.liv.prng.DiscreteProbabilityDistribution;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class DiscreteProbabilityDistributionTest extends TestCase {

	/**
	 * @uml.property name="p"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	protected DiscreteProbabilityDistribution p[];

	static final int NUM_TRIALS = 10000000;

	static final double probs[][] = { { 0.1, 0.2, 0.4, 0.2, 0.1 },
	    { 0.2, 0.2, 0.2, 0.2, 0.2 }, { 0.25, 0.2, 0.1, 0.2, 0.25 },
	    { 0, 1, 0, 0, 0 }, { 1, 0, 0, 0, 0 }, { 0, 0, 0, 0, 1 } };

	public DiscreteProbabilityDistributionTest(String name) {
		super(name);
	}

	public void setUp() {
		p = new DiscreteProbabilityDistribution[probs.length];
		for (int i = 0; i < probs.length; i++) {
			p[i] = new DiscreteProbabilityDistribution(probs[i].length);
			for (int j = 0; j < probs[i].length; j++) {
				p[i].setProbability(j, probs[i][j]);
			}
		}
	}

	public void testStats() {
		for (int test = 0; test < p.length; test++) {
			DiscreteProbabilityDistribution subject = p[test];
			SummaryStats eventData = new SummaryStats(
			    "Event_Data");
			for (int trial = 0; trial < NUM_TRIALS; trial++) {
				int event = subject.generateRandomEvent();
				eventData.newData(event);
			}
			System.out.println(eventData);
			double mean = subject.computeMean();

			System.out
			    .println("Testing with subject number " + test + ": " + subject);

			System.out.println("target mean = " + mean);
			assertTrue(approxEqual(eventData.getMean(), mean));

			double min = (double) subject.computeMin();
			System.out.println("target min = " + min);
			assertTrue(approxEqual(eventData.getMin(), min));

			double max = (double) subject.computeMax();
			System.out.println("target max = " + max);
			assertTrue(approxEqual(eventData.getMax(), max));

		}
	}

	public boolean approxEqual(double x, double y) {
		return Math.abs(x - y) < 0.005;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(DiscreteProbabilityDistributionTest.class);
	}
}
