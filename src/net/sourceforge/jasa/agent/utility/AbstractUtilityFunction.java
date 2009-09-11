package net.sourceforge.jasa.agent.utility;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.Market;

public abstract class AbstractUtilityFunction implements UtilityFunction {

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
	
	public double calculateProfit(Market auction, int quantity, double price) {
		if (agent.isBuyer(auction)) {
			return (agent.getValuation(auction) - price) * quantity;
		} else {
			return  (price - agent.getValuation(auction)) * quantity;
		}
	}
	
	
}
