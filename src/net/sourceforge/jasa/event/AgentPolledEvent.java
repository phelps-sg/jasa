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

package net.sourceforge.jasa.event;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.Market;

/**
 * An event that is fired whenever any agent is polled by an market for its
 * shout via the requestShout() method.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public class AgentPolledEvent extends MarketEvent {

	protected TradingAgent agent;

	public AgentPolledEvent(Market auction, int time, TradingAgent agent) {
		super(auction, time);
		this.agent = agent;
	}

	public TradingAgent getAgent() {
		return agent;
	}
}
