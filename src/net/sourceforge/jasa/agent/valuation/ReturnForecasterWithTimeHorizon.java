package net.sourceforge.jasa.agent.valuation;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jabm.util.TimeSeriesWindow;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketSimulation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

public abstract class ReturnForecasterWithTimeHorizon extends
		AbstractReturnForecaster implements InitializingBean, Cloneable {

	protected double timeHorizon = 1.0;
	
	protected TimeSeriesWindow historicalPredictions;
	
	protected TimeSeriesWindow historicalPrices;
	
	protected double currentPrediction;
	
	protected double totalSquaredError = 0.0;
	
	protected double alpha = 0.01;
	
	
	protected Market market;

	static Logger logger = Logger
			.getLogger(ReturnForecasterWithTimeHorizon.class);

	public double getTimeHorizon() {
		return timeHorizon;
	}

	public void setTimeHorizon(double timeHorizon) {
		this.timeHorizon = timeHorizon;
	}
	
	/**
	 * Calculate the forecast error at the end of each round.
	 * 
	 * @param event
	 */
	public void onRoundFinished(RoundFinishedEvent event) {
		this.market = (Market) event.getSimulation();
		historicalPredictions.addValue(this.currentPrediction);
		double currentPrice = market.getCurrentPrice();
		historicalPrices.addValue(currentPrice);
		//TODO decide correct use of timeHorizon
//		int lag = (int) Math.round(timeHorizon);
		int lag = 1;
		double previousReturn = (Math.log(currentPrice) - Math
				.log(historicalPrices.getValue(lag - 1))); //  * timeHorizon;
		double previousPredictedReturn = historicalPredictions
				.getValue(lag - 1);
		double error = previousReturn - previousPredictedReturn;
		if (!Double.isInfinite(previousReturn)) {
			if (Double.isNaN(totalSquaredError)) {
				totalSquaredError = error * error;
			} else {
				totalSquaredError = alpha * totalSquaredError + (1.0 - alpha)
						* error * error;
			}
		}
		if (logger.isDebugEnabled()) {
//			logger.debug("this = " + this);
			logger.debug("t = " + event.getSimulation().getSimulationTime());
//			logger.debug("previousPredictedReturn = " + previousPredictedReturn);
			logger.debug("currentPrice = " + currentPrice);
//			logger.debug("currentReturn = " + currentReturn);
//			logger.debug("totalSquaredError = " + totalSquaredError);
//			logger.debug("error = " + error);
		}
	}
	
	public double getForecastError() {
		return totalSquaredError;
	}
	
	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	@Override
	public double getReturnForecast(Market market) {
		// TODO: Decide how to correctly implement the timeHorizon
		this.currentPrediction = 
				getNextPeriodReturnForecast(market); // * timeHorizon;
		return this.currentPrediction;
	}
	
	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		super.subscribeToEvents(scheduler);
		scheduler.addListener(RoundFinishedEvent.class, this);
		scheduler.addListener(SimulationStartingEvent.class, this);
	}

	@Override
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof RoundFinishedEvent) {
			onRoundFinished((RoundFinishedEvent) event);
		} else if (event instanceof SimulationStartingEvent) {
			onSimulationStarting((SimulationStartingEvent) event);
		}
	}
	
	public void onSimulationStarting(SimulationStartingEvent event) {
		this.market = (MarketSimulation) event.getSimulation();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ReturnForecasterWithTimeHorizon result = 
				(ReturnForecasterWithTimeHorizon) super.clone();
		try {
			result.afterPropertiesSet();
			((MarketSimulation) market).addListener(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	@Override
	public void dispose() {
		((MarketSimulation) market).getSimulationController().removeListener(
				this);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		int n = (int) Math.round(timeHorizon);
		historicalPredictions = new TimeSeriesWindow(n);
		historicalPrices = new TimeSeriesWindow(n);
	}
	

	public abstract double getNextPeriodReturnForecast(Market market);

}
