package net.sourceforge.jasa.market.rules;

import net.sourceforge.jasa.market.auctioneer.AbstractAuctioneer;

public class NoQueueClearingPolicy implements ClearingPolicy {

	protected ClearingPolicy defaultClearingPolicy;
	
	protected AbstractAuctioneer auctioneer;
	
	public NoQueueClearingPolicy(ClearingPolicy defaultClearingPolicy,
			AbstractAuctioneer auctioneer) {
		super();
		this.defaultClearingPolicy = defaultClearingPolicy;
		this.auctioneer = auctioneer;
	}
	
	public NoQueueClearingPolicy(AbstractAuctioneer auctioneer) {
		super();
		this.auctioneer = auctioneer;
		this.defaultClearingPolicy = new EquilibriumClearingPolicy(auctioneer);
	}

	@Override
	public void clear() {
		defaultClearingPolicy.clear();
		auctioneer.getOrderBook().reset();
	}

}
