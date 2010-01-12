/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2009 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package net.sourceforge.jasa.market;

import java.io.Serializable;
import java.util.Iterator;

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.OrderReceivedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;

import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import net.sourceforge.jasa.market.auctioneer.ContinuousDoubleAuctioneer;

import net.sourceforge.jasa.sim.Agent;
import net.sourceforge.jasa.sim.EventScheduler;
import net.sourceforge.jasa.sim.Population;
import net.sourceforge.jasa.sim.RandomRobinAgentMixer;
import net.sourceforge.jasa.sim.SimulationController;
import net.sourceforge.jasa.sim.event.EventListener;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.init.BasicAgentInitialiser;
import net.sourceforge.jasa.sim.report.Report;
import net.sourceforge.jasa.sim.util.BeanFactorySingleton;

import org.apache.log4j.Logger;

import cern.jet.random.engine.RandomEngine;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class MarketFacade implements EventScheduler, Market, Serializable,
		Runnable {

	protected Auctioneer auctioneer = null;
	
	protected MarketSimulation marketSimulation;
	
	protected SimulationController controller;	

	/**
	 * The current trading day (period)
	 */
	protected int day = 0;

	public static final String ERROR_SHOUTSVISIBLE = "Auctioneer does not permit shout inspection";

	static Logger logger = Logger.getLogger(MarketFacade.class);
	
	
	public MarketFacade() {
	}
	
	public MarketFacade(RandomEngine prng, Population traders, Auctioneer auctioneer) {
		this.auctioneer = auctioneer;
		controller = new SimulationController(new BasicAgentInitialiser(), traders);
		controller.setAgentMixer(new RandomRobinAgentMixer(prng));
		controller.setPopulation(traders);
		marketSimulation = new MarketSimulation(controller);
		marketSimulation.setMarket(this);
		controller.setSimulation(marketSimulation);
	}
	
//	public MarketFacade(MarketSimulation marketSimulation, Auctioneer auctioneer) {
//		this.auctioneer = auctioneer;
//		this.marketSimulation = marketSimulation;
//	}
	
	public MarketFacade(RandomEngine prng, Auctioneer auctioneer) {
		this(prng, new Population(prng), auctioneer);			
	}
	
	public MarketFacade(RandomEngine prng) {
		this(prng, null);
	}
	
	public void clear(Order ask, Order bid, double transactionPrice) {
		assert ask.getQuantity() == bid.getQuantity();
		assert transactionPrice >= ask.getPrice();
		assert transactionPrice <= bid.getPrice();
		clear(ask, bid, transactionPrice, transactionPrice, ask.getQuantity());
	}

	public void clear(Order ask, Order bid, double buyerCharge,
	    double sellerPayment, int quantity) {

		TradingAgent buyer = (TradingAgent) bid.getAgent();
		TradingAgent seller = (TradingAgent) ask.getAgent();

//		assert buyer.isBuyer(getMarket());
//		assert seller.isSeller(getMarket());

		TransactionExecutedEvent transactionEvent = new TransactionExecutedEvent(
				marketSimulation.getMarket(), marketSimulation.getRound(), ask,
				bid, buyerCharge, ask.getQuantity());
		fireEvent(transactionEvent);
		
//		System.out.println(transactionEvent);

		auctioneer.getAccount().doubleEntry(buyer.getAccount(), buyerCharge,
		    seller.getAccount(), sellerPayment);
		seller.getCommodityHolding()
		    .transfer(buyer.getCommodityHolding(), quantity);

		buyer.orderFilled(getMarket(), bid, buyerCharge, quantity);
		seller.orderFilled(getMarket(), ask, sellerPayment, quantity);
	}

	public Market getMarket() {
		return marketSimulation.getMarket();
	}
	
	/**
	 * Determines whether or not the given shout was matched in the current round
	 * of trading.
	 */
	public boolean orderAccepted(Order shout) throws ShoutsNotVisibleException {
		return auctioneer.orderFilled(shout);
	}

	/**
	 * Determines whether or not any transactions have occured in the current
	 * round of trading.
	 */
	public boolean transactionsOccurred() throws ShoutsNotVisibleException {
		return auctioneer.transactionsOccurred();
	}


	/**
	 * Remove a trader from the market.
	 */
	public void remove(TradingAgent trader) {
//		if (!defunctTraders.contains(trader)) {
//			defunctTraders.add(trader);
//		}
	}

	/**
	 * Invokes the requestShout() method on each trader in the market, giving
	 * each trader the opportunity to bid in the market.
	 */
//	public void requestShouts() {
//		Iterator i = activeTraders.iterator();
//		while (i.hasNext()) {
//			TradingAgent trader = (TradingAgent) i.next();
//			requestShout(trader);
//		}
//	}

	public void beginRound() {
		marketSimulation.beginRound();
	}
	
	public void runSingleRound() throws AuctionClosedException {
		marketSimulation.runSingleRound();
	}

	/**
	 * Return the number of traders currently active in the market.
	 */
	public int getNumberOfTraders() {
		return getTraders().getAgentList().size();
	}

	/**
	 * Return the total number of traders registered in the market.
	 */
	public int getNumberOfRegisteredTraders() {
		return getNumberOfTraders();
	}

	/**
	 * Get the current round number
	 */
	public int getRound() {
		return marketSimulation.getRound();
	}

	public int getAge() {
		return marketSimulation.getAge();
	}

	public int getDay() {
		return marketSimulation.getDay();
	}

	/**
	 * Get the last bid placed in the market.
	 */
	public Order getLastBid() throws ShoutsNotVisibleException {
		return auctioneer.getLastBid();
	}

	/**
	 * Get the last ask placed in the market.
	 */
	public Order getLastAsk() throws ShoutsNotVisibleException {
		return auctioneer.getLastAsk();
	}
	protected void activate(TradingAgent agent) {
//		activeTraders.add(agent);
//		addAuctionEventListener(agent);
	}

	/**
	 * Terminate the current trading period (day)
	 */
	protected void endDay() {
		marketSimulation.endDay();
	}

	public int getRemainingTime() {
		return marketSimulation.getRemainingTime();
	}

	public int getLengthOfDay() {
		return marketSimulation.getLengthOfDay();
	}

	public void setLengthOfDay(int lengthOfDay) {
		marketSimulation.setLengthOfDay(lengthOfDay);
	}

	public int getMaximumDays() {
		return marketSimulation.getMaximumDays();
	}
	
	public void setMaximumDays(int maximumDays) {
		marketSimulation.setMaximumDays(maximumDays);
	}

	public void setMaximumRounds(int maximumRounds) {
		marketSimulation.setMaximumRounds(maximumRounds);
	}
	
	public int getMaximumRounds() {
		return marketSimulation.getMaximumRounds();
	}

	public MarketSimulation getMarketSimulation() {
		return marketSimulation;
	}

	public void setMarketSimulation(MarketSimulation marketSimulation) {
		this.marketSimulation = marketSimulation;
	}

	public SimulationController getController() {
		return controller;
	}

	public void setController(SimulationController controller) {
		this.controller = controller;
	}

	public Population getTraders() {
		return controller.getPopulation();
	}

	public void setTraders(Population traders) {
		controller.setPopulation(traders);
	}

	public void reset() {
		marketSimulation.reset();
	}

	public void setAuctioneer(Auctioneer auctioneer) {
		this.auctioneer = auctioneer;
//		addListener(auctioneer);
//		auctioneer.setAuction(getMarket());
	}

	public Auctioneer getAuctioneer() {
		return auctioneer;
	}

	public boolean closed() {
		return marketSimulation.isClosed();
	}

	/**
	 * Close the market.
	 */
	public void close() {
		marketSimulation.close();
	}

	public Order getLastOrder() throws ShoutsNotVisibleException {
		// if ( !auctioneer.shoutsVisible() ) {
		// throw new ShoutsNotVisibleException();
		// }
		// return lastShout;
		return auctioneer.getLastShout();
	}


	public MarketQuote getQuote() {
		return auctioneer.getQuote();
	}


	public void removeOrder(Order shout) {
		// Remove this shout and all of its children.
		for (Order s = shout; s != null; s = s.getChild()) {
			auctioneer.removeShout(s);
			// if ( s != shout ) {
			// ShoutPool.release(s);
			// }
		}
		shout.makeChildless();
	}
	
	/**
	 * Handle a new shout in the market.
	 * 
	 * @param shout
	 *          The new shout in the market.
	 */
	public void placeOrder(Order order) throws AuctionException {
		if (closed()) {
			throw new AuctionClosedException("Auction is closed.");
		}
		if (order == null) {
			throw new IllegalOrderException("null shout");
		}
		fireEvent(new OrderReceivedEvent(this, marketSimulation.getRound(), order));
		order.setTimeStamp(controller.getSimulation().getSimulationTime());
		auctioneer.newOrder(order);
//		System.out.println(order);
		fireEvent(new OrderPlacedEvent(this, marketSimulation.getRound(), order));

		// notifyObservers();
	}

	public void printState() {
		auctioneer.printState();
	}

//	public void initialise() {
//		marketSimulation.initialiseAgents();
//		marketSimulation.addListener(auctioneer);
//		controller.addListener(auctioneer);
//	}
	
	public void register(TradingAgent trader) {
		getTraders().add(trader);
		trader.register(this);
	}

//	public void run() {
//		initialise();
//		controller.run();
////		marketSimulation.run();
//	}

	public void step() throws AuctionClosedException {
		marketSimulation.step();
	}
	
	public Iterator<Agent> getTraderIterator() {
		return getTraders().getAgents().iterator();
	}

	public void addListener(EventListener listener) {
		marketSimulation.addListener(listener);
	}
	
	public void addReport(Report report) {
		controller.addReport(report);
	}

	public void begin() {
		marketSimulation.begin();
	}

	public void fireEvent(SimEvent event) {
		controller.fireEvent(event);
	}
	
	public static void main(String[] args) {
		Runnable market = 
			(Runnable) BeanFactorySingleton.getBean("market");
		logger.info("Starting...");
		market.run();	
		logger.info("done.");
	}
	
	public void initialise() {
		addListener(auctioneer);
	}

	public void run() {
		initialise();
		controller.run();
	}
}
