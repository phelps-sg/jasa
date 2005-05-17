/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.AuctionError;
import uk.ac.liv.auction.core.ShoutsNotVisibleException;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;

import java.io.Serializable;

import java.util.*;

import org.apache.commons.collections.bag.TreeBag;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Resetable;

import org.apache.log4j.Logger;

/**
 * <p>
 * A report that keeps a historical record of the shouts in the market that lead
 * to the last N transactions. This logger is used to keep historical data that
 * is used by various different trading strategies.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class HistoricalDataReport extends AbstractAuctionReport implements
    Serializable, Resetable {

  protected LinkedList asks = new LinkedList();

  protected LinkedList bids = new LinkedList();

  protected TreeBag sortedShouts = new TreeBag();

  protected HashSet acceptedShouts = new HashSet();

  protected int memorySize = 10;

  protected int currentMemoryCell = 0;

  protected int[] memoryBids = new int[memorySize];

  protected int[] memoryAsks = new int[memorySize];

  protected double lowestAskPrice;

  protected double highestBidPrice;

  static final String P_MEMORYSIZE = "memorysize";

  static Logger logger = Logger.getLogger(HistoricalDataReport.class);

  public void setup( ParameterDatabase parameters, Parameter base ) {
    memorySize = parameters.getIntWithDefault(base.push(P_MEMORYSIZE), null,
        memorySize);
    memoryBids = new int[memorySize];
    memoryAsks = new int[memorySize];
    for ( int i = 0; i < memorySize; i++ ) {
      memoryBids[i] = 0;
      memoryAsks[i] = 0;
    }
    logger.debug("memorysize = " + memorySize);
  }

  public void updateTransPriceLog( TransactionExecutedEvent event ) {
    currentMemoryCell = (currentMemoryCell + 1) % memorySize;
    if ( memoryAsks[currentMemoryCell] > 0 || memoryBids[currentMemoryCell] > 0 ) {
      for ( int i = 0; i < memoryAsks[currentMemoryCell]; i++ ) {
        asks.removeFirst();
      }
      for ( int i = 0; i < memoryBids[currentMemoryCell]; i++ ) {
        bids.removeFirst();
      }
      memoryBids[currentMemoryCell] = 0;
      memoryAsks[currentMemoryCell] = 0;
//      acceptedShouts.clear();
      markMatched(asks);
      markMatched(bids);
    }
  }

  public void initialise() {
    acceptedShouts.clear();
    bids.clear();
    asks.clear();
    sortedShouts.clear();
    for ( int i = 0; i < memorySize; i++ ) {
      memoryBids[i] = 0;
      memoryAsks[i] = 0;
    }
    initialisePriceRanges();
  }

  public void reset() {
    initialise();
  }

  public void updateShoutLog( ShoutPlacedEvent event ) {
    Shout shout = event.getShout();
    addToSortedShouts(shout);
    if ( shout.isAsk() ) {
      asks.add(shout);
      memoryAsks[currentMemoryCell]++;
      if ( shout.getPrice() < lowestAskPrice ) {
        lowestAskPrice = shout.getPrice();
      }
    } else {
      bids.add(shout);
      memoryBids[currentMemoryCell]++;
      if ( shout.getPrice() > highestBidPrice ) {
        highestBidPrice = shout.getPrice();
      }
    }
  }

  public void roundClosed( AuctionEvent event ) {
    markMatched(asks);
    markMatched(bids);
    //    if ( getNumberOfTrades() > memorySize ) {
    //   deleteOldShouts();
    //}
    initialisePriceRanges();
  }

  public void eventOccurred( AuctionEvent event ) {
    if ( event instanceof RoundClosedEvent ) {
      roundClosed(event);
    } else if ( event instanceof ShoutPlacedEvent ) {
      updateShoutLog((ShoutPlacedEvent) event);
    } else if ( event instanceof TransactionExecutedEvent ) {
      updateTransPriceLog((TransactionExecutedEvent) event);
    }
  }

  public int getNumberOfTrades() {
    return acceptedShouts.size() / 2;
  }

  public double getHighestBidPrice() {
    return highestBidPrice;
  }

  public double getLowestAskPrice() {
    return lowestAskPrice;
  }

  public List getBids() {
    return bids;
  }

  public List getAsks() {
    return asks;
  }

  public boolean accepted( Shout shout ) {
    return acceptedShouts.contains(shout);
  }

  public int getNumberOfAsks( double price, boolean accepted ) {
    return getNumberOfShouts(asks, price, accepted);
  }

  public int getNumberOfBids( double price, boolean accepted ) {
    return getNumberOfShouts(bids, price, accepted);
  }

  public Iterator sortedShoutIterator() {
    return sortedShouts.iterator();
  }

  public void addToSortedShouts( Shout shout ) {
    sortedShouts.add(shout);
  }

  public int getNumberOfShouts( List shouts, double price, boolean accepted ) {
    int numShouts = 0;
    Iterator i = shouts.iterator();
    while ( i.hasNext() ) {
      Shout shout = (Shout) i.next();
      if ( (price >= 0 && shout.getPrice() >= price)
          || (price < 0 && shout.getPrice() <= -price) ) {
        if ( accepted ) {
          if ( acceptedShouts.contains(shout) ) {
            numShouts++;
          }
        } else {
          numShouts++;
        }
      }
    }
    return numShouts;
  }

  public void produceUserOutput() {
  }

  public Map getVariables() {
    return new HashMap();
  }

  protected void initialisePriceRanges() {
    highestBidPrice = Double.NEGATIVE_INFINITY;
    lowestAskPrice = Double.POSITIVE_INFINITY;
  }
  
  protected void markMatched( List shouts ) {
    try {
      Iterator i = shouts.iterator();
      while ( i.hasNext() ) {
        Shout s = (Shout) i.next();
        if ( auction.shoutAccepted(s) ) {
          acceptedShouts.add(s);
        }
      }
    } catch ( ShoutsNotVisibleException e ) {
      throw new AuctionError(e);
    }
  }

  public void finalReport() {
  }

  public String toString() {
    return "(" + getClass() + " auction:" + auction + " memorySize:"
        + memorySize + " bids:" + bids + " asks:" + asks + ")";
  }

}