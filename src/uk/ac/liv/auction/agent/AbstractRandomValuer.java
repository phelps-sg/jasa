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

package uk.ac.liv.auction.agent;

import java.io.Serializable;

import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.event.AuctionEvent;

import cern.jet.random.AbstractContinousDistribution;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

public abstract class AbstractRandomValuer implements ValuationPolicy,
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

	public abstract void setup(ParameterDatabase params, Parameter base);

	public abstract void initialise();

	public abstract double getMaxValue();

	public abstract double getMinValue();

	public double determineValue(Auction auction) {
		return value;
	}

	public void consumeUnit(Auction auction) {
		// Do nothing
	}

	public void eventOccurred(AuctionEvent event) {
		// Do nothing
	}

	public void reset() {
		initialise();
	}

	public double getCurrentValuation() {
		return value;
	}

	public void drawRandomValue() {
		value = distribution.nextDouble();
	}

}
