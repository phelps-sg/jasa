package net.sourceforge.jasa.report;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.report.XYReportVariables;

public abstract class MarketPriceReportVariables implements Serializable,
		XYReportVariables {

	protected double price;
	
	protected int time;
	
	public static final String PRICE_VAR = "price";
	
	@Override
	public Map<Object, Number> getVariableBindings() {
		LinkedHashMap<Object, Number> result = 
			new LinkedHashMap<Object, Number>();
		result.put(getName() + ".t", time);
		result.put(getName() + "." + PRICE_VAR, price);
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
		this.price = Double.NaN;
		this.time = 0;
	}

	@Override
	public Number getX(int seriesIndex) {
		return this.time;
	}

	@Override
	public Number getY(int seriesIndex) {
		return this.price;
	}
	
	public int getNumberOfSeries() {
		return 1;
	}

	@Override
	public void eventOccurred(SimEvent ev) {
		if (ev instanceof RoundFinishedEvent) {
			onRoundFinished((RoundFinishedEvent) ev);
		}
	}

	public void onRoundFinished(RoundFinishedEvent event) {
		this.price = getPrice(event);
		this.time = (int) 
				event.getSimulation().getSimulationTime().getTicks();
	}

	@Override
	public List<Object> getyVariableNames() {
		LinkedList<Object> result = new LinkedList<Object>();
		result.add(getName() + "." + PRICE_VAR);
		return result;
 	}

	@Override
	public String getxVariableName() {
		return getName() + ".t";
	}

	@Override
	public abstract String getName();
	
	public abstract double getPrice(RoundFinishedEvent event);

}
