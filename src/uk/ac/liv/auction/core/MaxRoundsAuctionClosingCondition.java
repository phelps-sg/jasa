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

package uk.ac.liv.auction.core;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import uk.ac.liv.util.Parameterizable;

/**
 * The interface for expressing the condition of closing an auction.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 * 
 */

public class MaxRoundsAuctionClosingCondition extends TimingCondition implements
    Parameterizable, AuctionClosingCondition {

	public static final String P_MAXIMUM_ROUNDS = "maximumrounds";

	/**
	 * The maximum number of rounds in the auction. Ignored if negative.
	 * 
	 * @uml.property name="maximumRounds"
	 */
	protected int maximumRounds = -1;

	public MaxRoundsAuctionClosingCondition() {
		this(null);
	}

	public MaxRoundsAuctionClosingCondition(Auction auction) {
		super(auction);
	}

	/*
	 * @see uk.ac.liv.util.Parameterizable#setup(ec.util.ParameterDatabase,
	 *      ec.util.Parameter)
	 */
	public void setup(ParameterDatabase parameters, Parameter base) {

		maximumRounds = parameters.getIntWithDefault(base.push(P_MAXIMUM_ROUNDS),
		    new Parameter(P_DEF_BASE).push(P_MAXIMUM_ROUNDS), -1);
	}

	/**
	 * Return the maximum number of rounds for this auction.
	 * 
	 * @uml.property name="maximumRounds"
	 */
	public int getMaximumRounds() {
		return maximumRounds;
	}

	/**
	 * Set the maximum number of rounds for this auction. The auction will
	 * automatically close after this number of rounds has been dealt.
	 * 
	 * @param maximumRounds
	 *          The maximum number of roudns for this auction.
	 * @uml.property name="maximumRounds"
	 */
	public void setMaximumRounds(int maximumRounds) {
		this.maximumRounds = maximumRounds;
	}

	/*
	 * @see uk.ac.liv.auction.core.TimingCondition#eval()
	 */
	public boolean eval() {
		return getRemainingRounds() <= 0;
	}

	public int getRemainingRounds() {
		if (maximumRounds > getAuction().getAge())
			return maximumRounds - getAuction().getAge();
		else
			return 0;
	}
}