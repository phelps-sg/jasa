package net.sourceforge.jasa.agent.strategy;

import org.springframework.beans.factory.annotation.Required;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.Market;
import cern.jet.random.engine.RandomEngine;

/**
 * Decide whether to long or short based on whether the agents' valuation
 * for the asset is greater than the current price.  If there is no difference
 * between the actual price and the forecast then choose a direction randomly.
 * 
 * @author Steve Phelps
 *
 */
public class ForecastTradeDirectionPolicy implements TradeDirectionPolicy {

	protected RandomEngine prng;
	
	@Override
	public boolean isBuy(Market market, TradingAgent agent) {
		double currentPrice = market.getCurrentPrice();
		double forecastedPrice = agent.getValuation(market);
		assert !Double.isNaN(forecastedPrice);
		assert forecastedPrice >= -10E-5;
		return decideDirection(currentPrice, forecastedPrice);
	}
	
	public boolean decideDirection(double currentPrice, 
									double forecastedPrice) {
		if (Double.isNaN(currentPrice)) {
			return prng.nextDouble() >= 0.5;
		} else if (Math.abs(forecastedPrice - currentPrice) < 10E-5) {
			return prng.nextDouble() >= 0.5;
		} else {
			return forecastedPrice > currentPrice;
		}
	}

	public RandomEngine getPrng() {
		return prng;
	}

	@Required
	public void setPrng(RandomEngine prng) {
		this.prng = prng;
	}
	
}
