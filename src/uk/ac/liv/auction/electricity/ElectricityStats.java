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

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.io.Serializable;

import uk.ac.liv.util.Debug;

import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.core.ParameterizablePricing;
import uk.ac.liv.auction.core.Auctioneer;

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

  protected RoundRobinAuction auction;

  protected double rCon, rCap;

  protected double pBCE = 0, pSCE = 0;

  protected double pBA, pSA;

  protected double mPB, mPS;

  protected double eA;

  protected double numSellers;
  protected double numBuyers;

  protected int buyerCap, sellerCap;

  protected EquilibriaStats standardStats = null;

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

  protected void calculateTruthfulOutcomes() {
    Iterator i = auction.getTraderIterator();
    Auctioneer auctioneer = auction.getAuctioneer();
    while ( i.hasNext() ) {
      ElectricityTrader trader = (ElectricityTrader) i.next();

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

  public double getEA() {
    return eA;
  }

  public double getMPB() {
    return mPB;
  }

  public double getMPS() {
    return mPS;
  }

  public double getPBA() {
    return pBA;
  }

  public double getPSA() {
    return pSA;
  }

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
