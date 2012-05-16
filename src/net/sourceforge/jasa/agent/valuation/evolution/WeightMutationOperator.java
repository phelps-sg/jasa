package net.sourceforge.jasa.agent.valuation.evolution;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.evolution.MutationOperator;
import net.sourceforge.jasa.agent.SimpleTradingAgent;
import net.sourceforge.jasa.agent.valuation.LinearWeightedReturnForecaster;
import net.sourceforge.jasa.agent.valuation.ReturnForecastValuationPolicy;
import net.sourceforge.jasa.agent.valuation.ReturnForecasterWithTimeHorizon;

public class WeightMutationOperator implements MutationOperator {

	@Override
	public void mutate(Agent agent) {
		if (agent instanceof SimpleTradingAgent) {
			SimpleTradingAgent tradingAgent = (SimpleTradingAgent) agent;
			ReturnForecastValuationPolicy policy = 
					(ReturnForecastValuationPolicy) tradingAgent.getValuationPolicy();
			LinearWeightedReturnForecaster forecaster = 
					(LinearWeightedReturnForecaster) policy.getForecaster();
			forecaster.initialiseWeights();
		}
	}

}
