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

package net.sourceforge.jasa.replication.electricity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.util.Prototypeable;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.FixedDirectionTradingAgent;
import net.sourceforge.jasa.agent.SimpleTradingAgent;
import net.sourceforge.jasa.agent.TokenTradingAgent;
import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.IllegalOrderException;
import net.sourceforge.jasa.market.NotAnImprovementOverQuoteException;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import net.sourceforge.jasa.report.ReportVariable;
import net.sourceforge.jasa.report.SurplusReport;

import org.apache.log4j.Logger;

/**
 * <p>
 * Calculate the NPT market-power and efficiency variables. These are described
 * in detail in the following paper.
 * </p>
 * <p>
 * "Market Power and Efficiency in a Computational Electricity Market with
 * Discriminatory Double-Auction Pricing" Nicolaisen, J.; Petrov, V.; and
 * Tesfatsion, L. in IEEE Transactions on Evolutionary Computation, Vol. 5, No.
 * 5. 2001
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class ElectricityStats extends SurplusReport implements Cloneable {

	/**
	 * The relative concentration of sellers to buyers.
	 */
	protected double rCon;

	/**
	 * The relative generating-volume of buyers to sellers.
	 */
	protected double rCap;

	/**
	 * Strategic market-power for buyers.
	 */
	protected double sMPB = Double.NaN;

	/**
	 * Strategic market-power for sellers.
	 */
	protected double sMPS = Double.NaN;

	/**
	 * Profits of the buyers in truthful bidding.
	 */
	protected double pBT = Double.NaN;

	/**
	 * Profits of the sellers in truthful bidding.
	 */
	protected double pST = Double.NaN;

	/**
	 * The number of sellers.
	 */
	protected int numSellers;

	/**
	 * The number of buyers.
	 */
	protected int numBuyers;

	/**
	 * The total generating-volume of buyers.
	 */
	protected int buyerCap;

	/**
	 * The total generating-volume of sellers.
	 */
	protected int sellerCap;

	/**
	 * The approximated equilibrium price.
	 */
	protected double equilibPrice;

	/**
	 * The age of the market in rounds.
	 */
	protected int auctionAge;

	public static final ReportVariable VAR_RCAP = new ReportVariable(
	    "electricity.rcap",
	    "The relative generating volume of buyers to sellers");

	public static final ReportVariable VAR_RCON = new ReportVariable(
	    "electricity.rcon", "The ratio of sellers to buyers");

	static Logger logger = Logger.getLogger(ElectricityStats.class);

	public ElectricityStats(MarketFacade auction) {
		this.auction = auction;
		calculate();
	}

	public ElectricityStats() {
	}


	public void calculate() {

		initialise();

		super.calculate();

		auctionAge = calculateAuctionAge();

		equilibPrice = calculateEquilibriumPrice();

		Iterator<Agent> i = auction.getTraderIterator();
		while (i.hasNext()) {
			FixedDirectionTradingAgent trader = 
				(FixedDirectionTradingAgent) i.next();
			if (trader.isSeller()) {
				numSellers++;
				sellerCap += getCapacity(trader);
			} else {
				numBuyers++;
				buyerCap += getCapacity(trader);
			}
		}

		rCon = numSellers / numBuyers;
		rCap = (double) buyerCap / (double) sellerCap;

		// calculateStrategicMarketPower();
	}

	protected double calculateEquilibriumPrice() {
		return calculateMidEquilibriumPrice();
	}

	public void initialise() {
		sellerCap = 0;
		buyerCap = 0;
		pBA = 0;
		pSA = 0;
		numBuyers = 0;
		numSellers = 0;
		super.initialise();
	}

	/*
	 * public double equilibriumProfits( int quantity, AbstractTraderAgent trader ) {
	 * double surplus = 0; if ( trader.isSeller() ) { surplus = equilibPrice -
	 * trader.getPrivateValue(market); } else { surplus =
	 * trader.getPrivateValue(market) - equilibPrice; } if ( surplus < 0 ) {
	 * surplus = 0; } return auctionAge * equilibQuant(trader, equilibPrice) *
	 * surplus; }
	 */

	protected double getProfits(AbstractTradingAgent trader) {
		return ((SimpleTradingAgent) trader).getTotalPayoff();
	}

	protected double getCapacity(AbstractTradingAgent trader) {
		return trader.getVolume(auction);
	}

	public double equilibQuant(TokenTradingAgent t, double price) {
		double privateValue = t.getValuation(auction);
		if (t.isBuyer()) {
			if (price > privateValue) {
				return 0;
			} else {
				return t.getVolume(auction);
			}
		} else {
			if (price > privateValue) {
				return t.getVolume(auction);
			} else {
				return 0;
			}
		}
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public ElectricityStats newCopy() {
		Object copy = null;
		try {
			copy = this.clone();
		} catch (CloneNotSupportedException e) {
		}
		return (ElectricityStats) copy;
	}

	public String toString() {
		return "(" + getClass() + "\n\trCon:" + rCon + "\n\trCap:" + rCap
		    + "\n\tmPB:" + mPB + "\n\tmPS:" + mPS + "\n\tsMPB:" + sMPB
		    + "\n\tsMPS:" + sMPS + "\n\tpBA:" + pBA + "\n\tpSA:" + pSA
		    + "\n\tpBCE:" + pBCE + "\n\tpSCE:" + pSCE + "\n\tpST:" + pST
		    + "\n\tpBT:" + pBT + "\n)";
	}

	protected void simulateTruthfulBidding() {
		Auctioneer auctioneer = (Auctioneer) ((Prototypeable) auction
		    .getAuctioneer()).protoClone();
		LinkedList<Order> shouts = new LinkedList<Order>();
		Iterator<Agent> i = auction.getTraderIterator();
		while (i.hasNext()) {
			TokenTradingAgent trader = (TokenTradingAgent) i.next();
			Order truth = new Order(trader, trader.getVolume(auction), trader
			    .getValuation(auction), trader.isBuyer());
			shouts.add(truth);
			try {
				auctioneer.newOrder(truth);
			} catch (NotAnImprovementOverQuoteException e) {
				// do nothing
			} catch (IllegalOrderException e) {
				e.printStackTrace();
				throw new Error(e.getMessage());
			}
		}
		auctioneer.clear();
		Iterator<Order> shoutIterator = shouts.iterator();
		while (shoutIterator.hasNext()) {
			Order s = shoutIterator.next();
			auctioneer.removeShout(s);
		}
	}

	public void calculateStrategicMarketPower() {
		simulateTruthfulBidding();
		Iterator<Agent> i = auction.getTraderIterator();
		pBT = 0;
		pST = 0;
		while (i.hasNext()) {
			TokenTradingAgent trader = (TokenTradingAgent) i.next();
			double truthProfits = truthProfits(trader.getLastPayoff());
			if (trader.isBuyer()) {
				pBT += truthProfits;
			} else {
				pST += truthProfits;
			}
		}
		sMPB = (pBA - pBT) / pBCE;
		sMPS = (pSA - pST) / pSCE;
	}

	protected double truthProfits(double singleRoundProfits) {
		return singleRoundProfits * auctionAge;
	}

	protected int calculateAuctionAge() {
		return auction.getRound();
	}

	/**
	 * Get the market-efficiency calculation.
	 */
	public double getEA() {
		return eA;
	}

	/**
	 * Get the strategic buyer market-power calculation.
	 */
	public double getSMPB() {
		return sMPB;
	}

	/**
	 * Get the strategic seller market-power calculation.
	 */
	public double getSMPS() {
		return sMPS;
	}

	/**
	 * Get the truthful seller profits calculation.
	 */
	public double getPST() {
		return pST;
	}

	/**
	 * Get the truthful buyer profits calculation.
	 */
	public double getPBT() {
		return pBT;
	}

	public double getRCAP() {
		return rCap;
	}

	public double getRCON() {
		return rCon;
	}

	public void produceUserOutput() {
		super.produceUserOutput();
		logger.info("NPT Auction statistics");
		logger.info("----------------------");
		logger.info("Relative generating volume (RCAP) =\t" + getRCAP());
		logger.info("Relative concentration (RCON) =\t" + getRCON());
		logger.info("Strategic buyer market-power (SMPB) =\t" + getSMPB());
		logger.info("Strategic seller market-power (SMPS) =\t" + getSMPS());
	}

	@Override
	public Map<Object,Number> getVariableBindings() {
		Map<Object,Number> vars = super.getVariableBindings();
		vars.put(VAR_RCAP, new Double(getRCAP()));
		vars.put(VAR_RCON, new Double(getRCON()));
		return vars;
	}

}
