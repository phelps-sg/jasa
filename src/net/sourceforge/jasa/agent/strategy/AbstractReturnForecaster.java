package net.sourceforge.jasa.agent.strategy;

import java.io.Serializable;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.agent.valuation.ReturnForecaster;

public abstract class AbstractReturnForecaster 
		implements ReturnForecaster, Serializable {

	protected TradingAgent agent;

	public void setAgent(TradingAgent agent) {
		this.agent = agent;
	}
	
	public TradingAgent getAgent() {
		return agent;
	}
	
	public void subscribeToEvents(EventScheduler scheduler) {
		// Do nothing
	}

	@Override
	public void eventOccurred(SimEvent event) {
		//  Do nothing
		
	}

	
}
