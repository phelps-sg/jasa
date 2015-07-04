package net.sourceforge.jasa.report;

import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jasa.market.MarketSimulation;

public class MidPriceReportVariables extends MarketPriceReportVariables {

	public static final String NAME = "mid";
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public double getPrice(RoundFinishedEvent event) {
		double p = ((MarketSimulation) event.getSimulation())
				.getQuote().getMidPoint();
		if (Double.isNaN(p)) {
			return 0.0;
		} else {
			return p;
		}
	}

}
