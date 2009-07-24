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

package net.sourceforge.jasa.agent.strategy;

import java.io.Serializable;

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;

/**
 * A class representing a strategy in which we adapt our bids using a discrete
 * learning algorithm.
 * 
 * </p>
 * <p>
 * <b>Parameters</b><br>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.markupscale</tt><br>
 * <font size=-1>double &gt;= 0</font></td>
 * <td valign=top>(scaling factor by which to multiply the output from the
 * learner)</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class DiscreteLearnerStrategy extends AdaptiveStrategyImpl
    implements Serializable {

	/**
	 * A scaling factor used to multiply-up the output from the learning
	 * algorithm.
	 */
	protected double markupScale = 1;

	public static final String P_DEF_BASE = "discretelearnerstrategy";

	static final String P_MARKUPSCALE = "markupscale";

	static Logger logger = Logger.getLogger(DiscreteLearnerStrategy.class);

	public DiscreteLearnerStrategy(AbstractTradingAgent agent) {
		super(agent);
		initialise();
	}

	public DiscreteLearnerStrategy() {
		super();
		initialise();
	}

	public void initialise() {
		super.initialise();
	}


	public void endOfRound(Market auction) {
		if (agent.active()) {
			learn(auction);
		}
	}

	public boolean modifyShout(Order shout) {

		// Generate an action from the learning algorithm
		int action = act();

		// Now turn the action into a price
		double price;
		if (agent.isSeller(auction)) {
			price = agent.getValuation(auction) + action * markupScale;
		} else {
			price = agent.getValuation(auction) - action * markupScale;
		}
		if (price < 0) {
			// report.debug(this + ": set negative price- clipping at 0");
			price = 0;
		}

		shout.setPrice(price);
		shout.setQuantity(quantity);

		return super.modifyShout(shout);
	}

	public double getMarkupScale() {
		return markupScale;
	}

	public void setMarkupScale(double markupScale) {
		this.markupScale = markupScale;
	}

	/**
	 * Generate an action from the learning algorithm.
	 */
	public abstract int act();

	/**
	 * Perform learning.
	 */
	public abstract void learn(Market auction);

}
