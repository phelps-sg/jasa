package net.sourceforge.jasa.agent.strategy;

import junit.framework.TestCase;
import net.sourceforge.jabm.util.MathUtil;
import net.sourceforge.jasa.agent.valuation.FundamentalistForecaster;

public class FundamentalistForecasterTest extends TestCase {

	FundamentalistForecaster forecaster;
	
	MockMarket market;

	public static final double FUNDAMENTAL_PRICE = 500.0;

	public static final int TIME_HORIZON = 10;

	public void setUp() {
		forecaster = new FundamentalistForecaster();
		forecaster.setFundamentalPrice(FUNDAMENTAL_PRICE);
		forecaster.setTimeHorizon(TIME_HORIZON);
		market = new MockMarket();		
	}

	public void testGetter() {
		assertTrue(forecaster.getFundamentalPrice().equals(FUNDAMENTAL_PRICE));
	}

	public double getNextPeriodReturnForecast() {
		double forecastedReturn = forecaster
				.getNextPeriodReturnForecast(market);
		System.out.println("forecastedReturn = " + forecastedReturn);
		return forecastedReturn;
	}

	public void testDownwardMovement() {
		market.price = 600.0;
		double forecastedReturn = getNextPeriodReturnForecast();
		assertTrue(forecastedReturn < 0);
		checkPriceForecast(forecastedReturn);
	}
	
	public void testUpwardMovement() {
		market.price = 400.0;
		double forecastedReturn = getNextPeriodReturnForecast();
		assertTrue(forecastedReturn > 0);
		checkPriceForecast(forecastedReturn);
	}

	public void checkPriceForecast(double forecastedReturn) {
		double forecastedPrice = market.price * Math.exp(forecastedReturn);
		System.out.println("forecastedPrice = " + forecastedPrice);
		assertTrue(MathUtil.approxEqual(forecastedPrice, FUNDAMENTAL_PRICE));
	}
	
}
