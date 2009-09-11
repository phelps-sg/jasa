package net.sourceforge.jasa.agent.utility;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.Market;

public class RiskNeutralUtilityFunction extends AbstractUtilityFunction
		implements UtilityFunction {

	public RiskNeutralUtilityFunction() {
		super();
	}

	public RiskNeutralUtilityFunction(TradingAgent agent) {
		super(agent);
	}

	public double calculatePayoff(Market auction, int quantity, double price) {
		return calculateProfit(auction, quantity, price);
	}

}
