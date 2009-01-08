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

public class MaxDaysAuctionClosingCondition extends TimingCondition implements
    Parameterizable, AuctionClosingCondition {

	public static final String P_MAXIMUM_DAYS = "maximumdays";

	/**
	 * The maximum number of trading days before the auction closes
	 */

	protected int maximumDays = -1;

	public MaxDaysAuctionClosingCondition() {
		this(null);
	}

	public MaxDaysAuctionClosingCondition(Auction auction) {
		super(auction);
	}

	/*
	 * @see uk.ac.liv.util.Parameterizable#setup(ec.util.ParameterDatabase,
	 *      ec.util.Parameter)
	 */
	public void setup(ParameterDatabase parameters, Parameter base) {

		maximumDays = parameters.getIntWithDefault(base.push(P_MAXIMUM_DAYS),
		    new Parameter(P_DEF_BASE).push(P_MAXIMUM_DAYS), -1);
	}

	public void setMaximumDays(int maximumDays) {
		this.maximumDays = maximumDays;
	}

	public int getMaximumDays() {
		return maximumDays;
	}

	/*
	 * @see uk.ac.liv.auction.core.TimingCondition#eval()
	 */
	public boolean eval() {
		return getRemainingDays() <= 0;
	}

	public int getRemainingDays() {
		if (maximumDays > getAuction().getDay())
			return maximumDays - getAuction().getDay();
		else
			return 0;
	}
}