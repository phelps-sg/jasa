package net.sourceforge.jasa.agent.valuation.evolution;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.evolution.FitnessFunction;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.valuation.ReturnForecastValuationPolicy;
import net.sourceforge.jasa.agent.valuation.ReturnForecasterWithTimeHorizon;

public class ForecastErrorFitnessFunction implements FitnessFunction {

	@Override
	public double getFitness(Agent agent) {
		if (agent instanceof AbstractTradingAgent) {
			AbstractTradingAgent tradingAgent = (AbstractTradingAgent) agent;
			ReturnForecastValuationPolicy policy = 
					(ReturnForecastValuationPolicy) tradingAgent.getValuationPolicy();
			ReturnForecasterWithTimeHorizon forecaster = 
					(ReturnForecasterWithTimeHorizon) policy.getForecaster();
			double error = forecaster.getForecastError();
			if (Double.isNaN(error) || Double.isInfinite(error)) {
				return 0.0;
			} else {
				return 1.0 / (1.0 + error);
			}
		} else {
			return 0.0;
		}
	}

}
