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

package uk.ac.liv.ai.learning;

import uk.ac.liv.prng.GlobalPRNG;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Prototypeable;
import uk.ac.liv.util.io.DataWriter;

import java.io.Serializable;

import cern.jet.random.Uniform;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * A learner that simply plays a random action on each iteration without any
 * learning. This is useful for control experiments.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class DumbRandomLearner extends AbstractLearner implements
    Parameterizable, StimuliResponseLearner, Serializable, Prototypeable {

	protected int numActions;

	protected Uniform distribution;

	public static final int DEFAULT_NUM_ACTIONS = 10;

	public static final String P_K = "k";

	public DumbRandomLearner() {
		this(DEFAULT_NUM_ACTIONS);
	}

	public DumbRandomLearner(int numActions) {
		this.numActions = numActions;
		distribution = new Uniform(0, 1, GlobalPRNG.getInstance());
	}

	public void setup(ParameterDatabase params, Parameter base) {
		numActions = params.getIntWithDefault(base.push(P_K), null,
		    DEFAULT_NUM_ACTIONS);
	}

	public Object protoClone() {
		try {
			return this.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	public void reset() {
		// Do nothing
	}

	public int act() {
		return distribution.nextIntFromTo(0, numActions);
	}

	public double getLearningDelta() {
		return 0.0;
	}

	public void dumpState(DataWriter out) {
		// TODO
	}

	public int getNumberOfActions() {
		return numActions;
	}

	public void reward(double reward) {
		// No action
	}
}
