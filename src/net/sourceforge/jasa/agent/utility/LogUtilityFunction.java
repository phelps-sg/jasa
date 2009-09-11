package net.sourceforge.jasa.agent.utility;

import net.sourceforge.jasa.market.Market;

public class LogUtilityFunction extends AbstractUtilityFunction {

	@Override
	public double calculatePayoff(Market auction, int quantity, double price) {
		double profit = calculateProfit(auction, quantity, price);
		return Math.log(1+profit);
	}

}
