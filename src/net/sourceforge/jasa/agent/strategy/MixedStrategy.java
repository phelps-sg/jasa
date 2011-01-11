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
import java.util.Collection;

import net.sourceforge.jabm.prng.DiscreteProbabilityDistribution;
import net.sourceforge.jabm.util.Parameterizable;
import net.sourceforge.jabm.util.Resetable;
import net.sourceforge.jasa.agent.TradingStrategy;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;

/**
 * A class representing a mixed strategy. A mixed strategy is a strategy in
 * which we play a number of pure strategies with different probabilities on
 * each market round.
 * 
 * <p>
 * <b>Parameters</b><br>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base.</i><tt>n</tt><br>
 * <font size=-1>int &gt;= 1</font></td>
 * <td valign=top>(the number of pure strategies)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base.<i>i</i><br>
 * <font size=-1>classname, inherits net.sourceforge.jasa.agent.Strategy</font></td>
 * <td valign=top>(the class for pure strategy #<i>i</i>)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base.<i>i</i>.<tt>prob</tt><br>
 * <font size=-1>double [0, 1]</font></td>
 * <td valign=top>(the probability of playing pure strategy #<i>i</i>)</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class MixedStrategy extends AbstractStrategy implements Parameterizable,
    Resetable, Serializable {

	/**
	 * The probabilities for playing each strategy
	 */
	protected DiscreteProbabilityDistribution probabilities;

	/**
	 * The pure strategy components
	 */
	protected AbstractStrategy pureStrategies[];

	/**
	 * The strategy currently being played
	 */
	protected AbstractStrategy currentStrategy;

	static Logger logger = Logger.getLogger(MixedStrategy.class);

	public MixedStrategy(DiscreteProbabilityDistribution probabilities,
	    AbstractStrategy[] pureStrategies) {
		this();
		this.pureStrategies = pureStrategies;
		this.probabilities = probabilities;
	}

	public MixedStrategy() {
		currentStrategy = null;
	}

	public void addPureStrategies(Collection pureStrategies) {
		pureStrategies.addAll(pureStrategies);
	}

	public void setProbabilityDistribution(
	    DiscreteProbabilityDistribution probabilities) {
		this.probabilities = probabilities;
	}

	public boolean modifyShout(Order shout) {

		currentStrategy = pureStrategies[probabilities.generateRandomEvent()];

		return currentStrategy.modifyShout(shout);
	}

	public void onRoundClosed(Market auction) {
		currentStrategy.onRoundClosed(auction);
	}

	public TradingStrategy getCurrentStrategy() {
		return currentStrategy;
	}

	public void reset() {
		probabilities.reset();
		for (int i = 0; i < pureStrategies.length; i++) {
			((Resetable) pureStrategies[i]).reset();
		}
	}

	public int determineQuantity(Market auction) {
		return currentStrategy.determineQuantity(auction);
	}

}
