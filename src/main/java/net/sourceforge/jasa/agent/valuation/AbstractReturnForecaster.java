package net.sourceforge.jasa.agent.valuation;

import java.io.Serializable;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.SimEvent;

public abstract class AbstractReturnForecaster 
		implements ReturnForecaster, Serializable {
//	
//	protected SeriesReportVariables returnTimeSeries;
//	
//	protected int horizon = 10;

	public void subscribeToEvents(EventScheduler scheduler) {
//		scheduler.addListener(InteractionsFinishedEvent.class, this);
	}

	@Override
	public void eventOccurred(SimEvent event) {
//		if (event instanceof InteractionsFinishedEvent) {
//			int t = (int)
//					((InteractionsFinishedEvent) event).getSimulation().getSimulationTime().getTicks();
//			double historicalReturn = returnTimeSeries.getY(0, t - horizon).doubleValue();
//			
//		}
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
