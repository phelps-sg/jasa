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

package test.uk.ac.liv.auction.stats;

import java.util.*;

import junit.framework.*;

import test.uk.ac.liv.auction.agent.MockTrader;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.AbstractTraderAgent;

import uk.ac.liv.auction.stats.EquilibriaStats;
import uk.ac.liv.auction.stats.MarketStats;

public class EquilibriaStatsTest extends TestCase {

  RandomRobinAuction auction;

  MockTrader[] traders;

  Random randGenerator = new Random();

  static final int N = 10;
  static final double MAX_PV = 100;

  public EquilibriaStatsTest( String name ) {
    super(name);
  }

  public void setUp() {
    auction = new RandomRobinAuction();
    traders = new MockTrader[N];
    for( int i=0; i<N; i++ ) {
      traders[i] = new MockTrader(this, 0, 0, 0, randGenerator.nextBoolean());
      auction.register(traders[i]);
    }
  }

  protected void randomizePrivateValues() {
    for( int i=0; i<N; i++ ) {
      traders[i].setPrivateValue(randGenerator.nextDouble() * MAX_PV);
    }
  }

  public void testEquilibriaStats() {
    EquilibriaStats stats = new EquilibriaStats(auction);
    TestEquilibriaStats testStats = new TestEquilibriaStats(auction);
    for( int i=0; i<1000; i++ ) {
      randomizePrivateValues();
      stats.recalculate();
      testStats.recalculate();
      System.out.println(stats);
      System.out.println(testStats);
      //assertTrue( stats.getMaxPrice() == testStats.getMaxPrice() );
      //assertTrue( stats.getMinPrice() == testStats.getMinPrice() );
      //assertTrue( stats.getMinQuantity() == testStats.getMinQuantity() );
      //assertTrue( stats.getMaxQuantity() == testStats.getMaxQuantity() );
    }
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run (suite());
  }

  public static Test suite() {
    return new TestSuite(EquilibriaStatsTest.class);
  }

}

class TestEquilibriaStats implements MarketStats {

  RoundRobinAuction auction = null;

  List buyers = null;
  List sellers = null;

  ArrayList demandCurve = null;
  ArrayList supplyCurve = null;

  double equilibriaMinPrice, equilibriaMaxPrice;
  long equilibriaMinQty, equilibriaMaxQty;

  boolean equilibriaFound;

  static Comparator ascendingValues = new AscendingTraderComparator();
  static Comparator descendingValues = new DescendingTraderComparator();
  static Comparator quantityComparator = new QuantityComparator();

  public TestEquilibriaStats( RoundRobinAuction auction ) {
    this.auction = auction;
    calculate();
  }

  public TestEquilibriaStats() {
  }

  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
  }

  public void setPriceRange( long min, long max ) {
  }


  public void initialise() {
    sortTraders();
  }

  public void reset() {
    sortTraders();
  }

  public void calculate() {
    reset();
    recalculate();
  }

  public void recalculate() {
    buildCurves();
    calculateEquilibria();
  }

  protected void sortTraders() {
    int numTraders = auction.getNumberOfRegisteredTraders();
    buyers = new ArrayList(numTraders);
    sellers = new ArrayList(numTraders);
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      AbstractTraderAgent agent = (AbstractTraderAgent) i.next();
      if ( agent.isBuyer() ) {
        buyers.add(agent);
      } else {
        sellers.add(agent);
      }
    }

  }

  public void generateReport() {
    //DO nothing
  }

  protected static PriceQtyTuple newTuple( double minPrice, double maxPrice, long quantity ) {
    return new PriceQtyTuple(minPrice, maxPrice, quantity);
  }

  protected void buildCurve( ArrayList curve, List traders, double initVal, boolean ascending ) {
    double currentVal = initVal;
    long quantity = 0;
    for( int i=0; i<traders.size(); i++ ) {
      AbstractTraderAgent trader = (AbstractTraderAgent) traders.get(i);
      double val = trader.getValuation(auction);
      PriceQtyTuple priceQty = null;
      if ( ascending ) {
        priceQty = newTuple(currentVal, val, quantity);
      } else {
        priceQty = newTuple(val, currentVal, quantity);
      }
      curve.add(priceQty);
      while ( i<traders.size() &&
              ((AbstractTraderAgent) traders.get(i)).getValuation(auction) == val ) {
        quantity += ((AbstractTraderAgent) traders.get(i)).determineQuantity(auction);
        i++;
      }
      currentVal = val;
    }
  }

  protected void cleanCurve( ArrayList curve ) {
    curve.clear();
  }

  protected void buildCurves() {

    Collections.sort(buyers, descendingValues);
    Collections.sort(sellers, ascendingValues);

    if ( supplyCurve == null ) {
      supplyCurve = new ArrayList(sellers.size());
    } else {
      cleanCurve(supplyCurve);
    }

    if ( demandCurve == null ) {
      demandCurve = new ArrayList(buyers.size());
    } else {
      cleanCurve(demandCurve);
    }

    buildCurve(supplyCurve, sellers, 0D, true);
    buildCurve(demandCurve, buyers, Double.POSITIVE_INFINITY, false);
  }

  protected void searchSameQuantity() {
    Iterator i = demandCurve.iterator();
    while ( i.hasNext() ) {
      PriceQtyTuple dpq = (PriceQtyTuple) i.next();
      if ( dpq.quantity == 0 ) {
        continue;
      }
      PriceQtyTuple spq = findQty(supplyCurve, dpq);
      if ( spq != null ) {
        // found equilibria
        //equilibriaMinPrice = Math.max(spq.minPrice, dpq.minPrice);
        //equilibriaMaxPrice = Math.min(spq.maxPrice, dpq.maxPrice);
        equilibriaMinPrice = Math.min(spq.maxPrice, dpq.maxPrice);
        equilibriaMaxPrice = Math.max(spq.minPrice, dpq.minPrice);
        if ( equilibriaMaxPrice > equilibriaMinPrice ) {
          equilibriaMinQty = dpq.quantity;
          equilibriaMaxQty = equilibriaMinQty;
          equilibriaFound = true;
          break;
        }
      }
    }
  }

  protected void searchIntersect() {
    for( int i=0; i<demandCurve.size()-1; i++ ) {
      PriceQtyTuple t1 = (PriceQtyTuple) demandCurve.get(i);
      PriceQtyTuple t2 = (PriceQtyTuple) demandCurve.get(i+1);
      PriceQtyTuple ts = null;
      if ( (ts = findIntersection(t1, t2, supplyCurve)) != null ) {
        equilibriaFound = true;
        equilibriaMinQty = ts.quantity;
        equilibriaMaxQty = ts.quantity;
        equilibriaMinPrice = t1.maxPrice;
        equilibriaMaxPrice = t1.maxPrice;
        break;
      }
    }
  }

  protected void calculateEquilibria() {
    equilibriaFound = false;
    searchSameQuantity();
    if ( ! equilibriaFound ) {
      searchIntersect();
    }
  }

  protected PriceQtyTuple findQty( ArrayList curve, PriceQtyTuple tuple ) {
    int i = Collections.binarySearch(curve, tuple, quantityComparator);
    if ( i >= 0 ) {
      return (PriceQtyTuple) curve.get(i);
    } else {
      return null;
    }
  }

  protected PriceQtyTuple findIntersection( PriceQtyTuple t1, PriceQtyTuple t2,
                                      ArrayList curve ) {
    double x = t1.maxPrice;
    double y1 = t1.quantity;
    double y2 = t2.quantity;
    Iterator i = curve.iterator();
    while ( i.hasNext() ) {
      PriceQtyTuple ct = (PriceQtyTuple) i.next();
      if ( x >= ct.minPrice && x <= ct.maxPrice ) {
        if ( ct.quantity <= Math.max(y1,y2) && ct.quantity >= Math.min(y1,y2) ) {
          return ct;
        }
      }
    }
    return null;
  }

  public double getMinPrice() {
    return equilibriaMinPrice;
  }

  public double getMaxPrice() {
    return equilibriaMaxPrice;
  }

  public double getMinQuantity() {
    return equilibriaMinQty;
  }

  public double getMaxQuantity() {
    return equilibriaMaxQty;
  }

  public boolean equilibriaExists() {
    return equilibriaFound;
  }

  public String toString() {
    return "(" + getClass() + " equilibriaFound:" + equilibriaFound + " equilibriaMinPrice:" + equilibriaMinPrice + " equilibriaMaxPrice:" + equilibriaMaxPrice + " equilibriaMinQty: " + equilibriaMinQty + " equilibriaMaxQty:" + equilibriaMaxQty + ")";
  }

}


class AscendingTraderComparator implements Comparator {

  static Auction auction = new RoundRobinAuction();

  public int compare( Object a, Object b ) {
    AbstractTraderAgent x = (AbstractTraderAgent) a;
    AbstractTraderAgent y = (AbstractTraderAgent) b;
    if ( x.getValuation(auction) > y.getValuation(auction) ) {
      return 1;
    } else if ( x.getValuation(auction) == y.getValuation(auction) ) {
      return 0;
    } else {
      return -1;
    }
  }
}

class DescendingTraderComparator implements Comparator {

  static Auction auction = new RoundRobinAuction();

  public int compare( Object a, Object b ) {
    AbstractTraderAgent x = (AbstractTraderAgent) a;
    AbstractTraderAgent y = (AbstractTraderAgent) b;
    if ( x.getValuation(auction) > y.getValuation(auction) ) {
      return -1;
    } else if ( x.getValuation(auction) == y.getValuation(auction) ) {
      return 0;
    } else {
      return 1;
    }
  }

}

class QuantityComparator implements Comparator {

  public int compare( Object x, Object y ) {
    PriceQtyTuple t1 = (PriceQtyTuple) x;
    PriceQtyTuple t2 = (PriceQtyTuple) y;
    if ( t1.quantity > t2.quantity ) {
      return 1;
    } else if ( t1.quantity == t2.quantity ) {
      return 0;
    } else {
      return -1;
    }
  }
}


class PriceQtyTuple {

  public double minPrice, maxPrice;
  long quantity;

  public PriceQtyTuple() {
  }

  public PriceQtyTuple( double minPrice, double maxPrice, long quantity ) {
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
    this.quantity = quantity;
  }

  public String toString() {
    return "(" + getClass() + " minPrice:" + minPrice + " maxPrice:" + maxPrice + " quantity:" + quantity + ")";
  }
}
