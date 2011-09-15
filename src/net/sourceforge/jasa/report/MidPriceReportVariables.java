package net.sourceforge.jasa.report;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.report.XYReportVariables;
import net.sourceforge.jasa.event.OrderPlacedEvent;

public class MidPriceReportVariables implements XYReportVariables {

	protected double midPrice;
	
	protected int time;
	
	public static final String NAME = "mid";
	
	public static final String MIDPRICE_VAR = "price";
	
	@Override
	public Map<Object, Number> getVariableBindings() {
		LinkedHashMap<Object, Number> result = 
			new LinkedHashMap<Object, Number>();
		result.put(getName() + ".t", time);
		result.put(getName() + "." + MIDPRICE_VAR, midPrice);
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
		return NAME;
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

	@Override
	public List<Object> getyVariableNames() {
		LinkedList<Object> result = new LinkedList<Object>();
		result.add(getName() + "." + MIDPRICE_VAR);
		return result;
 	}

	@Override
	public String getxVariableName() {
		return getName() + ".t";
	}

}
