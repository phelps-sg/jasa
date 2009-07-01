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

import java.io.Serializable;

/**
 * A class representing an arbitrary grouping of agents. Every agent can
 * optionally belong to a group. This is useful for reporting purposes; for
 * example, agents can be grouped according to what kind of strategy they use
 * and then the net.sourceforge.jasa.report.PayoffLogger class can be used to historicalDataReport
 * on the payoff to each kind of strategy.
 * 
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class AgentGroup implements Serializable {

	protected String description;

	public static final int MAX_GROUPS = 100;

	/**
	 * A global list of groups indexed by number.
	 */
	private static AgentGroup[] groups = new AgentGroup[MAX_GROUPS];

	public AgentGroup(String description) {
		this.description = description;
	}

	public String toString() {
		return "(" + getClass() + " description:\"" + description + "\")";
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Get group n
	 * 
	 */
	public static AgentGroup getAgentGroup(int n) {
		if (groups[n] == null) {
			groups[n] = new AgentGroup("group " + n);
		}
		return groups[n];
	}

}
