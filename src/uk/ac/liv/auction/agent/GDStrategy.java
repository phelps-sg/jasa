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

package uk.ac.liv.auction.agent;

import java.io.Serializable;

import uk.ac.liv.util.Prototypeable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionOpenEvent;

import uk.ac.liv.auction.stats.HistoricalDataReport;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * An implementation of the Gjerstad Dickhaut strategy. Agents using this
 * strategy calculate the probability of any bid being accepted and bid to
 * maximize expected profit. See
 * </p>
 * <p>
 * "Price Formation in Double Auctions" S. Gjerstad, J. Dickhaut and R. Palmer
 * </p>
 * 
 * <p>
 * Note that you must configure a logger of type HistoricalDataReport in
 * order to use this strategy.
 * </p>
 * 
 * </p>
 * <p>
 * <b>Parameters </b> <br>
 * <table>
 * <tr>
 * <td valign=top><i>base </i> <tt>.maxprice</tt><br>
 * <font size=-1>double &gt;= 0 </font></td>
 * <td valign=top>(max price in auction)</td>
 * <tr>
 * 
 * </table>
 * 
 * @see uk.ac.liv.auction.stats.HistoricalDataReport
 * 
 * @author Marek Marcinkiewicz
 * @version $Revision$
 */

public class GDStrategy extends FixedQuantityStrategyImpl implements
                                                         Serializable,
                                                         Prototypeable {

  protected double maxPoint = 0;

  protected double max = 0;

  protected MarketQuote quote;
  
  protected HistoricalDataReport historyStats;

  public static final String P_MAXPRICE = "maxprice";

  public static double MAX_PRICE = 200;

  static Logger logger = Logger.getLogger(GDStrategy.class);

  public GDStrategy () {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    MAX_PRICE = parameters.getDoubleWithDefault(base.push(P_MAXPRICE), null,
        MAX_PRICE);
  }

  public Object protoClone() {
    GDStrategy clone = new GDStrategy();
    return clone;
  }
  
  public void eventOccurred( AuctionEvent event ) {
    super.eventOccurred(event);
    if ( event instanceof AuctionOpenEvent ) {
      auctionOpen((AuctionOpenEvent) event);
    }
  }
  
  public void auctionOpen( AuctionOpenEvent event ) {
    auction = event.getAuction();
    historyStats = 
      (HistoricalDataReport) auction.getReport(HistoricalDataReport.class);

    if ( historyStats == null ) {
      throw new AuctionError(getClass() + " requires a HistoricalDataReport to be configured");
    }
  }
  

  public boolean modifyShout( Shout.MutableShout shout ) {

    super.modifyShout(shout);
    
    quote = auction.getQuote();

    List sortedShouts = historyStats.getSortedShouts();

    boolean changed = false;
    double lastPoint = 0;
    double lastP = 0;
    double currentPoint = 0;
    double currentP = 0;
    maxPoint = 0;
    max = 0;

    if ( !agent.isBuyer() ) {
      lastP = 1;
      currentP = 1;
    }

    Iterator i = sortedShouts.iterator();

    while ( i.hasNext() ) {
      Shout nextShout = (Shout) i.next();
      if ( nextShout.getPrice() > lastPoint ) {
        currentPoint = nextShout.getPrice();
        currentP = calculateProbability(currentPoint);
        getMax(lastPoint, lastP, currentPoint, currentP);
        lastPoint = currentPoint;
        lastP = currentP;
      }
    }

    currentPoint = MAX_PRICE;
    currentP = 1;
    if ( !agent.isBuyer() ) {
      currentP = 0;
    }
    getMax(lastPoint, lastP, currentPoint, currentP);

    //set quote
    if ( maxPoint > 0 ) {
      shout.setPrice(maxPoint);
      return true;
    } else {
      return false;
    }

  }

  private double calculateProbability( double price ) {
    //              (taken bids below price) + (all asks below price)
    //-------------------------------------------------------------------------------
    //(taken bids below price) + (all asks below price) + (rejected bids above
    // price)
    if ( agent.isBuyer() ) {
      return ((double) (historyStats.getNumberOfBids(-1 * price, true) + historyStats
          .getNumberOfAsks(-1 * price, false)))
          / ((double) (historyStats.getNumberOfBids(-1 * price, true)
              + historyStats.getNumberOfAsks(-1 * price, false) + (historyStats
              .getNumberOfBids(price, false) - historyStats.getNumberOfBids(
              price, true))));
    } else {
      //              (taken asks above price) + (all bids above price)
      //-------------------------------------------------------------------------------
      //(taken asks above price) + (all bids above price) + (rejected asks
      // below price)
      return ((double) (historyStats.getNumberOfAsks(price, true) + historyStats
          .getNumberOfBids(price, false)))
          / ((double) (historyStats.getNumberOfAsks(price, true)
              + historyStats.getNumberOfBids(price, false) + (historyStats
              .getNumberOfAsks(-1 * price, false) - historyStats.getNumberOfAsks(-1
              * price, true))));
    }
  }

  private void getMax( double a1, double p1, double a2, double p2 ) {
    double pvalue = agent.getValuation(auction);

    double denom = (-6 * a1 * a1 * a2 * a2) + (4 * a1 * a1 * a1 * a2)
        + (4 * a1 * a2 * a2 * a2) + (-1 * a1 * a1 * a1 * a1)
        + (-1 * a2 * a2 * a2 * a2);
    double alpha3 = (2 * ((a1 * (p1 - p2)) + (a2 * (p2 - p1)))) / denom;
    double alpha2 = (3 * ((a1 * a1 * (p2 - p1)) + (a2 * a2 * (p1 - p2))))
        / denom;
    double alpha1 = (6 * (p1 - p2) * ((a1 * a1 * a2) - (a1 * a2 * a2))) / denom;
    double alpha0 = ((p1 * ((4 * a1 * a2 * a2 * a2) + (-3 * a1 * a1 * a2 * a2) + (-1
        * a2 * a2 * a2 * a2))) + (p2 * ((4 * a1 * a1 * a1 * a2)
        + (-3 * a1 * a1 * a2 * a2) + (-1 * a1 * a1 * a1 * a1))))
        / denom;

    int temp_maxPoint = 0;
    double temp = 0;

    double p = 0;

    double start = a1;
    double end = a2;
    if ( agent.isBuyer() ) {
      if ( a2 > pvalue ) {
        end = pvalue;
      }
    } else {
      if ( a1 < pvalue ) {
        start = pvalue;
      }
    }

    for ( double i = start; i < end; i++ ) {
      p = (alpha3 * i * i * i) + (alpha2 * i * i) + (alpha1 * i) + alpha0;
      if ( agent.isBuyer() ) {
        temp = p * (pvalue - i);
      } else {
        temp = p * (i - pvalue);
      }
      if ( temp > max ) {
        max = temp;
        maxPoint = i;
      }
    }
  }

  public void endOfRound( Auction auction ) {
    // Do nothing
  }


}