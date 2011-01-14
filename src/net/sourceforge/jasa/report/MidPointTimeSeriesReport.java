package net.sourceforge.jasa.report;

import net.sourceforge.jabm.event.BatchFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.RoundClosingEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;

public class MidPointTimeSeriesReport extends TimeSeriesReport {

	public MidPointTimeSeriesReport(String filename) {
		super(filename);
	}
	
	public MidPointTimeSeriesReport() {
		super();
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
			onRoundClosing((OrderPlacedEvent) event);
		}
	}

	public void onRoundClosing(OrderPlacedEvent event) {
		csvWriter.newData(event.getTime());
		double midPrice = event.getAuction().getQuote().getMidPoint();
		csvWriter.newData(midPrice);
	}
}
