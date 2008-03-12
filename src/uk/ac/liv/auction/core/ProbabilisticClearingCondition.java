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

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;
import uk.ac.liv.prng.GlobalPRNG;
import uk.ac.liv.util.Parameterizable;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * The class for expressing whether the market should be cleared or not.
 * 
 * For the moment, it presents a continuum between CDA and CH.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 * 
 */

public class ProbabilisticClearingCondition extends RoundClearingCondition
    implements Parameterizable {

	Uniform uniformDistribution;

	/**
	 * @uml.property name="threshold"
	 */
	private double threshold = 1;

	public static final String P_THRESHOLD = "threshold";

	static Logger logger = Logger.getLogger(ProbabilisticClearingCondition.class);

	protected void initialise() {
		RandomEngine prng = GlobalPRNG.getInstance();
		uniformDistribution = new Uniform(0, 1, prng);
	}

	public void setup(ParameterDatabase parameters, Parameter base) {
		threshold = parameters.getDoubleWithDefault(base.push(P_THRESHOLD),
		    new Parameter(P_DEF_BASE).push(P_THRESHOLD), threshold);
		assert (0 <= threshold && threshold <= 1);
	}

	public void eventOccurred(AuctionEvent event) {
		super.eventOccurred(event);

		if (event instanceof ShoutPlacedEvent) {
			double d = uniformDistribution.nextDouble();
			if (d < threshold) {
				setChanged();
				notifyObservers();
			}
		}
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double getThreshold() {
		return threshold;
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + " threshold:" + threshold + ")";
	}

}
