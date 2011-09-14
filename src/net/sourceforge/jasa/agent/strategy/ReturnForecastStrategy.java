package net.sourceforge.jasa.agent.strategy;

import net.sourceforge.jasa.agent.valuation.ReturnForecaster;
import net.sourceforge.jasa.market.Order;
import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.engine.RandomEngine;

public class ReturnForecastStrategy extends FixedQuantityStrategyImpl {
	
	protected RandomEngine prng;
	
	protected double markup;
	
	protected AbstractContinousDistribution markupDistribution;

	protected boolean isBuy;
	
	public double getReturnForecast() {
		ReturnForecaster forecaster = 
			(ReturnForecaster) agent.getValuationPolicy();
		return forecaster.determineValue(auction);
	}
	
	public double getPriceForecast(double currentPrice) {
		if (currentPrice < 10E-5) {
			currentPrice = 10E-5;
		}
		double forecastedReturn = getReturnForecast();
		double result = currentPrice * Math.exp(forecastedReturn);
		return result;
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
	
	@Override
	public boolean modifyShout(Order shout) {
		boolean result = super.modifyShout(shout);
		double currentPrice = auction.getCurrentPrice();
		if (!(currentPrice >= 0)) {
			assert currentPrice >= 0;
		}
		double forecastedPrice = getPriceForecast(currentPrice);
		assert !Double.isNaN(forecastedPrice);
		assert forecastedPrice >= -10E-5;
		this.isBuy = decideDirection(currentPrice, forecastedPrice);
		shout.setIsBid(isBuy);
		if (isBuy) {
			shout.setPrice(forecastedPrice * (1 - markup));
		} else {
			shout.setPrice(forecastedPrice * (1 + markup));
		}
		return result;
	}


//	public ReturnForecaster getForecaster() {
//		return forecaster;
//	}
//
//	public void setForecaster(ReturnForecaster forecaster) {
//		this.forecaster = forecaster;
//	}

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
	
	public boolean isBuy() {
		return isBuy;
	}

}
