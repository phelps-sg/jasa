package net.sourceforge.jasa.agent.utility;

import java.io.Serializable;

public class LogUtilityFunction extends AbstractUtilityFunction implements
		Serializable {

	protected double coefficient = 1.0;
	
	@Override
	public double calculatePayoff(double profit) {
		return Math.log(1 + coefficient*profit);
	}

	public double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(double coefficient) {
		this.coefficient = coefficient;
	}
	
	

}
