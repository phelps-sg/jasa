package net.sourceforge.jasa.agent.valuation;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import net.sourceforge.jasa.agent.strategy.AbstractReturnForecaster;
import net.sourceforge.jasa.market.Market;

import net.sourceforge.jasa.sim.util.MovingAverage;

public class ChartistForecaster extends AbstractReturnForecaster {

	protected MovingAverage history;
	
	public ChartistForecaster(int windowSize) {
		history = new MovingAverage(windowSize);
	}
	 
	@Override
	public double determineValue(Market market) {
		updatePriceHistory(market);
		return calculateMeanReturn();
	}
	
	public double calculateMeanReturn() {
		SummaryStatistics stats = new SummaryStatistics();
		int n = history.getWindowSize() - 1;
		for(int i=0; i<n; i++) {
			double p1 = history.getValue(i);
			double p0 = history.getValue(i+1);
			double r = (p1 - p0) / p0;
			stats.addValue(r);
		}
		return stats.getMean();
	}
	
	public void updatePriceHistory(Market market) {
		double currentPrice = market.getCurrentPrice();
		history.addValue(currentPrice);
	}

}
