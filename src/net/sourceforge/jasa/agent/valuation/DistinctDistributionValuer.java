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

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.MarketOpenEvent;
import net.sourceforge.jasa.sim.prng.GlobalPRNG;

import cern.jet.random.Uniform;

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

	public static final String P_DEF_BASE = "distinctdistributionvaluer";

	public static final String P_MINVALUEMIN = "minvaluemin";

	public static final String P_MINVALUEMAX = "minvaluemax";

	public static final String P_RANGEMIN = "rangemin";

	public static final String P_RANGEMAX = "rangemax";

	public DistinctDistributionValuer() {
		super();
	}

	public DistinctDistributionValuer(double minValueMin, double minValueMax,
	    double rangeMin, double rangeMax) {
		this.minValueMin = minValueMin;
		this.minValueMax = minValueMax;
		this.rangeMin = rangeMin;
	}

	public void initialise() {
		Uniform minValueDist = new Uniform(minValueMin, minValueMax, GlobalPRNG
		    .getInstance());
		Uniform rangeDist = new Uniform(rangeMin, rangeMax, GlobalPRNG
		    .getInstance());
		minValue = minValueDist.nextDouble();
		maxValue = minValue + rangeDist.nextDouble();
		distribution = new Uniform(minValue, maxValue, GlobalPRNG.getInstance());
	}

	public void eventOccurred(MarketEvent event) {
		super.eventOccurred(event);
		if (event instanceof MarketOpenEvent) {
			distribution = new Uniform(minValue, maxValue, GlobalPRNG.getInstance());
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
