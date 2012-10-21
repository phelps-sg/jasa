package net.sourceforge.jasa.agent;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.AgentArrivalEvent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.market.AuctionException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;

public class MarketMakerAgent extends AbstractTradingAgent {

	protected double priceOffset = 10.0;
	
	protected double minMargin = 1.0;
	
	protected Order bid;
	
	protected Order ask;
	
	protected int bidQuantity = 50;
	
	protected int askQuantity = 50;
	
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
	public boolean isBuyer(Market market) {
		return true;
	}

	@Override
	public boolean isSeller(Market market) {
		return true;
	}

	@Override
	public int determineQuantity(Market auction) {
		return bidQuantity + askQuantity;
	}

	@Override
	public void onAgentArrival(Market market, AgentArrivalEvent event) {
		try {
			if (bid != null) {
				market.removeOrder(bid);
			}
			if (ask != null) {
				market.removeOrder(ask);
			}
			double quoteAsk = market.getQuote().getAsk();
			double quoteBid = market.getQuote().getBid();
			double minPrice = Math.max(priceOffset, 
					market.getLastTransactionPrice());
			if (Double.isInfinite(quoteAsk)) {
				quoteAsk = minPrice;
			}
			if (Double.isInfinite(quoteBid)) {
				quoteBid = minPrice + priceOffset;
			}
			logger.debug("quoteBid = " + quoteBid);
			logger.debug("quoteAsk = " + quoteAsk);
			double askPrice = quoteAsk - priceOffset;
			double bidPrice = quoteBid + priceOffset;
			if (askPrice < minPrice) {
				askPrice = minPrice;
			}
			if (bidPrice < minPrice) {
				bidPrice = minPrice;
			}
			if (bidPrice > askPrice - minMargin) { 
				askPrice = bidPrice + minMargin;
			}
			if (bid == null) {
				bid = new Order(this, bidQuantity, bidPrice, true);				
			} else {
				bid.setPrice(bidPrice);
				bid.setQuantity(bidQuantity);
			}
			if (ask == null) {
				ask = new Order(this, askQuantity, askPrice, false);				
			} else {
				ask.setPrice(askPrice);
				ask.setQuantity(askQuantity);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("askPrice = " + askPrice);
				logger.debug("bidPrice = " + bidPrice);
			}
			if (bid.getPrice() <= ask.getPrice()) {
				market.placeOrder(bid);
				market.placeOrder(ask);
			} 
		} catch (AuctionException e) {
//			throw new RuntimeException(e);
			logger.warn(e);
		}
	}

	@Override
	public void orderFilled(Market auction, Order filledOrder, double price,
			int quantity) {
		
//		super.orderFilled(auction, filledOrder, price, quantity);
		
		if (filledOrder == ask) {
			logger.debug("ask filled: " + ask);
//			auction.removeOrder(filledOrder);
//			ask = null;
		}
		if (filledOrder == bid) {
			logger.debug("bid filled: " + bid);
//			auction.removeOrder(filledOrder);
//			bid = null;
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

	public int getBidQuantity() {
		return bidQuantity;
	}

	public void setBidQuantity(int bidQuantity) {
		this.bidQuantity = bidQuantity;
	}

	public int getAskQuantity() {
		return askQuantity;
	}

	public void setAskQuantity(int askQuantity) {
		this.askQuantity = askQuantity;
	}

	
	
}
