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

package net.sourceforge.jasa.market.rules;

import net.sourceforge.jasa.market.IllegalOrderException;
import net.sourceforge.jasa.market.NotAnImprovementOverQuoteException;
import net.sourceforge.jasa.market.Order;

/**
 * implements the NYSE rule under which a shout must improve the market quote to
 * be acceptable.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class QuoteBeatingAcceptingPolicy extends OrderAcceptancePolicy {

	protected static final String DISCLAIMER = "This exception was generated in a lazy manner for performance reasons.  Beware misleading stacktraces.";

	/**
	 * Reusable exceptions for performance
	 */
	protected static NotAnImprovementOverQuoteException askException = null;

	protected static NotAnImprovementOverQuoteException bidException = null;

	/**
	 * implements the NYSE shout improvement rule.
	 */
	public void check(Order shout) throws IllegalOrderException {
		double quote;
		if (shout.isBid()) {
			quote = auctioneer.bidQuote();
			if (shout.getPriceAsDouble() < quote) {
				if (bidException == null) {
					// Only construct a new exception the once (for improved
					// performance)
					bidException = new NotAnImprovementOverQuoteException(DISCLAIMER);
				}
				throw bidException;
			}
		} else {
			quote = auctioneer.askQuote();
			if (shout.getPriceAsDouble() > quote) {
				if (askException == null) {
					// Only construct a new exception the once (for improved
					// performance)
					askException = new NotAnImprovementOverQuoteException(DISCLAIMER);
				}
				throw askException;
			}
		}
	}
}
