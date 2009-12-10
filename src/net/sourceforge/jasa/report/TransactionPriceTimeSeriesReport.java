package net.sourceforge.jasa.report;

import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.sim.event.BatchFinishedEvent;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.event.SimulationFinishedEvent;

public class TransactionPriceTimeSeriesReport extends TimeSeriesReport {

	public TransactionPriceTimeSeriesReport(String filename) {
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
	public void recordEvent(SimEvent event) {
		if (event instanceof TransactionExecutedEvent) {
			TransactionExecutedEvent ev = (TransactionExecutedEvent) event;
			csvWriter.newData(ev.getTime());
			csvWriter.newData(ev.getPrice());
		} else if (event instanceof SimulationFinishedEvent) {
			csvWriter.endRecord();
		} else if (event instanceof BatchFinishedEvent) {
			csvWriter.close();
		}
	}

}
