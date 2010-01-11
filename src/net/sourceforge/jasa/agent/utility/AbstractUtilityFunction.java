package net.sourceforge.jasa.agent.utility;

import java.io.Serializable;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.Market;

public abstract class AbstractUtilityFunction implements Serializable,
		UtilityFunction {

	protected TradingAgent agent;

	public AbstractUtilityFunction(TradingAgent agent) {
		super();
		this.agent = agent;
	}

	public AbstractUtilityFunction() {
		super();
	}

	public TradingAgent getAgent() {
		return agent;
	}

	public void setAgent(TradingAgent agent) {
		this.agent = agent;
	}

}
