/*
 * Copyright (C) 2001-2005 Steve Phelps
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package uk.ac.liv.prng;

import org.apache.log4j.Logger;

import cern.jet.random.engine.RandomEngine;
import cern.jet.random.engine.RandomSeedGenerator;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class GlobalPRNG {

	protected static RandomEngine prng;

	protected static RandomSeedGenerator seedGenerator = new RandomSeedGenerator(
	    (int) System.currentTimeMillis(), 1);

	protected static long seed;

	public static final String P_SEED = "seed";

	public static final String P_PRNG = "prng";

	public static final String P_DEF_BASE = P_PRNG;

	static Logger logger = Logger.getLogger(GlobalPRNG.class);

	public static void setup(ParameterDatabase parameters, Parameter base) {

		uk.ac.liv.prng.PRNGFactory.setup(parameters, base.push(P_PRNG));

		long defaultSeed = seedGenerator.nextSeed();

		seed = parameters.getLongWithDefault(base.push(P_SEED), new Parameter(
		    P_DEF_BASE).push(P_SEED), defaultSeed);

		prng = PRNGFactory.getFactory().create(seed);
	}

	public static RandomEngine getInstance() {
		if (prng == null) {
			logger.warn("No PRNG configured: using default");
			long defaultSeed = seedGenerator.nextSeed();
			prng = PRNGFactory.getFactory().create(defaultSeed);
		}
		return prng;
	}

	public static long getSeed() {
		return seed;
	}

	public static void initialiseWithSeed(long seed) {
		GlobalPRNG.seed = seed;
		prng = PRNGFactory.getFactory().create(seed);
	}

	public static void generateNewSeed() {
		GlobalPRNG.seed = seedGenerator.nextSeed();
		prng = PRNGFactory.getFactory().create(seed);
	}

	public static void randomPermutation(Object[] a) {
		for (int i = 0; i < a.length - 1; i++) {
			// int choice = getInstance().choose(i, a.length-1);
			// int choice = getInstance().choose(i, a.length);
			int choice = (int) ((long) a.length * getInstance().raw());
			Object tmp = a[i];
			a[i] = a[choice];
			a[choice] = tmp;
		}
	}

}