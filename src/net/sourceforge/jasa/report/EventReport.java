package net.sourceforge.jasa.report;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sourceforge.jasa.sim.event.SimEvent;

public class EventReport extends AbstractAuctionReport {

	static Logger logger = Logger.getLogger(EventReport.class);
	
	@Override
	public void eventOccurred(SimEvent event) {
		// TODO Auto-generated method stub
		super.eventOccurred(event);
		logger.info(event.toString());
	}

	@Override
	public void produceUserOutput() {
	}

	@Override
	public void reset() {
	}

}
