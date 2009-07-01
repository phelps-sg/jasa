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

package net.sourceforge.jasa.market.rules;

import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.sim.util.Parameterizable;

/**
 * The interface for expressing the condition of closing an market.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 * 
 */

public class MaxRoundsAuctionClosingCondition extends TimingCondition implements
    Parameterizable, AuctionClosingCondition {

	public static final String P_MAXIMUM_ROUNDS = "maximumrounds";

	/**
	 * The maximum number of rounds in the market. Ignored if negative.
	 */
	protected int maximumRounds = -1;

	public MaxRoundsAuctionClosingCondition() {
		this(null);
	}

	public MaxRoundsAuctionClosingCondition(Market auction) {
		super(auction);
	}


	/**
	 * Return the maximum number of rounds for this market.
	 */
	public int getMaximumRounds() {
		return maximumRounds;
	}

	/**
	 * Set the maximum number of rounds for this market. The market will
	 * automatically close after this number of rounds has been dealt.
	 */
	public void setMaximumRounds(int maximumRounds) {
		this.maximumRounds = maximumRounds;
	}

	/*
	 * @see net.sourceforge.jasa.market.TimingCondition#eval()
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