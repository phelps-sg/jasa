package net.sourceforge.jasa.agent.valuation;

import net.sourceforge.jasa.agent.strategy.AbstractReturnForecaster;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.event.SimulationStartingEvent;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

public class NoiseTraderForecaster extends AbstractReturnForecaster {

	protected AbstractContinousDistribution noiseDistribution;
	
	protected AbstractContinousDistribution volatilityDistribution;
	
	protected RandomEngine prng;

	public NoiseTraderForecaster(RandomEngine prng) {
		this.prng = prng;
		volatilityDistribution = new Uniform(0.0, 1.0, prng);
	}
	
	@Override
	public double determineValue(Market market) {
		if (noiseDistribution == null) {
			initialiseNoiseDistribution();
		}
		return noiseDistribution.nextDouble();
	}

//	public AbstractContinousDistribution getNoiseDistribution() {
//		return noiseDistribution;
//	}
//
//	public void setNoiseDistribution(
//			AbstractContinousDistribution noiseDistribution) {
//		this.noiseDistribution = noiseDistribution;
//	}
	
	@Override
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof SimulationStartingEvent) {
			initialiseNoiseDistribution();
		}
	}
	
	public void initialiseNoiseDistribution() {
		double volatility = volatilityDistribution.nextDouble();
		noiseDistribution = new Normal(0, volatility, prng);
	}

	public AbstractContinousDistribution getVolatilityDistribution() {
		return volatilityDistribution;
	}

	public void setVolatilityDistribution(
			AbstractContinousDistribution volatilityDistribution) {
		this.volatilityDistribution = volatilityDistribution;
	}

	public RandomEngine getPrng() {
		return prng;
	}

	public void setPrng(RandomEngine prng) {
		this.prng = prng;
	}
	

}
