package net.sourceforge.jasa.agent.utility;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.Market;

public class RiskNeutralUtilityFunction extends AbstractUtilityFunction
		implements UtilityFunction {

	protected double coefficient = 1.0;
	
	public RiskNeutralUtilityFunction() {
		super();
	}

	public RiskNeutralUtilityFunction(TradingAgent agent) {
		super(agent);
	}

	public double calculatePayoff(Market auction, int quantity, double price) {
		return coefficient * calculateProfit(auction, quantity, price);
	}

	public double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(double coefficient) {
		this.coefficient = coefficient;
	}
	
	

}
