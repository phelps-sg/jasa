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

package uk.ac.liv.auction.ec.gp.func;

import java.io.Serializable;

import ec.gp.*;
import ec.*;

import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.core.*;

import uk.ac.liv.ec.gp.func.*;
import uk.ac.liv.ec.gp.*;

import uk.ac.liv.util.*;

/**
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 * @version $Revision$
 *
 */

public class GPTradingStrategy extends GPIndividualCtx
    implements FixedQuantityStrategy, QuoteProvider, Serializable, Resetable {


  AbstractTraderAgent agent = null;

  Shout currentShout = null;

  int quantity = 1;

  RoundRobinAuction currentAuction = null;

  CummulativeStatCounter priceStats = new CummulativeStatCounter("priceStats");


  public void setAgent( AbstractTraderAgent agent ) {
    this.agent = agent;
  }

  public AbstractTraderAgent getAgent() {
    return agent;
  }

  public void setQuantity( int quantity ) {
    this.quantity = quantity;
  }

  protected double getPrivateValue() {
    return agent.getPrivateValue(currentAuction);
  }

  public RoundRobinAuction getAuction() {
    return currentAuction;
  }

  public MarketQuote getQuote() {
    return currentAuction.getQuote();
  }

  public Shout modifyShout( Shout shout, Auction auction ) {
    currentAuction = (RoundRobinAuction) auction;
    double price = Double.NaN;
    FastNumber result = evaluateNumberTree(0);
    if ( !misbehaved ) {
      price = result.doubleValue();
    } else {
      price = 0;
    }
    if ( price < 0 || Double.isInfinite(price) || Double.isNaN(price)) {
      price = 0;
      misbehaved = true;
    }
    currentShout = ShoutFactory.getFactory().create(agent, quantity, price,
                                                      agent.isBuyer());
    priceStats.newData(price);
    result.release();
    return currentShout;
  }

  public void endOfRound( Auction auction ) {
    // Do nothing
  }

  public double getLastProfit() {
    return agent.getLastProfit();
  }


  public CummulativeStatCounter getPriceStats() {
    return priceStats;
  }

  public void reset() {
    priceStats.reset();
    misbehaved = false;
  }

  public Object protoClone() {
    GPTradingStrategy copy = null;
    try {
      copy = (GPTradingStrategy) super.protoClone();
      copy.priceStats = (CummulativeStatCounter) priceStats.clone();
    } catch ( CloneNotSupportedException e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
    return copy;
  }

  public int determineQuantity( Auction auction ) {
    return quantity;
  }


}

