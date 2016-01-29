package net.sourceforge.jasa.agent;

import net.sourceforge.jasa.market.Market;

public interface ProfitFunction {

    double calculateProfit(AbstractTradingAgent abstractTradingAgent, Market auction, int quantity, double price);

}
