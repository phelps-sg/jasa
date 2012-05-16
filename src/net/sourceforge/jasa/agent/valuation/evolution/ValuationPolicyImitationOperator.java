package net.sourceforge.jasa.agent.valuation.evolution;

import org.apache.log4j.Logger;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.evolution.ImitationOperator;
import net.sourceforge.jasa.agent.SimpleTradingAgent;
import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.agent.valuation.ReturnForecastValuationPolicy;

public class ValuationPolicyImitationOperator implements ImitationOperator {
	
	static Logger logger = Logger.getLogger(ValuationPolicyImitationOperator.class);

	@Override
	public void inheritBehaviour(Agent child, Agent parent) {
		if (parent instanceof SimpleTradingAgent && child instanceof SimpleTradingAgent) {
			try {
				SimpleTradingAgent tradingAgent = (SimpleTradingAgent) parent;
				ReturnForecastValuationPolicy policy = (ReturnForecastValuationPolicy) tradingAgent
						.getValuationPolicy();
				ReturnForecastValuationPolicy newPolicy;
				newPolicy = (ReturnForecastValuationPolicy) policy.clone();
				((SimpleTradingAgent) child).setValuationPolicy(newPolicy);
				newPolicy.setAgent((TradingAgent) child);
				if (logger.isDebugEnabled())
					logger.debug("Inheriting " + newPolicy + " for " + child);

			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
