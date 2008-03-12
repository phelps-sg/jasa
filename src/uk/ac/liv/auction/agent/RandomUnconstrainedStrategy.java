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
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.prng.GlobalPRNG;
import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

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
 * <td valign=top>(max price in auction)</td>
 * <tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class RandomUnconstrainedStrategy extends FixedQuantityStrategyImpl
    implements Serializable {

	public static final String P_MAXPRICE = "maxprice";

	public static final double DEFAULT_MAX_PRICE = 200;

	protected double maxPrice;

	protected AbstractContinousDistribution distribution;

	public RandomUnconstrainedStrategy() {
		super(null);
	}

	public RandomUnconstrainedStrategy(AbstractTradingAgent agent) {
		super(agent);
	}

	public void setup(ParameterDatabase parameters, Parameter base) {
		super.setup(parameters, base);
		maxPrice = parameters.getDoubleWithDefault(base.push(P_MAXPRICE), null,
		    DEFAULT_MAX_PRICE);
		initialise();
	}

	public boolean modifyShout(Shout.MutableShout shout) {

		double price = distribution.nextDouble();
		shout.setPrice(price);
		shout.setQuantity(quantity);

		return super.modifyShout(shout);
	}

	public void endOfRound(Auction auction) {
		// Do nothing
	}

	public void initialise() {
		super.initialise();
		distribution = new Uniform(0, maxPrice, GlobalPRNG.getInstance());
	}

	public double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
		initialise();
	}

	public String toString() {
		return "(" + getClass() + " quantity:" + quantity + ")";
	}

}