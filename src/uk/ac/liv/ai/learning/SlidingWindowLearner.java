/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at e;your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.ai.learning;

import uk.ac.liv.util.io.DataWriter;
import uk.ac.liv.util.FixedLengthQueue;
import uk.ac.liv.util.Prototypeable;

import uk.ac.liv.prng.GlobalPRNG;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.io.Serializable;

/**
 * maintains a sliding window over the trained data series and use the average
 * of data items falling into the window as the output learned.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class SlidingWindowLearner extends AbstractLearner implements
    MimicryLearner, SelfKnowledgable, Prototypeable, Serializable {

	/**
	 * @uml.property name="randomParamDistribution"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected AbstractContinousDistribution randomParamDistribution = new Uniform(
	    1, 10, GlobalPRNG.getInstance());

	/**
	 * A parameter used to adjust the size of the window
	 * 
	 * @uml.property name="memorySize"
	 */
	protected int windowSize = 4;

	public static final String P_WINDOWSIZE = "windowsize";

	/**
	 * The current output level.
	 * 
	 * @uml.property name="currentOutput"
	 */
	protected double currentOutput;

	public static final String P_DEF_BASE = "slidingwindowlearner";

	protected FixedLengthQueue memory;

	public SlidingWindowLearner() {
	}

	public void setup(ParameterDatabase parameters, Parameter base) {
		super.setup(parameters, base);

		windowSize = parameters.getIntWithDefault(base.push(P_WINDOWSIZE),
		    new Parameter(P_DEF_BASE).push(P_WINDOWSIZE), windowSize);

		initialise();
	}

	public void initialise() {
		createMemory();
	}

	public void reset() {
		if (memory != null) {
			memory.reset();
		}
	}

	public void randomInitialise() {
		windowSize = randomParamDistribution.nextInt();
	}

	/**
	 * @uml.property name="windowSize"
	 */
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	public int getWindowSize() {
		return windowSize;
	}

	protected void createMemory() {
		assert (windowSize >= 1);
		memory = new FixedLengthQueue(windowSize);
	}

	public double act() {
		return currentOutput;
	}

	public void train(double target) {
		memory.newData(target);
		currentOutput = memory.getMean();
	}

	public void dumpState(DataWriter out) {
		// TODO
	}

	/**
	 * @uml.property name="currentOutput"
	 */
	public double getCurrentOutput() {
		return currentOutput;
	}

	/**
	 * no effect on FixedLengthQueue-based next output!
	 */
	public void setOutputLevel(double currentOutput) {
		this.currentOutput = currentOutput;
	}

	public double getLearningDelta() {
		return 0;
	}

	public Object protoClone() {
		SlidingWindowLearner clone = new SlidingWindowLearner();
		clone.setWindowSize(windowSize);
		return clone;
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + " windowSize:" + windowSize + ")";
	}

	public boolean goodEnough() {
		return memory.count() >= windowSize;
	}
}
