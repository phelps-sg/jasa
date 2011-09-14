package net.sourceforge.jasa.agent.valuation;

import java.io.Serializable;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jabm.util.TimeSeriesWindow;
import net.sourceforge.jasa.agent.strategy.AbstractReturnForecaster;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketSimulation;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import cern.jet.random.AbstractContinousDistribution;




public class ChartistForecaster extends AbstractReturnForecaster 
		implements Serializable {

	protected TimeSeriesWindow history;
	
	protected AbstractContinousDistribution windowSizeDistribution;
	
	public ChartistForecaster(AbstractContinousDistribution windowSizeDistribution) {
		this.windowSizeDistribution = windowSizeDistribution;
	}
	 
	@Override
	public double determineValue(Market market) {
		return calculateHistoricalMeanReturn();
	}
	
	public double calculateHistoricalMeanReturn() {
		SummaryStatistics stats = new SummaryStatistics();
		int n = history.getWindowSize() - 1;
		for(int i=0; i<n; i++) {
			double p0 = history.getValue(i);
			double p1 = history.getValue(i+1);
			double r;
			if (Math.abs(p1 - p0) < 10E-8 || p0 < 10E-8) {
				r = 0;
			} else {
				r = (p1 - p0) / p0;
			}
			stats.addValue(r);
		}
		double result = stats.getMean();
		return result;
	}
	
	public void updatePriceHistory(RoundFinishedEvent event) {
		MarketSimulation simulation = (MarketSimulation) event.getSimulation();
		double currentPrice = simulation.getMarket().getCurrentPrice();
		history.addValue(currentPrice);
	}

	@Override
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof RoundFinishedEvent) {
			onRoundClosedEvent((RoundFinishedEvent) event);
		} else if (event instanceof SimulationStartingEvent) {
			onSimulationStarting();
		}
	}

	public void onSimulationStarting() {
		initialiseWindowSize();
	}
	
	public void onRoundClosedEvent(RoundFinishedEvent event) {
		updatePriceHistory(event);
	}
	
	public void initialiseWindowSize() {
		int windowSize = windowSizeDistribution.nextInt();
		history = new TimeSeriesWindow(windowSize);
	}

	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		scheduler.addListener(RoundFinishedEvent.class, this);
		scheduler.addListener(SimulationStartingEvent.class, this);
		super.subscribeToEvents(scheduler);
	}
	
}
