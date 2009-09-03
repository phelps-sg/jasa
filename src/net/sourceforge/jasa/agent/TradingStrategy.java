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

package net.sourceforge.jasa.agent;

import net.sourceforge.jasa.event.MarketEventListener;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.sim.strategy.Strategy;
import net.sourceforge.jasa.sim.util.Prototypeable;


/**
 * <p>
 * Classes implementing this interface define trading strategies for round-robin
 * traders.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public interface TradingStrategy extends Strategy, Prototypeable, MarketEventListener {

	/**
	 * Modify the trader's current shout according to the trading strategy being
	 * implemented.
	 * 
	 * @param shout
	 *          The shout to be updated
	 * @param market
	 *          The market in which this strategy is being employed
	 * @return The new shout, or null if no shout is to be placed.
	 */
	public Order modifyOrder(Order shout, Market auction);

	public void setAgent(AbstractTradingAgent agent);

	public int determineQuantity(Market auction);

	public void initialise();

}