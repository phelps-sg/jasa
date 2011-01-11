package net.sourceforge.jasa.agent.valuation;

import java.io.Serializable;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jasa.agent.strategy.AbstractReturnForecaster;
import net.sourceforge.jasa.market.Market;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

public class NoiseTraderForecaster extends AbstractReturnForecaster
		implements Serializable {

	protected AbstractContinousDistribution noiseDistribution;
	
	protected RandomEngine prng;

	public NoiseTraderForecaster(RandomEngine prng) {
		this.prng = prng;
		noiseDistribution = new Normal(0, 1.0, prng);
	}
	
	@Override
	public double determineValue(Market market) {
		return noiseDistribution.nextDouble();
	}

	@Override
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
	}
	
	public RandomEngine getPrng() {
		return prng;
	}

	public void setPrng(RandomEngine prng) {
		this.prng = prng;
	}

	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		super.subscribeToEvents(scheduler);
		scheduler.addListener(SimulationStartingEvent.class, this);
	}
	

}
