package uk.ac.liv.auction.stats;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.auction.agent.AbstractTraderAgent;
import uk.ac.liv.auction.core.RoundRobinAuction;

import uk.ac.liv.util.BinaryHeap;
import uk.ac.liv.util.Debug;

import java.util.*;

import huyd.poolit.*;

public class EquilibriaStats implements MarketStats {

  RoundRobinAuction auction;

  List buyers = null;
  List sellers = null;

  ArrayList demandCurve = null;
  ArrayList supplyCurve = null;

  double equilibriaMinPrice, equilibriaMaxPrice;
  long equilibriaMinQty, equilibriaMaxQty;

  boolean equilibriaFound;

  static Pooler tuplePool = null;

  static Comparator ascendingValues = new AscendingTraderComparator();
  static Comparator descendingValues = new DescendingTraderComparator();
  static Comparator quantityComparator = new QuantityComparator();

  static final int TUPLE_POOL_SIZE = 1000;

  public EquilibriaStats( RoundRobinAuction auction ) {
    this.auction = auction;
    calculate();
  }

  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
  }

  public void setPriceRange( long min, long max ) {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
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
    /*
    buyers = new BinaryHeap(descendingValues);
    sellers = new BinaryHeap(ascendingValues); */
    List traders = auction.getTraderList();
    buyers = new ArrayList(traders.size());
    sellers = new ArrayList(traders.size());
    Iterator i = traders.iterator();
    while ( i.hasNext() ) {
      AbstractTraderAgent agent = (AbstractTraderAgent) i.next();
      if ( agent.isBuyer() ) {
        buyers.add(agent);
      } else {
        sellers.add(agent);
      }
    }
    Collections.sort(buyers, descendingValues);
    Collections.sort(sellers, ascendingValues);
  }
/*
  protected void buildCurve( ArrayList curve, BinaryHeap traders,
                              double initVal, double finalVal ) {
    boolean descending = finalVal < initVal;
    double currentVal = initVal;
    long quantity = 0;
    while ( ! traders.isEmpty() ) {
      AbstractTraderAgent trader = (AbstractTraderAgent) traders.removeFirst();
      double val = trader.getPrivateValue();
      quantity = trader.determineQuantity(auction);
      while ( !traders.isEmpty() && ((AbstractTraderAgent) traders.getFirst()).getPrivateValue() == val ) {
        quantity += ((AbstractTraderAgent) traders.removeFirst()).determineQuantity(auction);
      }
      if ( descending ) {
        curve.add( new PriceQtyTuple(val, currentVal, quantity) );
      } else {
        curve.add( new PriceQtyTuple(currentVal, val, quantity) );
      }
      currentVal = val;
    }
  }
*/

  protected synchronized static void initialiseTuplePool() {
    try {
      if ( tuplePool == null ) {
        tuplePool = new FixedPooler(PriceQtyTuple.class, TUPLE_POOL_SIZE);
      }
    } catch ( CreateException e ) {
      e.printStackTrace();
      throw new Error(e);
    }
  }

  protected static PriceQtyTuple newTuple( double minPrice, double maxPrice, long quantity ) {
    initialiseTuplePool();
    try {
      PriceQtyTuple result = (PriceQtyTuple) tuplePool.fetch();
      result.maxPrice = maxPrice;
      result.minPrice = minPrice;
      result.quantity = quantity;
      return result;
    } catch ( FetchException e ) {
      e.printStackTrace();
      return new PriceQtyTuple(minPrice, maxPrice, quantity);
    }
  }

  protected void buildCurve( ArrayList curve, List traders, double initVal, boolean ascending ) {
    double currentVal = initVal;
    long quantity = 0;
    for( int i=0; i<traders.size(); i++ ) {
      AbstractTraderAgent trader = (AbstractTraderAgent) traders.get(i);
      double val = trader.getPrivateValue();
      PriceQtyTuple priceQty = null;
      if ( ascending ) {
        priceQty = newTuple(currentVal, val, quantity);
      } else {
        priceQty = newTuple(val, currentVal, quantity);
      }
      curve.add(priceQty);
      while ( i<traders.size() &&
              ((AbstractTraderAgent) traders.get(i)).getPrivateValue() == val ) {
        quantity += ((AbstractTraderAgent) traders.get(i)).determineQuantity(auction);
        i++;
      }
      currentVal = val;
    }
  }

  protected void cleanCurve( ArrayList curve ) {
    Iterator i = curve.iterator();
    while ( i.hasNext() ) {
      PriceQtyTuple tuple = (PriceQtyTuple) i.next();
      tuplePool.release(tuple);
    }
    curve.clear();
  }

  protected void buildCurves() {

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
        equilibriaMinPrice = Math.max(spq.minPrice, dpq.minPrice);
        equilibriaMaxPrice = Math.min(spq.maxPrice, dpq.maxPrice);
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

  public int compare( Object a, Object b ) {
    AbstractTraderAgent x = (AbstractTraderAgent) a;
    AbstractTraderAgent y = (AbstractTraderAgent) b;
    if ( x.getPrivateValue() > y.getPrivateValue() ) {
      return 1;
    } else if ( x.getPrivateValue() == y.getPrivateValue() ) {
      return 0;
    } else {
      return -1;
    }
  }
}

class DescendingTraderComparator implements Comparator {

  public int compare( Object a, Object b ) {
    AbstractTraderAgent x = (AbstractTraderAgent) a;
    AbstractTraderAgent y = (AbstractTraderAgent) b;
    if ( x.getPrivateValue() > y.getPrivateValue() ) {
      return -1;
    } else if ( x.getPrivateValue() == y.getPrivateValue() ) {
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