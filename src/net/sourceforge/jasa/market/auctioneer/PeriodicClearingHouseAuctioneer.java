/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2013 Steve Phelps
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

package net.sourceforge.jasa.market.auctioneer;

import java.io.Serializable;

import net.sourceforge.jabm.util.Parameterizable;
import net.sourceforge.jasa.market.DuplicateShoutException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;


/**
 * An auctioneer for a periodic k-double-market. The clearing operation is
 * performed periodically as well as at the end of every round. The length of
 * each period depends upon how many shouts have been made since last clearing.
 * 
 * @deprecated
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class PeriodicClearingHouseAuctioneer extends ClearingHouseAuctioneer
    implements Serializable, Parameterizable {

	public static final String P_SHOUTNUMEACHPERIOD = "shoutnumeachperiod";

	public static final int DEF_SHOUTNUMEACHPERIOD = 6;

	public static final String P_DEF_BASE = "pch";

	private int shoutNumEachPeriod;

	private int shoutNum;

	public PeriodicClearingHouseAuctioneer() {
		this(null);
	}

	public PeriodicClearingHouseAuctioneer(Market auction) {
		super(auction);
	}

//	public void setup(ParameterDatabase parameters, Parameter base) {
//		super.setup(parameters, base);
//
//		shoutNumEachPeriod = parameters.getInt(base.push(P_SHOUTNUMEACHPERIOD),
//		    new Parameter(P_DEF_BASE).push(P_SHOUTNUMEACHPERIOD),
//		    DEF_SHOUTNUMEACHPERIOD);
//
//		if (shoutNumEachPeriod <= 0)
//			shoutNumEachPeriod = DEF_SHOUTNUMEACHPERIOD;
//
//	}

	public void clear() {
		super.clear();
		shoutNum = 0;
	}

	protected void newShoutInternal(Order shout) throws DuplicateShoutException {
		super.newShoutInternal(shout);
		shoutNum++;
		if (shoutNum >= shoutNumEachPeriod) {
			generateQuote();
			clear();
		}
	}
}