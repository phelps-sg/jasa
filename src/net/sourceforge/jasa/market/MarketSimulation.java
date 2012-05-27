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

import net.sourceforge.jabm.AbstractSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.SimulationTime;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.event.EventListener;
import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.RoundStartingEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jabm.report.Report;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.event.EndOfDayEvent;
import net.sourceforge.jasa.event.MarketClosedEvent;
import net.sourceforge.jasa.event.MarketOpenEvent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.OrderReceivedEvent;
import net.sourceforge.jasa.event.RoundClosingEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import net.sourceforge.jasa.market.rules.AuctionClosingCondition;
import net.sourceforge.jasa.market.rules.CombiTimingCondition;
import net.sourceforge.jasa.market.rules.DayEndingCondition;
import net.sourceforge.jasa.market.rules.MaxDaysAuctionClosingCondition;
import net.sourceforge.jasa.market.rules.MaxRoundsAuctionClosingCondition;
import net.sourceforge.jasa.market.rules.MaxRoundsDayEndingCondition;
import net.sourceforge.jasa.market.rules.NullAuctionClosingCondition;
import net.sourceforge.jasa.market.rules.TimingCondition;

import org.apache.log4j.Logger;

import com.sun.org.apache.xml.internal.utils.UnImplNode;

/**
 * A simulation of an order-driven market.
 * 
 * @author Steve Phelps
 * @version $Revision$
 * 
 */
public class MarketSimulation extends AbstractSimulation 
		implements Serializable, Market {

	protected Auctioneer auctioneer = null;
	
//	protected Market market;
	
	protected boolean closed = false;
	
	/**
	 * The current round.
	 */
	protected int round;

	protected int age = 0;

	protected Account account = new Account();

	/**
	 * The current trading day (period)
	 */
	protected int day = 0;

	protected TimingCondition closingCondition 
		= new NullAuctionClosingCondition();

	protected TimingCondition dayEndingCondition;

	protected boolean endOfRound = false;

	protected double lastTransactionPrice;

	public static final String ERROR_SHOUTSVISIBLE 
	= "Auctioneer does not permit shout inspection";

	static Logger logger = Logger.getLogger(MarketSimulation.class);


	public MarketSimulation(SimulationController controller) {
		super(controller);
		initialiseCounters();
	}
	
	public MarketSimulation() {
		this(null);
	}
	
	public void initialiseCounters() {
		day = 0;
		round = 0;
		endOfRound = false;
		age = 0;
		closed = false;	
	}
	
	public void initialise() {
		initialiseCounters();
		addListener(auctioneer);
	}
	
	public void reset() {
		initialiseCounters();
	}
	
	public void informAuctionClosed() {
		fireEvent(new MarketClosedEvent(this, getRound()));
	}

	public void informEndOfDay() {
		fireEvent(new EndOfDayEvent(this, getRound()));
	}

//	public void informBeginOfDay() {
//		fireEvent(new DayOpeningEvent(market, getRound()));
//	}

	public void informAuctionOpen() {
		fireEvent(new MarketOpenEvent(this, getRound()));
	}
	
	public void informRoundOpening() {
		fireEvent(new RoundStartingEvent(this));
	}

	public void beginRound() {
		if (closingCondition.eval()) {
			close();
		} else {			
			endOfRound = false;
			informRoundOpening();
		}
	}

	/**
	 * Get the current round number
	 */
	public int getRound() {
		return round;
	}

	public int getAge() {
		return age;
	}

	public int getDay() {
		return day;
	}
	
	public Auctioneer getAuctioneer() {
		return auctioneer;
	}

	/**
	 * Get the last bid placed in the market.
	 */
	public Order getLastBid() throws ShoutsNotVisibleException {
		return getAuctioneer().getLastBid();
	}

	/**
	 * Get the last ask placed in the market.
	 */
	public Order getLastAsk() throws ShoutsNotVisibleException {
		return getAuctioneer().getLastAsk();
	}

	/**
	 * Runs the market.
	 */
	public void run() {

		initialise();
		
		if (getAuctioneer() == null) {
			throw new AuctionRuntimeException(
			    "No auctioneer has been assigned");
		}

		begin();

		try {
			while (!closed) {
				step();
			}

		} catch (AuctionClosedException e) {
			throw new AuctionRuntimeException(e);
		}

		end();
	}

	public void begin() {
		initialiseAgents();
		reset();
		fireEvent(new SimulationStartingEvent(this));
		informAuctionOpen();
	}

	public void end() {
		informAuctionClosed();
		fireEvent(new SimulationFinishedEvent(this));
	}

	public void step() throws AuctionClosedException {
		try {
			super.step();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		runSingleRound();
	}

	public void endRound() {
		informRoundClosing();

//		getAuctioneer().endOfRoundProcessing();

		endOfRound = true;
		round++;
		age++;

		informRoundClosed();
		checkEndOfDay();
	}
	
	public boolean isClosed() {
		return closed;
	}

	public void runSingleRound() throws AuctionClosedException {
		if (isClosed()) {
			throw new AuctionClosedException("Auction is closed.");
		}
		if (closingCondition.eval()) {
			close();
		} else {
			beginRound();
			invokeAgentInteractions();		
			endRound();
		}
	}

	public void informRoundClosing() {
		fireEvent(new RoundClosingEvent(this, getAge()));
	}

	public void informRoundClosed() {
		fireEvent(new RoundFinishedEvent(this));
	}

//	public void placeOrder(Order shout) throws AuctionException {
//
//		// TODO: to switch the following two lines?
//
//		fireEvent(new OrderReceivedEvent(market, round, shout));
//		market.placeOrder(shout);
//		fireEvent(new OrderPlacedEvent(market, round, shout));
//
////		setChanged();
////		notifyObservers();
//	}
//
//	public void changeShout(Order shout) throws AuctionException {
//		removeOrder(shout);
//		placeOrder(shout);
//	}
//	
//	public void removeOrder(Order shout) {
//		// Remove this shout and all of its children.
//		for (Order s = shout; s != null; s = s.getChild()) {
//			getAuctioneer().removeShout(s);
//			// if ( s != shout ) {
//			// ShoutPool.release(s);
//			// }
//		}
//		shout.makeChildless();
//	}

	/**
	 * Return an iterator iterating over all traders registered (as opposed to
	 * actively trading) in the market.
	 */
//	public Iterator getTraderIterator() {
////		return registeredTraders.iterator();
//		return null;
//	}

//	public Iterator getActiveTraderIterator() {
////		return activeTraders.iterator();
//		return null;
//	}
//
//	protected void initialise() {
//		round = 0;
//		day = 0;
//		age = 0;
//	}

	protected void checkEndOfDay() {
		if (dayEndingCondition != null && dayEndingCondition.eval())
			endDay();
	}
	
	public void close() {
		closed = true;
	}

	/**
	 * Terminate the current trading period (day)
	 */
	protected void endDay() {
		logger.debug("endDay()");
		// report.debug("day = " + day + " of " + getMaximumDays());
		round = 0;
		informEndOfDay();
//		getAuctioneer().endOfDayProcessing();
		day++;
	}

	public int getRemainingTime() {
		TimingCondition cond = getAuctionClosingCondition(MaxRoundsAuctionClosingCondition.class);

		if (cond != null) {
			return ((MaxRoundsAuctionClosingCondition) cond).getRemainingRounds();
		} else {
			cond = getDayEndingCondition(MaxRoundsDayEndingCondition.class);

			if (cond != null) {
				return ((MaxRoundsDayEndingCondition) cond).getRemainingRounds();
			} else {
				throw new AuctionRuntimeException(
				    getClass()
				        + " requires a TimingCondition knowing remaining time in the market to be configured");
			}
		}
	}

	public int getLengthOfDay() {
		TimingCondition cond = getDayEndingCondition(MaxRoundsDayEndingCondition.class);

		if (cond != null) {
			return ((MaxRoundsDayEndingCondition) cond).getLengthOfDay();
		} else {
			return -1;
		}
	}

	public void setLengthOfDay(int lengthOfDay) {
		MaxRoundsDayEndingCondition cond = new MaxRoundsDayEndingCondition(this);
		cond.setLengthOfDay(lengthOfDay);
		setDayEndingCondition(cond);
	}

	public int getMaximumDays() {
		TimingCondition cond = getAuctionClosingCondition(MaxDaysAuctionClosingCondition.class);

		if (cond != null) {
			return ((MaxDaysAuctionClosingCondition) cond).getMaximumDays();
		} else {
			return -1;
		}
	}

	public void setMaximumRounds(int maximumRounds) {
		MaxRoundsAuctionClosingCondition cond = 
			new MaxRoundsAuctionClosingCondition(this);
		cond.setMaximumRounds(maximumRounds);
		setAuctionClosingCondition(cond);
	}

	public int getMaximumRounds() {
		TimingCondition cond = 
			getAuctionClosingCondition(MaxRoundsAuctionClosingCondition.class);

		if (cond != null) {
			return ((MaxRoundsAuctionClosingCondition) cond).getMaximumRounds();
		} else {
			return -1;
		}
	}

	public void setMaximumDays(int maximumDays) {
		MaxDaysAuctionClosingCondition cond = new MaxDaysAuctionClosingCondition(
		    this);
		cond.setMaximumDays(maximumDays);
		setAuctionClosingCondition(cond);
	}

	@SuppressWarnings("rawtypes")
	protected TimingCondition getTimingCondition(TimingCondition cond,
	    Class conditionClass) {
		if (cond != null) {
			if (cond.getClass().equals(conditionClass)) {
				return cond;
			} else if (cond instanceof CombiTimingCondition) {
				Iterator i = ((CombiTimingCondition) cond).conditionIterator();
				while (i.hasNext()) {
					TimingCondition c = (TimingCondition) i.next();
					if (c.getClass().equals(conditionClass)) {
						return c;
					}
				}
			}
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	public TimingCondition getAuctionClosingCondition(Class conditionClass) {
		return getTimingCondition(closingCondition, conditionClass);
	}

	@SuppressWarnings("rawtypes")
	public TimingCondition getDayEndingCondition(Class conditionClass) {
		return getTimingCondition(dayEndingCondition, conditionClass);
	}

	public void setAuctionClosingCondition(TimingCondition cond) {
		assert (cond instanceof AuctionClosingCondition);
		closingCondition = cond;
	}

	public void setDayEndingCondition(TimingCondition cond) {
		assert (cond instanceof DayEndingCondition);
		dayEndingCondition = cond;
	}
//
//	public Market getMarket() {
//		return market;
//	}
//
//	public void setMarket(Market market) {
//		this.market = market;
//	}

	@Override
	public SimulationTime getSimulationTime() {
		return new SimulationTime(age);
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
				this, getAge(), ask,
				bid, buyerCharge, ask.getQuantity());
		fireEvent(transactionEvent);
		
		auctioneer.getAccount().doubleEntry(buyer.getAccount(), buyerCharge*quantity,
		    seller.getAccount(), sellerPayment*quantity);
		
		seller.getCommodityHolding()
		    .transfer(buyer.getCommodityHolding(), quantity);

		buyer.orderFilled(this, bid, buyerCharge, quantity);
		seller.orderFilled(this, ask, sellerPayment, quantity);
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

	protected void activate(TradingAgent agent) {
//		activeTraders.add(agent);
//		addAuctionEventListener(agent);
	}


//
//	public SimulationController getController() {
//		return controller;
//	}
//
//	public void setController(SimulationController controller) {
//		this.controller = controller;
//	}

	public Population getTraders() {
		return getPopulation();
	}
	
	

//	public void reset() {
//		getSimulation().reset();
//		this.lastTransactionPrice = 0.0;
//	}

	public void setAuctioneer(Auctioneer auctioneer) {
		this.auctioneer = auctioneer;
		this.auctioneer.setMarket(this);
	}

	public boolean closed() {
		return isClosed();
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
	 * Submit a new order to the market.
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
		fireEvent(new OrderReceivedEvent(this, getRound(), order));
		order.setTimeStamp(getSimulationTime());
		auctioneer.newOrder(order);
		fireEvent(new OrderPlacedEvent(this, getAge(), order));
	}

	public void printState() {
		auctioneer.printState();
	}
	
	public void register(TradingAgent trader) {
		getTraders().add(trader);
		trader.register(this);		
	}

	public Iterator<Agent> getTraderIterator() {
		return getTraders().getAgents().iterator();
	}

//	@Override
//	public void addListener(EventListener listener) {
//		controller.addListener(listener);
//	}
	
//	@Override
//	public void removeListener(EventListener listener) {
//		controller.removeListener(listener);
//	}
//
//	@Override
//	@SuppressWarnings("rawtypes")
//	public void addListener(Class eventClass, EventListener listener) {
//		controller.addListener(eventClass, listener);
//	}
	
//	public void addReport(Report report) {
//		controller.addReport(report);
//	}
//
//	public void fireEvent(SimEvent event) {
//		controller.fireEvent(event);
//	}	
	
//	public void initialise() {
//		addListener(auctioneer);
//	}

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

	@Override
	public void remove(AbstractTradingAgent abstractTradingAgent) {
		//TODO
//		throw new RuntimeException("method not implemented");
	}

	
	
}
