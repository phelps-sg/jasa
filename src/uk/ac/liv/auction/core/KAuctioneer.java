/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

import uk.ac.liv.util.Parameterizable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.util.List;
import java.util.Iterator;

/**
 * <p><b>Parameters</b><br></p>
 * <table>
 * <tr><td valign=top><i>base</i><tt>.k</tt><br>
 * <font size=-1>double [0, 1]</font></td>
 * <td valign=top>(the pricing parameter)</td><tr>
 * </table>

 * @author Steve Phelps
 */

public abstract class KAuctioneer extends AbstractAuctioneer
    implements ParameterizablePricing, Parameterizable {

  protected MarketQuote clearingQuote;

  protected KPricingPolicy pricingPolicy;

  public static final String P_PRICING = "pricing";

  public KAuctioneer( Auction auction, KPricingPolicy pricingPolicy ) {
    super(auction);
    this.pricingPolicy = pricingPolicy;
  }

  public KAuctioneer( KPricingPolicy pricingPolicy ) {
    this(null, pricingPolicy);
  }

  public KAuctioneer() {
    this( new UniformPricingPolicy(0.0) );
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    pricingPolicy = (KPricingPolicy)
        parameters.getInstanceForParameterEq(base.push(P_PRICING), null,
                                              KPricingPolicy.class);

    pricingPolicy.setup(parameters, base.push(P_PRICING));

  }


  public void setK( double k ) {
    pricingPolicy.setK(k);
  }

  public double getK() {
    return pricingPolicy.getK();
  }

  public void clear() {
    clearingQuote = new MarketQuote(askQuote(), bidQuote());
    List shouts = shoutEngine.getMatchedShouts();
    Iterator i = shouts.iterator();
    while (i.hasNext()) {
      Shout bid = (Shout) i.next();
      Shout ask = (Shout) i.next();
      double price = determineClearingPrice(bid, ask);
      auction.clear(ask, bid.getAgent(), ask.getAgent(), price,
                     ask.getQuantity());
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


}