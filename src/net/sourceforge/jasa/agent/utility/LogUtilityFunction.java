package net.sourceforge.jasa.agent.utility;

import net.sourceforge.jasa.market.Market;

public class LogUtilityFunction extends AbstractUtilityFunction {

	protected double coefficient = 1.0;
	
	@Override
	public double calculatePayoff(Market auction, int quantity, double price) {
		double profit = calculateProfit(auction, quantity, price);
		return Math.log(1 + coefficient*profit);
	}

	public double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(double coefficient) {
		this.coefficient = coefficient;
	}
	
	

}
