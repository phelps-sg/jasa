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

package uk.ac.liv.auction.ec.gp.func;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.stats.StatsMarketDataLogger;
import uk.ac.liv.auction.electricity.ElectricityStats;

import uk.ac.liv.util.Debug;

import ec.gp.*;
import ec.*;

import uk.ac.liv.util.*;

import uk.ac.liv.ec.gp.func.GPGenericData;
import uk.ac.liv.ec.gp.*;

import java.util.*;


/**
 * An Auctioneer whose princing rule is evolved using genetic programming.
 *
 */

public class GPAuctioneer extends GPIndividualCtx implements Auctioneer {

  final ShoutEngine shoutEngine = new FourHeapShoutEngine();

  protected Shout currentShout;

  protected Auction auction;

  protected MarketQuote currentQuote;

  protected Shout clearBid, clearAsk;

  /**
   * The market statistics for the last auction run by this auctioneer.
   */
  protected ElectricityStats stats;

  /**
   * A copy of the logger stats for the last auction run by this auctioneer.
   */
  protected StatsMarketDataLogger logger;

  /**
   * The last set of strategies played against this auctioneer.
   */
  protected LinkedList strategies;


  public GPAuctioneer() {
    super();
  }

  public void reset() {
    shoutEngine.reset();
  }

  public void endOfRoundProcessing() {
    clear();
  }

  public MarketQuote getQuote() {
    if ( currentQuote == null ) {
      generateQuote();
    }
    return currentQuote;
  }

  public synchronized void clear() {
    List shouts = shoutEngine.getMatchedShouts();
    Iterator i = shouts.iterator();
    while ( i.hasNext() ) {
      Shout bid = (Shout) i.next();  Debug.assertTrue( bid.isBid() );
      Shout ask = (Shout) i.next();  Debug.assertTrue( ask.isAsk() );
      double price = determineClearingPrice(bid, ask);
      auction.clear(ask, bid.getAgent(), ask.getAgent(), price, ask.getQuantity());
    }
  }

  public double determineClearingPrice( Shout bid, Shout ask ) {
    clearBid = bid;
    clearAsk = ask;
    GPGenericData input = new GPGenericData();
    try {
      evaluateTree(0, input);
    } catch ( ArithmeticException e ) {
      System.out.println("Caught: " + e);
      //e.printStackTrace();
      return 0;
    }
    return ((GenericNumber) input.data).doubleValue();
  }

  public void generateQuote() {
    double bid = Shout.maxPrice(shoutEngine.getHighestMatchedAsk(), shoutEngine.getHighestUnmatchedBid());
    double ask = Shout.minPrice(shoutEngine.getLowestUnmatchedAsk(), shoutEngine.getLowestMatchedBid());
    currentQuote = new MarketQuote(ask, bid);
  }

  public synchronized void newShout( Shout shout ) throws IllegalShoutException {
    if ( ! shout.isValid() ) {
      throw new IllegalShoutException("Malformed shout");
    }
    if ( shout.isBid() ) {
      shoutEngine.newBid(shout);
    } else {
      shoutEngine.newAsk(shout);
    }
  }

  public void endOfAuctionProcessing() {
    // Do nothing
  }

  public Shout getCurrentShout() {
    return currentShout;
  }

  public synchronized void removeShout( Shout shout ) {
    shoutEngine.removeShout(shout);
  }

  public synchronized void printState() {
    shoutEngine.printState();
  }

  public void setMarketStats( ElectricityStats stats ) { this.stats = stats; }
  public void setLogStats( StatsMarketDataLogger logger ) { this.logger = logger; }
  public void setStrategies( LinkedList strategies ) { this.strategies = strategies; }
  public void setAuction( Auction auction ) { this.auction = auction; }

  public ElectricityStats getMarketStats() { return stats; }
  public StatsMarketDataLogger getLogStats() { return logger; }
  public LinkedList getStrategies() { return strategies; }
  public Auction getAuction() { return auction; }


}
