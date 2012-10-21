package net.sourceforge.jasa.agent.strategy;

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.market.Order;

public abstract class FixedDirectionStrategy extends FixedQuantityStrategyImpl {

	public FixedDirectionStrategy() {
		super();
	}

	public FixedDirectionStrategy(AbstractTradingAgent agent) {
		super(agent);
	}

	public boolean modifyShout(Order shout) {
		return super.modifyShout(shout);
	}

	public boolean isBuy() {
		return tradeDirectionPolicy.isBuy(this.auction, getAgent());
	}
	
	public boolean isSell() {
		return !isBuy();
	}

	public void setBuy(boolean isBuy) {
		this.tradeDirectionPolicy = new FixedTradeDirectionPolicy();
		((FixedTradeDirectionPolicy) tradeDirectionPolicy).setBuy(isBuy);
	}
	
}
