package net.sourceforge.jasa.agent.strategy;

import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;

public class ReturnForecastStrategy extends FixedQuantityStrategyImpl {

	protected ReturnForecaster forecaster;

	public double getReturnForecast() {
		return forecaster.getReturnForecast();
	}
	
	public double getPriceForecast(double currentPrice) {
		double forecastedReturn = getReturnForecast();
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
		shout.setIsBid(forecastedPrice > currentPrice);
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

	
}
