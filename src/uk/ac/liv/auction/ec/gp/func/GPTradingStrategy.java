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
 *
 */

public class GPTradingStrategy extends GPIndividualCtx
    implements Strategy, QuoteProvider, Serializable, Resetable {

  AbstractTraderAgent agent = null;

  Shout currentShout = null;

  int quantity = 1;

  Auction currentAuction = null;

  CummulativeStatCounter priceStats = new CummulativeStatCounter("priceStats");

  boolean misbehaved = false;


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
    return agent.getPrivateValue();
  }

  public Auction getAuction() {
    return currentAuction;
  }

  public MarketQuote getQuote() {
    return currentAuction.getQuote();
  }

  public void modifyShout( Shout shout, Auction auction ) {
    currentShout = shout;
    currentAuction = auction;
    GPGenericData input = GPGenericData.newGPGenericData();
    double price = Double.NaN;
    GenericNumber result = null;
    try {
      evaluateTree(0, input);
      result = (GenericNumber) input.data;
      price = result.doubleValue();
    } catch ( ArithmeticException e ) {
      System.out.println("Caught: " + e);
      price = 0;
      misbehaved = true;
      //e.printStackTrace();
    }
    if ( price < 0 || Double.isInfinite(price) || Double.isNaN(price)) {
      price = 0;
      misbehaved = true;
    }
    if ( !misbehaved && price==0 ) {
      //System.err.println(agent + " shouting " + result);
    }
    shout.setPrice(price);
    shout.setQuantity(quantity);
    shout.setIsBid(agent.isBuyer());
    priceStats.newData(price);
    result.release();
    input.release();
  }

  public double getLastProfit() {
    return agent.getLastProfit();
  }


  public CummulativeStatCounter getPriceStats() {
    return priceStats;
  }

  public void reset() {
    priceStats = new CummulativeStatCounter("priceStats");
    misbehaved = false;
  }

  public boolean misbehaved() {
    return misbehaved;
  }


}

