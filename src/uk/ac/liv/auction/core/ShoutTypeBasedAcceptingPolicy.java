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

package uk.ac.liv.auction.core;

import org.apache.log4j.Logger;

import uk.ac.liv.prng.GlobalPRNG;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * implements the shout-accepting rule under which a shout must be more
 * competitive than an estimated equilibrium.
 * 
 * The equilibrium is estimated through some learning algorithm, e.g.
 * sliding-window-average learning and widrowhoff learning, by training with
 * transaction prices.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class ShoutTypeBasedAcceptingPolicy extends QuoteBeatingAcceptingPolicy {

	static Logger logger = Logger.getLogger(ShoutTypeBasedAcceptingPolicy.class);

	/**
	 * Reusable exceptions for performance
	 */
	protected static IllegalShoutException bidException = null;

	protected static IllegalShoutException askException = null;

	protected Uniform uniformDistribution;

	/**
	 * A parameter used to control the probability of next shout being from a
	 * seller.
	 * 
	 * @uml.property name="q"
	 */
	protected double q = 0.5;

	public static final String P_Q = "q";

	public static final String P_DEF_BASE = "shouttypebasedaccepting";

	public void setup(ParameterDatabase parameters, Parameter base) {
		super.setup(parameters, base);

		Parameter defBase = new Parameter(P_DEF_BASE);

		q = parameters.getDoubleWithDefault(base.push(P_Q), defBase.push(P_Q), q);
		assert (0 <= q && q <= 1);
	}

	public void initialise() {
		RandomEngine prng = GlobalPRNG.getInstance();
		uniformDistribution = new Uniform(0, 1, prng);
	}

	public void reset() {
		initialise();
	}

	/**
	 * checks whether
	 * <p>
	 * shout
	 * </p>
	 * can beat the estimated equilibrium.
	 */
	public void check(Shout shout) throws IllegalShoutException {
		super.check(shout);

		double d = uniformDistribution.nextDouble();
		if (d <= q) {
			if (shout.isBid()) {
				askExpectedException();
			}
		} else {
			if (shout.isAsk()) {
				bidExpectedException();
			}
		}
	}

	protected void askExpectedException() throws IllegalShoutException {
		if (bidException == null) {
			// Only construct a new exception the once (for improved
			// performance)
			bidException = new IllegalShoutException("Ask expected!");
		}
		throw bidException;
	}

	protected void bidExpectedException() throws IllegalShoutException {
		if (askException == null) {
			// Only construct a new exception the once (for improved
			// performance)
			askException = new IllegalShoutException("Bid expected!");
		}
		throw askException;
	}

	public void setQ(double q) {
		this.q = q;
	}

	public double getQ() {
		return q;
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + " q:" + q + ")";
	}
}
