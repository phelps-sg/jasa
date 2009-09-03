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


import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

import java.io.Serializable;

import net.sourceforge.jasa.agent.TradingAgent;

import org.apache.log4j.Logger;

/**
 * A valuation policy in which we randomly determine our valuation across all
 * auctions and all units at agent-initialisation time. Valuations are drawn
 * from a uniform distribution with the specified range.
 * 
 * </p>
 * <p>
 * <b>Parameters </b> <br>
 * <table>
 * <tr>
 * <td valign=top><i>base </i> <tt>.minvalue</tt><br>
 * <font size=-1>double &gt;= 0 </font></td>
 * <td valign=top>(the minimum valuation)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.maxvalue</tt><br>
 * <font size=-1>double &gt;=0 </font></td>
 * <td valign=top>(the maximum valuation)</td>
 * <tr></table>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class RandomValuer extends AbstractRandomValuer implements Serializable {

	static Logger logger = Logger.getLogger(RandomValuer.class);

	public RandomValuer() {
		super();
	}
	
	public RandomValuer(AbstractContinousDistribution distribution) {
		this.distribution = distribution;
		initialise();
	}

	public RandomValuer(double minValue, double maxValue, RandomEngine prng) {
		super();
		distribution = new Uniform(minValue, maxValue, prng);
		initialise();
	}

	public void initialise() {
		drawRandomValue();
	}

	public void setAgent(TradingAgent agent) {
		// No action required
	}
	
}
