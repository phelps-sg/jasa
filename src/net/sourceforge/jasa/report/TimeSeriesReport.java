package net.sourceforge.jasa.report;

import java.io.FileOutputStream;
import java.io.IOException;

import net.sourceforge.jabm.event.BatchFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jabm.report.CSVWriter;
import net.sourceforge.jabm.report.DataWriter;
import net.sourceforge.jasa.market.AuctionRuntimeException;

import org.apache.log4j.Logger;

public abstract class TimeSeriesReport extends AbstractAuctionReport {

	protected CSVWriter csvWriter;
	
	protected String baseFilename;
	
	protected int n = 0;
	
	static Logger logger = Logger.getLogger(TimeSeriesReport.class);
	
	public TimeSeriesReport(String filename) {
		this.baseFilename = filename;
	}
	
	public TimeSeriesReport() {
		this(null);
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


	public void onSimulationFinished() {
		csvWriter.close();
	}
	
	public void onSimulationStarting() {
		try {
			String filename = baseFilename + n + ".csv";
			logger.info("Writing time series to " + filename);
			csvWriter = 
				new CSVWriter(new FileOutputStream(filename), 2);
			n++;
		} catch (IOException e) {
			throw new AuctionRuntimeException(e);
		}
	}
	
	public void onBatchFinished() {
	}
	
	public DataWriter getCsvWriter() {
		return csvWriter;
	}

	public void setCsvWriter(CSVWriter dataWriter) {
		this.csvWriter = dataWriter;
	}

	public String getBaseFilename() {
		return baseFilename;
	}

	public void setBaseFilename(String baseFilename) {
		this.baseFilename = baseFilename;
	}

	
}
