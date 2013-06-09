/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2013 Steve Phelps
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

package net.sourceforge.jasa.report;

import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.SpringSimulationController;
import net.sourceforge.jabm.init.BasicAgentInitialiser;
import net.sourceforge.jabm.mixing.RandomRobinAgentMixer;
import net.sourceforge.jabm.util.MathUtil;
import net.sourceforge.jasa.agent.MockTrader;
import net.sourceforge.jasa.agent.strategy.TruthTellingStrategy;
import net.sourceforge.jasa.agent.valuation.FixedValuer;
import net.sourceforge.jasa.market.MarketSimulation;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import cern.jet.random.engine.MersenneTwister64;

public class EquilibriaStatsTest extends TestCase {

	MarketSimulation auction;

	MockTrader[] traders;

	Random randGenerator = new Random();

	static double[] NO_EP = { 100, 90, 80, 10, 20, 30 };

	static double[] SINGLE_CROSS = { 100, 90, 40, 10, 20, 50 };

	static double[] EXACT_OVERLAP = { 100, 90, 40, 10, 20, 40 };

	static double[] NPT = { 37, 17, 12, 11, 16, 37 };

	static final int N = 6;

	static final int NS = 3;

	static final double MAX_PV = 100;

	public EquilibriaStatsTest(String name) {
		super(name);
	}

	public void setUp() {
		initialiseAuction();
		traders = new MockTrader[N];
		for (int i = 0; i < N; i++) {
			traders[i] = new MockTrader(this, 0, 0, 0, auction);
			TruthTellingStrategy strategy = new TruthTellingStrategy(traders[i]);
			traders[i].setStrategy(strategy);
			strategy.setBuy(i >= NS);
			auction.register(traders[i]);
		}
	}
	
	public void initialiseAuction() {
		auction = new MarketSimulation();
		SimulationController controller = new SpringSimulationController();
		auction.setSimulationController(controller);
		auction.setPopulation(new Population());
//		auction.setAgentMixer(new RandomRobinAgentMixer(prng));
		auction.setAgentInitialiser(new BasicAgentInitialiser());
	}

	/**
	 * Check that EP is zero when valuations are zero.
	 * 
	 */
	public void testZeroEP() {
		EquilibriumReportVariables ep = new EquilibriumReportVariables(auction);
		ep.calculate();
		assertTrue(ep.calculateMidEquilibriumPrice() == 0);
	}

	public void testSingleCross() {
		checkEP(SINGLE_CROSS, 45);
	}

	public void testExactOverlap() {
		checkEP(EXACT_OVERLAP, 40);
	}

	public void testNPT() {
		checkEP(NPT, 16.5);
	}

	/**
	 * Check that no equilibria exists when supp/demand do not cross.
	 */
	public void testNoEP() {
		setValuations(NO_EP);
		EquilibriumReportVariables ep = new EquilibriumReportVariables(auction);
		ep.calculate();
		assertTrue(!ep.equilibriaExists());
	}

	/**
	 * Check that no equilibria exists when there are no traders.
	 */
	public void testNoTraders() {
		initialiseAuction();
		EquilibriumReportVariables ep = new EquilibriumReportVariables(auction);
		ep.calculate();
		assertTrue(!ep.equilibriaExists());
	}

	protected void checkEP(double[] valuations, double correctEP) {
		setValuations(valuations);
		EquilibriumReportVariables ep = new EquilibriumReportVariables(auction);
		ep.calculate();
		double mep = ep.calculateMidEquilibriumPrice();
		System.out.println("Mid EP = " + mep);
		assertTrue(MathUtil.approxEqual(mep, correctEP));
		assertTrue(ep.equilibriaExists());
	}

	protected void setValuations(double[] valuations) {
		for (int i = 0; i < N; i++) {
			traders[i].setValuationPolicy(new FixedValuer(valuations[i]));
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(EquilibriaStatsTest.class);
	}

}
