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

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.SimulationTime;

import net.sourceforge.jabm.agent.Agent;

import net.sourceforge.jabm.event.EventListener;
import net.sourceforge.jabm.event.SimEvent;

import net.sourceforge.jabm.init.BasicAgentInitialiser;

import net.sourceforge.jabm.mixing.RandomRobinAgentMixer;

import net.sourceforge.jabm.report.Report;

import net.sourceforge.jabm.spring.BeanFactorySingleton;

import net.sourceforge.jasa.agent.TradingAgent;

import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.OrderReceivedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;

import net.sourceforge.jasa.init.ResetterSimulationInitialiser;

import net.sourceforge.jasa.market.auctioneer.Auctioneer;


import org.apache.log4j.Logger;

import cern.jet.random.engine.RandomEngine;

/**
 * A facade for managing an agent-based market simulation comprising an
 *  simulation controller, a market simulation and an auctioneer agent.
 *   
 * @author Steve Phelps
 * @version $Revision$
 */

public class MarketFacade implements EventScheduler, Market, Serializable,
		Runnable {

	protected Auctioneer auctioneer = null;
	
	protected SimulationController controller;	

	/**
	 * The current trading day (period)
	 */
	protected int day = 0;

	protected double lastTransactionPrice;

	public static final String ERROR_SHOUTSVISIBLE = 
		"Auctioneer does not permit shout inspection";

	static Logger logger = Logger.getLogger(MarketFacade.class);
	
	
	public MarketFacade() {
	}
	
	public MarketFacade(RandomEngine prng, Population traders,
			Auctioneer auctioneer) {
		this.auctioneer = auctioneer;
		controller = new SimulationController();
		MarketSimulation marketSimulation = new MarketSimulation(controller);
		marketSimulation.setMarket(this);
		marketSimulation.setAgentMixer(new RandomRobinAgentMixer(prng));
		marketSimulation.setPopulation(traders);
		marketSimulation.setAgentInitialiser(new BasicAgentInitialiser());
		controller.setSimulation(marketSimulation);
		controller
				.setSimulationInitialiser(new ResetterSimulationInitialiser());
	}
	
	public MarketFacade(RandomEngine prng, Auctioneer auctioneer) {
		this(prng, new Population(prng), auctioneer);			
	}
	
	public MarketFacade(RandomEngine prng) {
		this(prng, null);
	}
	
	public SimulationTime getSimulationTime() {
		return getSimulation().getSimulationTime();
	}
	
	public void clear(Order ask, Order bid, double transactionPrice) {
		assert ask.getQuantity() == bid.getQuantity();
		assert transactionPrice >= ask.getPrice();
		assert transactionPrice <= bid.getPrice();
		lastTransactionPrice = transactionPrice;
		clear(ask, bid, transactionPrice, transactionPrice, ask.getQuantity());
	}

	public void clear(Order ask, Order bid, double buyerCharge,
	    double sellerPayment, int quantity) {

		TradingAgent buyer = (TradingAgent) bid.getAgent();
		TradingAgent seller = (TradingAgent) ask.getAgent();

		TransactionExecutedEvent transactionEvent = new TransactionExecutedEvent(
				getMarket(), getSimulation().getAge(), ask,
				bid, buyerCharge, ask.getQuantity());
		fireEvent(transactionEvent);
		
		auctioneer.getAccount().doubleEntry(buyer.getAccount(), buyerCharge*quantity,
		    seller.getAccount(), sellerPayment*quantity);
		
		seller.getCommodityHolding()
		    .transfer(buyer.getCommodityHolding(), quantity);

		buyer.orderFilled(getMarket(), bid, buyerCharge, quantity);
		seller.orderFilled(getMarket(), ask, sellerPayment, quantity);
	}

	public Market getMarket() {
		return getSimulation().getMarket();
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

	public void beginRound() {
		getSimulation().beginRound();
	}
	
	public void runSingleRound() throws AuctionClosedException {
		getSimulation().runSingleRound();
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
		return getSimulation().getRound();
	}

	public int getAge() {
		return getSimulation().getAge();
	}

	public int getDay() {
		return getSimulation().getDay();
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
		getSimulation().endDay();
	}

	public int getRemainingTime() {
		return getSimulation().getRemainingTime();
	}

	public int getLengthOfDay() {
		return getSimulation().getLengthOfDay();
	}

	public void setLengthOfDay(int lengthOfDay) {
		getSimulation().setLengthOfDay(lengthOfDay);
	}

	public int getMaximumDays() {
		return getSimulation().getMaximumDays();
	}
	
	public void setMaximumDays(int maximumDays) {
		getSimulation().setMaximumDays(maximumDays);
	}

	public void setMaximumRounds(int maximumRounds) {
		getSimulation().setMaximumRounds(maximumRounds);
	}
	
	public int getMaximumRounds() {
		return getSimulation().getMaximumRounds();
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

	public void reset() {
		getSimulation().reset();
	}

	public void setAuctioneer(Auctioneer auctioneer) {
		this.auctioneer = auctioneer;
	}

	public Auctioneer getAuctioneer() {
		return auctioneer;
	}

	public boolean closed() {
		return getSimulation().isClosed();
	}

	/**
	 * Close the market.
	 */
	public void close() {
		getSimulation().close();
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
		fireEvent(new OrderReceivedEvent(this, getSimulation().getRound(), order));
		order.setTimeStamp(controller.getSimulation().getSimulationTime());
		auctioneer.newOrder(order);
		fireEvent(new OrderPlacedEvent(this, getSimulation().getAge(), order));
	}

	public void printState() {
		auctioneer.printState();
	}
	
	public void register(TradingAgent trader) {
		getTraders().add(trader);
		trader.register(this);		
	}

	public void step() throws AuctionClosedException {
		getSimulation().step();
	}
	
	public Iterator<Agent> getTraderIterator() {
		return getTraders().getAgents().iterator();
	}

	@Override
	public void addListener(EventListener listener) {
		controller.addListener(listener);
	}
	
	@Override
	public void removeListener(EventListener listener) {
		controller.removeListener(listener);
	}

	@Override
	public void addListener(Class eventClass, EventListener listener) {
		controller.addListener(eventClass, listener);
	}
	
	public void addReport(Report report) {
		controller.addReport(report);
	}

	public void begin() {
		getSimulation().begin();
	}

	public void fireEvent(SimEvent event) {
		controller.fireEvent(event);
	}	
	
	public void initialise() {
		addListener(auctioneer);
	}

	public double getLastTransactionPrice() {
		return lastTransactionPrice;
	}

	public void setLastTransactionPrice(double lastTransactionPrice) {
		this.lastTransactionPrice = lastTransactionPrice;
	}
	
	@Override
	public double getCurrentPrice() {
		double result = getQuote().getMidPoint();
		if (Double.isNaN(result)) {
			result = getLastTransactionPrice();
		}
		return result;
	}
	
	public void run() {
		initialise();
		controller.run();
	}
	
	public MarketSimulation getSimulation() {
		return (MarketSimulation) controller.getSimulation();
	}
	

	public static void main(String[] args) {
		Runnable market = 
			(Runnable) BeanFactorySingleton.getBean("market");
		logger.info("Starting...");
		market.run();	
		logger.info("all done.");
	}
	
}
