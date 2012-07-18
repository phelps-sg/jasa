package net.sourceforge.jasa.agent.strategy;

import java.util.Iterator;

import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.Simulation;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.SimulationTime;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.market.AuctionException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.ShoutsNotVisibleException;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;

public class MockMarket implements Market, Simulation {

	protected MarketQuote quote;
	
	protected double price;
	
	@Override
	public void clear(Order ask, Order bid, double price) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear(Order ask, Order bid, double buyerCharge,
			double sellerPayment, int quantity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean closed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAge() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Auctioneer getAuctioneer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDay() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Order getLastOrder() throws ShoutsNotVisibleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfTraders() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRemainingTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRound() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean orderAccepted(Order shout)
			throws ShoutsNotVisibleException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void placeOrder(Order shout) throws AuctionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeOrder(Order shout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean transactionsOccurred() throws ShoutsNotVisibleException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MarketQuote getQuote() {
		return quote;
	}

	public void setQuote(MarketQuote quote) {
		this.quote = quote;
	}

	@Override
	public double getLastTransactionPrice() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getCurrentPrice() {
		return this.price;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<Agent> getTraderIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(AbstractTradingAgent abstractTradingAgent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Population getPopulation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SimulationController getSimulationController() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SimulationTime getSimulationTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void slow(int slowSleepInterval) {
		// TODO Auto-generated method stub
		
	}
	
}
