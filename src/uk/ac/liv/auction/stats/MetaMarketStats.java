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

package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.agent.AbstractTraderAgent;
import uk.ac.liv.auction.core.RoundRobinAuction;

import uk.ac.liv.util.CummulativeStatCounter;
import uk.ac.liv.util.Debug;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.util.*;

import java.io.Serializable;



public class MetaMarketStats implements MarketStats, Serializable {

  HashMap supplyCurve = new HashMap();

  HashMap demandCurve = new HashMap();

  RoundRobinAuction auction;

  long minPrice, maxPrice;

  int increment;

  LinkedList equilibria;

  CummulativeStatCounter priceStats, qtyStats;


  static final String P_MIN_PRICE = "minprice";
  static final String P_MAX_PRICE = "maxprice";


  public MetaMarketStats( long minPrice, long maxPrice, RoundRobinAuction auction ) {
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
    this.auction = auction;
    calculate();
  }

  public MetaMarketStats() {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    minPrice = parameters.getLongWithDefault(base.push(P_MIN_PRICE), null, 0);
    maxPrice = parameters.getLongWithDefault(base.push(P_MAX_PRICE), null, 200);
  }

  public void setPriceRange( long minPrice, long maxPrice ) {
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
  }

  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
  }

  public void calculate() {
    calculateSupplyAndDemand();
    calculateMarketEquilibria();
  }

  public void calculateSupplyAndDemand( ) {
    for( long price=minPrice; price<maxPrice; price++ ) {
      Iterator i = auction.getTraderIterator();
      while ( i.hasNext() ) {
        AbstractTraderAgent trader = (AbstractTraderAgent) i.next();
        double privateValue = trader.getPrivateValue(auction);
        if ( trader.isSeller() ) {
          if ( privateValue <= price ) {
            incrementAtPrice(supplyCurve, price, quantity(trader));
          }
        } else {
          if ( privateValue  >= price ) {
            incrementAtPrice(demandCurve, price, quantity(trader));
          }
        }
      }
    }
  }

  public void calculateMarketEquilibria() {
    equilibria = new LinkedList();
    priceStats = new CummulativeStatCounter("Equilibria Prices");
    qtyStats = new CummulativeStatCounter("Equilibria Quantities");
    boolean threshold = false;
    for( long p=minPrice; p<maxPrice; p++ ) {
      Long price = new Long(p);
      Integer supply = (Integer) supplyCurve.get(price);
      Integer demand = (Integer) demandCurve.get(price);
      if ( supply != null && demand != null && !threshold
          && (supply.equals(demand) || supply.compareTo(demand) > 0) ) {
        equilibria.add(price);
        equilibria.add(new Integer(supply.intValue() - demand.intValue()));
        priceStats.newData(price.doubleValue());
        qtyStats.newData(supply.doubleValue());
        if (! supply.equals(demand) ) {
          threshold = true;
        }
      }
    }
  }

  public List getMarketEquilibria() {
    return equilibria;
  }

  public CummulativeStatCounter getEquilibriaPriceStats() {
    return priceStats;
  }

  public CummulativeStatCounter getEquilibriaQtyStats() {
    return qtyStats;
  }

  protected static void incrementAtPrice( HashMap curve, long price, int quantity ) {
    Integer current = (Integer) curve.get( new Long(price) );
    if ( current == null ) {
      curve.put( new Long(price), new Integer(quantity) );
    } else {
      curve.put( new Long(price), new Integer( current.intValue() + quantity) );
    }
  }

  public int quantity( AbstractTraderAgent trader ) {
    return 1;
  }

  public String toString() {
    return "(" + getClass() +
              "\n\tpriceStats:" + priceStats +
              "\n\tqtyStats:" + qtyStats + ")";
  }

  public void generateReport() {
    //TODO
  }

}