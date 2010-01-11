package net.sourceforge.jasa.agent.strategy;

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

public abstract class FixedDirectionStrategy extends FixedQuantityStrategyImpl {

	protected boolean isBuy;
	
	public FixedDirectionStrategy() {
		super();
	}

	public FixedDirectionStrategy(AbstractTradingAgent agent) {
		super(agent);
	}

	public boolean modifyShout(Order shout) {
		shout.setIsBid(isBuy);
		return super.modifyShout(shout);
	}

	public boolean isBuy() {
		return isBuy;
	}
	
	public boolean isSell() {
		return !isBuy();
	}

	public void setBuy(boolean isBuy) {
		this.isBuy = isBuy;
	}
	
	
}
