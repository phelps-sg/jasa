package net.sourceforge.jasa.report;

import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jasa.market.MarketSimulation;

public class CurrentPriceReportVariables extends MarketPriceReportVariables {

	public static final String NAME = "current";
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public double getPrice(RoundFinishedEvent event) {
		return ((MarketSimulation) event.getSimulation()).getCurrentPrice();
	}
	
	

}
