package net.sourceforge.jasa.report;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.Simulation;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationEvent;
import net.sourceforge.jabm.report.AbstractReportVariables;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.valuation.LinearWeightedReturnForecaster;
import net.sourceforge.jasa.agent.valuation.ReturnForecastValuationPolicy;
import net.sourceforge.jasa.agent.valuation.ReturnForecaster;
import net.sourceforge.jasa.agent.valuation.ValuationPolicy;

public class PopulationWeightsReportVariables extends AbstractReportVariables 
		implements Serializable {

	protected SummaryStatistics[] weightStatistics =
			new SummaryStatistics[NUM_WEIGHTS];
	
	public static final int NUM_WEIGHTS = 3;
	
	public PopulationWeightsReportVariables() {
		super("weights");
		for(int i=0; i<NUM_WEIGHTS; i++) {
			weightStatistics[i] = new SummaryStatistics();
		}
	}

	@Override
	public void compute(SimEvent event) {
		super.compute(event);
		if (event instanceof SimulationEvent) {
			computeWeightStatistics(event);
		}
	}
	
	public void initialiseStatistics() {
		for(int i=0; i<NUM_WEIGHTS; i++) {
			weightStatistics[i] = new SummaryStatistics();
		}
	}
	
	public void recordStatistics(double[] weights) {
		for(int i=0; i<NUM_WEIGHTS; i++) {
			weightStatistics[i].addValue(weights[i]);
		}
	}

	public void computeWeightStatistics(SimEvent event) {
		SimulationEvent simEvent = (SimulationEvent) event;
		Simulation simulation = (Simulation) simEvent.getSimulation();
		Population population = simulation.getPopulation();
		
		initialiseStatistics();
		for(Agent agent : population.getAgents()) {
			ValuationPolicy policy =
					((AbstractTradingAgent) agent).getValuationPolicy();
			ReturnForecaster forecaster = ((ReturnForecastValuationPolicy) policy)
					.getForecaster();
			double[] weights = 
					((LinearWeightedReturnForecaster) forecaster).getWeights();
			recordStatistics(weights);
		}
	}

	@Override
	public Map<Object, Number> getVariableBindings() {
		Map<Object, Number> result = super.getVariableBindings();
		for(int i=0; i<NUM_WEIGHTS; i++) {
			String statName = "weights." + i;
			recordMoments(statName, result, weightStatistics[i]);
		}
		return result;
	}

	@Override
	public int getNumberOfSeries() {
		return NUM_WEIGHTS;
	}
	
	
	
}
