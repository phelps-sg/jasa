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

import java.util.*;

import org.apache.log4j.Logger;

/**
 * <p>
 * A class to calculate the true equilibrium price and quantity
 * ranges for a given auction.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class EquilibriaStats extends DirectRevelationStats
    implements MarketStats {

  /**
   * The minimum equilibrium price.
   */
  protected double minPrice;

  /**
   * The maximum equilibrium price.
   */
  protected double  maxPrice;

  /**
   * The minimum equilibrium quantity.
   */
  protected int minQty;

  /**
   * The maximum equilibrium quantity.
   */
  protected int maxQty;

  /**
   * Do any equilbria exist?
   */
  protected boolean equilibriaFound = false;

  /**
   * The profits of the buyers in equilibrium.
   */
  protected double pBCE = 0;

  /**
   * The profits of the sellers in equilibrium.
   */
  protected double pSCE = 0;

  /**
   * The actual profits of the buyers.
   */
  protected double pBA = 0;

  /**
   * The actual profits of the sellers.
   */
  protected double pSA = 0;

  static Logger logger = Logger.getLogger(EquilibriaStats.class);

  public EquilibriaStats( RoundRobinAuction auction ) {
    super(auction);
  }

  public EquilibriaStats() {
    super();
  }

  public void recalculate() {
    reset();
    calculate();
  }

  public void calculate() {
    super.calculate();
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

    calculateActualProfits();
  }

  protected void calculateActualProfits() {
    pSA = 0;
    pBA = 0;
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      AbstractTraderAgent agent = (AbstractTraderAgent) i.next();
      if ( agent.isSeller() ) {
        pSA += agent.getProfits();
      } else {
        pBA += agent.getProfits();
      }
    }
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
//    shoutEngine.printState();
    minPrice = Shout.maxPrice(shoutEngine.getHighestMatchedAsk(), shoutEngine.getHighestUnmatchedBid());
    maxPrice = Shout.minPrice(shoutEngine.getLowestUnmatchedAsk(), shoutEngine.getLowestMatchedBid());
    if ( ! (minPrice <= maxPrice) ) {
      shoutEngine.printState();
      Debug.assertTrue("equilibria price range invalid; minPrice="+minPrice + " maxPrice="+maxPrice,minPrice <= maxPrice);
    }
  }

  public void initialise() {
    super.initialise();
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
    return "(" + getClass() + " equilibriaFound:" + equilibriaFound +
           " minPrice:" + minPrice + " maxPrice:" + maxPrice +
           " minQty: " + minQty + " maxQty:" + maxQty +
           " pBCE:" + pBCE + " pSCE:" + pSCE + ")";
  }

  public void generateReport() {
    logger.info("");
    logger.info("Equilibrium analysis report");
    logger.info("---------------------------");
    logger.info("");
    logger.info("\tEquilibria Found?\t" + equilibriaFound);
    logger.info("\n\tprice:\n\t\tmin:\t" + minPrice + "\tmax:\t" + maxPrice);
    logger.info("\n\tquantity\n\t\tmin:\t" + minQty + "\tmax:\t" + maxQty + "\n");
    logger.info("\tbuyers' profits in equilibrium:\t" + pBCE);
    logger.info("\tsellers' profits in equilibrium:\t" + pSCE);
    logger.info("");
    logger.info("\tbuyers' actual profits:\t" + pBA);
    logger.info("\tsellers' actual profits:\t" + pSA);
    logger.info("");
  }

}
