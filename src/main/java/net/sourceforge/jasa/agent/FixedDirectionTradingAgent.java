package net.sourceforge.jasa.agent;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jasa.agent.strategy.FixedDirectionStrategy;

public class FixedDirectionTradingAgent extends AbstractTradingAgent {

	public FixedDirectionTradingAgent(EventScheduler scheduler) {
		super(scheduler);
	}

	public FixedDirectionTradingAgent(int stock, double funds,
			double privateValue, EventScheduler scheduler) {
		super(stock, funds, privateValue, scheduler);
	}

	public FixedDirectionTradingAgent(int stock, double funds,
			double privateValue, TradingStrategy strategy,
			EventScheduler scheduler) {
		super(stock, funds, privateValue, strategy, scheduler);
	}

	public FixedDirectionTradingAgent(int stock, double funds,
			EventScheduler scheduler) {
		super(stock, funds, scheduler);
	}

	public FixedDirectionTradingAgent(double privateValue,
			EventScheduler scheduler) {
		this(0, 0, privateValue, null, scheduler);
	}

	@Override
	public boolean active() {
		return true;
	}
	
	@Override
	public FixedDirectionStrategy getStrategy() {
		return (FixedDirectionStrategy) super.getStrategy();
	}
	
	public void setStrategy(FixedDirectionStrategy strategy) {
		super.setStrategy(strategy);
	}

	public void setIsBuyer(boolean isBuyer) {
		getStrategy().setBuy(isBuyer);
	}
	
	public boolean isBuyer() {
		return getStrategy().isBuy();
	}
	
	public boolean isSeller() {
		return !isBuyer();
	}
	
	public void setIsSeller(boolean isSeller) {
		getStrategy().setBuy(!isSeller);
	}
}
