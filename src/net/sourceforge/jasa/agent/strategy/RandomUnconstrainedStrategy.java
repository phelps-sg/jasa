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
 * A trading strategy in which an agent bid regardless its private value. This
 * strategy is often referred to as Zero Intelligence Unconstrained (ZI-U) in
 * the literature.
 * </p>
 * 
 * </p>
 * <p>
 * <b>Parameters </b> <br>
 * <table>
 * <tr>
 * <td valign=top><i>base </i> <tt>.maxprice</tt><br>
 * <font size=-1>double &gt;= 0 </font></td>
 * <td valign=top>(max price in market)</td>
 * <tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class RandomUnconstrainedStrategy extends FixedDirectionStrategy
    implements Serializable {

	public static final String P_MAXPRICE = "maxprice";

	public static final double DEFAULT_MAX_PRICE = 200;

	protected AbstractContinousDistribution distribution;


	public RandomUnconstrainedStrategy(AbstractContinousDistribution distribution,
											AbstractTradingAgent agent) {
		super(agent);
		this.distribution = distribution;
	}

	public boolean modifyShout(Order shout) {

		double price = distribution.nextDouble();
		shout.setPrice(price);
		shout.setQuantity(quantity);

		return super.modifyShout(shout);
	}

	public void endOfRound(Market auction) {
		// Do nothing
	}

	public void initialise() {
		super.initialise();
	}
	
//
//	public double getMaxPrice() {
//		return maxPrice;
//	}
//
//	public void setMaxPrice(double maxPrice) {
//		this.maxPrice = maxPrice;
//		initialise();
//	}

	public AbstractContinousDistribution getDistribution() {
		return distribution;
	}

	public void setDistribution(AbstractContinousDistribution distribution) {
		this.distribution = distribution;
	}

	public String toString() {
		return "(" + getClass() + " quantity:" + quantity + ")";
	}

}