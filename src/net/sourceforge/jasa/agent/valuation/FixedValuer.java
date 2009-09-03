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

import java.io.Serializable;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.sim.event.SimEvent;

/**
 * A valuation policy in which we maintain a fixed private valuation independent
 * of time or market.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class FixedValuer implements ValuationPolicy, Serializable {

	protected double value;

	public FixedValuer() {
	}

	public FixedValuer(double value) {
		this.value = value;
	}


	public double determineValue(Market auction) {
		return value;
	}

	public void consumeUnit(Market auction) {
		// Do nothing
	}

	public void eventOccurred(SimEvent event) {
		// Do nothing
	}

	public void reset() {
	}
	
	public void initialise() {
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void setAgent(TradingAgent agent) {
		// No action required
	}

	public String toString() {
		return "(" + getClass() + " value:" + value + ")";
	}

}