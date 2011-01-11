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

package net.sourceforge.jasa.market.rules;

import java.io.Serializable;

import net.sourceforge.jabm.util.FixedLengthQueue;
import net.sourceforge.jabm.util.Parameterizable;
import net.sourceforge.jabm.util.Resetable;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;

/**
 * A discriminatory pricing policy that uses the average of the last <i>n</i>
 * pair of bid and ask prices leading to transactions as the clearing price. In
 * case of the price falls out of the range between the current bid and ask, the
 * nearest boundary is used.
 * 
 * <p>
 * <b>Parameters </b>
 * </p>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.n</tt><br>
 * <font size=-1>int >= 1 </font></td>
 * <td valign=top>(the number of latest successful shout pairs used to
 * determine next clearing price)</td>
 * <tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class NPricingPolicy implements PricingPolicy, Resetable, Serializable,
    Parameterizable {

	protected int n;

	public static final String P_N = "n";

	public static final String P_DEF_BASE = "npricingpolicy";

	protected FixedLengthQueue queue;

	static Logger logger = Logger.getLogger(NPricingPolicy.class);

	public NPricingPolicy() {
		this(1);
	}

	public NPricingPolicy(int n) {
		this.n = n;
	}

	public void initialize() {
		queue = new FixedLengthQueue(2 * n);
	}

	public void reset() {
		queue.reset();
	}

	public double determineClearingPrice(Order bid, Order ask,
	    MarketQuote clearingQuote) {

		queue.newData(bid.getPrice());
		queue.newData(ask.getPrice());
		double avg = queue.getMean();

		double price = (avg >= bid.getPrice()) ? bid.getPrice() : ((avg <= ask
		    .getPrice()) ? ask.getPrice() : avg);

		return price;
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + " n:" + n + ")";
	}

}
