package net.sourceforge.jasa.agent.valuation;

import java.io.Serializable;

import cern.jet.random.AbstractContinousDistribution;
import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.agent.strategy.AbstractReturnForecaster;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.sim.EventScheduler;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.event.SimulationStartingEvent;

public class LinearWeightedReturnForecaster extends AbstractReturnForecaster
		implements Serializable {

	protected AbstractReturnForecaster[] forecasters;
	
	protected double[] weights;
	
	protected AbstractContinousDistribution[] distributions;

	@Override
	public double determineValue(Market auction) {
		double result = 0.0;
		for(int i=0; i<forecasters.length; i++) {
			double forecast = forecasters[i].determineValue(auction);
			result += weights[i] * forecast;
		}
		return result;
	}
	
	@Override
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof SimulationStartingEvent) {
			onSimulationStarting();
		}
	}
	
	public void onSimulationStarting() {
		initialiseWeights();
	}
	
	public void initialiseWeights() {
		weights = new double[distributions.length];
		for(int i=0; i<distributions.length; i++) {
			weights[i] = distributions[i].nextDouble();
		}
	}

	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		scheduler.addListener(SimulationStartingEvent.class, this);
		super.subscribeToEvents(scheduler);
		for(int i=0; i<forecasters.length; i++) {
			forecasters[i].subscribeToEvents(scheduler);
		}
	}

	@Override
	public void setAgent(TradingAgent agent) {
		super.setAgent(agent);
		for(int i=0; i<forecasters.length; i++) {
			forecasters[i].setAgent(agent);
		}
	}

	public AbstractReturnForecaster[] getForecasters() {
		return forecasters;
	}

	public void setForecasters(AbstractReturnForecaster[] forecasters) {
		this.forecasters = forecasters;
	}

	public double[] getWeights() {
		return weights;
	}

	public void setWeights(double[] weights) {
		this.weights = weights;
	}

	public AbstractContinousDistribution[] getDistributions() {
		return distributions;
	}

	public void setDistributions(AbstractContinousDistribution[] distributions) {
		this.distributions = distributions;
	}
	
	
	
}
