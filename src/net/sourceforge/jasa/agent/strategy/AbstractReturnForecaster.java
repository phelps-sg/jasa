package net.sourceforge.jasa.agent.strategy;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.agent.valuation.ReturnForecaster;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.sim.event.SimEvent;

public abstract class AbstractReturnForecaster implements ReturnForecaster {

	protected TradingAgent agent;
	
	@Override
	public void consumeUnit(Market auction) {
	}

	@Override
	public void initialise() {
	}

	@Override
	public void setAgent(TradingAgent agent) {
		this.agent = agent;
	}
	
	public TradingAgent getAgent() {
		return agent;
	}

	@Override
	public void reset() {
	}

	@Override
	public void eventOccurred(SimEvent event) {

	}

}
