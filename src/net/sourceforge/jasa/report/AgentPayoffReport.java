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
 * A historicalDataReport that lists the ratio of actual to theoretical profits of each agent
 * group in the market.
 * 
 * @see net.sourceforge.jasa.agent.AgentGroup
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public class AgentPayoffReport extends PayoffReport {

	public Object getKey(AbstractTradingAgent agent) {
		return new Long(agent.getId());
	}

	public String getKeyName() {
		return "agent";
	}

	public String getReportText() {
		return "agent";
	}

}
