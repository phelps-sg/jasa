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

import net.sourceforge.jasa.event.DayOpeningEvent;
import net.sourceforge.jasa.event.EndOfDayEvent;
import net.sourceforge.jasa.event.MarketClosedEvent;
import net.sourceforge.jasa.event.MarketOpenEvent;
import net.sourceforge.jasa.event.RoundClosedEvent;
import net.sourceforge.jasa.event.RoundClosingEvent;

import net.sourceforge.jasa.market.auctioneer.Auctioneer;

import net.sourceforge.jasa.market.rules.AuctionClosingCondition;
import net.sourceforge.jasa.market.rules.CombiTimingCondition;
import net.sourceforge.jasa.market.rules.DayEndingCondition;
import net.sourceforge.jasa.market.rules.MaxDaysAuctionClosingCondition;
import net.sourceforge.jasa.market.rules.MaxRoundsAuctionClosingCondition;
import net.sourceforge.jasa.market.rules.MaxRoundsDayEndingCondition;
import net.sourceforge.jasa.market.rules.NullAuctionClosingCondition;
import net.sourceforge.jasa.market.rules.TimingCondition;

import net.sourceforge.jasa.sim.AbstractSimulation;
import net.sourceforge.jasa.sim.SimulationController;

import net.sourceforge.jasa.sim.event.SimulationStartingEvent;
import net.sourceforge.jasa.sim.event.SimulationFinishedEvent;

import net.sourceforge.jasa.view.AuctionConsoleFrame;

import org.apache.log4j.Logger;

/**
 * @author Steve Phelps
 * @version $Revision$
 * 
 */

public class MarketSimulation extends AbstractSimulation implements Serializable {

	protected Market market;
	
	protected boolean closed = false;
	
	/**
	 * The current round.
	 */
	protected int round;

	protected int age = 0;

	protected Account account = new Account();

	/**
	 * Optional graphical console
	 */
	protected AuctionConsoleFrame guiConsole = null;

	/**
	 * The current trading day (period)
	 */
	protected int day = 0;

	protected TimingCondition closingCondition = new NullAuctionClosingCondition();

	protected TimingCondition dayEndingCondition;

	protected boolean endOfRound = false;
		

	public static final String ERROR_SHOUTSVISIBLE = "Auctioneer does not permit shout inspection";

	static Logger logger = Logger.getLogger(MarketSimulation.class);


	public MarketSimulation(SimulationController controller, Market market) {
		super(controller);
		this.market = market;
		initialise();
	}
	
	public void initialise() {
		day = 0;
		round = 0;
		endOfRound = false;
		age = 0;
		closed = false;
	}
	
	public void reset() {
		initialise();
	}
	
	public void informAuctionClosed() {
		fireEvent(new MarketClosedEvent(market, getRound()));
	}

	public void informEndOfDay() {
		fireEvent(new EndOfDayEvent(market, getRound()));
	}

	public void informBeginOfDay() {
		fireEvent(new DayOpeningEvent(market, getRound()));
	}

	public void informAuctionOpen() {
		fireEvent(new MarketOpenEvent(market, getRound()));
	}

//
//	/**
//	 * Register a new trader in the market.
//	 */
//	public void register(TradingAgent trader) {
////		registeredTraders.add(trader);
////		activate(trader);
//	}
//
//	/**
//	 * Remove a trader from the market.
//	 */
//	public void remove(TradingAgent trader) {
////		if (!defunctTraders.contains(trader)) {
////			defunctTraders.add(trader);
////		}
//	}

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
		if (closingCondition.eval()) {
			close();
		} else {			
			endOfRound = false;
		}
	}

//	public void requestNextShout() {
//		if (currentTrader < shoutingTraders.length) {
//			requestShout((TradingAgent) shoutingTraders[currentTrader++]);
//		} else {
//			endOfRound = true;
//		}
//		setChanged();
//		notifyObservers();
//	}
//
//	public void requestShout(TradingAgent trader) {
//		trader.requestShout(this);
//		fireEvent(new AgentPolledEvent(this, getRound(), trader));
//	}

	/**
	 * Return the number of traders currently active in the market.
	 */
	public int getNumberOfTraders() {
//		return activeTraders.size();
		return 0;
	}

	/**
	 * Return the total number of traders registered in the market.
	 */
	public int getNumberOfRegisteredTraders() {
//		return registeredTraders.size();
		return 0;
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
		return market.getAuctioneer();
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
		fireEvent(new SimulationStartingEvent(this));
		informAuctionOpen();
	}

	public void end() {
		informAuctionClosed();
		fireEvent(new SimulationFinishedEvent(this));
	}

	public void step() throws AuctionClosedException {
		runSingleRound();
	}

	public void endRound() {
		informRoundClosing();

		getAuctioneer().endOfRoundProcessing();

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
		fireEvent(new RoundClosingEvent(market, round));
	}

	public void informRoundClosed() {
		fireEvent(new RoundClosedEvent(market, round));
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
		getAuctioneer().endOfDayProcessing();
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
		MaxRoundsDayEndingCondition cond = new MaxRoundsDayEndingCondition(market);
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
		MaxRoundsAuctionClosingCondition cond = new MaxRoundsAuctionClosingCondition(
		    market);
		cond.setMaximumRounds(maximumRounds);
		setAuctionClosingCondition(cond);
	}

	public int getMaximumRounds() {
		TimingCondition cond = getAuctionClosingCondition(MaxRoundsAuctionClosingCondition.class);

		if (cond != null) {
			return ((MaxRoundsAuctionClosingCondition) cond).getMaximumRounds();
		} else {
			return -1;
		}
	}

	public void setMaximumDays(int maximumDays) {
		MaxDaysAuctionClosingCondition cond = new MaxDaysAuctionClosingCondition(
		    market);
		cond.setMaximumDays(maximumDays);
		setAuctionClosingCondition(cond);
	}

	private TimingCondition getTimingCondition(TimingCondition cond,
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

	public TimingCondition getAuctionClosingCondition(Class conditionClass) {
		return getTimingCondition(closingCondition, conditionClass);
	}

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

	public Market getMarket() {
		return market;
	}

	public void setMarket(Market market) {
		this.market = market;
	}
	
}