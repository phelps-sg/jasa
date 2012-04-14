package net.sourceforge.jasa.agent.valuation;

import java.io.Serializable;

import net.sourceforge.jasa.agent.TradingAgent;

public abstract class AbstractValuationPolicy implements ValuationPolicy,
		Serializable {

	protected TradingAgent agent;

	@Override
	public void setAgent(TradingAgent agent) {
		this.agent = agent;
	}

	@Override
	public TradingAgent getAgent() {
		return agent;
	}

}
