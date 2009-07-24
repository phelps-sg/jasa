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

import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.MarketEventListener;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.IllegalOrderException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.report.ReportVariableBoard;
import net.sourceforge.jasa.sim.util.FixedLengthQueue;

import org.apache.log4j.Logger;

/**
 * @deprecated GenericDoubleAuctioneer with EquilibriumBeatingAcceptingPolicy
 *             should be used.
 * 
 * <p>
 * An auctioneer for a double market with continuous clearing and equlibrium
 * price estimation.
 * </p>
 * 
 * <p>
 * The clearing operation is performed every time a shout arrives. Shouts must
 * beat the current quote and be at the right side of the estimated equilibrium
 * price in order to be accepted.
 * </p>
 * 
 * <p>
 * <b>Parameters </b>
 * </p>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.memorysize</tt><br>
 * <font size=-1>int >=1 </font></td>
 * <td valign=top>(how many recent transaction prices memorized to get the
 * average as the esimated equilibrium)</td>
 * 
 * <td valign=top><i>base </i> <tt>.delta</tt><br>
 * <font size=-1>0 <=double <=1 </font></td>
 * <td valign=top>(relaxing the restriction put by the estimated equilibrium
 * price )</td>
 * <tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class ContinuousDoubleAuctioneerEE extends ContinuousDoubleAuctioneer
    implements Serializable, MarketEventListener {

	static Logger logger = Logger.getLogger(ContinuousDoubleAuctioneerEE.class);

	private double expectedHighestAsk;

	private double expectedLowestBid;

	/**
	 * A parameter used to adjust the equilibrium price estimate so as to relax
	 * the restriction.
	 */
	protected double delta = 0;

	public static final String P_DELTA = "delta";

	/**
	 * A parameter used to adjust the number of recent transaction prices to be
	 * memorized so as to compute the average as the equilibrium price estimate
	 */
	protected int memorySize = 4;

	public static final String P_MEMORYSIZE = "memorysize";

	public static final String EST_EQUILIBRIUM_PRICE = "estimated.equilibrium.price";

	protected FixedLengthQueue memory;

	public static final String P_DEF_BASE = "cdaee";

	public ContinuousDoubleAuctioneerEE() {
		this(null);
	}

	public ContinuousDoubleAuctioneerEE(Market auction) {
		super(auction);
	}

//	public void setup(ParameterDatabase parameters, Parameter base) {
//		super.setup(parameters, base);
//
//		delta = parameters.getDoubleWithDefault(base.push(P_DELTA), new Parameter(
//		    P_DEF_BASE).push(P_DELTA), delta);
//		assert (0 <= delta);
//
//		memorySize = parameters.getIntWithDefault(base.push(P_MEMORYSIZE),
//		    new Parameter(P_DEF_BASE).push(P_MEMORYSIZE), memorySize);
//		assert (0 <= memorySize);
//		memory = new FixedLengthQueue(memorySize);
//
//	}

	protected void initialise() {
		super.initialise();

		expectedHighestAsk = Double.POSITIVE_INFINITY;
		expectedLowestBid = 0;

		if (memory != null)
			memory.initialize();
	}

	public void reset() {
		super.reset();
		initialise();
	}

	public void checkImprovement(Order shout) throws IllegalOrderException {
		super.checkImprovement(shout);

		if (shout.isBid()) {
			if (shout.getPrice() < expectedLowestBid) {
				bidNotAnImprovementException();
			}
		} else {
			if (shout.getPrice() > expectedHighestAsk) {
				askNotAnImprovementException();
			}
		}
	}

	public void eventOccurred(MarketEvent event) {

		if (event instanceof TransactionExecutedEvent) {
			memory.newData(((TransactionExecutedEvent) event).getPrice());

			if (memory.count() >= memorySize) {
				expectedLowestBid = memory.getMean() - delta;
				expectedHighestAsk = memory.getMean() + delta;

				ReportVariableBoard.getInstance().reportValue(EST_EQUILIBRIUM_PRICE,
				    memory.getMean(), event);
			}

		}
	}
}