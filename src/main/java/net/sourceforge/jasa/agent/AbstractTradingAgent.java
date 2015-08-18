/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2013 Steve Phelps
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
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.agent.AbstractAgent;
import net.sourceforge.jabm.agent.utility.RiskNeutralUtilityFunction;
import net.sourceforge.jabm.agent.utility.UtilityFunction;
import net.sourceforge.jabm.event.AgentArrivalEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.util.IdAllocator;
import net.sourceforge.jabm.util.Prototypeable;
import net.sourceforge.jabm.util.Resetable;
import net.sourceforge.jasa.agent.strategy.FixedQuantityStrategy;
import net.sourceforge.jasa.agent.valuation.FixedValuer;
import net.sourceforge.jasa.agent.valuation.ValuationPolicy;
import net.sourceforge.jasa.event.EndOfDayEvent;
import net.sourceforge.jasa.event.MarketClosedEvent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.MarketOpenEvent;
import net.sourceforge.jasa.market.Account;
import net.sourceforge.jasa.market.AuctionClosedException;
import net.sourceforge.jasa.market.AuctionException;
import net.sourceforge.jasa.market.AuctionRuntimeException;
import net.sourceforge.jasa.market.IllegalOrderException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;

/**
 * <p>
 * An abstract class representing a simple agent trading in a round-robin
 * market. Traders of this type deal in a single commodity for which they have
 * a well-defined valuation.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AbstractTradingAgent extends AbstractAgent implements
		TradingAgent, Serializable, Prototypeable, Cloneable {

	/**
	 * The inventory of the agent.
	 */
	protected Inventory stock = new Inventory();

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
	 * The utility function of this agent.
	 */
	protected UtilityFunction utilityFunction;

    protected ProfitFunction profitFunction = new SimpleProfitFunction();
	
	/**
	 * The profit made in the last round.
	 */
	protected double lastPayoff = 0;

	/**
	 * The total profits to date
	 */
	protected double totalPayoff = 0;

	/**
	 * Did the last order we place in the market result in a transaction?
	 */
	protected boolean lastOrderFilled = false;

	/**
	 * The current position for this trader.
	 */
	protected Order currentOrder;

	/**
	 * The grouping that this agent belongs to.
	 */
	protected AgentGroup group = null;
	
	protected Set<Market> markets = new HashSet<Market>();

	static Logger logger = Logger.getLogger(AbstractTradingAgent.class);
	
	/**	
	 * @param stock
	 *          The quantity of stock for this trader.
	 * @param funds
	 *          The amount of money for this trader.
	 * @param privateValue
	 *          The private value of the commodity traded by this trader.
	 */
	public AbstractTradingAgent(int stock, double funds, double privateValue,
			EventScheduler scheduler) {
		this(stock, funds, privateValue, null, scheduler);
	}

	public AbstractTradingAgent(int stock, double funds, double privateValue,
			TradingStrategy strategy, EventScheduler scheduler) {
		super(scheduler);
		this.strategy = strategy;
		if (strategy != null) {
			strategy.setAgent(this);
		}
		initialStock = stock;
		initialFunds = funds;
		account = new Account(this, initialFunds);
		this.valuer = new FixedValuer(privateValue);
		this.utilityFunction = new RiskNeutralUtilityFunction();
		initialise();
	}

	public AbstractTradingAgent(int stock, double funds,
			EventScheduler scheduler) {
		this(stock, funds, 0, scheduler);
	}
	
	public AbstractTradingAgent(EventScheduler scheduler) {
		this(0, 0, 0, scheduler);
	}
	
	public TradingStrategy getTradingStrategy() {
		return (TradingStrategy) strategy;
	}

	/**
	 * Place an order in the market as determined by the agent's strategy.
	 */
	public void onAgentArrival(Market market, AgentArrivalEvent event) {
		try {
			if (currentOrder != null) {
				// Currently JASA does not provide an API call for order
				//  revision, so we implement this by cancelling the previous
				//  order and placing a new revised order.
				market.removeOrder(currentOrder);
			}
			Order newOrder = 
				getTradingStrategy().modifyOrder(currentOrder, market);
			lastPayoff = 0;
			lastOrderFilled = false;
			if (active() && newOrder != null) {
				if (logger.isDebugEnabled()) logger.debug(newOrder);
				market.placeOrder(newOrder);
			}
			currentOrder = newOrder;
			super.onAgentArrival(event);
		} catch (AuctionClosedException e) {
			logger.debug("requestShout(): Received AuctionClosedException");
			// do nothing
		} catch (IllegalOrderException e) {
			logger.debug("requestShout(): Received IllegalOrderException");
			// do nothing
		} catch (AuctionException e) {
			logger.warn(e.getMessage());
			e.printStackTrace();
		}
	}

	
	@Override
	public void subscribeToEvents() {
		scheduler.addListener(SimEvent.class, this);
		if (valuer != null) {
			valuer.subscribeToEvents(scheduler);
		}
		if (strategy != null) {
			strategy.subscribeToEvents(scheduler);
		}
	} 

	public void eventOccurred(SimEvent ev) {
		super.eventOccurred(ev);
		if (ev instanceof MarketEvent) {
			MarketEvent event = (MarketEvent) ev;
			if (event instanceof MarketOpenEvent) {
				onMarketOpen(event);
			} else if (event instanceof MarketClosedEvent) {
				onMarketClosed(event);		
			} else if (event instanceof EndOfDayEvent) {
				onEndOfDay(event);
			}
		}
//		valuer.eventOccurred(ev);
//		strategy.eventOccurred(ev);
	}

	public void onEndOfDay(MarketEvent event) {
		// Do nothing
	}

	public void onMarketOpen(MarketEvent event) {
		lastOrderFilled = false;

		if (valuer == null) {
			throw new AuctionRuntimeException(
			    "No valuation policy configured for agent " + this);
		}

		if (strategy == null) {
			throw new AuctionRuntimeException("No strategy configured for agent "
			    + this);
		}
	}

	public void onMarketClosed(MarketEvent event) {
		((Market) event.getAuction()).remove(this);
	}

	public Order getCurrentOrder() {
		return currentOrder;
	}

	public Account getAccount() {
		return account;
	}

	public void giveFunds(AbstractTradingAgent recipient,
			double amount) {
		account.transfer(recipient.getAccount(), amount);
	}

	/**
	 * This method is invoked by a buyer on a seller when it wishes to transfer
	 * funds.
	 * 
	 * @param amount
	 *          The total amount of money to give to the seller
	 */
	public void pay(double amount) {
		account.credit(amount);
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
		lastPayoff = 0;
		totalPayoff = 0;
		lastOrderFilled = false;
		currentOrder = null;
		if (strategy != null) {
			getTradingStrategy().initialise();
			strategy.subscribeToEvents(scheduler);
		}
		if (valuer != null) {
			valuer.initialise();
		}
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

	/**
	 * Return the profit made in the most recent market round. This can be used
	 * as, e.g. input to a re-inforcement learning algorithm.
	 */
	public double getLastPayoff() {
		return lastPayoff;
	}

	public double getTotalPayoff() {
		return totalPayoff;
	}

	public int determineQuantity(Market auction) {
		return getTradingStrategy().determineQuantity(auction);
	}

	public Object protoClone() {
		AbstractTradingAgent copy = null;
		try {
			copy = (AbstractTradingAgent) clone();
			copy.strategy = (TradingStrategy) ((Prototypeable) strategy).protoClone();
			copy.reset();
		} catch (CloneNotSupportedException e) {
		}
		return copy;
	}

	public double calculatePayoff(Market auction, int quantity, double price) {
		double profit = calculateProfit(auction, quantity, price);
		return utilityFunction.calculatePayoff(profit);
	}

	@Override
	public void orderFilled(Market auction, Order shout, double price,
	    int quantity) {
		lastOrderFilled = true;
		lastPayoff = calculatePayoff(auction, quantity, price);
		totalPayoff += lastPayoff;
		valuer.consumeUnit(auction);
	}

	public boolean lastOrderFilled() {
		return lastOrderFilled;
	}

	public ValuationPolicy getValuationPolicy() {
		return valuer;
	}

	public void setValuationPolicy(ValuationPolicy valuer) {
		this.valuer = valuer;
		valuer.setAgent(this);
	}
	
	public UtilityFunction getUtilityFunction() {
		return utilityFunction;
	}

	public void setUtilityFunction(UtilityFunction utilityFunction) {
		this.utilityFunction = utilityFunction;
	}

	public AgentGroup getGroup() {
		return group;
	}

	public void setGroup(AgentGroup group) {
		this.group = group;
	}

	public Inventory getCommodityHolding() {
		return stock;
	}
	
	public Collection<Market> getMarkets() {
		return markets;
	}

	public void setMarkets(Collection<Market> markets) {
		this.markets = new HashSet<Market>(markets);
	}
	
	public void setMarket(Market market) {
		markets = new HashSet<Market>();
		markets.add(market);
	}
	
	public Market getMarket() {
		return markets.iterator().next();
	}
	
	public boolean register(Market market) {
		markets = new HashSet<Market>();
		return markets.add(market);
	}
	
	// TODO: jniu
	public double equilibriumProfitsEachDay(Market auction,
	    double equilibriumPrice, int quantity) {
		return 0;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void setPayoff(double payoff) {
		this.totalPayoff = payoff;
	}

	@Override
	public double getPayoff() {
		return totalPayoff;
	}

	@Override
	public void onAgentArrival(AgentArrivalEvent event) {
		if (markets == null || markets.isEmpty()) {
			throw new RuntimeException("Agent is not configured in any markets");
		}
		for(Market market : markets) {
			onAgentArrival(market, event);
		}
	}

	@Override
	public boolean isInteracted() {
		return active();
	}

	public int getVolume(Market auction) {
		return ((FixedQuantityStrategy) strategy).getQuantity();
	}

	public boolean isBuyer(Market auction) {
		if (currentOrder == null) {
			return getTradingStrategy().isBuy(auction);
		} else {
			return currentOrder.isBid();
		}
	}
	
	public boolean isSeller(Market auction) {
		return !isBuyer(auction);
	}

	@Override
	public double calculateProfit(Market auction, int quantity, double price) {
        return profitFunction.calculateProfit(this, auction, quantity, price);
	}
	
	/**
	 * Calculate the hypothetical surplus this agent will receive if the market
	 * had cleared uniformly at the specified equilibrium price and quantity.
	 */
	public double equilibriumProfits(Market auction, double equilibriumPrice,
			int quantity) {
		return calculateProfit(auction, quantity, equilibriumPrice);
	}
	
	@Override
	public double getPayoffDelta() {
		return getLastPayoff();
	}

	/**
	 * Determine whether or not this trader is active. Inactive traders do not
	 * place shouts in the market, but do carry on learning through their
	 * strategy.
	 * 
	 * @return true if the trader is active.
	 */
	public abstract boolean active();

    public ProfitFunction getProfitFunction() {
        return profitFunction;
    }

    public void setProfitFunction(ProfitFunction profitFunction) {
        this.profitFunction = profitFunction;
    }

}