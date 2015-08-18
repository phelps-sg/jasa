package net.sourceforge.jasa.agent;

import net.sourceforge.jasa.market.Market;

/**
 * (C) Steve Phelps 2015
 */
public class LogReturnProfitFunction implements ProfitFunction {

    @Override
    public double calculateProfit(AbstractTradingAgent agent, Market auction, int quantity, double price) {
        double result = 0.0;
		if (agent.isBuyer(auction)) {
			result = Math.log(agent.getValuation(auction)) - Math.log(price);
		} else {
			result = Math.log(price - agent.getValuation(auction));
		}
		if (!Double.isNaN(result)) {
				return result;
        } else {
            return 0.0;
        }
    }

}
