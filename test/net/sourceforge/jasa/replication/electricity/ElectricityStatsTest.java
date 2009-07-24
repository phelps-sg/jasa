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

package net.sourceforge.jasa.replication.electricity;

import net.sourceforge.jasa.agent.SimpleTradingAgent;
import net.sourceforge.jasa.agent.strategy.PureSimpleStrategy;
import net.sourceforge.jasa.sim.util.SummaryStats;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class ElectricityStatsTest extends ElectricityTest {

	/**
	 * @uml.property name="sMPS"
	 * @uml.associationEnd
	 */
	SummaryStats sMPS;

	/**
	 * @uml.property name="sMPB"
	 * @uml.associationEnd
	 */
	SummaryStats sMPB;

	/**
	 * @uml.property name="pSA"
	 * @uml.associationEnd
	 */
	SummaryStats pSA;

	/**
	 * @uml.property name="pBA"
	 * @uml.associationEnd
	 */
	SummaryStats pBA;

	/**
	 * @uml.property name="pST"
	 * @uml.associationEnd
	 */
	SummaryStats pST;

	/**
	 * @uml.property name="pBT"
	 * @uml.associationEnd
	 */
	SummaryStats pBT;

	static final int NUM_SCHEDULES = 10;

	public ElectricityStatsTest(String name) {
		super(name);
//		org.apache.log4j.BasicConfigurator.configure();
	}

	public void checkSMP() {
		System.out.println(sMPB);
		System.out.println(sMPS);
		System.out.println(pBA);
		System.out.println(pSA);
		System.out.println(pBT);
		System.out.println(pST);
		assertTrue(Double.isNaN(sMPB.getMean()) || sMPB.getMean() < 0.01);
		assertTrue(Double.isNaN(sMPS.getMean()) || sMPS.getMean() < 0.01);
	}

	public void testSMPrcap1() {
		// for( double k=0; k<1; k+=0.5 ) {
		// System.out.println("Testing with k = " + k);
		// for( int i=0; i<NUM_SCHEDULES; i++ ) {
		// System.out.println("Schedule " + i);
		// randomizePrivateValues();
		// experimentSetup(3, 3, 10, 10);
		// auctioneer.setK(k);
		// runExperiment();
		// checkSMP();
		// }
		// }
	}

	public void initStats() {
		super.initStats();
		sMPS = new SummaryStats("SMPS");
		sMPB = new SummaryStats("SMPB");
		pBA = new SummaryStats("PBA");
		pSA = new SummaryStats("PSA");
		pBT = new SummaryStats("PBT");
		pST = new SummaryStats("PST");
	}

	public void updateStats() {
		super.updateStats();
		// stats.calculateStrategicMarketPower();
		sMPS.newData(stats.getSMPS());
		sMPB.newData(stats.getSMPB());
		pBA.newData(stats.getPBA());
		pSA.newData(stats.getPSA());
		pBT.newData(stats.getPBT());
		pST.newData(stats.getPST());
	}

	public void assignStrategy(SimpleTradingAgent agent) {
		PureSimpleStrategy strategy = new PureSimpleStrategy(agent, 0, agent
		    .getVolume());
		agent.setStrategy(strategy);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(ElectricityStatsTest.class);
	}

}
