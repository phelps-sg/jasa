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

import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.core.*;

import uk.ac.liv.util.Resetable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.util.*;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * This class computes the hypothetical auction state when all agents
 * bid at their private valuation, ie when all agents bid truthfully.
 * This can be used, for example, for equilibrium calculations.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class DirectRevelationReport extends AbstractMarketStatsReport implements Resetable, Serializable {

  /**
   * The auction state after forced direct revelation.
   */
  protected FourHeapShoutEngine shoutEngine = new FourHeapShoutEngine();

  /**
   * The auction we are computing stats for.
   */
  protected RoundRobinAuction auction;

  /**
   * The truthful shouts of all traders in the auction.
   */
  protected ArrayList shouts;


  static Logger logger = Logger.getLogger(DirectRevelationReport.class);


  public DirectRevelationReport( RoundRobinAuction auction ) {
    this();
    this.auction = auction;
  }

  public DirectRevelationReport() {
    shouts = new ArrayList();
  }

  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
  }


  public void calculate() {
    initialise();
    simulateDirectRevelation();
  }

  /**
   *  Update the auction state with a truthful shout from each trader.
   */
  protected void simulateDirectRevelation() {
    Iterator traders = auction.getTraderIterator();
    while ( traders.hasNext() ) {
      AbstractTradingAgent trader = (AbstractTradingAgent) traders.next();
      int quantity = trader.determineQuantity(auction);
      double value = trader.getValuation(auction);
      boolean isBid = trader.isBuyer();
      Shout shout = new Shout(trader, quantity, value, isBid);
      shouts.add(shout);
      enumerateTruthfulShout(shout);
    }
  }



  public void initialise() {
    shouts.clear();
    shoutEngine.reset();
  }


  public void reset() {
    initialise();
  }

  /**
   * Process a truthful shout from an agent
   *
   * @param shout The truthful shout
   */
  protected void enumerateTruthfulShout( Shout shout ) {
    try {
      if ( shout.isBid() ) {
        shoutEngine.newBid(shout);
      } else {
        shoutEngine.newAsk(shout);
       }
    } catch ( DuplicateShoutException e ) {
      logger.error(e.getMessage());
      throw new Error(e);
    }
  }



}
