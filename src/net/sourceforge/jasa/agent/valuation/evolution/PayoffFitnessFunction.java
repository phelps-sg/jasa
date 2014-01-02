package net.sourceforge.jasa.agent.valuation.evolution;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.evolution.FitnessFunction;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.valuation.ReturnForecastValuationPolicy;
import net.sourceforge.jasa.agent.valuation.ReturnForecasterWithTimeHorizon;

public class PayoffFitnessFunction implements FitnessFunction {

	@Override
	public double getFitness(Agent agent) {
		if (agent instanceof AbstractTradingAgent) {
			AbstractTradingAgent tradingAgent = (AbstractTradingAgent) agent;
            return tradingAgent.getPayoff();
		} else {
			return 0.0;
		}
	}

}
