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

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.liv.ai.learning.NPTRothErevLearner;
import uk.ac.liv.ai.learning.RothErevLearnerTest;
import uk.ac.liv.auction.agent.AdaptiveStrategy;
import uk.ac.liv.auction.electricity.ElectricityTrader;

/**
 * 
 * Attempt an approximate replication of some of the experiments described in
 * 
 * "Market Power and Efficiency in a Computational Electricity Market with
 * Discriminatory Double-Auction Pricing" <br>
 * Nicolaisen, Petrov, and Tesfatsion <i>IEEE Transactions on Evolutionary
 * Computation, Vol. 5, No. 5. 2001</I>
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class NPTReplicationTest extends ElectricityTest {

	public NPTReplicationTest(String name) {
		super(name);
		generatePRNGseeds();
	}

	public void testRCAP1_2() {
		experimentSetup(3, 3, 20, 10);
		runExperiment();
		assertTrue(mPB.getMean() < 0);
		assertTrue(mPS.getMean() > 0);
		assertTrue(mPB.getStdDev() <= 1);
		assertTrue(mPS.getStdDev() <= 1);
		assertTrue(eA.getMean() >= 87.0);
		assertTrue(eA.getStdDev() <= 20);
	}

	public void testRCAP_1() {
		experimentSetup(3, 3, 10, 10);
		runExperiment();
		assertTrue(mPB.getMean() < 0);
		assertTrue(mPS.getMean() > 0);
		assertTrue(mPB.getStdDev() <= 1);
		assertTrue(mPS.getStdDev() <= 1);
		assertTrue(eA.getMean() >= 87.0);
		assertTrue(eA.getStdDev() <= 20);
	}

	public void testRCAP_2() {
		experimentSetup(3, 3, 10, 20);
		runExperiment();
		assertTrue(mPB.getStdDev() <= 1);
		assertTrue(mPS.getStdDev() <= 1);
		assertTrue(eA.getMean() >= 87.0);
		assertTrue(eA.getStdDev() <= 20);
	}

	public void runExperiment() {
		super.runExperiment();
		checkRothErevProbabilities();
	}

	public void checkRothErevProbabilities() {
		Iterator i = auction.getTraderIterator();
		while (i.hasNext()) {
			ElectricityTrader t = (ElectricityTrader) i.next();
			NPTRothErevLearner l = (NPTRothErevLearner) ((AdaptiveStrategy) t
			    .getStrategy()).getLearner();
			RothErevLearnerTest.checkProbabilities(l);
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(NPTReplicationTest.class);
	}

}