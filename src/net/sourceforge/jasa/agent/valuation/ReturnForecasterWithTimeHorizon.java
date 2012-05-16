package net.sourceforge.jasa.agent.valuation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.util.TimeSeriesWindow;
import net.sourceforge.jasa.market.Market;

public abstract class ReturnForecasterWithTimeHorizon 
	extends	AbstractReturnForecaster implements InitializingBean, Cloneable {

	protected double timeHorizon = 1.0;
	
	protected TimeSeriesWindow historicalPredictions;
	
	protected TimeSeriesWindow historicalPrices;
	
	protected double currentPrediction;
	
	protected double totalSquaredError = 0.0;

	static Logger logger = Logger
			.getLogger(ReturnForecasterWithTimeHorizon.class);

	public double getTimeHorizon() {
		return timeHorizon;
	}

	public void setTimeHorizon(double timeHorizon) {
		this.timeHorizon = timeHorizon;
	}
	
	public void onRoundFinished(RoundFinishedEvent event) {
		Market market = (Market) event.getSimulation();
		historicalPredictions.addValue(this.currentPrediction);
		double currentPrice = market.getCurrentPrice();
		historicalPrices.addValue(currentPrice);
		int lag = (int) Math.round(timeHorizon);
		double currentReturn = 
				(Math.log(currentPrice) - Math.log(historicalPrices.getValue(lag-1))) / timeHorizon;
		double previousPredictedReturn = historicalPredictions.getValue(lag-1);
		double error = currentReturn - previousPredictedReturn;
		if (!Double.isInfinite(currentReturn)) {
			if (Double.isNaN(totalSquaredError)) {
				totalSquaredError = error * error;
			} else {
				totalSquaredError = 0.01 * totalSquaredError + 0.99 * error * error;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("previousPredictedReturn = " + previousPredictedReturn);
			logger.debug("currentPrice = " + currentPrice);
			logger.debug("currentReturn = " + currentReturn);
			logger.debug("totalSquaredError = " + totalSquaredError);
			logger.debug("error = " + error);
		}
	}
	
	public double getForecastError() {
		return totalSquaredError;
	}
	
	@Override
	public double getReturnForecast(Market market) {
		this.currentPrediction = 
				getNextPeriodReturnForecast(market) / timeHorizon;
		return this.currentPrediction;
	}
	
	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		super.subscribeToEvents(scheduler);
		scheduler.addListener(RoundFinishedEvent.class, this);
	}

	@Override
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof RoundFinishedEvent) {
			onRoundFinished((RoundFinishedEvent) event);
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ReturnForecasterWithTimeHorizon result = 
				(ReturnForecasterWithTimeHorizon) super.clone();
		try {
			result.afterPropertiesSet();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		int n = (int) Math.round(timeHorizon);
		historicalPredictions = new TimeSeriesWindow(n);
		historicalPrices = new TimeSeriesWindow(n);
	}

	public abstract double getNextPeriodReturnForecast(Market market);

}
