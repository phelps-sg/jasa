package net.sourceforge.jasa.report;

import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.RoundClosingEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.sim.event.BatchFinishedEvent;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.event.SimulationFinishedEvent;

public class MidPointTimeSeriesReport extends TimeSeriesReport {

	public MidPointTimeSeriesReport(String filename) {
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
			onRoundClosing((OrderPlacedEvent) event);
		}
	}

	public void onRoundClosing(OrderPlacedEvent event) {
		csvWriter.newData(event.getTime());
		double midPrice = event.getAuction().getQuote().getMidPoint();
		csvWriter.newData(midPrice);
	}
}
