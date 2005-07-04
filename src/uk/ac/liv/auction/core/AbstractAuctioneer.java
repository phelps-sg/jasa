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

package uk.ac.liv.auction.core;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;
import uk.ac.liv.util.Prototypeable;

import org.apache.log4j.Logger;

import ec.util.Parameter;
import ec.util.ParameterDatabase;


/**
 * An abstract class representing an auctioneer managing shouts in an auction.
 * Different auction rules should be encapsulated in different Auctioneer
 * classes.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AbstractAuctioneer
    implements Serializable, Auctioneer, Resetable, Prototypeable, Cloneable, Parameterizable {

  /**
   * The auction container for this auctioneer.
   */
  protected Auction auction;

  /**
   * The shout engine for this auction.
   */
  protected ShoutEngine shoutEngine = new FourHeapShoutEngine();

  /**
   * The current quote
   */
  protected MarketQuote currentQuote = null;

  static Logger logger = Logger.getLogger(AbstractAuctioneer.class);

  
  protected MarketQuote clearingQuote;

  protected PricingPolicy pricingPolicy;

  public static final String P_PRICING = "pricing";



  public AbstractAuctioneer() {
    initialise();
  }

  public AbstractAuctioneer( Auction auction ) {
    this();
    this.auction = auction;
  }

  public Object protoClone() {
    try {
      AbstractAuctioneer clone = (AbstractAuctioneer) clone();
      clone.shoutEngine = new FourHeapShoutEngine();
      clone.reset();
      return clone;
    } catch ( CloneNotSupportedException e ) {
      throw new Error(e);
    }
  }
  
  public void setup( ParameterDatabase parameters, Parameter base ) {

    pricingPolicy = (PricingPolicy)
        parameters.getInstanceForParameterEq(base.push(P_PRICING), null,
                                              PricingPolicy.class);

    if (pricingPolicy instanceof Parameterizable)
      ((Parameterizable)pricingPolicy).setup(parameters, base.push(P_PRICING));

  }
  
  public void setPricingPolicy( PricingPolicy pricingPolicy ) {
    this.pricingPolicy = pricingPolicy;
  }


  /**
   * Code for handling a new shout in the auction.
   * Subclasses should override this method if they wish
   * to provide different handling for different auction rules.
   *
   *  @param shout  The new shout to be processed
   *
   *  @exception IllegalShoutException  Thrown if the shout is invalid in some way.
   */
  public void newShout( Shout shout ) throws IllegalShoutException {
    if ( ! shout.isValid() ) {
      logger.error("malformed shout: " + shout);
      throw new IllegalShoutException("Malformed shout");
    }
    if ( shout.isBid() ) {
      newBid(shout);
    } else {
      newAsk(shout);
    }
  }


  /**
   * Handle a request to retract a shout.
   */
  public void removeShout( Shout shout ) {
    shoutEngine.removeShout(shout);
  }

  /**
   * Log the current state of the auction.
   */
  public void printState() {
    shoutEngine.printState();
  }

  public void reset() {
    shoutEngine.reset();
    initialise();
  }

  protected void initialise() {
    currentQuote = null;
  }

  public MarketQuote getQuote() {
    if ( currentQuote == null ) {
      generateQuote();
    }
    return currentQuote;
  }
  
  public Iterator askIterator() {
    return shoutEngine.askIterator();
  }
  
  public Iterator bidIterator() {
    return shoutEngine.bidIterator();
  }

  public abstract void generateQuote();

  /**
   * Default rules for handling a new ask.
   * Subclasses should override this method if they wish to provide
   * different handling for different auction rules.
   *
   * @param ask   The new ask (offer to sell) to process
   */
  protected void newAsk( Shout ask ) throws DuplicateShoutException {
    shoutEngine.newAsk(ask);
  }

  /**
   * Default rules for handling a new bid.
   * Subclasses should override this method if they wish to provide
   * different handling for different auction rules.
   *
   * @param bid The new bid (offer to buy) to process
   */
  protected void newBid( Shout bid ) throws DuplicateShoutException {
    shoutEngine.newBid(bid);
  }

  public void setAuction( Auction auction ) {
    this.auction = auction;
  }

  /*
   * Find out which auction we are the auctioneer for.
   */
  public Auction getAuction() {
    return auction;
  }
  
  public void endOfDayProcessing() {
    shoutEngine.reset();
  }

  public void clear() {
    clearingQuote = new MarketQuote(askQuote(), bidQuote());
    List shouts = shoutEngine.getMatchedShouts();
    Iterator i = shouts.iterator();
    while (i.hasNext()) {
      Shout bid = (Shout) i.next();
      Shout ask = (Shout) i.next();
      double price = determineClearingPrice(bid, ask);
      auction.clear(ask, bid, price);
    }
  }

  public double determineClearingPrice( Shout bid, Shout ask ) {
    return pricingPolicy.determineClearingPrice(bid, ask, clearingQuote);
  }

  protected double bidQuote() {
    return Shout.maxPrice(shoutEngine.getHighestMatchedAsk(),
                           shoutEngine.getHighestUnmatchedBid());
  }

  protected double askQuote() {
    return Shout.minPrice(shoutEngine.getLowestUnmatchedAsk(),
                           shoutEngine.getLowestMatchedBid());
  }
  
  public void eventOccurred(AuctionEvent event) {
  }
}


