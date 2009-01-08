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

package uk.ac.liv.auction.stats;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimePeriodValue;

import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.core.DuplicateShoutException;
import uk.ac.liv.auction.core.FourHeapShoutEngine;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.event.AuctionClosedEvent;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionOpenEvent;
import uk.ac.liv.auction.event.EndOfDayEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;
import uk.ac.liv.util.FixedLengthQueue;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * A class updates values of major ReportVariables on ReportVariableBoard.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class ReportVariableBoardUpdater extends AbstractAuctionReport implements
    Parameterizable, Resetable {

	public static String TRANS_PRICE = "transaction.price";

	public static String TRANS_ASK_PRICE = "transaction.ask.price";

	public static String TRANS_BID_PRICE = "transaction.bid.price";

	public static String TRANS_PRICE_MEAN_MYOPIC = "transaction.price.mean.myopic";

	public static String TRANS_PRICE_SPREAD = "transaction.price.spread";

	public static String CUMULATIVE_CONVERGENCE_COEFFICIENT = "cumulative.convergence.coefficient";

	public static String CONVERGENCE_COEFFICIENT = "convergence.coefficient";

	public static String EQUIL_PRICE = "equilibrium.price";

	public static String ALLOCATIVE_EFFICIENCY = "allocative.efficiency";

	public static String DAILY_ALLOCATIVE_EFFICIENCY = "daily.allocative.efficiency";

	public static final ReportVariable VAR_CUMULATIVE_CONVERGENCE_COEFFICIENT = new ReportVariable(
	    CUMULATIVE_CONVERGENCE_COEFFICIENT,
	    "The cumulative convergence coefficient");

	public static final ReportVariable VAR_ALLOCATIVE_EFFICIENCY = new ReportVariable(
	    ALLOCATIVE_EFFICIENCY, "The allocative efficiency");

	public static String P_TRANS_PRICE_MEMORY = "transpricememory";

	protected double equilPrice;

	protected FixedLengthQueue transPriceMemory;

	protected TransactionPriceTracker transPriceDay;

	protected TransactionPriceTracker transPriceAuction;

	protected double pCE;

	protected double eA;

	protected double alpha;

	public ReportVariableBoardUpdater() {
		initialize();
	}

	protected void initialize() {
		transPriceDay = new TransactionPriceTracker();
		transPriceAuction = new TransactionPriceTracker();
	}

	public void produceUserOutput() {
	}

	public void setup(ParameterDatabase parameters, Parameter base) {
		transPriceMemory = new FixedLengthQueue(parameters.getIntWithDefault(base
		    .push(P_TRANS_PRICE_MEMORY), null, 3));
	}

	public void reset() {
		transPriceMemory.reset();
	}

	public Map getVariables() {
		HashMap vars = new HashMap();

		vars.put(VAR_CUMULATIVE_CONVERGENCE_COEFFICIENT, new Double(alpha));
		vars.put(VAR_ALLOCATIVE_EFFICIENCY, new Double(eA));

		return vars;
	}

	public void eventOccurred(AuctionEvent event) {

		Millisecond time = new Millisecond(new Date(event.getPhysicalTime()));

		if (event instanceof TransactionExecutedEvent) {
			ReportVariableBoard.getInstance().reportValue(
			    TRANS_PRICE,
			    new TimePeriodValue(time, ((TransactionExecutedEvent) event)
			        .getPrice()));

			transPriceMemory.newData(((TransactionExecutedEvent) event).getPrice());
			ReportVariableBoard.getInstance().reportValue(TRANS_PRICE_MEAN_MYOPIC,
			    new TimePeriodValue(time, transPriceMemory.getMean()));

			ReportVariableBoard.getInstance().reportValue(
			    TRANS_PRICE_SPREAD,
			    new TimePeriodValue(time, ((TransactionExecutedEvent) event).getBid()
			        .getPrice()
			        - ((TransactionExecutedEvent) event).getAsk().getPrice()));
			ReportVariableBoard.getInstance().reportValue(
			    TRANS_ASK_PRICE,
			    new TimePeriodValue(time, ((TransactionExecutedEvent) event).getAsk()
			        .getPrice()));
			ReportVariableBoard.getInstance().reportValue(
			    TRANS_BID_PRICE,
			    new TimePeriodValue(time, ((TransactionExecutedEvent) event).getBid()
			        .getPrice()));

			transPriceDay.count++;
			transPriceDay.devSquareSum += Math.pow(((TransactionExecutedEvent) event)
			    .getPrice()
			    - equilPrice, 2);

			transPriceAuction.count++;
			transPriceAuction.devSquareSum += Math.pow(
			    ((TransactionExecutedEvent) event).getPrice() - equilPrice, 2);
		} else if (event instanceof AuctionOpenEvent) {
			EquilibriumReport eqmReport = new EquilibriumReport(getAuction());
			eqmReport.calculate();
			equilPrice = eqmReport.calculateMidEquilibriumPrice();
			ReportVariableBoard.getInstance().reportValue(EQUIL_PRICE,
			    new TimePeriodValue(time, equilPrice));

			pCE = computeTheoreticalProfit();
			eA = 0;
			alpha = Double.NaN;

			transPriceAuction.reset();
			transPriceDay.reset();

		} else if (event instanceof AuctionClosedEvent) {

			ReportVariableBoard.getInstance().reportValue(EQUIL_PRICE,
			    new TimePeriodValue(time, equilPrice));

		} else if (event instanceof EndOfDayEvent) {
			// compute efficiency
			// TODO: update how computeActualProfit() works.

			double temp = computeActualProfit() / pCE;
			double dailyEA = 100 * temp - (eA * getAuction().getDay());
			eA = 100 * temp / (getAuction().getDay() + 1);

			ReportVariableBoard.getInstance().reportValue(ALLOCATIVE_EFFICIENCY,
			    new TimePeriodValue(time, eA));
			ReportVariableBoard.getInstance().reportValue(
			    DAILY_ALLOCATIVE_EFFICIENCY, new TimePeriodValue(time, dailyEA));

			// CONVERGENCE_COEFFICIENT (each day)

			double dalyAlpha = Double.NaN;
			if (transPriceDay.count > 0) {
				dalyAlpha = 100 * (Math.sqrt(transPriceDay.devSquareSum
				    / transPriceDay.count) / equilPrice);
			}

			ReportVariableBoard.getInstance().reportValue(CONVERGENCE_COEFFICIENT,
			    new TimePeriodValue(time, dalyAlpha));
			transPriceDay.reset();

			// CUMULATIVE_CONVERGENCE_COEFFICIENT (each auction)

			if (transPriceAuction.count > 0) {
				alpha = 100 * (Math.sqrt(transPriceAuction.devSquareSum
				    / transPriceAuction.count) / equilPrice);
			}

			ReportVariableBoard.getInstance().reportValue(
			    CUMULATIVE_CONVERGENCE_COEFFICIENT, new TimePeriodValue(time, alpha));

		} else if (event instanceof RoundClosedEvent) {
		}

	}

	private double computeActualProfit() {
		double pA = 0;
		Iterator i = auction.getTraderIterator();
		while (i.hasNext()) {
			AbstractTradingAgent agent = (AbstractTradingAgent) i.next();
			pA += agent.getProfits();
		}

		return pA;
	}

	private double computeTheoreticalProfit() {
		double pCE = 0;

		// init
		FourHeapShoutEngine shoutEngine = new FourHeapShoutEngine();
		ArrayList shouts = new ArrayList();

		// simulate a direct revelation process
		Iterator traders = auction.getTraderIterator();
		while (traders.hasNext()) {
			AbstractTradingAgent trader = (AbstractTradingAgent) traders.next();
			int quantity = trader.determineQuantity(auction);
			double value = trader.getValuation(auction);
			boolean isBid = trader.isBuyer(getAuction());
			Shout shout = new Shout(trader, quantity, value, isBid);
			shouts.add(shout);

			try {
				shoutEngine.newShout(shout);
			} catch (DuplicateShoutException e) {
				logger.error(e.getMessage());
				throw new Error(e);
			}
		}

		// compute theoretical profit
		Shout hiAsk = shoutEngine.getHighestMatchedAsk();
		Shout loBid = shoutEngine.getLowestMatchedBid();
		if (hiAsk == null || loBid == null) {
			return pCE; // no equilibrium
		} else {
			double minPrice = Shout.maxPrice(shoutEngine.getHighestMatchedAsk(),
			    shoutEngine.getHighestUnmatchedBid());

			double maxPrice = Shout.minPrice(shoutEngine.getLowestUnmatchedAsk(),
			    shoutEngine.getLowestMatchedBid());

			double midEquilibriumPrice = (minPrice + maxPrice) / 2;

			List matchedShouts = shoutEngine.getMatchedShouts();
			if (matchedShouts != null) {
				Iterator i = matchedShouts.iterator();
				while (i.hasNext()) {
					Shout bid = (Shout) i.next();
					Shout ask = (Shout) i.next();

					pCE += ((AbstractTradingAgent) bid.getAgent())
					    .equilibriumProfitsEachDay(getAuction(), midEquilibriumPrice, bid
					        .getQuantity());

					pCE += ((AbstractTradingAgent) ask.getAgent())
					    .equilibriumProfitsEachDay(getAuction(), midEquilibriumPrice, ask
					        .getQuantity());
				}
			}
		}

		return pCE;

	}

	public double getEA() {
		return eA;
	}

	public double getAlpha() {
		return alpha;
	}

	private class TransactionPriceTracker {
		int count;

		double devSquareSum;

		void reset() {
			count = 0;
			devSquareSum = 0;
		}
	}
}