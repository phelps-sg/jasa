package net.sourceforge.jasa.agent.valuation.evolution;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.evolution.MutationOperator;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.valuation.LinearWeightedReturnForecaster;
import net.sourceforge.jasa.agent.valuation.ReturnForecastValuationPolicy;

public class WeightMutationOperator implements MutationOperator {

	@Override
	public void mutate(Agent agent) {
		if (agent instanceof AbstractTradingAgent) {
			AbstractTradingAgent tradingAgent = (AbstractTradingAgent) agent;
			ReturnForecastValuationPolicy policy = 
					(ReturnForecastValuationPolicy) tradingAgent.getValuationPolicy();
			LinearWeightedReturnForecaster forecaster = 
					(LinearWeightedReturnForecaster) policy.getForecaster();
			forecaster.initialiseWeights();
		}
	}

}
