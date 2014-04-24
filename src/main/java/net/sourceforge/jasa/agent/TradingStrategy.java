/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2014 Steve Phelps
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

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.strategy.Strategy;
import net.sourceforge.jasa.event.MarketEventListener;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

/**
 * <p>
 * Classes implementing this interface define trading strategies for traders.
 * </p>
 * 
 * @author Steve Phelps
 */
public interface TradingStrategy extends Strategy, MarketEventListener {

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
	
	/**
	 * Configure the agent for this strategy.
	 * 
	 * @param agent  The agent associated with this strategy.
	 * 				
	 */
	public void setAgent(AbstractTradingAgent agent);

	/**
	 * The current volume traded by this strategy in the specified market.
	 * This is mainly used for computing supply and demand statistics.
	 * 
	 * @param auction	The market we are querying.
	 * @return	The current volume traded in auction.
	 */
	public int determineQuantity(Market auction);

	/**
	 * Initialise the strategy.
	 */
	public void initialise();

	public void subscribeToEvents(EventScheduler scheduler);
	
	/**
	 * Query the current trade direction taken by this strategy.
	 * 
	 * @param auction
	 * 
	 * @return true for a long position or false for a short position.
	 */
	public boolean isBuy(Market auction);

}