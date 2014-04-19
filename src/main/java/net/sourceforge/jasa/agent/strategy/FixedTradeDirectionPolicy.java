package net.sourceforge.jasa.agent.strategy;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.Market;

/**
 * A trade direction policy which specifies a configurable fixed 
 * position to take in the market.
 * 
 * @author Steve Phelps
 *
 */
public class FixedTradeDirectionPolicy implements TradeDirectionPolicy {

	protected boolean isBuy;
	
	@Override
	public boolean isBuy(Market market, TradingAgent agent) {
		return isBuy;
	}

	public boolean isBuy() {
		return isBuy;
	}

	public void setBuy(boolean isBuy) {
		this.isBuy = isBuy;
	}
	
}

