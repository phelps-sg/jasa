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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;


/**
 * <p>
 * A class to calculate the true equilibrium price and quantity ranges for a
 * given market.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class EquilibriumReportVariables extends DirectRevelationReportVariables
		implements Serializable, InitializingBean {

	/**
	 * The minimum equilibrium price.
	 */
	protected double minPrice;

	/**
	 * The maximum equilibrium price.
	 */
	protected double maxPrice;

	/**
	 * Do any equilbria exist?
	 */
	protected boolean equilibriaFound = false;

	protected List<Order> matchedShouts;

	protected int quantity;

	public static final String VAR_EXISTS = "equilibria.exists";

	public static final String VAR_MINPRICE = "equilibria.minprice";

	public static final String VAR_MAXPRICE = "equilibria.maxprice";

	public static final String VAR_QUANTITY = "equilibria.quantity";

	static Logger logger = Logger.getLogger(EquilibriumReportVariables.class);


	public EquilibriumReportVariables() {
		this(null);
	}

	public EquilibriumReportVariables(Market auction) {
		super("equilibrium");
		this.auction = auction;
	}

	@Override
	public void compute(SimEvent event) {
		super.compute(event);
		Order hiAsk = shoutEngine.getHighestMatchedAsk();
		Order loBid = shoutEngine.getLowestMatchedBid();
		if (hiAsk == null || loBid == null) {
			equilibriaFound = false;
		} else {
			calculateEquilibriaPriceRange();
			equilibriaFound = true;
			matchedShouts = shoutEngine.matchOrders();
			calculateEquilibriaQuantity();
		}
	}
	
	public void calculate() {
		compute(null);
	}

	@Override
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof SimulationStartingEvent) {
			this.auction = (Market) ((SimulationStartingEvent) event).getSimulation();
//			compute(event);
		} 
	}

	protected void calculateEquilibriaQuantity() {
		quantity = 0;
		Iterator<Order> i = matchedShouts.iterator();
		while (i.hasNext()) {
			@SuppressWarnings("unused")
			Order bid = i.next();
			Order ask = i.next();
			quantity += ask.getQuantity();
		}
	}

	protected void calculateEquilibriaPriceRange() {

		minPrice = Order.maxPrice(shoutEngine.getHighestMatchedAsk(), shoutEngine
		    .getHighestUnmatchedBid());

		maxPrice = Order.minPrice(shoutEngine.getLowestUnmatchedAsk(), shoutEngine
		    .getLowestMatchedBid());

		assert minPrice <= maxPrice;
	}

	public void initialise() {
		super.initialise();
		quantity = 0;
	}

	public double getMinPrice() {
		return minPrice;
	}

	public double getMaxPrice() {
		return maxPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public boolean equilibriaExists() {
		return equilibriaFound;
	}

	public double calculateMidEquilibriumPrice() {
		return (getMinPrice() + getMaxPrice()) / 2;
	}

	public String toString() {
		return "(" + getClass() + " equilibriaFound:" + equilibriaFound
		    + " minPrice:" + minPrice + " maxPrice:" + maxPrice + ")";
	}

	public void produceUserOutput() {
		logger.debug("");
		logger.debug("Equilibrium analysis historicalDataReport");
		logger.debug("---------------------------");
		logger.debug("");
		logger.debug("\tEquilibria Found?\t" + equilibriaFound);
		logger.debug("\n\tquantity:\t" + quantity + "\n");
		logger.debug("\n\tprice:\n\t\tmin:\t" + minPrice + "\tmax:\t" + maxPrice);
		logger.debug("");
	}

	@Override
	public Map<Object,Number> getVariableBindings() {
		Map<Object,Number> reportVars = super.getVariableBindings();
		reportVars.put(VAR_MINPRICE, new Double(minPrice));
		if (equilibriaFound) {
			reportVars.put(VAR_EXISTS, new Integer(1));
		} else {
			reportVars.put(VAR_EXISTS, new Integer(0));
		}
		reportVars.put(VAR_QUANTITY, new Long(quantity));
		reportVars.put(VAR_MAXPRICE, new Double(maxPrice));
		return reportVars;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		yVariableNames.add(VAR_MINPRICE);
		yVariableNames.add(VAR_EXISTS);
		yVariableNames.add(VAR_QUANTITY);
		yVariableNames.add(VAR_MAXPRICE);
	}
	
//
//	@Override
//	public List<Object> getyVariableNames() {
//		ArrayList<Object> result = new ArrayList<Object>();
//		result.add(VAR_MINPRICE);
//		return result;
//	}
	
	

}
