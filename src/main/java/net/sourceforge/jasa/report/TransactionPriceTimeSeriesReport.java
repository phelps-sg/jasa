package net.sourceforge.jasa.report;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;

/**
 * 
 * @deprecated Replaced by TransactionPriceReportVariables
 * @author sphelps
 *
 */
public class TransactionPriceTimeSeriesReport extends TimeSeriesReport {

	public TransactionPriceTimeSeriesReport(String filename) {
		super(filename);
	}
	
	public TransactionPriceTimeSeriesReport() {
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
		if (event instanceof TransactionExecutedEvent) {
			onTransactionExecuted((TransactionExecutedEvent) event);
			
		}
	}

	public void onTransactionExecuted(TransactionExecutedEvent event) {
		csvWriter.newData(event.getTime());
		csvWriter.newData(event.getPrice());		
	}
}
