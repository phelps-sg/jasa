package net.sourceforge.jasa.report;

import java.util.Map;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.AuctionException;
import net.sourceforge.jasa.market.AuctionRuntimeException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.event.SimulationStartingEvent;

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
		if (event instanceof SimulationStartingEvent) {
			onSimulationStarting((SimulationStartingEvent) event);
		}
	}

	public void onSimulationStarting(SimulationStartingEvent event) {
		try {
			market.placeOrder(new Order(tradingAgent, 1, bidPrice, true));
			market.placeOrder(new Order(tradingAgent, 1, askPrice, false));
		} catch (AuctionException e) {
			throw new AuctionRuntimeException(e);
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
