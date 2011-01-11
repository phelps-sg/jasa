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

import java.io.Serializable;
import java.util.Iterator;

import net.sourceforge.jabm.prng.PRNGFactory;
import net.sourceforge.jabm.util.Parameterizable;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.SimpleTradingAgent;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.report.EquilibriumReport;

import org.apache.log4j.Logger;

import cern.jet.random.engine.RandomEngine;
import cern.jet.random.engine.RandomSeedGenerator;

public class StandardRandomizer implements Parameterizable, Serializable {

	protected MarketFacade auction;

	protected double minPrivateValue = 30;

	protected double maxPrivateValue = 1000;

	protected ElectricityExperiment experiment;

	protected RandomEngine privValuePRNG;

	protected long[] seeds;

	static Logger logger = Logger.getLogger(StandardRandomizer.class);

	static final String P_MAXPRIVATEVALUE = "maxprivatevalue";

	static final String P_MINPRIVATEVALUE = "minprivatevalue";

	public StandardRandomizer(ElectricityExperiment simulation) {
		this();
		setExperiment(experiment);
	}

	public StandardRandomizer() {
	}

	public void setExperiment(ElectricityExperiment experiment) {
		this.experiment = experiment;
		this.auction = experiment.auction;
	}

	public double randomValue(RandomEngine prng, double min, double max) {
		return min + prng.raw() * (max - min);
	}

	public double randomPrivateValue(double min, double max) {
		return randomValue(privValuePRNG, min, max);
	}

	public double randomPrivateValue() {
		return randomPrivateValue(minPrivateValue, maxPrivateValue);
	}

	public void randomizePrivateValues(double[][] values, int iteration) {
		Iterator i = auction.getTraderIterator();
		int traderNumber = 0;
		while (i.hasNext()) {
			SimpleTradingAgent trader = (SimpleTradingAgent) i.next();
			trader.setPrivateValue(values[iteration][traderNumber++]);
		}
	}

	protected double[][] generateRandomizedPrivateValues(int numTraders,
	    int numIterations) {
		double[][] values = new double[numIterations][numTraders];
		EquilibriumReport stats = new EquilibriumReport(auction);
		for (int i = 0; i < numIterations; i++) {
			privValuePRNG = PRNGFactory.getFactory().create(seeds[i]);
			do {
				Iterator traders = auction.getTraderIterator();
				for (int t = 0; t < numTraders; t++) {
					double value = randomPrivateValue();
					AbstractTradingAgent agent = (AbstractTradingAgent) traders.next();
					agent.setPrivateValue(value);
					values[i][t] = value;
				}
				stats.recalculate();
			} while (!stats.equilibriaExists());
		}
		return values;
	}

	protected void generatePRNGseeds(int numIterations) {
		seeds = new long[numIterations];
		RandomSeedGenerator seedGenerator = new RandomSeedGenerator();
		for (int i = 0; i < numIterations; i++) {
			seeds[i] = seedGenerator.nextSeed();
		}
	}

	public String toString() {
		return "(" + getClass() + " minPrivateValue:" + minPrivateValue
		    + " maxPrivateValue:" + maxPrivateValue + ")";
	}

}