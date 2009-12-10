package net.sourceforge.jasa.report;

import java.io.FileOutputStream;
import java.io.IOException;

import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.AuctionRuntimeException;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.report.CSVWriter;
import net.sourceforge.jasa.sim.report.DataWriter;

public abstract class TimeSeriesReport extends AbstractAuctionReport {

	protected CSVWriter csvWriter;
	
	public TimeSeriesReport(String filename) {
		try {
			csvWriter = new CSVWriter(new FileOutputStream(filename));
		} catch (IOException e) {
			throw new AuctionRuntimeException(e);
		}
	}
	@Override
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		recordEvent(event);
	}
	
	public abstract void recordEvent(SimEvent event);

	@Override
	public void produceUserOutput() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
	}

	public DataWriter getCsvWriter() {
		return csvWriter;
	}

	public void setCsvWriter(CSVWriter dataWriter) {
		this.csvWriter = dataWriter;
	}

	
}
