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


package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.agent.AbstractTraderAgent;
import uk.ac.liv.auction.core.*;

import uk.ac.liv.util.Debug;
import uk.ac.liv.util.Resetable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.util.*;

import huyd.poolit.*;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * <p>
 * A class to calculate the true equilibrium price and quantity
 * ranges for a given auction.
 * </p>
 *
 * @author Steve Phelps
 */

public class EquilibriaStats implements MarketStats, Resetable {

  protected FourHeapShoutEngine shoutEngine = new FourHeapShoutEngine();

  protected RoundRobinAuction auction;

  protected double minPrice, maxPrice;
  protected int minQty, maxQty;

  protected boolean equilibriaFound = false;

  protected double pBCE = 0;
  protected double pSCE = 0;

  protected ArrayList shouts;

  public EquilibriaStats( RoundRobinAuction auction ) {
    this();
    this.auction = auction;
  }

  public EquilibriaStats() {
    shouts = new ArrayList();
  }

  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
  }

  public void setPriceRange( long min, long max ) {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
  }

  public void recalculate() {
    reset();
    calculate();
  }

  public void calculate() {
    simulateDirectRevelation();
    Shout hiAsk = shoutEngine.getHighestMatchedAsk();
    Shout loBid = shoutEngine.getLowestMatchedBid();
    if ( hiAsk == null || loBid == null ) {
      equilibriaFound = false;
    } else {
      calculateEquilibriaPriceRange();
      calculateQuantitiesAndProfits();
      equilibriaFound = true;
    }
    releaseShouts();
  }

  protected void simulateDirectRevelation() {
    try {
      Iterator traders = auction.getTraderIterator();
      while ( traders.hasNext() ) {
        AbstractTraderAgent trader = (AbstractTraderAgent) traders.next();
        int quantity = trader.determineQuantity(auction);
        double value = trader.getPrivateValue();
        boolean isBid = trader.isBuyer();
        Shout shout = ShoutPool.fetch(trader, quantity, value, isBid);
        shouts.add(shout);
        if ( isBid ) {
          shoutEngine.newBid(shout);
        } else {
          shoutEngine.newAsk(shout);
        }
      }
    } catch ( DuplicateShoutException e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
  }

  protected void calculateQuantitiesAndProfits() {
    int qty = 0;
    List matches = shoutEngine.getMatchedShouts();
    Iterator i = matches.iterator();
    while ( i.hasNext() ) {
      Shout bid = (Shout) i.next();
      Shout ask = (Shout) i.next();
      qty += bid.getQuantity();

      pBCE += equilibriumProfits(bid.getQuantity(),
                                  (AbstractTraderAgent) bid.getAgent());

      pSCE += equilibriumProfits(ask.getQuantity(),
                                  (AbstractTraderAgent) ask.getAgent());

    }

    minQty = qty;
    maxQty = qty;
  }

  public double equilibriumProfits( int quantity, AbstractTraderAgent trader ) {
    double surplus = 0;
    double ep = calculateMidEquilibriumPrice();
    if ( trader.isSeller() ) {
      surplus = ep - trader.getPrivateValue();
    } else {
      surplus = trader.getPrivateValue() - ep;
    }
    //TODO
    if ( surplus < 0 ) {
      surplus = 0;
    }
    return auction.getAge() * quantity * surplus;
  }

  protected void calculateEquilibriaPriceRange() {
    minPrice = Shout.maxPrice(shoutEngine.getHighestMatchedAsk(), shoutEngine.getHighestUnmatchedBid());
    maxPrice = Shout.minPrice(shoutEngine.getLowestUnmatchedAsk(), shoutEngine.getLowestMatchedBid());
    if ( ! (minPrice <= maxPrice) ) {
      shoutEngine.printState();
      Debug.assertTrue("equilibria price range invalid; minPrice="+minPrice + " maxPrice="+maxPrice,minPrice <= maxPrice);
    }
  }

  protected void releaseShouts() {
    Iterator i = shouts.iterator();
    while ( i.hasNext() ) {
      Shout s = (Shout) i.next();
      ShoutPool.release(s);
    }
  }

  public void reset() {
    shouts.clear();
    shoutEngine.reset();
    pBCE = 0;
    pSCE = 0;
  }

  public double getMinPrice() {
    return minPrice;
  }

  public double getMaxPrice() {
    return maxPrice;
  }

  public double getMinQuantity() {
    return minQty;
  }

  public double getMaxQuantity() {
    return maxQty;
  }

  public double getPBCE() {
    return pBCE;
  }

  public double getPSCE() {
    return pSCE;
  }

  public boolean equilibriaExists() {
    return equilibriaFound;
  }

  public double calculateMidEquilibriumPrice() {
    return (getMinPrice() + getMaxPrice()) / 2;
  }


  public String toString() {
    return "(" + getClass() + " equilibriaFound:" + equilibriaFound + " minPrice:" + minPrice + " maxPrice:" + maxPrice + " minQty: " + minQty + " maxQty:" + maxQty + ")";
  }

}
