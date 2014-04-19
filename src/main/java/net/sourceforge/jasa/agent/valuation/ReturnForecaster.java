package net.sourceforge.jasa.agent.valuation;

import net.sourceforge.jabm.event.EventListener;
import net.sourceforge.jabm.event.EventSubscriber;
import net.sourceforge.jasa.market.Market;

public interface ReturnForecaster extends EventListener, EventSubscriber,
		Cloneable {

	public double getReturnForecast(Market market);

	public Object clone() throws CloneNotSupportedException;

	public void dispose();

}
