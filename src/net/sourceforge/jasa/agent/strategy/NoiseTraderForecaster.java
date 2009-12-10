package net.sourceforge.jasa.agent.strategy;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Normal;
import cern.jet.random.engine.RandomEngine;

public class NoiseTraderForecaster implements ReturnForecaster {

	protected AbstractContinousDistribution noiseDistribution;

	public NoiseTraderForecaster() {
	}
	
	public NoiseTraderForecaster(RandomEngine prng) {
		noiseDistribution = new Normal(0, 1, prng);
	}
	
	@Override
	public double getReturnForecast() {
		return noiseDistribution.nextDouble();
	}

	public AbstractContinousDistribution getNoiseDistribution() {
		return noiseDistribution;
	}

	public void setNoiseDistribution(
			AbstractContinousDistribution noiseDistribution) {
		this.noiseDistribution = noiseDistribution;
	}

	@Override
	public void setStrategy(ReturnForecastStrategy strategy) {
		// TODO Auto-generated method stub
		
	}

}
