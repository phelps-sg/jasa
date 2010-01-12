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

import net.sourceforge.jasa.agent.TokenTradingAgent;
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
 * An implementation of the modified Gjerstad Dickhaut strategy in which
 * quadratic, instead of cubic originally, functions are used to calculate the
 * probability of any bid being accepted and bid to maximize expected profit.
 * See
 * </p>
 * <p>
 * "Price Formation in Double Auctions" S. Gjerstad, J. Dickhaut and R. Palmer
 * </p>
 * 
 * <p>
 * Note that you must configure a report of type HistoricalDataReport in order
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
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class GDQStrategy extends FixedDirectionStrategy implements
    Serializable, Prototypeable {

	protected double maxPoint = 0;

	protected double max = 0;

	protected HistoricalDataReport historicalDataReport;

	public static final String P_DEF_BASE = "gdqstrategy";

	public static final String P_MAXPRICE = "maxprice";

	public static double MAX_PRICE = 200;

	static Logger logger = Logger.getLogger(GDQStrategy.class);

	public GDQStrategy() {
	}

	public Object protoClone() {
		GDQStrategy clone = new GDQStrategy();
		return clone;
	}

	public void eventOccurred(MarketEvent event) {
		super.eventOccurred(event);
		if (event instanceof MarketOpenEvent) {
			auctionOpen((MarketOpenEvent) event);
		}
	}

	public void auctionOpen(MarketOpenEvent event) {
//		auction = event.getAuction();
//		historicalDataReport = (HistoricalDataReport) auction
//		    .getReport(HistoricalDataReport.class);
//
//		if (historicalDataReport == null) {
//			throw new AuctionRuntimeException(getClass()
//			    + " requires a HistoricalDataReport to be configured");
//		}
	}
	
	public TokenTradingAgent getAgent() {
		return (TokenTradingAgent) agent;
	}

	public boolean modifyShout(Order shout) {

		super.modifyShout(shout);

		Iterator<Order> sortedShouts = historicalDataReport.sortedShoutIterator();

		double lastPoint = 0;
		double lastP = 0;
		double currentPoint = 0;
		double currentP = 0;
		double slope = 0;
		maxPoint = 0;
		max = 0;

		if (!getAgent().isBuyer()) {
			lastP = 1;
			currentP = 1;
		}

		while (sortedShouts.hasNext()) {
			Order nextShout = (Order) sortedShouts.next();
			if (nextShout.getPrice() > lastPoint) {
				currentPoint = nextShout.getPrice();
				currentP = calculateProbability(currentPoint);
				slope = getMax(lastPoint, lastP, currentPoint, currentP, slope);
				lastPoint = currentPoint;
				lastP = currentP;
			}
		}

		currentPoint = MAX_PRICE;
		currentP = 1;
		if (!getAgent().isBuyer()) {
			currentP = 0;
		}
		getMax(lastPoint, lastP, currentPoint, currentP, slope);

		// set quote
		if (maxPoint > 0) {
			shout.setPrice(maxPoint);
			return true;
		} else {
			return false;
		}
	}

	private double calculateProbability(double price) {
		// (taken bids below price) + (all asks below price)
		// -------------------------------------------------------------------------------
		// (taken bids below price) + (all asks below price) + (rejected bids
		// above
		// price)
		if (getAgent().isBuyer()) {
			// return ((double) (historicalDataReport.getNumberOfBids(-1 * price, true)
			// + historicalDataReport
			// .getNumberOfAsks(-1 * price, false)))
			// / ((double) (historicalDataReport.getNumberOfBids(-1 * price, true)
			// + historicalDataReport.getNumberOfAsks(-1 * price, false) + (historicalDataReport
			// .getNumberOfBids(price, false) - historicalDataReport.getNumberOfBids(
			// price, true))));
			return ((double) (historicalDataReport.getIncreasingQueryAccelerator()
			    .getNumOfAcceptedBidsBelow(price) + historicalDataReport
			    .getIncreasingQueryAccelerator().getNumOfAsksBelow(price)))
			    / ((double) (historicalDataReport.getIncreasingQueryAccelerator()
			        .getNumOfAcceptedBidsBelow(price)
			        + historicalDataReport.getIncreasingQueryAccelerator().getNumOfAsksBelow(
			            price) + historicalDataReport.getIncreasingQueryAccelerator()
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
			return ((double) (historicalDataReport.getIncreasingQueryAccelerator()
			    .getNumOfAcceptedAsksAbove(price) + historicalDataReport
			    .getIncreasingQueryAccelerator().getNumOfBidsAbove(price)))
			    / ((double) (historicalDataReport.getIncreasingQueryAccelerator()
			        .getNumOfAcceptedAsksAbove(price)
			        + historicalDataReport.getIncreasingQueryAccelerator().getNumOfBidsAbove(
			            price) + historicalDataReport.getIncreasingQueryAccelerator()
			        .getNumOfRejectedAsksBelow(price)));
		}
	}

	/**
	 * 
	 * @param a1
	 * @param p1
	 * @param a2
	 * @param p2
	 * @param s
	 *          the slope at a1
	 */
	private double getMax(double a1, double p1, double a2, double p2, double s) {

		if (a1 > MAX_PRICE) {
			a1 = MAX_PRICE;
		}

		if (a2 > MAX_PRICE) {
			a2 = MAX_PRICE;
		}

		if (p1 < 0 || p1 > 1 || p2 < 0 || p2 > 1) {
			System.out.println("p1 = " + p1);
			System.out.println("p2 = " + p2);
			assert p1 >= 0 && p1 <= (1 + 10E-6) && p2 >= 0 && p2 <= (1 + 10E-6);
		}

		double pvalue = agent.getValuation(auction);

		double a12 = a1 - a2;
		double alpha2 = (s * a12 - (p1 - p2)) / (a12 * a12);
		double alpha1 = s - 2 * a1 * alpha2;
		double alpha0 = p1 - alpha2 * a1 * a1 - alpha1 * a1;

		double temp = 0;

		double p = 0;

		double start = a1;
		double end = a2;
		if (getAgent().isBuyer()) {
			if (a2 > pvalue) {
				end = pvalue;
			}
		} else {
			if (a1 < pvalue) {
				start = pvalue;
			}
		}

		for (double i = start; i < end; i++) {
			p = (alpha2 * i * i) + (alpha1 * i) + alpha0;
			if (getAgent().isBuyer()) {
				temp = p * (pvalue - i);
			} else {
				temp = p * (i - pvalue);
			}
			if (temp > max) {
				max = temp;
				maxPoint = i;
			}
		}

		return 2 * alpha2 * a2 + alpha1;
	}

	public void onRoundClosed(Market auction) {
		// Do nothing
	}

	public HistoricalDataReport getHistoricalDataReport() {
		return historicalDataReport;
	}

	public void setHistoricalDataReport(HistoricalDataReport historicalDataReport) {
		this.historicalDataReport = historicalDataReport;
	}
	
	

}