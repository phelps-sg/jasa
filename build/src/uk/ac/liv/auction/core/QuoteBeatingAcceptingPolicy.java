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

/**
 * implements the NYSE rule under which a shout must improve the market
 * quote to be acceptable.
 *
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class QuoteBeatingAcceptingPolicy extends ShoutAcceptingPolicy {

	protected static final String DISCLAIMER = "This exception was generated in a lazy manner for performance reasons.  Beware misleading stacktraces.";

	/**
	 * Reusable exceptions for performance
	 */
	protected static NotAnImprovementOverQuoteException askException = null;

	protected static NotAnImprovementOverQuoteException bidException = null;

	/**
	 * implements the NYSE shout improvement rule.
	 */
	public void check(Shout shout) throws IllegalShoutException {
		double quote;
		if (shout.isBid()) {
			quote = auctioneer.bidQuote();
			if (shout.getPrice() < quote) {
				if (bidException == null) {
					// Only construct a new exception the once (for improved performance)
					bidException = new NotAnImprovementOverQuoteException(DISCLAIMER);
				}
				throw bidException;
			}
		} else {
			quote = auctioneer.askQuote();
			if (shout.getPrice() > quote) {
				if (askException == null) {
					// Only construct a new exception the once (for improved performance)
					askException = new NotAnImprovementOverQuoteException(DISCLAIMER);
				}
				throw askException;
			}
		}
	}
}
