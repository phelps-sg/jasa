/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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

import uk.ac.liv.auction.stats.*;

import uk.ac.liv.auction.agent.AbstractTraderAgent;

/**
 * @author Steve Phelps
 */

public class ElectricityStats
    implements Serializable, Cloneable, MarketStats {

//  RoundRobinAuction auction;
  RoundRobinAuction auction;

  public double rCon, rCap;

  public double pBCE = 0, pSCE = 0;

  public double pBA, pSA;

  public double mPB, mPS;

  public double eA;

  public double numSellers;
  public double numBuyers;

  int buyerCap, sellerCap;

  public EquilibriaStats standardStats = null;

  protected long minPrice, maxPrice;

  static final String P_MIN_PRICE = "minprice";
  static final String P_MAX_PRICE = "maxprice";

  public ElectricityStats( long minPrice, long maxPrice, RoundRobinAuction auction ) {
    this.auction = auction;
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
    calculate();
  }

  public ElectricityStats() {
  }

  public void setPriceRange( long minPrice, long maxPrice ) {
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    minPrice = parameters.getLongWithDefault(base.push(P_MIN_PRICE), null, 0);
    maxPrice = parameters.getLongWithDefault(base.push(P_MAX_PRICE), null, 200);
  }

  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
    standardStats.setAuction(auction);
  }


  public void recalculate() {
    calculate(false);
  }

  public void calculate() {
    calculate(true);
  }

  protected void calculate( boolean equilibrium ) {
    if ( equilibrium ) {
      if ( standardStats == null ) {
        standardStats = new EquilibriaStats(auction);
      } else {
        standardStats.recalculate();
      }
    }
    Iterator i = auction.getTraderIterator();
    sellerCap = 0;
    buyerCap = 0;
    pBA = 0;
    pSA = 0;
    numBuyers = 0;
    numSellers = 0;
    if ( equilibrium ) {
      pSCE = 0;
      pBCE = 0;
    }
    double equilibPrice = (standardStats.getMinPrice() + standardStats.getMaxPrice()) / 2;
    //Debug.println("equilibPrice = " + equilibPrice);
    while ( i.hasNext() ) {
      ElectricityTrader trader = (ElectricityTrader) i.next();
      if ( trader.isSeller() ) {
        //Debug.println("seller: " + trader);
        numSellers++;
        sellerCap += trader.getCapacity();
        pSA += trader.getProfits();
        /*
        Debug.println("age = " + auction.getAge());
        Debug.println("equilibQuant() = " + equilibQuant(trader, equilibPrice));
        Debug.println("privValue = " + trader.getPrivateValue());
        */
        if ( equilibrium ) {
          double ep = auction.getAge() * (
                    equilibQuant(trader, equilibPrice)
                      * (equilibPrice - trader.getPrivateValue()));
          pSCE += ep;
        }
      } else {
        //Debug.println("buyer: " + trader);
        numBuyers++;
        buyerCap += trader.getCapacity();
        pBA += trader.getProfits();
        if ( equilibrium ) {
          double ep = auction.getAge() * (
                    equilibQuant(trader, equilibPrice)
                      * (trader.getPrivateValue() - equilibPrice));
          pBCE += ep;
        }
      }
    }
    rCon = numSellers / numBuyers;
    rCap = (double) buyerCap / (double) sellerCap;
    mPB = (pBA - pBCE) / pBCE;
    mPS = (pSA - pSCE) / pSCE;
    eA = (pBA + pSA) / (pBCE + pSCE) * 100;
  }

  public double equilibQuant( ElectricityTrader t, double price ) {
    double privateValue = t.getPrivateValue();
    if ( t.isBuyer() ) {
      if ( price > privateValue ) {
        return 0;
      } else {
        return t.getCapacity();
      }
    } else {
      if ( price > privateValue ) {
        return t.getCapacity();
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

}

class ElectricityMetaStats extends MetaMarketStats {

  public ElectricityMetaStats( double min, double max, RoundRobinAuction auction  ) {
    super( (long) min, (long) max, auction);
  }

  public int quantity( AbstractTraderAgent agent ) {
    return ((ElectricityTrader) agent).getCapacity();
  }

}