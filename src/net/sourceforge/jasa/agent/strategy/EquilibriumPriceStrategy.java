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

import net.sourceforge.jabm.util.Prototypeable;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.TokenTradingAgent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.report.EquilibriumReport;

/**
 * A strategy which will bid at the true equilibrium price, if profitable, or
 * bid truthfully otherwise. Although this is not a realistic strategy, it can
 * be useful for testing and control experiments.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class EquilibriumPriceStrategy extends FixedDirectionStrategy
    implements Serializable, Prototypeable {

	public EquilibriumPriceStrategy(AbstractTradingAgent agent, double price,
	    int quantity) {
		super(agent);

		this.quantity = quantity;
	}

	public EquilibriumPriceStrategy() {
		super(null);
	}

	public Object protoClone() {
		Object clonedStrategy;
		try {
			clonedStrategy = this.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
		return clonedStrategy;
	}

	public boolean modifyShout(Order shout) {
		EquilibriumReport eqReport = new EquilibriumReport(
		    (MarketFacade) auction);
		eqReport.calculate();
		double price = eqReport.calculateMidEquilibriumPrice();
		TokenTradingAgent tokenTradingAgent = (TokenTradingAgent) agent;
		if (tokenTradingAgent.isBuyer()
				&& price <= tokenTradingAgent.getValuation(auction)
				|| tokenTradingAgent.isSeller()
				&& price >= tokenTradingAgent.getValuation(auction)) {
			shout.setPrice(price);
		} else {
			shout.setPrice(tokenTradingAgent.getValuation(auction));
		}
		return super.modifyShout(shout);
	}

	public void onRoundClosed(Market auction) {
		// Do nothing
	}

}