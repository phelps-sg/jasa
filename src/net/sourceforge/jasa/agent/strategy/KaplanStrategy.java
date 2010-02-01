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

import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.MarketOpenEvent;
import net.sourceforge.jasa.market.AuctionRuntimeException;
import net.sourceforge.jasa.market.DataUnavailableException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.report.DailyStatsReport;
import net.sourceforge.jasa.sim.EventScheduler;
import net.sourceforge.jasa.sim.event.SimEvent;
import net.sourceforge.jasa.sim.util.Distribution;
import net.sourceforge.jasa.sim.util.Prototypeable;

import org.apache.log4j.Logger;

/**
 * <p>
 * An implementation of Todd Kaplan's sniping strategy. Agents using this
 * strategy wait until the last minute before attempting to "steal the bid". See
 * </p>
 * <p>
 * "Behaviour of trading automata in a computerized double market market" J.
 * Rust, J. Miller and R. Palmer in "The Double Auction Market: Institutions,
 * Theories and Evidence" 1992, Addison-Wesley
 * </p>
 * 
 * <p>
 * Note that you must configure a report of type DailyStatsMarketDataLogger in
 * order to use this strategy.
 * </p>
 * 
 * </p>
 * <p>
 * <b>Parameters</b><br>
 * <table>
 * <tr>
 * <td valign=top><i>base</i><tt>.s</tt><br>
 * <font size=-1>double &gt;= 0</font></td>
 * <td valign=top>(the spread factor)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.t</tt><br>
 * <font size=-1>double &gt;= 0</font></td>
 * <td valign=top>(the time factor)</td>
 * <tr>
 * 
 * </table>
 * 
 * @see net.sourceforge.jasa.report.DailyStatsReport
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class KaplanStrategy extends FixedDirectionStrategy implements
    Serializable, Prototypeable {

	/**
	 * The time factor. Kaplan will bid if the remaining time in the current
	 * period is less than t.
	 */
	protected int t = 4;

	/**
	 * The spread factor.
	 */
	protected double s = 0.5;

	protected MarketQuote quote;

	protected DailyStatsReport dailyStatsReport;

	public static final String P_DEF_BASE = "kaplanstrategy";

	public static final String P_T = "t";

	public static final String P_S = "s";

	static Logger logger = Logger.getLogger(KaplanStrategy.class);

	public KaplanStrategy() {
	}


	public Object protoClone() {
		Object clone;
		try {
			clone = clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
		return clone;
	}
	
	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		super.subscribeToEvents(scheduler);
		scheduler.addListener(MarketOpenEvent.class, this);
	}


	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof MarketOpenEvent) {
			onMarketOpen((MarketOpenEvent) event);
		}
	}

	public void onMarketOpen(MarketOpenEvent event) {
//		dailyStatsReport = (DailyStatsReport) event.getAuction().getReport(
//		    DailyStatsReport.class);
//		if (dailyStatsReport == null) {
//			throw new AuctionRuntimeException(getClass()
//			    + " requires a DailyStatsReport to be configured");
//		}
	}

	public boolean modifyShout(Order shout) {
		super.modifyShout(shout);
		quote = auction.getQuote();
		if (timeRunningOut() || juicyOffer() || smallSpread()) {
			logger.debug("quote = " + quote);
			logger.debug("my priv value = " + agent.getValuation(auction));
			shout.setPrice(agent.getValuation(auction));
			if (isBuy()) {
				if (quote.getAsk() <= agent.getValuation(auction)) {
					shout.setPrice(quote.getAsk());
				}
			} else {
				if (quote.getBid() >= agent.getValuation(auction)) {
					shout.setPrice(quote.getBid());
				}
			}
			logger.debug(this + ": price = " + shout.getPrice());
			return true;
		} else {
			return false;
		}
	}

	public void onRoundClosed(Market auction) {
		// Do nothing
	}

	public boolean juicyOffer() {

		boolean juicyOffer = false;

		Distribution transPrice = null;

		transPrice = dailyStatsReport.getPreviousDayTransPriceStats();

		if (transPrice == null) {
			return false;
		}

		if (isBuy()) {
			juicyOffer = quote.getAsk() < transPrice.getMin();
		} else {
			juicyOffer = quote.getBid() > transPrice.getMax();
		}

		if (juicyOffer) {
			logger.debug(this + ": juicy offer detected");
		}

		return juicyOffer;
	}

	public boolean smallSpread() {

		boolean smallSpread = false;

		Distribution transPrice = null;

		transPrice = dailyStatsReport.getPreviousDayTransPriceStats();

		double spread = Math.abs(quote.getAsk() - quote.getBid());
		if (isBuy()) {
			smallSpread = (transPrice == null || (quote.getAsk() < transPrice
			    .getMax()))
			    && (spread / quote.getAsk()) < s;
		} else {
			smallSpread = (transPrice == null || (quote.getBid() > transPrice
			    .getMin()))
			    && (spread / quote.getBid()) < s;
		}

		if (smallSpread) {
			logger.debug(this + ": small spread detected");
		}

		return smallSpread;
	}

	public boolean timeRunningOut() {
		boolean timeOut = auction.getRemainingTime() < t;
		if (timeOut) {
			logger.debug(this + ": time running out");
		}
		return timeOut;
	}

	public double getS() {
		return s;
	}

	public void setS(double s) {
		this.s = s;
	}

	public double getT() {
		return t;
	}

	public void setT(int t) {
		this.t = t;
	}

	public String toString() {
		return "(" + getClass() + " s:" + s + " t:" + t + ")";
	}

	protected void error(DataUnavailableException e) {
		logger
		    .error("Auction is not configured with loggers appropriate for this strategy");
		logger.error(e.getMessage());
		throw new AuctionRuntimeException(e);
	}

	public DailyStatsReport getDailyStatsReport() {
		return dailyStatsReport;
	}

	public void setDailyStatsReport(DailyStatsReport dailyStatsReport) {
		this.dailyStatsReport = dailyStatsReport;
	}
	
	

}