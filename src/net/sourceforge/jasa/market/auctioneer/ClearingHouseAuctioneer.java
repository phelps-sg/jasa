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

import net.sourceforge.jasa.market.Account;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.ZeroFundsAccount;
import net.sourceforge.jasa.market.rules.UniformPricingPolicy;

/**
 * An auctioneer for a clearing house market. The clearing operation is
 * performed at the end of every round.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class ClearingHouseAuctioneer extends TransparentAuctioneer implements
    Serializable {

	protected ZeroFundsAccount account;

	public ClearingHouseAuctioneer(Market auction) {
		super(auction);
		setPricingPolicy(new UniformPricingPolicy(0));
		account = new ZeroFundsAccount(this);
	}

	public void generateQuote() {
		currentQuote = new MarketQuote(askQuote(), bidQuote());
	}

	public void onRoundClosed() {
		super.onRoundClosed();
		generateQuote();
		clear();
	}

//	public void endOfAuctionProcessing() {
//		super.endOfAuctionProcessing();
//	}

	public Account getAccount() {
		return account;
	}


}