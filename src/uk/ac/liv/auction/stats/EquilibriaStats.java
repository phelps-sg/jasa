package uk.ac.liv.auction.stats;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.auction.agent.AbstractTraderAgent;
import uk.ac.liv.auction.core.RoundRobinAuction;

import uk.ac.liv.util.BinaryHeap;
import uk.ac.liv.util.Debug;

import java.util.*;

public class EquilibriaStats implements MarketStats {

  RoundRobinAuction auction;

  BinaryHeap buyers;
  BinaryHeap sellers;

  ArrayList demandCurve;
  ArrayList supplyCurve;

  double equilibriaMinPrice, equilibriaMaxPrice;
  long equilibriaMinQty, equilibriaMaxQty;

  boolean equilibriaFound;

  static Comparator ascendingValues = new AscendingTraderComparator();
  static Comparator descendingValues = new DescendingTraderComparator();

  public EquilibriaStats( RoundRobinAuction auction ) {
    this.auction = auction;
  }

  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
  }

  public void setPriceRange( long min, long max ) {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
  }

  public void calculate() {
    sortTraders();
    buildCurves();
    calculateEquilibria();
  }

  protected void sortTraders() {
    buyers = new BinaryHeap(descendingValues);
    sellers = new BinaryHeap(ascendingValues);
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

  protected void buildCurves() {
    supplyCurve = new ArrayList();
    demandCurve = new ArrayList();

    double currentVal = 0;
    long quantity = 0;
    while ( ! sellers.isEmpty() ) {
      AbstractTraderAgent seller = (AbstractTraderAgent) sellers.removeFirst();
      double val = seller.getPrivateValue();
      supplyCurve.add( new PriceQtyTuple(currentVal, val, quantity) );
      quantity += seller.determineQuantity(auction);
      while ( !sellers.isEmpty() && ((AbstractTraderAgent) sellers.getFirst()).getPrivateValue() == val ) {
        quantity += ((AbstractTraderAgent) sellers.removeFirst()).determineQuantity(auction);
      }
      currentVal = val;
    }

    currentVal = Double.POSITIVE_INFINITY;
    quantity = 0;
    while ( ! buyers.isEmpty() ) {
      AbstractTraderAgent buyer = (AbstractTraderAgent) buyers.removeFirst();
      double val = buyer.getPrivateValue();
      demandCurve.add( new PriceQtyTuple(val, currentVal, quantity) );
      quantity += buyer.determineQuantity(auction);
      while ( !buyers.isEmpty() && ((AbstractTraderAgent) buyers.getFirst()).getPrivateValue() == val ) {
        quantity += ((AbstractTraderAgent) buyers.removeFirst()).determineQuantity(auction);
      }
      currentVal = val;
    }

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
    int i = Collections.binarySearch(curve, tuple, new QuantityComparator());
    return (PriceQtyTuple) curve.get(i);
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


class PriceQtyTuple {

  public double minPrice, maxPrice;
  long quantity;

  public PriceQtyTuple( double minPrice, double maxPrice, long quantity ) {
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
    this.quantity = quantity;
  }

  public String toString() {
    return "(" + getClass() + " minPrice:" + minPrice + " maxPrice:" + maxPrice + " quantity:" + quantity + ")";
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