package net.sourceforge.jasa.agent.valuation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.util.TimeSeriesWindow;
import net.sourceforge.jasa.market.Market;

public abstract class ReturnForecasterWithTimeHorizon 
	extends	AbstractReturnForecaster implements InitializingBean {

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
				Math.log(currentPrice) - Math.log(historicalPrices.getValue(lag));
		double previousPredictedReturn = historicalPredictions.getValue(lag);
		double error = currentReturn - previousPredictedReturn;
		if (!Double.isNaN(error)) {
			totalSquaredError += error * error;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("previousPredictedReturn = " + previousPredictedReturn);
			logger.debug("currentPrice = " + currentPrice);
			logger.debug("currentReturn = " + currentReturn);
			logger.debug("totalSquaredError = " + totalSquaredError);
			logger.debug("error = " + error);
		}
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
	public void afterPropertiesSet() throws Exception {
		int n = (int) Math.round(timeHorizon);
		historicalPredictions = new TimeSeriesWindow(n);
		historicalPrices = new TimeSeriesWindow(n);
	}

	public abstract double getNextPeriodReturnForecast(Market market);

}
