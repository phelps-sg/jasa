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
import uk.ac.liv.auction.core.*;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * A MarketStats report that calculates the actual surplus of buyers
 * and sellers in the auction verses the theoretical surplus when
 * trades occur at the equilibrium price.  Note that this report
 * assumes that the equilibrium price is constant.  To calculate
 * theoretical surplus with dynamic supply and demand you should configure
 * an EquilibriumSurplusLogger.
 *
 * @see EquilibriumSurplusLogger
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class SurplusStats extends EquilibriaStats {

  /**
   * The profits of the buyers in theoretical equilibrium.
   */
  protected double pBCE = 0;

  /**
   * The profits of the sellers in theoretical equilibrium.
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

  protected int minQty;

  protected int maxQty;

  static Logger logger = Logger.getLogger(SurplusStats.class);


  public SurplusStats( RoundRobinAuction auction ) {
    super(auction);
  }

  public SurplusStats() {
    super();
  }

  public void calculate() {
    super.calculate();
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
    return trader.equilibriumProfits(auction, calculateMidEquilibriumPrice(),
                                       quantity);
  }


  public void initialise() {
    super.initialise();
    pBCE = 0;
    pSCE = 0;
    minQty = 0;
    maxQty = 0;
  }

  public double getMinQuantity() {
    return minQty;
  }

  public double getMaxQuantity() {
    return maxQty;
  }


  /**
   * @return The theoretical surplus available to buyers in competitive
   * equilibrium.
   */
  public double getPBCE() {
    return pBCE;
  }

  /**
   * @return The theoretical surplus available to sellers in competitive
   * equilibrium.
   */
  public double getPSCE() {
    return pSCE;
  }

  /**
   * @return The actual surplus of all buyers in the market.
   */
  public double getPBA() {
    return pBA;
  }

  /**
   * @return The actual surplus of all sellers in the market.
   */
  public double getPSA() {
    return pSA;
  }


  public String toString() {
    return "(" + getClass() + " equilibriaFound:" + equilibriaFound +
           " minPrice:" + minPrice + " maxPrice:" + maxPrice +
           " minQty: " + minQty + " maxQty:" + maxQty +
           " pBCE:" + pBCE + " pSCE:" + pSCE + ")";
  }

  public void generateReport() {
    super.generateReport();
    logger.info("");
    logger.info("Profit analysis");
    logger.info("---------------");
    logger.info("");
    logger.info("\tbuyers' profits in equilibrium:\t" + pBCE);
    logger.info("\tsellers' profits in equilibrium:\t" + pSCE);
    logger.info("");
    logger.info("\tbuyers' actual profits:\t" + pBA);
    logger.info("\tsellers' actual profits:\t" + pSA);
    logger.info("");
  }

}
