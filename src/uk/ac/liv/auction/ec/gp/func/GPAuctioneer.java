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

package uk.ac.liv.auction.ec.gp.func;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.stats.PriceStatisticsReport;

import uk.ac.liv.util.*;

import uk.ac.liv.ec.gp.*;

import java.util.*;


/**
 * An Auctioneer whose pricing rule is evolved using genetic programming.
 *
 */

public class GPAuctioneer extends GPSchemeIndividual
                            implements Auctioneer, Resetable {

  final ShoutEngine shoutEngine = new FourHeapShoutEngine();

  protected Shout currentShout;

  protected Auction auction;

  protected MarketQuote currentQuote = null;

  protected Shout.MutableShout clearBid = new Shout.MutableShout();
  protected Shout.MutableShout clearAsk = new Shout.MutableShout();

  protected Shout.MutableShout lastBid = new Shout.MutableShout();
  protected Shout.MutableShout lastAsk = new Shout.MutableShout();

  /**
   * The market statistics for the last auction run by this auctioneer.
   */
  protected CummulativeDistribution stats;

  /**
   * A copy of the logger stats for the last auction run by this auctioneer.
   */
  protected PriceStatisticsReport logger;

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
        auction.clear(ask, bid, price);
      }
    }
  }


  public double determineClearingPrice( Shout bid, Shout ask ) {
    clearBid.copyFrom(bid);
    clearAsk.copyFrom(ask);
    assert clearBid.getPrice() >= clearAsk.getPrice();
    Number result = evaluateNumberTree(0);    
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
  
  public void endOfDayProcessing() {
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


  public void setMarketStats( CummulativeDistribution stats ) { this.stats = stats; }
  public void setLogStats( PriceStatisticsReport logger ) { this.logger = logger; }
  public void setStrategies( LinkedList strategies ) { this.strategies = strategies; }
  public void setAuction( Auction auction ) { this.auction = auction; }

  public CummulativeDistribution getMarketStats() { return stats; }
  public PriceStatisticsReport getLogStats() { return logger; }
  public LinkedList getStrategies() { return strategies; }
  public Auction getAuction() { return auction; }
  public boolean shoutsVisible() { return true; }

}
