package net.sourceforge.jasa.report;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.report.XYReportVariables;
import net.sourceforge.jasa.event.OrderPlacedEvent;

public class MidPriceReportVariables implements XYReportVariables {

	protected double midPrice;
	
	protected int time;
	
	@Override
	public Map<Object, Number> getVariableBindings() {
		LinkedHashMap<Object, Number> result = 
			new LinkedHashMap<Object, Number>();
		result.put(getName() + ".t", time);
		result.put(getName() + ".midprice", midPrice);
		return result;
	}

	@Override
	public void compute(SimEvent ev) {
		eventOccurred(ev);
	}

	@Override
	public void dispose(SimEvent event) {
		// Do nothing
	}

	@Override
	public void initialise(SimEvent event) {
		// Do nothing

	}

	@Override
	public String getName() {
		return toString();
	}

	@Override
	public Number getX(int seriesIndex) {
		return this.time;
	}

	@Override
	public Number getY(int seriesIndex) {
		return this.midPrice;
	}
	
	public int getNumberOfSeries() {
		return 1;
	}

	@Override
	public void eventOccurred(SimEvent ev) {
		if (ev instanceof OrderPlacedEvent) {
			OrderPlacedEvent event = (OrderPlacedEvent) ev;
			this.midPrice = event.getAuction().getQuote().getMidPoint();
			this.time = event.getTime();
		}
	}

}
