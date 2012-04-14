package net.sourceforge.jasa.agent.valuation;

import net.sourceforge.jabm.event.EventListener;
import net.sourceforge.jabm.event.EventSubscriber;
import net.sourceforge.jasa.market.Market;

public interface ReturnForecaster extends EventListener, EventSubscriber {

	public double getReturnForecast(Market market);
	
	public void setValuationPolicy(ValuationPolicy policy);
	
	public ValuationPolicy getValuationPolicy();
	
}
