package net.sourceforge.jasa.agent.strategy;

import junit.framework.TestCase;
import net.sourceforge.jasa.agent.valuation.NoiseTraderForecaster;
import net.sourceforge.jasa.sim.PRNGTestSeeds;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

public class NoiseTraderForecastTest extends TestCase {

	NoiseTraderForecaster forecaster;
	
	protected void setUp() throws Exception {
		super.setUp();
		RandomEngine prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);
		forecaster = new NoiseTraderForecaster(prng);
		forecaster.setPrng(prng);
	}
	
	public void testForecast() {
		SummaryStatistics stats = new SummaryStatistics();
		for(int i=0; i<10000; i++) {
			double forecast = forecaster.getReturnForecast(null);
			stats.addValue(forecast);
		}
		assertEquals(0, stats.getMean(), 0.01);
//		assertEquals(1, stats.getStandardDeviation(), 0.01);
	}

}
