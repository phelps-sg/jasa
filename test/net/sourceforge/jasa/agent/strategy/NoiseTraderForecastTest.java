package net.sourceforge.jasa.agent.strategy;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import net.sourceforge.jasa.sim.PRNGTestSeeds;
import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;
import junit.framework.TestCase;

public class NoiseTraderForecastTest extends TestCase {

	NoiseTraderForecaster forecaster;
	
	protected void setUp() throws Exception {
		super.setUp();
		RandomEngine prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);
		Normal distribution = new Normal(0, 1, prng);
		forecaster = new NoiseTraderForecaster();
		forecaster.setNoiseDistribution(distribution);
	}
	
	public void testForecast() {
		SummaryStatistics stats = new SummaryStatistics();
		for(int i=0; i<10000; i++) {
			double forecast = forecaster.getReturnForecast(0);
			stats.addValue(forecast);
		}
		assertEquals(0, stats.getMean(), 0.01);
		assertEquals(1, stats.getStandardDeviation(), 0.01);
	}

}
