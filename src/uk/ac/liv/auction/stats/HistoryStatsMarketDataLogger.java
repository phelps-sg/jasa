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

import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.core.Auction;

import java.util.*;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Resetable;

import org.apache.log4j.Logger;

/**
 * A MarketDataLogger that keeps a historical record of the shouts in
 * the market that lead to the last N transactions.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class HistoryStatsMarketDataLogger extends AbstractMarketDataLogger
    implements Resetable {

  protected LinkedList asks = new LinkedList();

  protected LinkedList bids = new LinkedList();

  protected HashSet acceptedShouts = new HashSet();

  protected int memorySize = 10;

  static final String P_MEMORYSIZE = "memorysize";

  static Logger logger = Logger.getLogger(HistoryStatsMarketDataLogger.class);


  public void setup( ParameterDatabase parameters, Parameter base ) {
    memorySize =
        parameters.getIntWithDefault(base.push(P_MEMORYSIZE), null, memorySize);
    auction.setHistoryStats(this);
  }

  public void updateQuoteLog( int time, MarketQuote quote ) {
  }

  public void updateTransPriceLog( int time, Shout ask, Shout bid, double price,
                                   int quantity ) {
  }

  public void initialise() {
    acceptedShouts.clear();
    bids.clear();
    asks.clear();
  }

  public void reset() {
    initialise();
  }

  public void updateShoutLog( int time, Shout shout ) {
    if ( shout.isAsk() ) {
      asks.add(shout);
    } else {
      bids.add(shout);
    }
  }



  public void roundClosed( Auction auction ) {
    markMatched(asks);
    markMatched(bids);
    if ( getNumberOfTrades() > memorySize ) {
      deleteOldShouts();
    }
  }

  public void auctionClosed( Auction auction ) {
    // Do nothing
  }

  public void endOfDay( Auction auction ) {
    // Do nothing
  }

  public int getNumberOfTrades() {
    return acceptedShouts.size() / 2;
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


  public int getNumberOfShouts( List shouts, double price, boolean accepted ) {
    int numShouts = 0;
    Iterator i = shouts.iterator();
    while ( i.hasNext() ) {
      Shout shout = (Shout) i.next();
      if ( (price >= 0 && shout.getPrice() >= price) || (price < 0 && shout.getPrice() <= -price) ) {
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


  protected void deleteOldShouts() {
    deleteOldShouts(asks);
    deleteOldShouts(bids);
  }


  protected void deleteOldShouts( List shouts ) {
    while ( !acceptedShouts.contains(shouts.get(0)) ) {
      shouts.remove(0);
    }
  }


  protected void markMatched( List shouts ) {
    Iterator i = shouts.iterator();
    while ( i.hasNext() ) {
      Shout s = (Shout) i.next();
      if ( auction.shoutAccepted(s) ) {
        acceptedShouts.add(s);
      }
    }
  }




  public void finalReport() {
  }


  public String toString() {
    return "(" + getClass() + " auction:" + auction + " memorySize:" +
              memorySize + " bids:" + bids + " asks:" + asks + ")";
  }

}