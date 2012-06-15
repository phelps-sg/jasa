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
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class TruthTellingStrategy extends FixedDirectionStrategy implements
		Serializable {

	public TruthTellingStrategy(AbstractTradingAgent agent) {
		super(agent);
	}

	public TruthTellingStrategy() {
		super();
	}

	public boolean modifyShout(Order shout) {
		shout.setPrice(getAgent().getValuation(auction));
		return super.modifyShout(shout);
	}

	public void onRoundClosed(Market auction) {
	}
}