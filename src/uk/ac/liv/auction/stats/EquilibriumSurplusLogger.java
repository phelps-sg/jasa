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

import uk.ac.liv.auction.core.RoundRobinAuction;

import uk.ac.liv.auction.event.*;

import uk.ac.liv.auction.agent.AbstractTradingAgent;

import java.util.*;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Resetable;
import uk.ac.liv.util.MutableDoubleWrapper;

import org.apache.log4j.Logger;

/**
 * <p>
 * A MarketDataLogger that keeps track of the surplus available to each agent in
 * theoretical equilibrium. The equilibrium price is recomputed at the end of
 * each day, thus this class can be used to keep track of theoretically
 * available surplus even when supply and demand are changing over time.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class EquilibriumSurplusLogger extends AbstractMarketDataLogger
    implements Resetable {

  protected EquilibriaStats equilibriaStats;

  private HashMap surplusTable = new HashMap();
  
  protected int quantity = 1;
  
  public static final String P_QUANTITY = "quantity";

  static Logger logger = Logger.getLogger(EquilibriumSurplusLogger.class);

  public void setup( ParameterDatabase parameters, Parameter base ) {
    quantity = 
      parameters.getIntWithDefault(base.push(P_QUANTITY), null, quantity);
  }

  public void setAuction( RoundRobinAuction auction ) {
    super.setAuction(auction);
    equilibriaStats = new EquilibriaStats(auction);
  }

  public void eventOccurred( AuctionEvent event ) {
    if ( event instanceof EndOfDayEvent ) {
      recalculate(event);
    }
  }

  public void recalculate( AuctionEvent event  ) {
    
    equilibriaStats.recalculate();
    double ep = equilibriaStats.calculateMidEquilibriumPrice();
    
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      AbstractTradingAgent agent = (AbstractTradingAgent) i.next();
      double surplus = equilibriumSurplus(agent, ep, quantity);
      updateStats(agent, surplus);
    }
    
  }

  public double getEquilibriumProfits( AbstractTradingAgent agent ) {
    MutableDoubleWrapper stats = (MutableDoubleWrapper) surplusTable.get(agent);
    if ( stats == null ) {
      return 0;
    } else {
      return stats.value;
    }
  }

  public double calculateTotalEquilibriumSurplus() {
    double totalSurplus = 0;
    Iterator i = surplusTable.keySet().iterator();
    while ( i.hasNext() ) {
      AbstractTradingAgent agent = (AbstractTradingAgent) i.next();
      totalSurplus += getEquilibriumProfits(agent);
    }
    return totalSurplus;
  }

  protected void updateStats( AbstractTradingAgent agent, double lastSurplus ) {   
    MutableDoubleWrapper stats = (MutableDoubleWrapper) surplusTable.get(agent);
    if ( stats == null ) {
      stats = new MutableDoubleWrapper(lastSurplus);
      surplusTable.put(agent, stats);
    } else {
      stats.value += lastSurplus;
    }
  }

  protected double equilibriumSurplus( AbstractTradingAgent agent, double ep, int quantity ) {
    double surplus;
    if ( agent.isSeller() ) {
      surplus = (ep - agent.getValuation(auction)) * quantity;
    } else {
      surplus = (agent.getValuation(auction) - ep) * quantity;
    }  
    if ( surplus >= 0 ) {      
      return surplus;
    } else {  
      return 0;
    }
  }

  public void initialise() {
    surplusTable.clear();
  }

  public void reset() {
    initialise();
  }

  
  public void generateReport() {
  }
  
  public Map getVariables() {
    return new HashMap();
  }

}

