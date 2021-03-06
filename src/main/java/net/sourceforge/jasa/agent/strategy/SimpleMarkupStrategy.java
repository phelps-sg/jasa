/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2014 Steve Phelps
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

import net.sourceforge.jasa.agent.valuation.ReturnForecastValuationPolicy;
import net.sourceforge.jasa.market.Order;
import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.engine.RandomEngine;

/**
 * A strategy which sets the current price and direction of the agent's order
 * based on a forecast of the next period price, as specified by the agent's
 * valuation policy.
 * 
 * @see ReturnForecastValuationPolicy
 * @author Steve Phelps
 */
public class SimpleMarkupStrategy extends FixedQuantityStrategyImpl {
	
	protected RandomEngine prng;
	
	protected double markup;
	
	protected AbstractContinousDistribution markupDistribution;

	@Override
	public boolean modifyShout(Order shout) {
		boolean result = super.modifyShout(shout);
		double forecastedPrice = getAgent().getValuation(auction);
		if (shout.isBid()) {
			shout.setPrice(forecastedPrice * (1 - markup));
		} else {
			shout.setPrice(forecastedPrice * (1 + markup));
		}
		return result;
	}

	public RandomEngine getPrng() {
		return prng;
	}

	public void setPrng(RandomEngine prng) {
		this.prng = prng;
	}

	public AbstractContinousDistribution getMarkupDistribution() {
		return markupDistribution;
	}

	public void setMarkupDistribution(
			AbstractContinousDistribution markupDistribution) {
		this.markupDistribution = markupDistribution;
		initialiseMarkup();
	}
	
	public void initialiseMarkup() {
		this.markup = markupDistribution.nextDouble();
	}

}
