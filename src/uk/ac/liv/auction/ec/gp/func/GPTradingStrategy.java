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

import java.io.Serializable;

import ec.util.*;

import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.core.*;

import uk.ac.liv.ec.gp.*;

import uk.ac.liv.util.*;

/** 
 * @author Steve Phelps
 * @version $Revision$
 *
 */

public class GPTradingStrategy extends FixedQuantityStrategyImpl
    implements GPObject, Cloneable, Serializable, Resetable {


	protected GPGenericIndividual gpIndividual;
	
  private CummulativeStatCounter priceStats = new CummulativeStatCounter("priceStats");


  public void setup( ParameterDatabase parameters, Parameter base ) {    
  }
  
  public void setGPIndividual( GPGenericIndividual individual ) {
  	this.gpIndividual = individual;
  }
  
  public void setAgent( AbstractTraderAgent agent ) {
    this.agent = agent;
  }

  public AbstractTraderAgent getAgent() {
    return agent;
  }


  protected double getPrivateValue() {
    return agent.getPrivateValue(auction);
  }

  public Auction getAuction() {
    return auction;
  }

  public MarketQuote getQuote() {
    return auction.getQuote();
  }

  public boolean modifyShout( Shout.MutableShout shout ) {    
    Number result = gpIndividual.evaluateNumberTree(0);
    double price;
    if ( !gpIndividual.misbehaved() ) {
      price = result.doubleValue();
    } else {
      return false;
    }
    if ( price < 0 || 
    			Double.isInfinite(price) || Double.isNaN(price)) {      
      gpIndividual.illegalResult();
      return false;
    }
    shout.setPrice(price);    
    priceStats.newData(price);    
    return true;
  }

  public void endOfRound( Auction auction ) {
    // Do nothing
  }

  public double getLastProfit() {
    return agent.getLastProfit();
  }
  
  public boolean lastShoutAccepted() {
  	return agent.lastShoutAccepted();
  }


  public CummulativeStatCounter getPriceStats() {
    return priceStats;
  }

  public void reset() {
    priceStats.reset();
    //TODO gpIndividual.reset();
  }
  
  public boolean misbehaved() {
  	return gpIndividual.misbehaved();
  }
  
  public GPGenericIndividual getGPIndividual() {
  	return gpIndividual;
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


}

