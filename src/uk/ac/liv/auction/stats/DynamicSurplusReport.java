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

package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.core.RoundRobinAuction;

import uk.ac.liv.auction.event.*;

import uk.ac.liv.auction.agent.AbstractTradingAgent;

import java.util.*;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Resetable;

import org.apache.log4j.Logger;

import gnu.trove.TObjectDoubleHashMap;
import gnu.trove.TObjectDoubleIterator;

/**
 * <p>
 * A report that keeps track of the surplus available to each agent in
 * theoretical equilibrium. The equilibrium price is recomputed at the end of
 * each day, thus this class can be used to keep track of theoretically
 * available surplus even when supply and demand are changing over time.
 * Each agent is assumed to be hypothetically able to trade the specified 
 * quantity of units in each day.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class DynamicSurplusReport extends AbstractMarketStatsReport
    implements Resetable {

  /**
   * The report used to calculate the equilibrium price.
   */
  protected EquilibriumReport equilibriaStats;

  /**
   * Total theoretically available profits per agent.
   * This table maps AbstractTradingAgent keys onto double values. 
   */
  private TObjectDoubleHashMap surplusTable = new TObjectDoubleHashMap();
  
  /**
   * The quantity that each agent can theoretically trade per day.
   * This should normally be set equal to agents' trade entitlement.
   */
  protected int quantity = 1;
  
  public static final String P_QUANTITY = "quantity";

  static Logger logger = Logger.getLogger(DynamicSurplusReport.class);

  public void setup( ParameterDatabase parameters, Parameter base ) {
    quantity = 
      parameters.getIntWithDefault(base.push(P_QUANTITY), null, quantity);
  }

  public void setAuction( RoundRobinAuction auction ) {
    super.setAuction(auction);
    equilibriaStats = new EquilibriumReport(auction);
  }

  public void eventOccurred( AuctionEvent event ) {
    super.eventOccurred(event);
    if ( event instanceof EndOfDayEvent ) {
      recalculate(event);
    }
  }
  
  public void calculate() {    
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
    return surplusTable.get(agent);    
  }

  public double calculateTotalEquilibriumSurplus() {
    double totalSurplus = 0;    
    TObjectDoubleIterator i = surplusTable.iterator();
    while ( i.hasNext() ) {
      i.advance();
      AbstractTradingAgent agent = (AbstractTradingAgent) i.key();
      totalSurplus += i.value();
    }
    return totalSurplus;
  }

  /**
   * Increment the surplus available to the specified agent by the specified
   * amount.
   */
  protected void updateStats( AbstractTradingAgent agent, double lastSurplus ) {    
    if ( ! surplusTable.adjustValue(agent, lastSurplus) ) {
      surplusTable.put(agent, lastSurplus);
    }
  }

  /**
   * Calculate the surplus available to the specified agent given the specified
   * equilibrium price and quantity.
   * 
   * @param agent
   *          The agent to calculate theoretically available surplus to.
   * @param ep
   *          The hypothetical equilibrium price
   * @param quantity
   *          The hypothetical quantity that this agent is able to trade in any
   *          given day.
   */
  protected double equilibriumSurplus( AbstractTradingAgent agent, 
      									double ep, int quantity ) {
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

  
  public void produceUserOutput() {
  }
  
  public Map getVariables() {
    return new HashMap();
  }
  
  public int getQuantity() {
    return quantity;
  }
  
  public void setQuantity( int quantity ) {
    this.quantity = quantity;
  }
}

