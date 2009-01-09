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

package uk.ac.liv.auction.core;

import java.io.Serializable;

/**
 * An auctioneer for a double-auction with continuous clearing. The clearing
 * operation is performed every time a shout arrives. Shouts must beat the
 * current quote in order to be accepted.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class ContinuousDoubleAuctioneer extends TransparentAuctioneer implements
    Serializable {

	protected ZeroFundsAccount account;

	public ContinuousDoubleAuctioneer() {
		this(null);
	}

	public ContinuousDoubleAuctioneer(Auction auction) {
		super(auction);
		account = new ZeroFundsAccount(this);
		setPricingPolicy(new DiscriminatoryPricingPolicy(0));
	}

	public void generateQuote() {
		currentQuote = new MarketQuote(askQuote(), bidQuote());
	}

	public void endOfRoundProcessing() {
		super.endOfRoundProcessing();
	}

	public void endOfAuctionProcessing() {
		super.endOfAuctionProcessing();
	}

	public boolean shoutsVisible() {
		return true;
	}

	public void newShoutInternal(Shout shout) throws DuplicateShoutException {
		shoutEngine.newShout(shout);
		generateQuote();
		clear();
	}

	protected void checkShoutValidity(Shout shout) throws IllegalShoutException {
		super.checkShoutValidity(shout);
		checkImprovement(shout);
	}

	protected void checkImprovement(Shout shout) throws IllegalShoutException {
		double quote;
		if (shout.isBid()) {
			quote = bidQuote();
			if (shout.getPrice() < quote) {
				bidNotAnImprovementException();
			}
		} else {
			quote = askQuote();
			if (shout.getPrice() > quote) {
				askNotAnImprovementException();
			}
		}
	}

	public Account getAccount() {
		return account;
	}

	protected void askNotAnImprovementException()
	    throws NotAnImprovementOverQuoteException {
		if (askException == null) {
			// Only construct a new exception the once (for improved
			// performance)
			askException = new NotAnImprovementOverQuoteException(DISCLAIMER);
		}
		throw askException;
	}

	protected void bidNotAnImprovementException()
	    throws NotAnImprovementOverQuoteException {
		if (bidException == null) {
			// Only construct a new exception the once (for improved
			// performance)
			bidException = new NotAnImprovementOverQuoteException(DISCLAIMER);
		}
		throw bidException;
	}

	/**
	 * Reusable exceptions for performance
	 */
	protected static NotAnImprovementOverQuoteException askException = null;

	protected static NotAnImprovementOverQuoteException bidException = null;

	protected static final String DISCLAIMER = "This exception was generated in a lazy manner for performance reasons.  Beware misleading stacktraces.";
}
