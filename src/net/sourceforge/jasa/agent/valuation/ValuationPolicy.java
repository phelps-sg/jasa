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

package net.sourceforge.jasa.agent.valuation;

import net.sourceforge.jabm.event.EventSubscriber;
import net.sourceforge.jabm.util.Resetable;
import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.event.MarketEventListener;
import net.sourceforge.jasa.market.Market;

/**
 * A commodity valuation policy for RoundRobinTrader agents.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public interface ValuationPolicy extends Resetable, EventSubscriber,
    MarketEventListener {

	/**
	 * Determine the current valuation of commodity in the given market.
	 */
	public double determineValue(Market auction);

	/**
	 * Recalculate valuation after consumption of the commodity being traded in
	 * the given market.
	 */
	public void consumeUnit(Market auction);

	public void setAgent(TradingAgent agent);
	
	public TradingAgent getAgent();

	public void initialise();

}