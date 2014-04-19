package net.sourceforge.jasa.agent.valuation;

import java.io.Serializable;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jabm.util.TimeSeriesWindow;
import net.sourceforge.jasa.market.Market;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import cern.jet.random.AbstractContinousDistribution;

public class ChartistForecaster extends ReturnForecasterWithTimeHorizon 
		implements Serializable {

	protected TimeSeriesWindow history;
	
	protected AbstractContinousDistribution windowSizeDistribution;
	
	protected int sampleInterval = 1;
	
	public ChartistForecaster(AbstractContinousDistribution windowSizeDistribution) {
		this.windowSizeDistribution = windowSizeDistribution;
	}
	
	public ChartistForecaster() {
	}
	 
	@Override
	public double getNextPeriodReturnForecast(Market market) {
		return calculateHistoricalMeanReturn();
	}
	
	public double calculateHistoricalMeanReturn() {
		SummaryStatistics stats = new SummaryStatistics();
		int n = history.getWindowSize() - 1;
		for(int i=0; i<n; i+= sampleInterval) {
			double p0 = history.getValue(i);
			double p1 = history.getValue(i + sampleInterval);
			double r = 0.0;
			if (p1 > 0 && p0 > 0) {
				r = Math.log(p0) - Math.log(p1);
			}
			stats.addValue(r);
		}
		double result = stats.getMean();
		return result;
	}
	
	public void updatePriceHistory(RoundFinishedEvent event) {
		Market market = (Market) event.getSimulation();
		double currentPrice = market.getCurrentPrice();
		history.addValue(currentPrice);
	}

	@Override
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof RoundFinishedEvent) {
			onRoundClosedEvent((RoundFinishedEvent) event);
		} else if (event instanceof SimulationStartingEvent) {
			onSimulationStarting((SimulationStartingEvent) event);
		}
	}

	public void onSimulationStarting(SimulationStartingEvent event) {
		initialiseWindowSize();
	}
	
	public void onRoundClosedEvent(RoundFinishedEvent event) {
		updatePriceHistory(event);
	}
	
	public void initialiseWindowSize() {
		setWindowSize(windowSizeDistribution.nextInt());
	}
	
	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		scheduler.addListener(RoundFinishedEvent.class, this);
		scheduler.addListener(SimulationStartingEvent.class, this);
		super.subscribeToEvents(scheduler);
	}

	public AbstractContinousDistribution getWindowSizeDistribution() {
		return windowSizeDistribution;
	}

	public void setWindowSizeDistribution(
			AbstractContinousDistribution windowSizeDistribution) {
		this.windowSizeDistribution = windowSizeDistribution;
	}

	public int getSampleInterval() {
		return sampleInterval;
	}

	public void setSampleInterval(int sampleInterval) {
		this.sampleInterval = sampleInterval;
	}
	
	public void setWindowSize(int windowSize) {
		history = new TimeSeriesWindow(windowSize);
	}
	
}
