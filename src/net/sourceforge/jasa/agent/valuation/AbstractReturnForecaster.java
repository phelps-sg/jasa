package net.sourceforge.jasa.agent.valuation;

import java.io.Serializable;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.SimEvent;

public abstract class AbstractReturnForecaster 
		implements ReturnForecaster, Serializable {

//	protected ValuationPolicy valuationPolicy;
//
//	@Override
//	public void setValuationPolicy(ValuationPolicy policy) {
//		this.valuationPolicy = policy;
//	}
//
//	@Override
//	public ValuationPolicy getValuationPolicy() {
//		return valuationPolicy;
//	}

	public void subscribeToEvents(EventScheduler scheduler) {
		// Do nothing
	}

	@Override
	public void eventOccurred(SimEvent event) {
		//  Do nothing
		
	}

	
}
