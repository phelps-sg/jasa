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

package net.sourceforge.jasa.agent.strategy;

import java.io.Serializable;
import java.util.Iterator;

import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.MarketOpenEvent;
import net.sourceforge.jasa.market.AuctionRuntimeException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.report.HistoricalDataReport;
import net.sourceforge.jasa.sim.util.Prototypeable;

import org.apache.log4j.Logger;

/**
 * <p>
 * An implementation of the Gjerstad Dickhaut strategy. Agents using this
 * strategy calculate the probability of any bid being accepted and bid to
 * maximize expected profit. See
 * </p>
 * <p>
 * "Price Formation in Double Auctions" S. Gjerstad, J. Dickhaut and R. Palmer
 * </p>
 * 
 * <p>
 * Note that you must configure a logger of type HistoricalDataReport in order
 * to use this strategy.
 * </p>
 * 
 * </p>
 * <p>
 * <b>Parameters </b> <br>
 * <table>
 * <tr>
 * <td valign=top><i>base </i> <tt>.maxprice</tt><br>
 * <font size=-1>double &gt;= 0 </font></td>
 * <td valign=top>(max price in market)</td>
 * <tr>
 * 
 * </table>
 * 
 * @see net.sourceforge.jasa.report.HistoricalDataReport
 * 
 * @author Marek Marcinkiewicz
 * @version $Revision$
 */

public class GDStrategy extends FixedQuantityStrategyImpl implements
    Serializable, Prototypeable {

	protected double maxPoint = 0;

	protected double max = 0;

	protected HistoricalDataReport historyStats;

	public static final String P_DEF_BASE = "gdstrategy";

	public static final String P_MAXPRICE = "maxprice";

	public static double MAX_PRICE = 200;

	static Logger logger = Logger.getLogger(GDStrategy.class);

	public GDStrategy() {
	}

	public Object protoClone() {
		GDStrategy clone = new GDStrategy();
		return clone;
	}

	public void eventOccurred(MarketEvent event) {
		super.eventOccurred(event);
		if (event instanceof MarketOpenEvent) {
			auctionOpen((MarketOpenEvent) event);
		}
	}

	public void auctionOpen(MarketOpenEvent event) {
		auction = event.getAuction();
//		historicalDataReport = (HistoricalDataReport) auction
//		    .getReport(HistoricalDataReport.class);

		if (historyStats == null) {
			throw new AuctionRuntimeException(getClass()
			    + " requires a HistoricalDataReport to be configured");
		}
	}

	public boolean modifyShout(Order.MutableShout shout) {

		super.modifyShout(shout);

		Iterator sortedShouts = historyStats.sortedShoutIterator();

		double lastPoint = 0;
		double lastP = 0;
		double currentPoint = 0;
		double currentP = 0;
		maxPoint = 0;
		max = 0;

		if (!agent.isBuyer(auction)) {
			lastP = 1;
			currentP = 1;
		}

		while (sortedShouts.hasNext()) {
			Order nextShout = (Order) sortedShouts.next();
			if (nextShout.getPrice() > lastPoint) {
				currentPoint = nextShout.getPrice();
				currentP = calculateProbability(currentPoint);
				getMax(lastPoint, lastP, currentPoint, currentP);
				lastPoint = currentPoint;
				lastP = currentP;
			}
		}

		currentPoint = MAX_PRICE;
		currentP = 1;
		if (!agent.isBuyer(auction)) {
			currentP = 0;
		}
		getMax(lastPoint, lastP, currentPoint, currentP);

		// set quote
		if (maxPoint > 0) {
			shout.setPrice(maxPoint);
			return true;
		} else {
			return false;
		}
	}

	public double calculateProbability(double price) {
		// (taken bids below price) + (all asks below price)
		// -------------------------------------------------------------------------------
		// (taken bids below price) + (all asks below price) + (rejected bids
		// above
		// price)
		if (agent.isBuyer(auction)) {
			// return ((double) (historicalDataReport.getNumberOfBids(-1 * price, true)
			// + historicalDataReport
			// .getNumberOfAsks(-1 * price, false)))
			// / ((double) (historicalDataReport.getNumberOfBids(-1 * price, true)
			// + historicalDataReport.getNumberOfAsks(-1 * price, false) + (historicalDataReport
			// .getNumberOfBids(price, false) - historicalDataReport.getNumberOfBids(
			// price, true))));
			return ((double) (historyStats.getIncreasingQueryAccelerator()
			    .getNumOfAcceptedBidsBelow(price) + historyStats
			    .getIncreasingQueryAccelerator().getNumOfAsksBelow(price)))
			    / ((double) (historyStats.getIncreasingQueryAccelerator()
			        .getNumOfAcceptedBidsBelow(price)
			        + historyStats.getIncreasingQueryAccelerator().getNumOfAsksBelow(
			            price) + historyStats.getIncreasingQueryAccelerator()
			        .getNumOfRejectedBidsAbove(price)));

		} else {
			// (taken asks above price) + (all bids above price)
			// -------------------------------------------------------------------------------
			// (taken asks above price) + (all bids above price) + (rejected
			// asks
			// below price)
			// return ((double) (historicalDataReport.getNumberOfAsks(price, true) +
			// historicalDataReport
			// .getNumberOfBids(price, false)))
			// / ((double) (historicalDataReport.getNumberOfAsks(price, true)
			// + historicalDataReport.getNumberOfBids(price, false) + (historicalDataReport
			// .getNumberOfAsks(-1 * price, false) -
			// historicalDataReport.getNumberOfAsks(-1
			// * price, true))));
			return ((double) (historyStats.getIncreasingQueryAccelerator()
			    .getNumOfAcceptedAsksAbove(price) + historyStats
			    .getIncreasingQueryAccelerator().getNumOfBidsAbove(price)))
			    / ((double) (historyStats.getIncreasingQueryAccelerator()
			        .getNumOfAcceptedAsksAbove(price)
			        + historyStats.getIncreasingQueryAccelerator().getNumOfBidsAbove(
			            price) + historyStats.getIncreasingQueryAccelerator()
			        .getNumOfRejectedAsksBelow(price)));
		}
	}

	private void getMax(double a1, double p1, double a2, double p2) {

		if (a1 > MAX_PRICE) {
			a1 = MAX_PRICE;
		}

		if (a2 > MAX_PRICE) {
			a2 = MAX_PRICE;
		}

		assert p1 >= 0 && p1 <= (1 + 10E-6) && p2 >= 0 && p2 <= (1 + 10E-6);

		double pvalue = agent.getValuation(auction);

		// double denom = (-6 * a1 * a1 * a2 * a2) + (4 * a1 * a1 * a1 * a2)
		// + (4 * a1 * a2 * a2 * a2) + (-1 * a1 * a1 * a1 * a1)
		// + (-1 * a2 * a2 * a2 * a2);
		// double alpha3 = (2 * ((a1 * (p1 - p2)) + (a2 * (p2 - p1)))) / denom;
		// double alpha2 = (3 * ((a1 * a1 * (p2 - p1)) + (a2 * a2 * (p1 - p2))))
		// / denom;
		// double alpha1 = (6 * (p1 - p2) * ((a1 * a1 * a2) - (a1 * a2 * a2))) /
		// denom;
		// double alpha0 = ((p1 * ((4 * a1 * a2 * a2 * a2) + (-3 * a1 * a1 * a2
		// * a2) + (-1
		// * a2 * a2 * a2 * a2))) + (p2 * ((4 * a1 * a1 * a1 * a2)
		// + (-3 * a1 * a1 * a2 * a2) + (-1 * a1 * a1 * a1 * a1))))
		// / denom;
		double a11 = a1 * a1;
		double a1111 = a11 * a11;
		double a22 = a2 * a2;
		double a2222 = a22 * a22;
		double a1122 = a11 * a22;
		double a12 = a1 * a2;
		double a1112 = a11 * a12;
		double a1222 = a12 * a22;
		double p12 = p1 - p2;

		double denom = (-6 * a1122) + 4 * (a1112 + a1222) - a1111 - a2222;
		double alpha3 = (2 * ((a1 - a2) * p12)) / denom;
		double alpha2 = (3 * (a22 - a11) * p12) / denom;
		double alpha1 = (6 * p12 * (a12 * (a1 - a2))) / denom;
		double alpha0 = ((p1 * ((4 * a1222) - 3 * a1122 - a2222)) + (p2 * ((4 * a1112)
		    - 3 * a1122 - a1111)))
		    / denom;
		//
		//    
		double temp = 0;

		double p = 0;

		double start = a1;
		double end = a2;
		if (agent.isBuyer(auction)) {
			if (a2 > pvalue) {
				end = pvalue;
			}
		} else {
			if (a1 < pvalue) {
				start = pvalue;
			}
		}

		for (double i = start; i < end; i++) {
			p = (alpha3 * i * i * i) + (alpha2 * i * i) + (alpha1 * i) + alpha0;
			if (agent.isBuyer(auction)) {
				temp = p * (pvalue - i);
			} else {
				temp = p * (i - pvalue);
			}
			if (temp > max) {
				max = temp;
				maxPoint = i;
			}
		}
	}

	public void endOfRound(Market auction) {
		// Do nothing
	}

	public HistoricalDataReport getHistoryStats() {
		return historyStats;
	}

	public void setHistoryStats(HistoricalDataReport historyStats) {
		this.historyStats = historyStats;
	}
	

}