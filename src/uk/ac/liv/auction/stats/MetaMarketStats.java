package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.agent.AbstractTraderAgent;

import uk.ac.liv.util.CummulativeStatCounter;
import uk.ac.liv.util.Debug;

import java.util.*;

import java.io.Serializable;



public class MetaMarketStats implements Serializable {

  HashMap supplyCurve = new HashMap();
  HashMap demandCurve = new HashMap();
  List traders;
  long minPrice, maxPrice;
  int increment;

  LinkedList equilibria;
  CummulativeStatCounter priceStats, qtyStats;

  public MetaMarketStats( long minPrice, long maxPrice, List traders ) {
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
    this.traders = traders;
    calculateSupplyAndDemand();
    calculateMarketEquilibria();
  }

  public void calculateSupplyAndDemand( ) {
    for( long price=minPrice; price<maxPrice; price++ ) {
      Iterator i = traders.iterator();
      while ( i.hasNext() ) {
        AbstractTraderAgent trader = (AbstractTraderAgent) i.next();
        double privateValue = trader.getPrivateValue();
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
    for( long p=minPrice; p<maxPrice; p++ ) {
      Long price = new Long(p);
      Integer supply = (Integer) supplyCurve.get(price);
      Integer demand = (Integer) demandCurve.get(price);
      if ( supply != null && supply.equals(demand) ) {
        equilibria.add(price);
        equilibria.add(supply);
        priceStats.newData(price.doubleValue());
        qtyStats.newData(supply.doubleValue());
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

}