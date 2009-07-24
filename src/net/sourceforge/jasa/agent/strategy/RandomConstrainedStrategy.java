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

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;

/**
 * <p>
 * A trading strategy that in which we bid a different random markup on our
 * agent's private value in each market round. This strategy is often referred
 * to as Zero Intelligence Constrained (ZI-C) in the literature.
 * </p>
 * 
 * </p>
 * <p>
 * <b>Parameters</b><br>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.maxmarkup</tt><br>
 * <font size=-1>double &gt;= 0</font></td>
 * <td valign=top>(the maximum markup to bid for)</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class RandomConstrainedStrategy extends FixedQuantityStrategyImpl
    implements Serializable {

	protected AbstractContinousDistribution markupDistribution;

	public RandomConstrainedStrategy() {
		this(null);
	}

	public RandomConstrainedStrategy(AbstractTradingAgent agent) {
		super(agent);
	}

	public boolean modifyShout(Order shout) {
		double markup = markupDistribution.nextDouble();
		double price = 0;
		if (agent.isBuyer(auction)) {
			price = agent.getValuation(auction) - markup;
		} else {
			price = agent.getValuation(auction) + markup;
		}
		if (price > 0) {
			shout.setPrice(price);
		} else {
			shout.setPrice(0);
		}
		shout.setQuantity(quantity);

		return super.modifyShout(shout);
	}

	public void endOfRound(Market auction) {
		// Do nothing
	}

	public String toString() {
		return "(" + getClass() + " markupDistribution:" + markupDistribution + " quantity:"
		    + quantity + ")";
	}

	public AbstractContinousDistribution getMarkupDistribution() {
		return markupDistribution;
	}

	public void setMarkupDistribution(
			AbstractContinousDistribution markupDistribution) {
		this.markupDistribution = markupDistribution;
	}

	
}
