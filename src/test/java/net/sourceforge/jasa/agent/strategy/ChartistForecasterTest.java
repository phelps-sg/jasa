package net.sourceforge.jasa.agent.strategy;

import junit.framework.TestCase;
import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jasa.agent.valuation.ChartistForecaster;

public class ChartistForecasterTest extends TestCase {

	protected ChartistForecaster forecaster;
	
	protected MockMarket market;
	
	public void setUp() {
		market = new MockMarket();
		forecaster = new ChartistForecaster();
		//TODO
//		forecaster.setWindowSize(3);
		try {
			forecaster.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testUpwardTrend() {
		//TODO
//		double[] prices = new double[] { 100.0, 200.0, 300.0 };
//		simulatePrices(prices);
//		double forecastedReturn = 
//				forecaster.getNextPeriodReturnForecast(market);
//		System.out.println("forecastedReturn = " + forecastedReturn);
//		assertTrue(forecastedReturn > 0.0);
//		double forecastedPrice = market.price * Math.exp(forecastedReturn);
//		System.out.println("forecastedPrice = " + forecastedPrice);
	}
	
	public void testDownardTrend() {
		//TODO
//		double[] prices = new double[] { 300.0, 200.0, 100.0 };
//		simulatePrices(prices);
//		double forecastedReturn = 
//				forecaster.getNextPeriodReturnForecast(market);
//		System.out.println("forecastedReturn = " + forecastedReturn);
//		assertTrue(forecastedReturn < 0.0);
//		double forecastedPrice = market.price * Math.exp(forecastedReturn);
//		System.out.println("forecastedPrice = " + forecastedPrice);
	}
	
	public void simulatePrices(double[] prices) {
		for(int i=0; i<prices.length; i++) {
			market.price = prices[i];
			RoundFinishedEvent event = new RoundFinishedEvent(market);
			forecaster.eventOccurred(event);
		}
	}
	
}
