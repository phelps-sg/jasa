/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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

package uk.ac.liv.auction.core;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.util.Parameterizable;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * <p>
 * An modular auctioneer for a double auction.
 * </p>
 * 
 * <p>
 * <b>Parameters </b>
 * </p>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.clearing</tt><br>
 * <font size=-1> MarketClearingCondition </font></td>
 * <td valign=top>(the market-clearing condition)</td>
 * <tr>
 * <tr>
 * <td valign=top><i>base </i> <tt>.accepting</tt><br>
 * <font size=-1> ShoutAcceptingPolicy </font></td>
 * <td valign=top>(the shout-accepting policy)</td>
 * <tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class GenericDoubleAuctioneer extends TransparentAuctioneer implements
    Serializable, Observer {

	static Logger logger = Logger.getLogger(GenericDoubleAuctioneer.class);

	protected ZeroFundsAccount account;

	/**
	 * @uml.property name="clearingCondition"
	 * @uml.associationEnd
	 */
	protected MarketClearingCondition clearingCondition;

	/**
	 * @uml.property name="acceptingPolicy"
	 * @uml.associationEnd
	 */
	protected ShoutAcceptingPolicy acceptingPolicy;

	public static final String P_CLEARING = "clearing";

	public static final String P_ACCEPTING = "accepting";

	public static final String P_DEF_BASE = "gda";

	public GenericDoubleAuctioneer() {
		this(null);
	}

	public GenericDoubleAuctioneer(Auction auction) {
		super(auction);
		account = new ZeroFundsAccount(this);
	}

	protected void initialise() {
	}

	public void setup(ParameterDatabase parameters, Parameter base) {
		super.setup(parameters, base);

		Parameter defBase = new Parameter(P_DEF_BASE);

		clearingCondition = (MarketClearingCondition) parameters
		    .getInstanceForParameterEq(base.push(P_CLEARING), defBase
		        .push(P_CLEARING), MarketClearingCondition.class);

		if (clearingCondition instanceof Parameterizable) {
			((Parameterizable) clearingCondition).setup(parameters, base
			    .push(P_CLEARING));
		}
		clearingCondition.addObserver(this);

		acceptingPolicy = (ShoutAcceptingPolicy) parameters
		    .getInstanceForParameterEq(base.push(P_ACCEPTING), defBase
		        .push(P_ACCEPTING), ShoutAcceptingPolicy.class);

		if (acceptingPolicy instanceof Parameterizable) {
			((Parameterizable) acceptingPolicy).setup(parameters, base
			    .push(P_ACCEPTING));
		}
		acceptingPolicy.setAuctioneer(this);
	}

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

	public void setAcceptingPolicy(ShoutAcceptingPolicy acceptingPolicy) {
		this.acceptingPolicy = acceptingPolicy;
	}

	public ShoutAcceptingPolicy getAcceptingPolicy() {
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

	protected void checkShoutValidity(Shout shout) throws IllegalShoutException {
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
	 * listen to the auction directly
	 */
	public void eventOccurred(AuctionEvent event) {
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