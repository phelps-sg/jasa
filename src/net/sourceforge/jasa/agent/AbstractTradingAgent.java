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

package net.sourceforge.jasa.agent;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.jasa.agent.valuation.FixedValuer;
import net.sourceforge.jasa.agent.valuation.ValuationPolicy;

import net.sourceforge.jasa.event.MarketClosedEvent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.MarketOpenEvent;
import net.sourceforge.jasa.event.EndOfDayEvent;
import net.sourceforge.jasa.event.RoundClosedEvent;

import net.sourceforge.jasa.market.Account;
import net.sourceforge.jasa.market.AuctionClosedException;
import net.sourceforge.jasa.market.AuctionException;
import net.sourceforge.jasa.market.AuctionRuntimeException;
import net.sourceforge.jasa.market.IllegalShoutException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.MarketFacade;

import net.sourceforge.jasa.sim.Agent;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.event.EventListener;
import net.sourceforge.jasa.sim.strategy.Strategy;
import net.sourceforge.jasa.sim.util.IdAllocator;
import net.sourceforge.jasa.sim.util.Parameterizable;
import net.sourceforge.jasa.sim.util.Prototypeable;
import net.sourceforge.jasa.sim.util.Resetable;

import org.apache.log4j.Logger;

/**
 * <p>
 * An abstract class representing a simple agent trading in a round-robin
 * market. Traders of this type deal in a single commodity for which they have
 * a well-defined valuation.
 * 
 * </p>
 * <p>
 * <b>Parameters</b><br>
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.isseller</tt><br>
 * <font size=-1>boolean</font></td>
 * <td valign=top>(is this agent a seller)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.strategy</tt><br>
 * <font size=-1>class</font></td>
 * <td valign=top>(the trading strategy to use)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.initialstock</tt><br>
 * <font size=-1>int &gt;= 0</font></td>
 * <td valign=top>(the initial quantity of the commoditiy possessed by this
 * agent)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.initialfunds</tt><br>
 * <font size=-1>double</font></td>
 * <td valign=top>(the initial funds)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.valuer</tt><br>
 * <font size=-1>class, inherits net.sourceforge.jasa.agent.Valuer</td>
 * <td valign=top>(the valuation policy to use)</td>
 * <tr>
 * 
 * <tr>
 * <td valign-top><i>base</i><tt>.group</tt><br>
 * <font size=-1>int &gt;= 0</font></td>
 * <td valign=top>(the group that this agent belongs to)</td>
 * <tr>
 * 
 * </table>
 * 
 * @see net.sourceforge.jasa.market.MarketFacade
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AbstractTradingAgent implements TradingAgent,
    Serializable, Parameterizable, Prototypeable, Cloneable {

	/**
	 * The number of items of stock this agent posseses.
	 */
	protected CommodityHolding stock = new CommodityHolding();

	/**
	 * The initial stock of this agent
	 */
	protected int initialStock = 0;

	/**
	 * The amount of money this agent posseses.
	 */
	protected Account account;

	/**
	 * The initial amount of money for this agent
	 */
	protected double initialFunds = 0;

	/**
	 * Used to allocate each agent with a unique id.
	 */
	static IdAllocator idAllocator = new IdAllocator();

	/**
	 * The valuer for this agent.
	 */
	protected ValuationPolicy valuer;

	/**
	 * Unique id for this trader. Its used mainly for debugging purposes.
	 */
	protected long id;

	/**
	 * Flag indicating whether this trader is a seller or buyer.
	 */
	protected boolean isSeller = false;

	/**
	 * The bidding strategy for this trader. The default strategy is to bid
	 * truthfully for a single unit.
	 */
	protected TradingStrategy strategy = null;

	/**
	 * The profit made in the last round.
	 */
	protected double lastProfit = 0;

	/**
	 * The total profits to date
	 */
	protected double profits = 0;

	/**
	 * Did the last shout we place in the market result in a transaction?
	 */
	protected boolean lastShoutAccepted = false;

	/**
	 * The current shout for this trader.
	 */
	protected Order currentOrder;

	/**
	 * The arbitrary grouping that this agent belongs to.
	 */
	protected AgentGroup group = null;
	
	protected Collection<Market> markets = new LinkedList<Market>();

	static Logger logger = Logger.getLogger(AbstractTradingAgent.class);

	
	/**	
	 * @param stock
	 *          The quantity of stock for this trader.
	 * @param funds
	 *          The amount of money for this trader.
	 * @param privateValue
	 *          The private value of the commodity traded by this trader.
	 * @param isSeller
	 *          Whether or not this trader is a seller.
	 */
	public AbstractTradingAgent(int stock, double funds, double privateValue,
	    boolean isSeller) {
		id = idAllocator.nextId();
		initialStock = stock;
		initialFunds = funds;
		account = new Account(this, initialFunds);
		this.valuer = new FixedValuer(privateValue);
		this.isSeller = isSeller;
		initialise();
	}

	public AbstractTradingAgent(int stock, double funds, double privateValue,
	    boolean isSeller, TradingStrategy strategy) {
		this(stock, funds, privateValue, isSeller);
		this.strategy = strategy;
	}

	public AbstractTradingAgent(int stock, double funds) {
		this(stock, funds, 0, false);
	}
	
	public AbstractTradingAgent() {
		this(0, 0, 0, false);
	}

	/**
	 * Place a shout in the market as determined by the agent's strategy.
	 */
	public void requestShout(Market market) {
		try {
			if (currentOrder != null) {
				market.removeOrder(currentOrder);
			}
			currentOrder = strategy.modifyOrder(currentOrder, market);
			lastProfit = 0;
			lastShoutAccepted = false;
			if (active() && currentOrder != null) {
				market.placeOrder(currentOrder);
			}
		} catch (AuctionClosedException e) {
			logger.debug("requestShout(): Received AuctionClosedException");
			// do nothing
		} catch (IllegalShoutException e) {
			logger.debug("requestShout(): Received IllegalShoutException");
			// do nothing
		} catch (AuctionException e) {
			logger.warn(e.getMessage());
			e.printStackTrace();
		}
	}

	public void eventOccurred(SimEvent ev) {
		if (ev instanceof MarketEvent) {
			MarketEvent event = (MarketEvent) ev;
			if (event instanceof MarketOpenEvent) {
				auctionOpen(event);
			} else if (event instanceof MarketClosedEvent) {
				auctionClosed(event);
			} else if (event instanceof RoundClosedEvent) {
				roundClosed(event);
			} else if (event instanceof EndOfDayEvent) {
				endOfDay(event);
			}
			valuer.eventOccurred(event);
			strategy.eventOccurred(event);
		}
	}

	public void roundClosed(MarketEvent event) {
		// Do nothing
	}

	public void endOfDay(MarketEvent event) {
		// Do nothing
	}

	public void auctionOpen(MarketEvent event) {
		lastShoutAccepted = false;

		if (valuer == null) {
			throw new AuctionRuntimeException(
			    "No valuation policy configured for agent " + this);
		}

		if (strategy == null) {
			throw new AuctionRuntimeException("No strategy configured for agent "
			    + this);
		}
	}

	public void auctionClosed(MarketEvent event) {
		((MarketFacade) event.getAuction()).remove(this);
	}

	public Order getCurrentShout() {
		return currentOrder;
	}

	public Account getAccount() {
		return account;
	}

	public synchronized void giveFunds(AbstractTradingAgent recipient, double amount) {
		account.transfer(recipient.getAccount(), amount);
	}

	/**
	 * This method is invoked by a buyer on a seller when it wishes to transfer
	 * funds.
	 * 
	 * @param amount
	 *          The total amount of money to give to the seller
	 */
	public synchronized void pay(double amount) {
		account.credit(amount);
	}

	public long getId() {
		return id;
	}

	public double getFunds() {
		return account.getFunds();
	}

	public int getStock() {
		return stock.getQuantity();
	}

	public void initialise() {
		stock.setQuantity(initialStock);
		account.setFunds(initialFunds);
		lastProfit = 0;
		profits = 0;
		lastShoutAccepted = false;
		currentOrder = null;
	}

	public void reset() {
		initialise();
		if (valuer != null) {
			valuer.reset();
		}
		if (strategy != null) {
			((Resetable) strategy).reset();
		}
	}

	public double getValuation(Market auction) {
		return valuer.determineValue(auction);
	}

	public void setPrivateValue(double privateValue) {
		((FixedValuer) valuer).setValue(privateValue);
	}

	public boolean isSeller(Market auction) {
		return isSeller;
	}

	public boolean isBuyer(Market auction) {
		return !isSeller;
	}

	public void setStrategy(TradingStrategy strategy) {
		this.strategy = strategy;
		strategy.setAgent(this);
	}

	public void setIsSeller(boolean isSeller) {
		this.isSeller = isSeller;
	}

	public TradingStrategy getStrategy() {
		return strategy;
	}

	/**
	 * Return the profit made in the most recent market round. This can be used
	 * as, e.g. input to a re-inforcement learning algorithm.
	 */
	public double getLastProfit() {
		return lastProfit;
	}

	public double getProfits() {
		return profits;
	}

	public int determineQuantity(Market auction) {
		return strategy.determineQuantity(auction);
	}

	public Object protoClone() {
		AbstractTradingAgent copy = null;
		try {
			copy = (AbstractTradingAgent) clone();
			copy.id = idAllocator.nextId();
			copy.strategy = (TradingStrategy) ((Prototypeable) strategy).protoClone();
			copy.reset();
		} catch (CloneNotSupportedException e) {
		}
		return copy;
	}

	public void cashIn(Market auction, int quantity, double price) {
		assert isBuyer(auction);
		stock.remove(quantity);
		lastProfit = (getValuation(auction) - price) * quantity;
		profits += lastProfit;
		account.credit(lastProfit);
	}

	public void shoutAccepted(Market auction, Order shout, double price,
	    int quantity) {
		lastShoutAccepted = true;
		if (isBuyer(auction)) {
			cashIn(auction, quantity, price);
		} else {
			lastProfit = (price - getValuation(auction)) * quantity;
			profits += lastProfit;
		}
		valuer.consumeUnit(auction);
	}

	public boolean lastShoutAccepted() {
		return lastShoutAccepted;
	}

	public ValuationPolicy getValuationPolicy() {
		return valuer;
	}

	public void setValuationPolicy(ValuationPolicy valuer) {
		this.valuer = valuer;
	}

	public AgentGroup getGroup() {
		return group;
	}

	public void setGroup(AgentGroup group) {
		this.group = group;
	}

	public CommodityHolding getCommodityHolding() {
		return stock;
	}

	
	public Collection<Market> getMarkets() {
		return markets;
	}

	public void setMarkets(Collection<Market> markets) {
		this.markets = markets;
	}
	
	public boolean register(Market market) {
		MarketFacade auction = (MarketFacade) market;
//		auction.addListener(this);
		return markets.add(market);
	}

	public boolean equals(Object other) {
		return this.id == ((AbstractTradingAgent) other).id;
	}

	public int hashCode() {
		return (int) id;
	}

	/**
	 * Calculate the hypothetical surplus this agent will receive if the market
	 * had cleared uniformly at the specified equilibrium price and quantity.
	 */
	public abstract double equilibriumProfits(Market auction,
	    double equilibriumPrice, int quantity);

	// TODO: jniu
	public double equilibriumProfitsEachDay(Market auction,
	    double equilibriumPrice, int quantity) {
		return 0;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public void addListener(EventListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getPayoff() {
		return profits;
	}

	@Override
	public void interact(List<Agent> other) {
		for(Market market : markets) {
			requestShout(market);
		}
	}

	@Override
	public boolean isInteracted() {
		// TODO Auto-generated method stub
		return active();
	}


	@Override
	public void setStrategy(Strategy strategy) {
		this.strategy = (TradingStrategy) strategy;
	}

	/**
	 * Determine whether or not this trader is active. Inactive traders do not
	 * place shouts in the market, but do carry on learning through their
	 * strategy.
	 * 
	 * @return true if the trader is active.
	 */
	public abstract boolean active();

}