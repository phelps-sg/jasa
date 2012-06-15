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

/**
 * The interface for expressing the condition of closing an market.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 * 
 */

public class MaxDaysAuctionClosingCondition extends TimingCondition implements
    AuctionClosingCondition {

	/**
	 * The maximum number of trading days before the market closes
	 */

	protected int maximumDays = -1;

	public MaxDaysAuctionClosingCondition() {
		this(null);
	}

	public MaxDaysAuctionClosingCondition(Market auction) {
		super(auction);
	}

	public void setMaximumDays(int maximumDays) {
		this.maximumDays = maximumDays;
	}

	public int getMaximumDays() {
		return maximumDays;
	}

	/*
	 * @see net.sourceforge.jasa.market.TimingCondition#eval()
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