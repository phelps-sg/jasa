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

package net.sourceforge.jasa.agent.valuation;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.event.MarketOpenEvent;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

/**
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public class DistinctDistributionValuer extends AbstractRandomValuer {

	protected double minValueMin;

	protected double minValueMax;

	protected double rangeMin;

	protected double rangeMax;

	protected static double minValue;

	protected static double maxValue;
	
	protected RandomEngine prng;

	public DistinctDistributionValuer(RandomEngine prng) {
		super();
		this.prng = prng;
	}

	public DistinctDistributionValuer(double minValueMin, double minValueMax,
	    double rangeMin, double rangeMax, RandomEngine prng) {
		this.minValueMin = minValueMin;
		this.minValueMax = minValueMax;
		this.rangeMin = rangeMin;
		this.prng = prng;
	}

	public void initialise() {
		Uniform minValueDist = new Uniform(minValueMin, minValueMax, prng);
		Uniform rangeDist = new Uniform(rangeMin, rangeMax, prng);
		minValue = minValueDist.nextDouble();
		maxValue = minValue + rangeDist.nextDouble();
		distribution = new Uniform(minValue, maxValue, prng);
	}
	

	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		scheduler.addListener(MarketOpenEvent.class, this);
	}

	public void eventOccurred(SimEvent event) {
		if (event instanceof MarketOpenEvent) {
			distribution = new Uniform(minValue, maxValue, prng);
			drawRandomValue();
		}
	}

	public double getMaxValue() {
		return maxValue;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setAgent(TradingAgent agent) {
		// No action required
	}


}
