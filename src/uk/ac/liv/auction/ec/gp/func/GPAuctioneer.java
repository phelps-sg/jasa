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
 * An Auctioneer whose pricing rule is evolved using genetic programming.
 *
 */

public class GPAuctioneer extends GPIndividualCtx
                            implements Auctioneer, Resetable {

  final ShoutEngine shoutEngine = new FourHeapShoutEngine();

  protected Shout currentShout;

  protected Auction auction;

  protected MarketQuote currentQuote = null;

  protected Shout clearBid = new Shout();
  protected Shout clearAsk = new Shout();

  protected Shout lastBid = new Shout();
  protected Shout lastAsk = new Shout();

  /**
   * The market statistics for the last auction run by this auctioneer.
   */
  protected CummulativeStatCounter stats;

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
    misbehaved = false;
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
      Shout bid = (Shout) i.next();
      Shout ask = (Shout) i.next();
      double price = determineClearingPrice(bid, ask);
      if ( !misbehaved ) {
        auction.clear(ask, bid.getAgent(), ask.getAgent(), price, ask.getQuantity());
      }
    }
  }


  public double determineClearingPrice( Shout bid, Shout ask ) {
    clearBid.copyFrom(bid);
    clearAsk.copyFrom(ask);
    Debug.assertTrue( clearBid.getPrice() >= clearAsk.getPrice() );
    FastNumber result = evaluateNumberTree(0);
    result.release();
    if ( misbehaved ) {
      return 0;
    }
    if ( result.doubleValue() < 0 ) {
      misbehaved = true;
      return 0;
    }
    return result.doubleValue();
  }


  protected double bidQuote() {
    return Shout.maxPrice(shoutEngine.getHighestMatchedAsk(),
                           shoutEngine.getHighestUnmatchedBid());
  }

  protected double askQuote() {
    return Shout.minPrice(shoutEngine.getLowestUnmatchedAsk(),
                           shoutEngine.getLowestMatchedBid());
  }


  public void generateQuote() {

    if ( currentQuote == null ) {
      currentQuote = new MarketQuote(askQuote(),bidQuote());
    }

    currentQuote.setBid(bidQuote());
    currentQuote.setAsk(askQuote());
  }


  public synchronized void newShout( Shout shout ) throws IllegalShoutException {
    if ( ! shout.isValid() ) {
      throw new IllegalShoutException("Malformed shout");
    }
    if ( shout.isBid() ) {
      lastBid.copyFrom(shout);
      shoutEngine.newBid(shout);
    } else {
      lastAsk.copyFrom(shout);
      shoutEngine.newAsk(shout);
    }
  }


  public void endOfRoundProcessing() {
    clear();
  }


  public void endOfAuctionProcessing() {
    // Do nothing
  }

  public Shout getCurrentShout() {
    return currentShout;
  }

  public Shout getLastBid() {
    return lastBid;
  }

  public Shout getLastAsk() {
    return lastAsk;
  }

  public synchronized void removeShout( Shout shout ) {
    shoutEngine.removeShout(shout);
  }

  public synchronized void printState() {
    shoutEngine.printState();
  }


  public void setMarketStats( CummulativeStatCounter stats ) { this.stats = stats; }
  public void setLogStats( StatsMarketDataLogger logger ) { this.logger = logger; }
  public void setStrategies( LinkedList strategies ) { this.strategies = strategies; }
  public void setAuction( Auction auction ) { this.auction = auction; }

  public CummulativeStatCounter getMarketStats() { return stats; }
  public StatsMarketDataLogger getLogStats() { return logger; }
  public LinkedList getStrategies() { return strategies; }
  public Auction getAuction() { return auction; }

}
