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

package uk.ac.liv.auction.core;

import java.util.Iterator;
import java.util.List;

import uk.ac.liv.util.Debug;
import uk.ac.liv.util.Parameterizable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * An Auctioneer for a uniform-price, continuous k-double-auction,
 * in which clearing takes place after every round of
 * bidding, and both buyers and sellers can make offers.
 */

public class ContinuousDoubleAuctioneer extends AbstractAuctioneer
                                            implements
                                              Parameterizable,
                                              ParameterizablePricing {

  /**
   * k is a parameter that determines the clearing price of currently matched shouts.
   */
  double k = 1;

  public ContinuousDoubleAuctioneer() {
    super();
  }

  public ContinuousDoubleAuctioneer( Auction auction ) {
    super(auction);
  }

  /**
   * @param auction The auction container for this auctioneer.
   * @param k The parameter k determines the price at which shouts are cleared.
   * The price for each clearing is p = ka + (1-k)b, where a is the current global ask price,
   * and b is the bid price.  Use k = 0 for a Vickrey auction and k = 1 for first price auction.
   */
  public ContinuousDoubleAuctioneer( Auction auction, double k ) {
    this(auction);
    this.k = k;
  }

  public ContinuousDoubleAuctioneer( double k ) {
    this();
    this.k = k;
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    k = parameters.getDoubleWithDefault(base.push("k"), null, 0.5);
  }

  public synchronized void clear() {
    double price = determineClearingPrice();
    List shouts = shoutEngine.getMatchedShouts();
    Iterator i = shouts.iterator();
    while ( i.hasNext() ) {
      Shout bid = (Shout) i.next();  Debug.assertTrue( bid.isBid() );
      Shout ask = (Shout) i.next();  Debug.assertTrue( ask.isAsk() );
      auction.clear(ask, bid.getAgent(), ask.getAgent(), price, ask.getQuantity());
    }
  }

  public void setK( double k ) {
    this.k = k;
  }

  public double getK() {
    return k;
  }

  protected double determineClearingPrice() {
    return (k * (double) bidQuote() + (1.0 - k) * (double) askQuote());
  }

  protected double bidQuote() {
    return Shout.maxPrice(shoutEngine.getHighestMatchedAsk(), shoutEngine.getHighestUnmatchedBid());
  }

  protected double askQuote() {
    return Shout.minPrice(shoutEngine.getLowestUnmatchedAsk(), shoutEngine.getLowestMatchedBid());
  }

  public void generateQuote() {
    currentQuote = new MarketQuote(askQuote(), bidQuote());
  }

  public void endOfRoundProcessing() {
    clear();
    generateQuote();
  }

  public void endOfAuctionProcessing() {
    // do nothing
  }
}