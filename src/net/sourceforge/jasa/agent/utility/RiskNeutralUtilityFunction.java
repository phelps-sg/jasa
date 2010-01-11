package net.sourceforge.jasa.agent.utility;

import java.io.Serializable;

import net.sourceforge.jasa.agent.TradingAgent;

public class RiskNeutralUtilityFunction extends AbstractUtilityFunction
		implements Serializable, UtilityFunction {

	protected double coefficient = 1.0;
	
	public RiskNeutralUtilityFunction() {
		super();
	}

	public RiskNeutralUtilityFunction(TradingAgent agent) {
		super(agent);
	}

	public double calculatePayoff(double profit) {
		return coefficient * profit;
	}

	public double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(double coefficient) {
		this.coefficient = coefficient;
	}
	
}
