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

import uk.ac.liv.util.io.DataWriter;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * A class to calculate the supply and demand curves and write them
 * to the specified <code>DataWriter</code>s.  This can be used to log
 * data to <code>DataSeriesWriter</code>s, which can then be viewed
 * in a JSci graph or a swing table.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class ReportedSupplyAndDemandStats extends SupplyAndDemandStats {

  /**
   * The sorted list of agent's truthful bids (ie buyers' private values).
   */
  protected ArrayList bids = new ArrayList();

  /**
   * The sorted list of agents' truthful asks (ie sellers' private values).
   */
  protected ArrayList asks = new ArrayList();


  static Logger logger = Logger.getLogger(TrueSupplyAndDemandStats.class);


  public ReportedSupplyAndDemandStats( RoundRobinAuction auction,
      								DataWriter supplyStats,
      								DataWriter demandStats ) {
    super(auction, supplyStats, demandStats);
  }
  


  public void writeSupplyStats() {
    writeStats(supplyStats, asks, new AscendingShoutComparator());
  }

  public void writeDemandStats() {
    writeStats(demandStats, bids, new DescendingShoutComparator());
  }


  protected void enumerateTruthfulShout( Shout truthfulShout ) {
    //super.enumerateTruthfulShout(truthfulShout);
    AbstractTradingAgent agent = (AbstractTradingAgent) truthfulShout
        .getAgent();
    Shout actualShout = agent.getCurrentShout();
    if ( actualShout != null ) {
      if ( actualShout.isBid() ) {
        bids.add(actualShout);
      } else {
        asks.add(actualShout);
      }
    }
    super.enumerateTruthfulShout(truthfulShout);
  }

  public void initialise() {
    super.initialise();
    asks.clear();
    bids.clear();
  }
  
  public Map getVariables() {
    return new HashMap();
  }


}
