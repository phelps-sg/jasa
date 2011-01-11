package net.sourceforge.jasa.report;

import net.sourceforge.jabm.event.BatchFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;

public class OrderFlowTimeSeriesReport extends TimeSeriesReport {

	public OrderFlowTimeSeriesReport(String filename) {
		super(filename);
	}

	@Override
	public void produceUserOutput() {
		// TODO Auto-generated method stub
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
	}

	@Override
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof OrderPlacedEvent) {
			onOrderPlaced((OrderPlacedEvent) event);
		} 
	}
	
	public void onOrderPlaced(OrderPlacedEvent event) {
		csvWriter.newData(event.getTime());
		double price = event.getOrder().getPrice();
		if (event.getOrder().isAsk()) {
			price = -price;
		}
		csvWriter.newData(price);
	}
	
}
