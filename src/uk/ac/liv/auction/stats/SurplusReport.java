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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.Shout;

/**
 * A report that calculates the actual surplus of buyers and sellers in the
 * auction verses the theoretical surplus when trades occur at the equilibrium
 * price. Note that this report assumes that the equilibrium price is constant.
 * To calculate theoretical surplus with dynamic supply and demand you should
 * configure a DynamicSurplusReport.
 * 
 * @see DynamicSurplusReport
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class SurplusReport extends EquilibriumReport {

	/**
	 * The profits of the buyers in theoretical equilibrium.
	 */
	protected double pBCE = 0;

	/**
	 * The profits of the sellers in theoretical equilibrium.
	 */
	protected double pSCE = 0;

	/**
	 * The actual profits of the buyers.
	 */
	protected double pBA = 0;

	/**
	 * The actual profits of the sellers.
	 */
	protected double pSA = 0;

	/**
	 * Global market efficiency.
	 */
	protected double eA;

	protected double mPB;

	protected double mPS;

	public static final ReportVariable VAR_MPB = new ReportVariable(
	    "surplus.mpb", "The market-power of buyers");

	public static final ReportVariable VAR_MPS = new ReportVariable(
	    "surplus.mps", "The market-power of sellers");

	private DecimalFormat percentageFormatter = new DecimalFormat("#00.00");

	public static final ReportVariable VAR_EA = new ReportVariable("surplus.ea",
	    "Market efficiency");

	public static final ReportVariable VAR_PBA = new ReportVariable(
	    "surplus.pba", "Profits of buyers in the actual auction");

	public static final ReportVariable VAR_PSA = new ReportVariable(
	    "surplus.psa", "Profits of sellers in the actual auction");

	public static final ReportVariable VAR_PBCE = new ReportVariable(
	    "surplus.pbce", "Profits of buyers in competitive equilibrium");

	public static final ReportVariable VAR_PSCE = new ReportVariable(
	    "surplus.psce", "Profits of sellers in competitive equilibrium");

	static Logger logger = Logger.getLogger(SurplusReport.class);

	public SurplusReport(RandomRobinAuction auction) {
		super(auction);
	}

	public SurplusReport() {
		super();
	}

	public void calculate() {
		super.calculate();
		if (matchedShouts != null) {
			Iterator i = matchedShouts.iterator();
			while (i.hasNext()) {
				Shout bid = (Shout) i.next();
				Shout ask = (Shout) i.next();

				pBCE += equilibriumProfits(bid.getQuantity(),
				    (AbstractTradingAgent) bid.getAgent());

				pSCE += equilibriumProfits(ask.getQuantity(),
				    (AbstractTradingAgent) ask.getAgent());

			}
		}

		calculateActualProfits();

		eA = (pBA + pSA) / (pBCE + pSCE) * 100;

		mPB = (pBA - pBCE) / pBCE;
		mPS = (pSA - pSCE) / pSCE;

	}

	protected void calculateActualProfits() {
		pSA = 0;
		pBA = 0;
		Iterator i = auction.getTraderIterator();
		while (i.hasNext()) {
			AbstractTradingAgent agent = (AbstractTradingAgent) i.next();
			if (agent.isSeller(auction)) {
				pSA += agent.getProfits();
			} else {
				pBA += agent.getProfits();
			}
		}
	}

	public double equilibriumProfits(int quantity, AbstractTradingAgent trader) {
		return trader.equilibriumProfits(auction, calculateMidEquilibriumPrice(),
		    quantity);
	}

	public void initialise() {
		super.initialise();
		pBCE = 0;
		pSCE = 0;
	}

	/**
	 * @return The theoretical surplus available to buyers in competitive
	 *         equilibrium.
	 */
	public double getPBCE() {
		return pBCE;
	}

	/**
	 * @return The theoretical surplus available to sellers in competitive
	 *         equilibrium.
	 */
	public double getPSCE() {
		return pSCE;
	}

	/**
	 * @return The actual surplus of all buyers in the market.
	 */
	public double getPBA() {
		return pBA;
	}

	/**
	 * @return The actual surplus of all sellers in the market.
	 */
	public double getPSA() {
		return pSA;
	}

	/**
	 * Get the buyer market-power calculation.
	 */
	public double getMPB() {
		return mPB;
	}

	/**
	 * Get the seller market-power calculation.
	 */
	public double getMPS() {
		return mPS;
	}

	public double getEA() {
		return eA;
	}

	public String toString() {
		return "(" + getClass() + " equilibriaFound:" + equilibriaFound
		    + " minPrice:" + minPrice + " maxPrice:" + maxPrice + " pBCE:" + pBCE
		    + " pSCE:" + pSCE + ")";
	}

	public void produceUserOutput() {
		super.produceUserOutput();
		logger.info("");
		logger.info("Profit analysis");
		logger.info("---------------");
		logger.info("");
		logger.info("\tbuyers' profits in equilibrium:\t" + pBCE);
		logger.info("\tsellers' profits in equilibrium:\t" + pSCE);
		logger.info("");
		logger.info("\tbuyers' actual profits:\t" + pBA);
		logger.info("\tsellers' actual profits:\t" + pSA);
		logger.info("");
		logger.info("\tBuyer market-power:\t" + mPB);
		logger.info("\tSeller market-power:\t" + mPS);
		logger.info("");
		logger.info("\tAllocative efficiency:\t" + percentageFormatter.format(eA)
		    + "%");
		logger.info("");
	}

	public Map getVariables() {
		HashMap vars = new HashMap();
		vars.putAll(super.getVariables());
		vars.put(VAR_PBCE, new Double(pBCE));
		vars.put(VAR_PSCE, new Double(pSCE));
		vars.put(VAR_PBA, new Double(pBA));
		vars.put(VAR_PSA, new Double(pSA));
		vars.put(VAR_EA, new Double(eA));
		vars.put(VAR_MPB, new Double(getMPB()));
		vars.put(VAR_MPS, new Double(getMPS()));
		return vars;
	}

}
