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

package net.sourceforge.jasa.market.rules;

import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.sim.util.Parameterizable;

import org.apache.log4j.Logger;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

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

	AbstractDistribution distribution;

	private double threshold = 1;

	public static final String P_THRESHOLD = "threshold";

	static Logger logger = Logger.getLogger(ProbabilisticClearingCondition.class);

	
	public ProbabilisticClearingCondition(AbstractDistribution distribution) {
		super();
		this.distribution = distribution;
	}


	public void eventOccurred(MarketEvent event) {
		super.eventOccurred(event);

		if (event instanceof OrderPlacedEvent) {
			double d = distribution.nextDouble();
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
