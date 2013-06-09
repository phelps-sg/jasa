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

package net.sourceforge.jasa.agent.valuation;

import java.io.Serializable;

import net.sourceforge.jasa.market.Market;
import cern.jet.random.AbstractContinousDistribution;

public abstract class AbstractRandomValuer extends AbstractValuationPolicy implements 
    Serializable {

	/**
	 * The current valuation.
	 */
	protected double value;

	/**
	 * The probability distribution to use for drawing valuations.
	 */
	protected AbstractContinousDistribution distribution;

	public AbstractRandomValuer() {

	}

	public abstract void initialise();
//
//	public abstract double getMaxValue();
//
//	public abstract double getMinValue();

	public double determineValue(Market auction) {
		return value;
	}

	public void consumeUnit(Market auction) {
		// Do nothing
	}

//	public void eventOccurred(SimEvent event) {
//		// Do nothing
//	}

	public void reset() {
		initialise();
	}

	public double getCurrentValuation() {
		return value;
	}

	public void drawRandomValue() {
		value = distribution.nextDouble();
	}

	public AbstractContinousDistribution getDistribution() {
		return distribution;
	}

	public void setDistribution(AbstractContinousDistribution distribution) {
		this.distribution = distribution;
	}
	
	public String toString() {
		return "(" + getClass() + " value:" + value + " distribution:" + distribution + ")";
	}

}
