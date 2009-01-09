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

package uk.ac.liv.auction.agent;

import java.io.Serializable;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;

import uk.ac.liv.util.Resetable;

/**
 * <p>
 * An abstract implementation of the Strategy interface that provides skeleton
 * functionality for making trading decisions.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public abstract class AbstractStrategy implements Serializable, Strategy,
    Resetable, Cloneable {

	protected AbstractTradingAgent agent;

	protected Shout.MutableShout currentShout;

	protected Auction auction;

	public AbstractStrategy() {
		initialise();
	}

	public AbstractStrategy(AbstractTradingAgent agent) {
		this();
		this.agent = agent;
	}

	public void setAgent(AbstractTradingAgent agent) {
		this.agent = agent;
	}

	public void reset() {
		initialise();
	}

	public Object protoClone() {
		try {
			AbstractStrategy copy = (AbstractStrategy) clone();
			copy.reset();
			return copy;
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	public Shout modifyShout(Shout shout, Auction auction) {
		this.auction = auction;
		if (modifyShout(currentShout)) {
			return new Shout(currentShout.getAgent(), currentShout.getQuantity(),
			    currentShout.getPrice(), currentShout.isBid());
		} else {
			return null;
		}
	}

	/**
	 * Modify the price and quantity of the given shout according to this
	 * strategy.
	 * 
	 * @return false if no shout is to be placed at this time
	 */
	public boolean modifyShout(Shout.MutableShout shout) {
		shout.setIsBid(agent.isBuyer(auction));
		shout.setAgent(agent);
		return true;
	}

	public void initialise() {
		currentShout = new Shout.MutableShout();
	}

	public void eventOccurred(AuctionEvent event) {
		if (event instanceof RoundClosedEvent) {
			endOfRound(event.getAuction());
		}
	}

	public AbstractTradingAgent getAgent() {
		return agent;
	}

	public abstract void endOfRound(Auction auction);
}