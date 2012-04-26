package net.sourceforge.jasa.agent.valuation;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.market.Market;
  
public class ReturnForecastValuationPolicy extends AbstractValuationPolicy {

	protected ReturnForecaster forecaster;
	
	public double getReturnForecast(Market auction) {
		return forecaster.getReturnForecast(auction);
	}
	
	public double getPriceForecast(Market auction) {
		double currentPrice = auction.getCurrentPrice();
		if (currentPrice < 10E-5) {
			currentPrice = 10E-5;
		}
		double forecastedReturn = getReturnForecast(auction);
		double result = currentPrice * Math.exp(forecastedReturn);
		return result;
	}
	
	@Override
	public double determineValue(Market auction) {
		return getPriceForecast(auction);
	}
	
	@Override
	public void reset() {
	}

	@Override
	public void eventOccurred(SimEvent event) {
	}

	@Override
	public void consumeUnit(Market auction) {
	}

	@Override
	public void initialise() {
	}

	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		forecaster.subscribeToEvents(scheduler);
	}

	public ReturnForecaster getForecaster() {
		return forecaster;
	}

	public void setForecaster(ReturnForecaster forecaster) {
		this.forecaster = forecaster;
//		forecaster.setValuationPolicy(this);
	}

	
}
