package net.sourceforge.jasa.agent.valuation.evolution;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.evolution.ImitationOperator;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.MarketMakerAgent;
import net.sourceforge.jasa.agent.valuation.ReturnForecastValuationPolicy;

import org.apache.log4j.Logger;

public class ValuationPolicyImitationOperator implements ImitationOperator {
	
	static Logger logger = Logger.getLogger(ValuationPolicyImitationOperator.class);

	@Override
	public void inheritBehaviour(Agent i, Agent j) {
		if (!(i instanceof MarketMakerAgent && j instanceof MarketMakerAgent)) {
			try {
				AbstractTradingAgent child = (AbstractTradingAgent) i;
				AbstractTradingAgent parent = (AbstractTradingAgent) j;
				ReturnForecastValuationPolicy oldPolicy = (ReturnForecastValuationPolicy) child
						.getValuationPolicy();
				ReturnForecastValuationPolicy policy = 
						(ReturnForecastValuationPolicy) parent
						.getValuationPolicy();
				ReturnForecastValuationPolicy newPolicy;
				newPolicy = (ReturnForecastValuationPolicy) policy.clone();
				child.setValuationPolicy(newPolicy);
				oldPolicy.dispose();
				newPolicy.setAgent(child);
				if (logger.isDebugEnabled())
					logger.debug("Inheriting " + newPolicy + " for " + child);

			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
