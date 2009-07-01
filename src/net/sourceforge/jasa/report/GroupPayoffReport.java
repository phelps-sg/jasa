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
public class GroupPayoffReport extends PayoffReport {

	public Object getKey(AbstractTradingAgent agent) {
		return agent.getGroup();
	}

	public String getKeyName() {
		return "group";
	}

	public String getReportText() {
		return "in group";
	}

}
