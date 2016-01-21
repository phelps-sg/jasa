package net.sourceforge.jasa.agent;

import java.io.Serializable;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jasa.agent.ProfitFunction;
import net.sourceforge.jasa.market.Market;

/**
 * (C) Steve Phelps 2015
 */
public class SimpleProfitFunction implements ProfitFunction, Serializable {

    @Override
    public double calculateProfit(AbstractTradingAgent agent, Market auction, int quantity, double price) {
        double result = (agent.getValuation(auction) - price) * quantity;
        if (agent.isSeller(auction)) {
            result = -result;
        }
        return result;
    }


}
