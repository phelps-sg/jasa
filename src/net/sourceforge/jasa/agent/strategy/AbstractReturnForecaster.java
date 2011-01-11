package net.sourceforge.jasa.agent.strategy;

import java.io.Serializable;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.agent.valuation.ReturnForecaster;
import net.sourceforge.jasa.market.Market;

public abstract class AbstractReturnForecaster 
		implements ReturnForecaster, Serializable {

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
		// Do nothing
	}
	
	public void subscribeToEvents(EventScheduler scheduler) {
		// Do nothing
	}

}
