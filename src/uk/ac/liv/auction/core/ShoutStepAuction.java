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

import org.apache.log4j.Logger;

/**
 * 
 * @author Steve Phelps
 * @version $Revision$
 * 
 */

public class ShoutStepAuction extends RandomRobinAuction implements
    Serializable {

	static Logger logger = Logger.getLogger(ShoutStepAuction.class);

	public ShoutStepAuction(String name) {
		super(name);
		initialise();
	}

	public ShoutStepAuction() {
		this(null);
	}

	public void step() throws AuctionClosedException {
		if (closed()) {
			throw new AuctionClosedException("Auction " + name + " is closed.");
		}
		if (closingCondition.eval()) {
			close();
		} else {
			if (endOfRound) {
				beginRound();
			}
			requestNextShout();
			if (endOfRound) {
				endRound();
			}
		}
	}

}
