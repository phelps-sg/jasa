package net.sourceforge.jasa.report;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationEvent;
import net.sourceforge.jabm.report.XYReportVariables;
import net.sourceforge.jasa.event.TransactionExecutedEvent;

public class TransactionPriceReportVariables implements XYReportVariables {

	protected double lastTransactionPrice;
	
	protected int time;
	
	@Override
	public Map<Object, Number> getVariableBindings() {
		LinkedHashMap<Object, Number> result = 
			new LinkedHashMap<Object, Number>();
		result.put(getName() + ".t", time);
		result.put(getName() + ".transactionprice", lastTransactionPrice);
		return result;
	}

	@Override
	public void eventOccurred(SimEvent ev) {
		if (ev instanceof TransactionExecutedEvent) {
			TransactionExecutedEvent event = (TransactionExecutedEvent) ev;
			this.lastTransactionPrice = event.getPrice();
			this.time = event.getTime();
		}
	}

	@Override
	public void compute(SimulationEvent event) {
		// Do nothing
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
		return this.lastTransactionPrice;
	}
	
	public int getNumberOfSeries() {
		return 1;
	}

}
