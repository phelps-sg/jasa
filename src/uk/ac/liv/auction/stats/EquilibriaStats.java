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

import uk.ac.liv.auction.core.*;

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
   * Do any equilbria exist?
   */
  protected boolean equilibriaFound = false;
  
  protected List matchedShouts;
  
  protected int quantity;

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
      equilibriaFound = true;
      matchedShouts = shoutEngine.getMatchedShouts();
      calculateEquilibriaQuantity();
    }    
  }
  
  protected void calculateEquilibriaQuantity() {
    quantity = 0;
    Iterator i = matchedShouts.iterator();
    while ( i.hasNext() ) {
      Shout bid = (Shout) i.next();
      Shout ask = (Shout) i.next();
      quantity += ask.getQuantity();
    }
  }

  protected void calculateEquilibriaPriceRange() {

    minPrice = Shout.maxPrice(shoutEngine.getHighestMatchedAsk(),
                               shoutEngine.getHighestUnmatchedBid());

    maxPrice = Shout.minPrice(shoutEngine.getLowestUnmatchedAsk(),
                               shoutEngine.getLowestMatchedBid());

    assert minPrice <= maxPrice;
  }


  public void initialise() {
    super.initialise();
    quantity = 0;
  }

  public double getMinPrice() {
    return minPrice;
  }

  public double getMaxPrice() {
    return maxPrice;
  }
  
  public int getQuantity() {
    return quantity;
  }


  public boolean equilibriaExists() {
    return equilibriaFound;
  }

  public double calculateMidEquilibriumPrice() {
    return (getMinPrice() + getMaxPrice()) / 2;
  }

  public String toString() {
    return "(" + getClass() + " equilibriaFound:" + equilibriaFound +
           " minPrice:" + minPrice + " maxPrice:" + maxPrice + ")";
  }

  public void generateReport() {
    logger.info("");
    logger.info("Equilibrium analysis report");
    logger.info("---------------------------");
    logger.info("");
    logger.info("\tEquilibria Found?\t" + equilibriaFound);
    logger.info("\n\tquantity:\t" + quantity + "\n");
    logger.info("\n\tprice:\n\t\tmin:\t" + minPrice + "\tmax:\t" + maxPrice);
    logger.info("");
  }
  
  public Map getVariables() {
    HashMap reportVars = new HashMap();
    reportVars.put("equilibria.found", new Boolean(equilibriaFound));
    reportVars.put("equilibria.quantity", new Long(quantity));
    reportVars.put("equilibria.minprice", new Double(minPrice));
    reportVars.put("equilibria.maxPrice", new Double(maxPrice));
    return reportVars;
  }

}
