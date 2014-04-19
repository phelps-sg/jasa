package net.sourceforge.jasa.agent.strategy;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.Market;

/**
 * If the agent's inventory is above a pre-specified maximum,
 *  then buy or sell to reduce the inventory.  Otherwise use
 *  the specified chained policy.
 *  
 * @author Steve Phelps
 *
 */
public class MaxInventoryTradeDirectionPolicy implements TradeDirectionPolicy {

	protected int maxInventory = 1;
	
	protected TradeDirectionPolicy chainedPolicy;
	
	@Override
	public boolean isBuy(Market market, TradingAgent agent) {
		int currentInventory = agent.getCommodityHolding().getQuantity();
		if (Math.abs(currentInventory) >= maxInventory) {
			return currentInventory < 0;
		} else {
			return chainedPolicy.isBuy(market, agent);
		}
	}

	public int getMaxInventory() {
		return maxInventory;
	}

	public void setMaxInventory(int maxInventory) {
		this.maxInventory = maxInventory;
	}

	public TradeDirectionPolicy getChainedPolicy() {
		return chainedPolicy;
	}

	public void setChainedPolicy(TradeDirectionPolicy chainedPolicy) {
		this.chainedPolicy = chainedPolicy;
	}

}
