/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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
 * "Market Power and Efficiency in a Computational Electricity Market
 * with Discriminatory Double-Auction Pricing"
 * Nicolaisen, J.; Petrov, V.; and Tesfatsion, L.
 * in IEEE Transactions on Evolutionary Computation, Vol. 5, No. 5. 2001
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class ElectricityStats extends SurplusStats implements Cloneable {

  /**
   * The relative concentration of sellers to buyers.
   */
  protected double rCon;

  /**
   * The relative generating-capacity of buyers to sellers.
   */
  protected double rCap;

  /**
   * The market-power of buyers.
   */
  protected double mPB;

  /**
   * The market-power of sellers.
   */
  protected double mPS;

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
   * The total generating-capacity of buyers.
   */
  protected int buyerCap;

  /**
   * The total generating-capacity of sellers.
   */
  protected int sellerCap;

  /**
   * The approximated equilibrium price.
   */
  protected double equilibPrice;

  /**
   * The age of the auction in rounds.
   */
  protected int auctionAge;

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
      AbstractTraderAgent trader = (AbstractTraderAgent) i.next();
      if ( trader.isSeller() ) {
        numSellers++;
        sellerCap += getCapacity(trader);
      } else {
        numBuyers++;
        buyerCap += getCapacity(trader);
      }
    }

    rCon = numSellers / numBuyers;
    rCap = (double) buyerCap / (double) sellerCap;
    mPB = (pBA - pBCE) / pBCE;
    mPS = (pSA - pSCE) / pSCE;
    
//    calculateStrategicMarketPower();
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
  public double equilibriumProfits( int quantity, AbstractTraderAgent trader ) {
    double surplus = 0;
    if ( trader.isSeller() ) {
      surplus = equilibPrice - trader.getPrivateValue(auction);
    } else {
      surplus = trader.getPrivateValue(auction) - equilibPrice;
    }
    if ( surplus < 0 ) {
      surplus = 0;
    }
    return auctionAge * equilibQuant(trader, equilibPrice) * surplus;
  }
*/

  protected double getProfits( AbstractTraderAgent trader ) {
    return ((ElectricityTrader) trader).getProfits();
  }


  protected double getCapacity( AbstractTraderAgent trader ) {
    return ((ElectricityTrader) trader).getCapacity();
  }


  public double equilibQuant( AbstractTraderAgent t, double price ) {
    double privateValue = t.getValuation(auction);
    if ( t.isBuyer() ) {
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
      + "\n\tmPB:" + mPB + "\n\tmPS:" + mPS
      + "\n\tsMPB:" + sMPB + "\n\tsMPS:" + sMPS + "\n\tpBA:" + pBA
      + "\n\tpSA:" + pSA + "\n\tpBCE:" + pBCE + "\n\tpSCE:" + pSCE
      + "\n\tpST:" + pST + "\n\tpBT:" + pBT
      + "\n)";
  }

  protected void simulateTruthfulBidding() {
    Auctioneer auctioneer = (Auctioneer)
        ((Prototypeable) auction.getAuctioneer()).protoClone();
    LinkedList shouts = new LinkedList();
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      ElectricityTrader trader = (ElectricityTrader) i.next();
      Shout truth = ShoutFactory.getFactory().create(trader, trader.getCapacity(),
                                    trader.getValuation(auction), trader.isBuyer());
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
      if ( trader.isBuyer() ) {
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

  /**
   * Get the profits of the buyers in the actual auction.
   */
  public double getPBA() {
    return pBA;
  }

  /**
   * Get the profits of the sellers in the actual auction.
   */
  public double getPSA() {
    return pSA;
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

  /**
   * Get the profits of the sellers in competitive equilibrium.
   */
  public double getPSCE() {
    return pSCE;
  }

  /**
   * Get the profits of the buyers in competitive equilibrium.
   */
  public double getPBCE() {
    return pBCE;
  }

  public double getRCAP() {
    return rCap;
  }

  public double getRCON() {
    return rCon;
  }


  public void generateReport() {
    super.generateReport();
    logger.info("NPT Auction statistics");
    logger.info("----------------------");
    logger.info("Buyer market-power (MPB) =\t" + getMPB());
    logger.info("Seller market-power (MPS) =\t" + getMPS());
    logger.info("Relative generating capacity (RCAP) =\t" + getRCAP());
    logger.info("Relative concentration (RCON) =\t" + getRCON());
    logger.info("Strategic buyer market-power (SMPB) =\t" + getSMPB());
    logger.info("Strategic seller market-power (SMPS) =\t" + getSMPS());
  }
  
  public Map getVariables() {
    HashMap vars = new HashMap();
    vars.putAll(super.getVariables());
    vars.put("electricity.mpb", new Double(getMPB()));
    vars.put("electricity.mps", new Double(getMPS()));
    vars.put("electricity.rcap", new Double(getRCAP()));
    vars.put("electricity.rcon", new Double(getRCON()));
    return vars;
  }

}

