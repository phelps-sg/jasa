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

import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.MarketEventListener;
import net.sourceforge.jasa.market.IllegalShoutException;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.auctioneer.AbstractAuctioneer;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.util.Parameterizable;
//import ec.util.Parameter;
//import ec.util.ParameterDatabase;

/**
 * Classes implementing this interface define policies for accepting shouts. A
 * shout-accepting policy determines whether a shout should be accepted or not.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public abstract class OrderAcceptancePolicy implements Parameterizable,
    MarketEventListener {

	protected AbstractAuctioneer auctioneer;

	public static final String P_DEF_BASE = "accepting";

	public OrderAcceptancePolicy() {
		initialise();
	}

	public void initialise() {
	}

	public void reset() {
	}

	public void setAuctioneer(AbstractAuctioneer auctioneer) {
		this.auctioneer = auctioneer;
	}

	public AbstractAuctioneer getAuctioneer() {
		return auctioneer;
	}

//	public void setup(ParameterDatabase parameters, Parameter base) {
//	}

	/**
	 * checks whether
	 * <p>
	 * shout
	 * </p>
	 * is acceptable or not. If not, an IllegalShoutException is thrown.
	 * 
	 * @param shout
	 * @throws IllegalShoutException
	 */
	public abstract void check(Order shout) throws IllegalShoutException;

	public void eventOccurred(SimEvent event) {
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + ")";
	}

}