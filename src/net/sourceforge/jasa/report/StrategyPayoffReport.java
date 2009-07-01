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

package net.sourceforge.jasa.report;

import net.sourceforge.jasa.agent.AbstractTradingAgent;

/**
 * A historicalDataReport that lists the ratio of actual to theoretical profits of each
 * strategy being played in the market. Note that strategies are identified by
 * their class, so two agents configured with the same class of strategy, but
 * with different parameters, will be grouped together.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public class StrategyPayoffReport extends PayoffReport {

	public Object getKey(AbstractTradingAgent agent) {
		return agent.getStrategy().getClass().getName();
	}

	public String getKeyName() {
		return "strategy";
	}

	public String getReportText() {
		return "agents playing strategy";
	}

}
