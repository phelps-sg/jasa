/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.io.Serializable;

import uk.ac.liv.util.Debug;
import uk.ac.liv.util.Resetable;

import uk.ac.liv.auction.core.*;

import uk.ac.liv.auction.stats.*;

import uk.ac.liv.auction.agent.*;


/**
 * <p>
 * Calculate the market-power and efficiency variables described in:
 * </p>
 * <p>
 * "Markert Power and Efficiency in a Computational Electricity Market
 * with Discriminatory Double-Auction Pricing"
 * Nicolaisen, J.; Petrov, V.; and Tesfatsion, L.
 * in IEEE Trans. on Evol. Computation, Vol. 5, No. 5. 2001
 * </p>
 *
 * @author Steve Phelps
 */

public class ElectricityStats implements Serializable, Cloneable, MarketStats {

  /**
   * The auction for which we are calculating market statistics.
   */
  protected RoundRobinAuction auction;

  /**
   * The relative concentration of sellers to buyers.
   */
  protected double rCon;

  /**
   * The relative generating-capacity of buyers to sellers.
   */
  protected double rCap;

  /**
   * The profits of the buyers in competitive equilibrium.
   */
  protected double pBCE = 0;

  /**
   * The profits of the sellers in competitive equilibrium.
   */
  protected double pSCE = 0;

  /**
   * The profits of the buyers in the actual auction.
   */
  protected double pBA;

  /**
   * The profits of the sellers in the actual auction.
   */
  protected double pSA;

  /**
   * The market-power of buyers.
   */
  protected double mPB;

  /**
   * The market-power of sellers.
   */
  protected double mPS;

  /**
   * Global market efficiency.
   */
  protected double eA;

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
  protected double numSellers;

  /**
   * The number of buyers.
   */
  protected double numBuyers;

  /**
   * The total generating-capacity of buyers.
   */
  protected int buyerCap;

  /**
   * The total generating-capacity of sellers.
   */
  protected int sellerCap;

  /**
   * The equilibrium calculations for this auction.
   */
  protected EquilibriaStats standardStats = null;

  /**
   * The approximated equilibrium price.
   */
  protected double equilibPrice;


  public ElectricityStats( RoundRobinAuction auction ) {
    this.auction = auction;
    calculate();
  }

  public ElectricityStats() {
  }

  /**
   * @deprecated
   */
  public ElectricityStats( long minPrice, long maxPrice, RoundRobinAuction auction ) {
    this(auction);
  }

  /**
   * @deprecated
   */
  public void setPriceRange( long minPrice, long maxPrice ) {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
  }


  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
  }


  public void recalculate() {
    calculate(false);
  }


  public void calculate() {
    calculate(true);
  }


  protected void calculate( boolean equilibrium ) {

    zeroTotals();
    if ( equilibrium ) {
      calculateEquilibria();
      zeroEquilibriumTotals();
      equilibPrice = calculateEquilibriumPrice();
    }

    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      AbstractTraderAgent trader = (AbstractTraderAgent) i.next();
      if ( trader.isSeller() ) {
        numSellers++;
        sellerCap += getCapacity(trader);
        pSA += getProfits(trader);
        if ( equilibrium ) {
          pSCE += equilibriumProfits(trader);
        }
      } else {
        numBuyers++;
        buyerCap += getCapacity(trader);
        pBA += getProfits(trader);
        if ( equilibrium ) {
          pBCE += equilibriumProfits(trader);
        }
      }
    }

    rCon = numSellers / numBuyers;
    rCap = (double) buyerCap / (double) sellerCap;
    mPB = (pBA - pBCE) / pBCE;
    mPS = (pSA - pSCE) / pSCE;
    eA = (pBA + pSA) / (pBCE + pSCE) * 100;
  }


  protected void calculateEquilibria() {
    if ( standardStats == null ) {
      standardStats = new EquilibriaStats(auction);
      standardStats.calculate();
    } else {
      standardStats.recalculate();
    }
  }


  protected void zeroTotals() {
    sellerCap = 0;
    buyerCap = 0;
    pBA = 0;
    pSA = 0;
    numBuyers = 0;
    numSellers = 0;
  }


  protected void zeroEquilibriumTotals() {
    pSCE = 0;
    pBCE = 0;
  }


  public double calculateEquilibriumPrice() {
    return (standardStats.getMinPrice() + standardStats.getMaxPrice()) / 2;
  }


  public double equilibriumProfits( AbstractTraderAgent trader ) {
    double surplus = 0;
    if ( trader.isSeller() ) {
      surplus = equilibPrice - trader.getPrivateValue();
    } else {
      surplus = trader.getPrivateValue() - equilibPrice;
    }
    //TODO
    if ( surplus < 0 ) {
      surplus = 0;
    }
    return auction.getAge() * equilibQuant(trader, equilibPrice) * surplus;
  }


  protected double getProfits( AbstractTraderAgent trader ) {
    return ((ElectricityTrader) trader).getProfits();
  }


  protected double getCapacity( AbstractTraderAgent trader ) {
    return ((ElectricityTrader) trader).getCapacity();
  }


  public double equilibQuant( AbstractTraderAgent t, double price ) {
    double privateValue = t.getPrivateValue();
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
      + "\n\tmPB:" + mPB + "\n\tmPS:" + mPS + "\n\tpBA:" + pBA
      + "\n\tpSA:" + pSA + "\n\tpBCE:" + pBCE + "\n\tpSCE:" + pSCE
      + "\n\teA:" + eA
      + "\n\tstandardStats:" + standardStats
      + "\n)";
  }

  protected void simulateTruthfulBidding() {
    Auctioneer auctioneer = auction.getAuctioneer();
    ((Resetable) auctioneer).reset();
    LinkedList shouts = new LinkedList();
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      ElectricityTrader trader = (ElectricityTrader) i.next();
      Shout truth = ShoutPool.fetch(trader, trader.getCapacity(),
                                    trader.getPrivateValue(), trader.isBuyer());
      shouts.add(truth);
      try {
        auctioneer.newShout(truth);
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
      ShoutPool.release(s);
    }

  }

  public void calculateStrategicMarketPower() {
    simulateTruthfulBidding();
    Iterator i = auction.getTraderIterator();
    double buyerProfits = 0;
    double sellerProfits = 0;
    while ( i.hasNext() ) {
      ElectricityTrader trader = (ElectricityTrader) i.next();
      if ( trader.isBuyer() ) {
        buyerProfits += trader.getProfits();
      } else {
        sellerProfits += trader.getProfits();
      }
    }
    pBT = truthfulProfits(buyerProfits, pBA);
    pST = truthfulProfits(sellerProfits, pSA);
    sMPB = (pBA - pBT) / pBT;
    sMPS = (pSA - pST) / pST;
  }

  protected double truthfulProfits( double singleRoundProfits,
                                    double accumulatedProfits ) {
    return (singleRoundProfits - accumulatedProfits) * auction.getAge();
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
   * Get the equilibrium market statistics.
   */
  public EquilibriaStats getEquilibriaStats() {
    return standardStats;
  }

}


class ElectricityMetaStats extends MetaMarketStats {

  public ElectricityMetaStats( double min, double max, RoundRobinAuction auction  ) {
    super( (long) min, (long) max, auction);
  }

  public int quantity( AbstractTraderAgent agent ) {
    return ((ElectricityTrader) agent).getCapacity();
  }

}
