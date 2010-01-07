package net.sourceforge.jasa.report;

import java.io.FileOutputStream;
import java.io.IOException;

import net.sourceforge.jasa.market.AuctionRuntimeException;

import net.sourceforge.jasa.sim.event.BatchFinishedEvent;
import net.sourceforge.jasa.sim.event.BatchStartingEvent;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.event.SimulationFinishedEvent;
import net.sourceforge.jasa.sim.event.SimulationStartingEvent;

import net.sourceforge.jasa.sim.report.CSVWriter;
import net.sourceforge.jasa.sim.report.DataWriter;

public abstract class TimeSeriesReport extends AbstractAuctionReport {

	protected CSVWriter csvWriter;
	
	protected String filename;
	
	protected int n = 0;
	
	public TimeSeriesReport(String filename) {
		this.filename = filename;
	}
	
	@Override
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof SimulationStartingEvent) {
			onSimulationStarting();
		} else if (event instanceof SimulationFinishedEvent) {
			onSimulationFinished();
		} else if (event instanceof BatchFinishedEvent) {
			onBatchFinished();
		}
	}
	
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


	public void onSimulationFinished() {
		csvWriter.close();
	}
	
	public void onSimulationStarting() {
		try {
			csvWriter = 
				new CSVWriter(new FileOutputStream(filename + n + ".csv"), 2);
			n++;
		} catch (IOException e) {
			throw new AuctionRuntimeException(e);
		}
	}
	
	public void onBatchFinished() {
//		csvWriter.close();
	}

	
}
