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

package net.sourceforge.jasa.market.auctioneer;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.market.Account;
import net.sourceforge.jasa.market.IllegalOrderException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.ZeroFundsAccount;
import net.sourceforge.jasa.market.rules.MarketClearingCondition;
import net.sourceforge.jasa.market.rules.OrderAcceptancePolicy;
import net.sourceforge.jasa.sim.util.Parameterizable;

import org.apache.log4j.Logger;

/**
 * <p>
 * An modular auctioneer for a double market.
 * </p>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class GenericAuctioneer extends TransparentAuctioneer implements
    Serializable, Observer {

	static Logger logger = Logger.getLogger(GenericAuctioneer.class);

	protected ZeroFundsAccount account;

	protected MarketClearingCondition clearingCondition;

	protected OrderAcceptancePolicy acceptingPolicy;

	public GenericAuctioneer() {
		this(null);
	}

	public GenericAuctioneer(Market auction) {
		super(auction);
		account = new ZeroFundsAccount(this);
	}

	protected void initialise() {
	}

//	public void setup(ParameterDatabase parameters, Parameter base) {
//		super.setup(parameters, base);
//
//		Parameter defBase = new Parameter(P_DEF_BASE);
//
//		clearingCondition = (MarketClearingCondition) parameters
//		    .getInstanceForParameterEq(base.push(P_CLEARING), defBase
//		        .push(P_CLEARING), MarketClearingCondition.class);
//
//		if (clearingCondition instanceof Parameterizable) {
//			((Parameterizable) clearingCondition).setup(parameters, base
//			    .push(P_CLEARING));
//		}
//		clearingCondition.addObserver(this);
//
//		acceptingPolicy = (ShoutAcceptingPolicy) parameters
//		    .getInstanceForParameterEq(base.push(P_ACCEPTING), defBase
//		        .push(P_ACCEPTING), ShoutAcceptingPolicy.class);
//
//		if (acceptingPolicy instanceof Parameterizable) {
//			((Parameterizable) acceptingPolicy).setup(parameters, base
//			    .push(P_ACCEPTING));
//		}
//		acceptingPolicy.setAuctioneer(this);
//	}

	public void reset() {
		super.reset();
		initialise();

		if (clearingCondition != null) {
			clearingCondition.reset();
		}

		if (acceptingPolicy != null) {
			acceptingPolicy.reset();
		}
	}

	public void setClearingCondition(MarketClearingCondition clearingCondition) {
		this.clearingCondition = clearingCondition;
	}

	public MarketClearingCondition getClearingCondition() {
		return clearingCondition;
	}

	public void setAcceptingPolicy(OrderAcceptancePolicy acceptingPolicy) {
		this.acceptingPolicy = acceptingPolicy;
	}

	public OrderAcceptancePolicy getAcceptingPolicy() {
		return acceptingPolicy;
	}

	public Account getAccount() {
		return account;
	}

	protected void clearMarket() {
		generateQuote();
		clear();
	}

	public void generateQuote() {
		currentQuote = new MarketQuote(askQuote(), bidQuote());
	}

	protected void checkShoutValidity(Order shout) throws IllegalOrderException {
		super.checkShoutValidity(shout);
		if (acceptingPolicy != null) {
			acceptingPolicy.check(shout);
		}
	}

	public void update(Observable source, Object arg) {
		if (source instanceof MarketClearingCondition) {
			clearMarket();
		}
	}

	/**
	 * TODO: temporarily put here, clearingCondition and acceptingPolicy should
	 * listen to the market directly
	 */
	public void eventOccurred(MarketEvent event) {
		if (clearingCondition != null) {
			clearingCondition.eventOccurred(event);
		}

		if (acceptingPolicy != null) {
			acceptingPolicy.eventOccurred(event);
		}
	}

	public String toString() {
		String s = getClass().getName();
		if (pricingPolicy != null) {
			s += "\n\t" + pricingPolicy;
		}

		if (clearingCondition != null) {
			s += "\n\t" + clearingCondition;
		}

		if (acceptingPolicy != null) {
			s += "\n\t" + acceptingPolicy;
		}

		return s;
	}

}