package net.sourceforge.jasa.report;

import net.sourceforge.jabm.event.BatchFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;

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
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof TransactionExecutedEvent) {
			onTransactionExecuted((TransactionExecutedEvent) event);
			
		}
	}

	public void onTransactionExecuted(TransactionExecutedEvent event) {
		csvWriter.newData(event.getTime());
		csvWriter.newData(event.getPrice());
	}
}
