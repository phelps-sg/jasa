package net.sourceforge.jasa.agent.utility;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.Market;

public interface UtilityFunction {

	public double calculatePayoff(Market auction, int quantity, double price);

	public void setAgent(TradingAgent tradingAgent);
	
	public TradingAgent getAgent();
	
}
