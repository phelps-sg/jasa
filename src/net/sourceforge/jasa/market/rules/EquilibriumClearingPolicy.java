package net.sourceforge.jasa.market.rules;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.OrderBook;
import net.sourceforge.jasa.market.auctioneer.AbstractAuctioneer;

public class EquilibriumClearingPolicy implements Serializable, ClearingPolicy {

	protected AbstractAuctioneer auctioneer;
	
	public EquilibriumClearingPolicy(AbstractAuctioneer auctioneer) {
		super();
		this.auctioneer = auctioneer;
	}

	public void clear() {
		MarketQuote clearingQuote = 
			new MarketQuote(auctioneer.askQuote(), auctioneer.bidQuote());
		auctioneer.setClearingQuote(clearingQuote);
		OrderBook orderBook = auctioneer.getOrderBook();
		List<Order> matchedOrders = orderBook.matchOrders();
		Iterator<Order> i = matchedOrders.iterator();
		while (i.hasNext()) {
			Order bid = i.next();
			Order ask = i.next();
			double price = auctioneer.determineClearingPrice(bid, ask);
			auctioneer.clear(ask, bid, price);
		}
	}

	public AbstractAuctioneer getAuctioneer() {
		return auctioneer;
	}

	public void setAuctioneer(AbstractAuctioneer auctioneer) {
		this.auctioneer = auctioneer;
	}

	
}
