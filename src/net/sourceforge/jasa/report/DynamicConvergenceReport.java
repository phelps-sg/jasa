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

package net.sourceforge.jasa.report;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.EndOfDayEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.sim.util.Resetable;

import org.apache.log4j.Logger;

/**
 * <p>
 * A historicalDataReport that keeps track of the convergence of transaction prices, computing
 * the coefficient of convergence Vernon Smith used. The equilibrium price is
 * recomputed at the end of each day, thus this class can be used to keep track
 * of theoretically available surplus even when supply and demand are changing
 * over time. Each agent is assumed to be hypothetically able to trade the
 * specified quantity of units in each day.
 * </p>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class DynamicConvergenceReport extends AbstractMarketStatsReport
    implements Resetable {

	/**
	 * The historicalDataReport used to calculate the equilibrium price.
	 */
	protected EquilibriumReport equilibriaStats;

	/**
	 * The quantity that each agent can theoretically trade per day. This should
	 * normally be set equal to agents' trade entitlement.
	 */
	protected int quantity = 1;

	protected double alpha;

	protected double equilibriumPrice;

	protected double devSquareSum;

	public static final String P_DEF_BASE = "dynamicconvergencereport";

	public static final String P_QUANTITY = "quantity";

	public static final ReportVariable VAR_ALPHA = new ReportVariable(
	    "convergence.alpha", "coefficient of convergence");

	static Logger logger = Logger.getLogger(DynamicConvergenceReport.class);

	public void setAuction(MarketFacade auction) {
		super.setAuction(auction);
		equilibriaStats = new EquilibriumReport(auction);
	}

	public void eventOccurred(MarketEvent event) {
		super.eventOccurred(event);
		if (event instanceof EndOfDayEvent) {
			recalculate();
		} else if (event instanceof TransactionExecutedEvent) {
			newPrice(((TransactionExecutedEvent) event).getPrice());
		}
	}

	public void calculate() {

	}

	public double getAlpha() {
		return alpha;
	}

	protected void recalculate() {
		equilibriaStats.recalculate();
		equilibriumPrice = equilibriaStats.calculateMidEquilibriumPrice();
	}

	protected void newPrice(double price) {

	}

	public void initialise() {
	}

	public void reset() {
		initialise();
	}

	public void produceUserOutput() {
		logger.info("Convergence Report (Dynamic)");
		logger.info("------------------------");
		logger.info("");
		logger.info("\talpha =\t" + alpha);
		logger.info("");
	}

	@Override
	public Map<Object,Number> getVariableBindings() {
		Map<Object,Number> vars = super.getVariableBindings();
		vars.put(VAR_ALPHA, new Double(alpha));
		return vars;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
