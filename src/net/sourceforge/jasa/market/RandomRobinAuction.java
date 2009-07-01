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
import java.util.LinkedList;

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.event.AgentPolledEvent;
import net.sourceforge.jasa.event.MarketEventListener;
import net.sourceforge.jasa.event.RoundClosedEvent;
import net.sourceforge.jasa.event.RoundClosingEvent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.OrderReceivedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import net.sourceforge.jasa.market.auctioneer.ContinuousDoubleAuctioneer;
import net.sourceforge.jasa.market.rules.AuctionClosingCondition;
import net.sourceforge.jasa.market.rules.CombiTimingCondition;
import net.sourceforge.jasa.market.rules.DayEndingCondition;
import net.sourceforge.jasa.market.rules.MaxDaysAuctionClosingCondition;
import net.sourceforge.jasa.market.rules.MaxRoundsAuctionClosingCondition;
import net.sourceforge.jasa.market.rules.MaxRoundsDayEndingCondition;
import net.sourceforge.jasa.market.rules.NullAuctionClosingCondition;
import net.sourceforge.jasa.market.rules.TimingCondition;
import net.sourceforge.jasa.report.AuctionReport;
import net.sourceforge.jasa.report.ReportVariableBoard;
import net.sourceforge.jasa.sim.Agent;
import net.sourceforge.jasa.sim.Population;
import net.sourceforge.jasa.sim.RandomRobinAgentMixer;
import net.sourceforge.jasa.sim.SimulationController;
import net.sourceforge.jasa.sim.event.EventListener;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.event.SimulationStartingEvent;
import net.sourceforge.jasa.sim.event.SimulationTerminatedEvent;
import net.sourceforge.jasa.sim.init.BasicAgentInitialiser;
import net.sourceforge.jasa.sim.prng.GlobalPRNG;
import net.sourceforge.jasa.sim.report.Report;
import net.sourceforge.jasa.sim.util.Parameterizable;
import net.sourceforge.jasa.sim.util.Resetable;
import net.sourceforge.jasa.view.AuctionConsoleFrame;

import org.apache.log4j.Logger;

import cern.jet.random.engine.RandomEngine;

/**
 * <p>
 * A class representing an market in which RoundRobinTraders can trade by
 * placing shouts in a synchronous round-robin shedule.
 * </p>
 * 
 * <p>
 * TraderAgents are notified that it is their turn to bid by invokation of the
 * requestShout() method on each agent.
 * </p>
 * 
 * <p>
 * This class implements Runnable so auctions can be run as threads, e.g.:
 * </p>
 * 
 * <code>
 *  Thread t = new Thread(market);
 *  t.start();
 * </code><br>
 * 
 * <p>
 * However, this class is not necessarily itself thread-safe.
 * </p>
 * 
 * <p>
 * <b>Parameters </b> <br>
 * </p>
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.maximumrounds</tt><br>
 * <font size=-1>int >= 0 </font></td>
 * <td valign=top>(the number of market rounds)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.maximumdays</tt><br>
 * <font size=-1>int >= 0 </font></td>
 * <td valign=top>(the number of days in the market)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.lengthofday</tt><br>
 * <font size=-1>int >= 0 </font></td>
 * <td valign=top>(the maximum number of rounds in a trading day)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.auctioneer</tt><br>
 * <font size=-1>class, inherits net.sourceforge.jasa.market.Auctioneer </font></td>
 * <td valign=top>(the market protocol to use)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.logger</tt><br>
 * <font size=-1>class, inherits net.sourceforge.jasa.report.MarketDataLogger
 * </font></td>
 * <td valign=top>(the MarketDataLogger to use)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.stats</tt><br>
 * <font size=-1>class, inherits net.sourceforge.jasa.report.MarketStats </font>
 * </td>
 * <td valign=top>(the MarketStats to use)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.name</tt><br>
 * <font size=-1>string </font></td>
 * <td valign=top>(the name of this market)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.agenttype.</tt> <i>n </i> <br>
 * <font size=-1>int </font></td>
 * <td valign=top>(the number of different agent types)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.agenttype.</tt> <i>i </i> <br>
 * <font size=-1>classname, inherits net.sourceforge.jasa.agent.RoundRobinTrader
 * </font></td>
 * <td valign=top>(the class for agent type # <i>i </i>)</td>
 * </tr>
 * 
 * </table>
 * 
 * 
 * @see net.sourceforge.jasa.agent.TradingAgent
 * 
 * @author Steve Phelps
 * @version $Revision$
 * 
 */

public class RandomRobinAuction  implements Market, Serializable, Runnable {

	/**
	 * The plugable market rules to use for this market, e.g.
	 * AscendingAuctioneer.
	 */
	protected Auctioneer auctioneer = null;
	
	protected MarketSimulation marketSimulation;
	
	protected SimulationController controller;
	
	protected Population traders;

	protected Account account = new Account();

	/**
	 * Optional graphical console
	 */
	protected AuctionConsoleFrame guiConsole = null;

	/**
	 * The current trading day (period)
	 */
	protected int day = 0;


	public static final String ERROR_SHOUTSVISIBLE = "Auctioneer does not permit shout inspection";

	static Logger logger = Logger.getLogger(RandomRobinAuction.class);

	public RandomRobinAuction(RandomEngine prng, Auctioneer auctioneer) {
		traders = new Population(prng);
		controller = new SimulationController(new BasicAgentInitialiser(), traders);
		controller.setAgentMixer(new RandomRobinAgentMixer(prng));
		marketSimulation = new MarketSimulation(controller, this);
		this.auctioneer = auctioneer;		
	}
	
	public RandomRobinAuction(RandomEngine prng) {
		this(prng, new ContinuousDoubleAuctioneer());
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

		assert buyer.isBuyer(getMarket());
		assert seller.isSeller(getMarket());

		TransactionExecutedEvent transactionEvent = new TransactionExecutedEvent(
		    marketSimulation.getMarket(), marketSimulation.getRound(), ask, bid, buyerCharge, ask.getQuantity());
		fireEvent(transactionEvent);

		auctioneer.getAccount().doubleEntry(buyer.getAccount(), buyerCharge,
		    seller.getAccount(), sellerPayment);
		seller.getCommodityHolding()
		    .transfer(buyer.getCommodityHolding(), quantity);

		buyer.shoutAccepted(getMarket(), bid, buyerCharge, quantity);
		seller.shoutAccepted(getMarket(), ask, sellerPayment, quantity);

	}

	public Market getMarket() {
		return marketSimulation.getMarket();
	}
	
	/**
	 * Determines whether or not the given shout was matched in the current round
	 * of trading.
	 */
	public boolean orderAccepted(Order shout) throws ShoutsNotVisibleException {

		return auctioneer.shoutAccepted(shout);
	}

	/**
	 * Determines whether or not any transactions have occured in the current
	 * round of trading.
	 */
	public boolean transactionsOccured() throws ShoutsNotVisibleException {
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
		return traders.getAgentList().size();
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

	public void reset() {
		marketSimulation.reset();
	}

	public void setAuctioneer(Auctioneer auctioneer) {
		this.auctioneer = auctioneer;
		auctioneer.setAuction(getMarket());
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
	public void placeOrder(Order shout) throws AuctionException {
		if (closed()) {
			throw new AuctionClosedException("Auction is closed.");
		}
		if (shout == null) {
			throw new IllegalShoutException("null shout");
		}
		fireEvent(new OrderReceivedEvent(this, marketSimulation.getRound(), shout));
		auctioneer.newShout(shout);
		fireEvent(new OrderPlacedEvent(this, marketSimulation.getRound(), shout));

		// notifyObservers();
	}

	public void printState() {
		auctioneer.printState();
	}

	public void initialiseAgents() {
		marketSimulation.initialiseAgents();
	}
	
	public void register(TradingAgent trader) {
		traders.add(trader);
		trader.register(this);
	}

	public void run() {
		controller.setListeners();
		marketSimulation.run();
	}

	public void step() throws AuctionClosedException {
		marketSimulation.step();
	}
	
	public Iterator<Agent> getTraderIterator() {
		return traders.getAgents().iterator();
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
}
