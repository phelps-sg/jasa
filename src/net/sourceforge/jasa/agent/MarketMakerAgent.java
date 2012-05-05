package net.sourceforge.jasa.agent;

import org.apache.log4j.Logger;

import net.sourceforge.jabm.EventScheduler;

import net.sourceforge.jabm.event.AgentArrivalEvent;
import net.sourceforge.jasa.event.MarketEvent;

import net.sourceforge.jasa.market.AuctionException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

public class MarketMakerAgent extends AbstractTradingAgent {

	protected double priceOffset = 2.0;
	
	protected Order bid;
	
	protected Order ask;
	
	protected int bidQuantity = 1;
	
	protected int askQuantity = 1;
	
	static Logger logger = Logger.getLogger(MarketMakerAgent.class);
	
	public MarketMakerAgent(EventScheduler scheduler) {
		super(scheduler);
	}
	
	public MarketMakerAgent() {
		this(null);
	}

	@Override
	public boolean active() {
		return true;
	}

	@Override
	public void onAgentArrival(Market market, AgentArrivalEvent event) {
		try {
			double quoteAsk = market.getQuote().getAsk();
			double quoteBid = market.getQuote().getBid();
			if (Double.isInfinite(quoteAsk)) {
				quoteAsk = 0;
			}
			if (Double.isInfinite(quoteBid)) {
				quoteBid = 1000;
			}
			double bidPrice = quoteAsk + priceOffset;
			double askPrice= quoteBid - priceOffset;
			if (askPrice < priceOffset) {
				askPrice = priceOffset;
			}
			if (bid == null) {
				bid = new Order(this, bidQuantity, bidPrice, true);
				market.placeOrder(bid);
			} else {
				bid.setPrice(bidPrice);
			}
			if (ask == null) {
				ask = new Order(this, askQuantity, askPrice, false);
				market.placeOrder(ask);
			} else {
				ask.setPrice(askPrice);
			}
		} catch (AuctionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void orderFilled(Market auction, Order filledOrder, double price,
			int quantity) {
		
//		super.orderFilled(auction, filledOrder, price, quantity);
		
		if (filledOrder == ask) {
			logger.debug("ask filled: " + ask);
			auction.removeOrder(filledOrder);
			ask = null;
		}
		if (filledOrder == bid) {
			logger.debug("bid filled: " + bid);
			auction.removeOrder(filledOrder);
			bid = null;
		}
		
	}

	@Override
	public void onMarketOpen(MarketEvent event) {
		bid = null;
		ask = null;
	}

	public double getPriceOffset() {
		return priceOffset;
	}

	public void setPriceOffset(double priceOffset) {
		this.priceOffset = priceOffset;
	}

	
	
}
