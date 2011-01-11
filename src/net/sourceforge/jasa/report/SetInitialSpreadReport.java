package net.sourceforge.jasa.report;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.agent.TradingAgent;

import net.sourceforge.jasa.event.EndOfDayEvent;
import net.sourceforge.jasa.event.MarketOpenEvent;

import net.sourceforge.jasa.market.AuctionException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;


/**
 * This class provides a mechanism for initialising the quote
 * prior to actual trading.  It allows the initial price of the
 * market to be set without having to simulate a batch auction.
 * 
 * @author Steve Phelps
 */
public class SetInitialSpreadReport extends AbstractAuctionReport {

	protected Market market;
	
	protected double bidPrice;
	
	protected double askPrice;
	
	protected TradingAgent tradingAgent;
	
//	@Override
//	public Map<Object, Number> getVariables() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public void eventOccurred(SimEvent event) {
		if (event instanceof MarketOpenEvent) {
			onMarketOpen((MarketOpenEvent) event);
		} else if (event instanceof EndOfDayEvent) {
			onEndOfDay((EndOfDayEvent) event);
		}
	}

	public void onEndOfDay(EndOfDayEvent event) {
//		initialiseSpread();
	}

	public void onMarketOpen(MarketOpenEvent event) {
		initialiseSpread();
	}
	
	public void initialiseSpread() {
		try {
			market.placeOrder(new Order(tradingAgent, 1, bidPrice, false));
			market.placeOrder(new Order(tradingAgent, 1, askPrice, true));
		} catch (AuctionException e) {
			logger.debug(e);
		}
	}

	public Market getMarket() {
		return market;
	}

	public void setMarket(Market market) {
		this.market = market;
	}

	public double getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(double bidPrice) {
		this.bidPrice = bidPrice;
	}

	public double getAskPrice() {
		return askPrice;
	}

	public void setAskPrice(double askPrice) {
		this.askPrice = askPrice;
	}

	public TradingAgent getTradingAgent() {
		return tradingAgent;
	}

	public void setTradingAgent(TradingAgent tradingAgent) {
		this.tradingAgent = tradingAgent;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	
}
