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

package uk.ac.liv.auction.electricity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.stats.*;
import uk.ac.liv.auction.agent.*;

import uk.ac.liv.util.Prototypeable;

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
   * 
   * @uml.property name="rCon"
   */
  protected double rCon;

  /**
   * The relative generating-capacity of buyers to sellers.
   * 
   * @uml.property name="rCap"
   */
  protected double rCap;

  /**
   * Strategic market-power for buyers.
   * 
   * @uml.property name="sMPB"
   */
  protected double sMPB = Double.NaN;

  /**
   * Strategic market-power for sellers.
   * 
   * @uml.property name="sMPS"
   */
  protected double sMPS = Double.NaN;

  /**
   * Profits of the buyers in truthful bidding.
   * 
   * @uml.property name="pBT"
   */
  protected double pBT = Double.NaN;

  /**
   * Profits of the sellers in truthful bidding.
   * 
   * @uml.property name="pST"
   */
  protected double pST = Double.NaN;

  /**
   * The number of sellers.
   * 
   * @uml.property name="numSellers"
   */
  protected int numSellers;

  /**
   * The number of buyers.
   * 
   * @uml.property name="numBuyers"
   */
  protected int numBuyers;

  /**
   * The total generating-capacity of buyers.
   * 
   * @uml.property name="buyerCap"
   */
  protected int buyerCap;

  /**
   * The total generating-capacity of sellers.
   * 
   * @uml.property name="sellerCap"
   */
  protected int sellerCap;

  /**
   * The approximated equilibrium price.
   * 
   * @uml.property name="equilibPrice"
   */
  protected double equilibPrice;

  /**
   * The age of the auction in rounds.
   * 
   * @uml.property name="auctionAge"
   */
  protected int auctionAge;

  public static final ReportVariable VAR_RCAP = new ReportVariable(
      "electricity.rcap",
      "The relative generating capacity of buyers to sellers");

  public static final ReportVariable VAR_RCON = new ReportVariable(
      "electricity.rcon", "The ratio of sellers to buyers");

  static Logger logger = Logger.getLogger(ElectricityStats.class);

  public ElectricityStats( RoundRobinAuction auction ) {
    this.auction = auction;
    calculate();
  }

  public ElectricityStats() {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
  }

  public void calculate() {

    initialise();

    super.calculate();

    auctionAge = calculateAuctionAge();

    equilibPrice = calculateEquilibriumPrice();

    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      AbstractTradingAgent trader = (AbstractTradingAgent) i.next();
      if ( trader.isSeller(auction) ) {
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
   * trader.getPrivateValue(auction); } else { surplus =
   * trader.getPrivateValue(auction) - equilibPrice; } if ( surplus < 0 ) {
   * surplus = 0; } return auctionAge * equilibQuant(trader, equilibPrice) *
   * surplus; }
   */

  protected double getProfits( AbstractTradingAgent trader ) {
    return ((ElectricityTrader) trader).getProfits();
  }

  protected double getCapacity( AbstractTradingAgent trader ) {
    return ((ElectricityTrader) trader).getCapacity();
  }

  public double equilibQuant( AbstractTradingAgent t, double price ) {
    double privateValue = t.getValuation(auction);
    if ( t.isBuyer(auction) ) {
      if ( price > privateValue ) {
        return 0;
      } else {
        return ((ElectricityTrader) t).getCapacity();
      }
    } else {
      if ( price > privateValue ) {
        return ((ElectricityTrader) t).getCapacity();
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
    } catch ( CloneNotSupportedException e ) {
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
    LinkedList shouts = new LinkedList();
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      ElectricityTrader trader = (ElectricityTrader) i.next();
      Shout truth = new Shout(trader, trader.getCapacity(), trader
          .getValuation(auction), trader.isBuyer(auction));
      shouts.add(truth);
      try {
        auctioneer.newShout(truth);
      } catch ( NotAnImprovementOverQuoteException e ) {
        // do nothing
      } catch ( IllegalShoutException e ) {
        e.printStackTrace();
        throw new Error(e.getMessage());
      }
    }
    auctioneer.clear();
    Iterator shoutIterator = shouts.iterator();
    while ( shoutIterator.hasNext() ) {
      Shout s = (Shout) shoutIterator.next();
      auctioneer.removeShout(s);
    }

  }

  public void calculateStrategicMarketPower() {
    simulateTruthfulBidding();
    Iterator i = auction.getTraderIterator();
    pBT = 0;
    pST = 0;
    while ( i.hasNext() ) {
      ElectricityTrader trader = (ElectricityTrader) i.next();
      double truthProfits = truthProfits(trader.getLastProfit());
      if ( trader.isBuyer(auction) ) {
        pBT += truthProfits;
      } else {
        pST += truthProfits;
      }
    }
    sMPB = (pBA - pBT) / pBCE;
    sMPS = (pSA - pST) / pSCE;
  }

  protected double truthProfits( double singleRoundProfits ) {
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
   * 
   * @uml.property name="sMPB"
   */
  public double getSMPB() {
    return sMPB;
  }

  /**
   * Get the strategic seller market-power calculation.
   * 
   * @uml.property name="sMPS"
   */
  public double getSMPS() {
    return sMPS;
  }

  /**
   * Get the truthful seller profits calculation.
   * 
   * @uml.property name="pST"
   */
  public double getPST() {
    return pST;
  }

  /**
   * Get the truthful buyer profits calculation.
   * 
   * @uml.property name="pBT"
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
    logger.info("Relative generating capacity (RCAP) =\t" + getRCAP());
    logger.info("Relative concentration (RCON) =\t" + getRCON());
    logger.info("Strategic buyer market-power (SMPB) =\t" + getSMPB());
    logger.info("Strategic seller market-power (SMPS) =\t" + getSMPS());
  }

  public Map getVariables() {
    HashMap vars = new HashMap();
    vars.putAll(super.getVariables());
    vars.put(VAR_RCAP, new Double(getRCAP()));
    vars.put(VAR_RCON, new Double(getRCON()));
    return vars;
  }

}
