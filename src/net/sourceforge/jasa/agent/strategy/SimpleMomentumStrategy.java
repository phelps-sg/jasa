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

package net.sourceforge.jasa.agent.strategy;

import java.io.Serializable;

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import cern.jet.random.engine.RandomEngine;


/**
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class SimpleMomentumStrategy extends MomentumStrategy implements
    Serializable {

	public SimpleMomentumStrategy(AbstractTradingAgent agent, RandomEngine prng) {
		super(agent, prng);
		// TODO Auto-generated constructor stub
	}

	protected void adjustMargin() {
		if (agent.lastOrderFilled()) {
			adjustMargin(1.0);
		} else if (agent.active()) {
			adjustMargin(0);
		}
	}

}
