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

import net.sourceforge.jasa.agent.utility.AbstractUtilityFunction;
import net.sourceforge.jasa.agent.utility.UtilityFunction;
import net.sourceforge.jasa.event.MarketEventListener;
import net.sourceforge.jasa.market.Account;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.sim.Agent;
import net.sourceforge.jasa.sim.util.Resetable;


/**
 * <p>
 * Classes implementing this interface can trade in round-robin auctions, as
 * implemented by the RoundRobinAuction class.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public interface TradingAgent extends Agent, MarketEventListener {
	
	public boolean register(Market market);
	/**
	 * Request a shout from this trader. The trader will perform any bidding
	 * activity in this method and return when it is done. An market invokes this
	 * method on a trader when it is the traders "turn" to bid in that market.
	 * 
	 * @param market
	 *          The market in which to trade
	 */
	public void requestShout(Market auction);

	/**
	 * Returns true if the agent is a buyer in the specified market.
	 */
	public boolean isBuyer(Market auction);

	/**
	 * Returns true if the agent is a seller in the specified market.
	 */
	public boolean isSeller(Market auction);

	public Account getAccount();

	public CommodityHolding getCommodityHolding();

	public void shoutAccepted(Market auction, Order shout, double price,
	    int quantity);
	
	public double getValuation(Market auction);
	
	public void setUtilityFunction(UtilityFunction utilityFunction);
	
	public UtilityFunction getUtilityFunction();

}