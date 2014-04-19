package net.sourceforge.jasa.agent.strategy;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.Market;

/**
 * Classes defining this interface are responsible for deciding which 
 * direction --- long or short --- should be taken by the specified
 * TradingAgent.
 * 
 * @author Steve Phelps
 */
public interface TradeDirectionPolicy {

	/**
	 * Decide whether to go long long (buy) or short (sell).
	 * @param market  The market in which to make a trading decision
	 * @return  true for a long position or false for a short position
	 */
	public boolean isBuy(Market market, TradingAgent agent);
//	
//	/**
//	 * Configure the trading agent which corresponds to this policy.
//	 * @param agent
//	 */
//	public void setAgent(TradingAgent agent);
//	
//	public TradingAgent getAgent();
//	
}
