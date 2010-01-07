package net.sourceforge.jasa.agent.strategy;

import cern.jet.random.engine.RandomEngine;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;

public class ReturnForecastStrategy extends FixedQuantityStrategyImpl {

	protected ReturnForecaster forecaster;
	
	protected RandomEngine prng;

	public double getReturnForecast(double currentPrice) {
		return forecaster.getReturnForecast(currentPrice);
	}
	
	public double getPriceForecast(double currentPrice) {
		double forecastedReturn = getReturnForecast(currentPrice);
		if (Double.isInfinite(currentPrice) || 
				Double.isNaN(currentPrice) || currentPrice < 10E-100) {
			currentPrice = 100;
		}
		return currentPrice * Math.exp(forecastedReturn);
	}
	
	@Override
	public boolean modifyShout(Order shout) {
		double currentPrice = auction.getQuote().getMidPoint();
		double forecastedPrice = getPriceForecast(currentPrice);
		boolean isBid = false;
		if (Double.isNaN(currentPrice) || Double.isInfinite(currentPrice)) {
			isBid = prng.nextDouble() > 0.5;
		} else {
			isBid = forecastedPrice > currentPrice;
		}
		shout.setIsBid(isBid);
		shout.setPrice(forecastedPrice);
		return super.modifyShout(shout);
	}

	@Override
	public void endOfRound(Market auction) {
	}

	public ReturnForecaster getForecaster() {
		return forecaster;
	}

	public void setForecaster(ReturnForecaster forecaster) {
		this.forecaster = forecaster;
		forecaster.setStrategy(this);
	}

	public RandomEngine getPrng() {
		return prng;
	}

	public void setPrng(RandomEngine prng) {
		this.prng = prng;
	}

}
