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

public class SupplyAndDemandStats extends DirectRevelationStats
    implements MarketStats {

  /**
   * The DataWriter to write the supply curve to.
   */
  protected DataWriter supplyStats;

  /**
   * The DataWriter to write the demand curve to.
   */
  protected DataWriter demandStats;

  /**
   * The sorted list of agent's truthful bids (ie buyers' private values).
   */
  protected ArrayList bids = new ArrayList();

  /**
   * The sorted list of agents' truthful asks (ie sellers' private values).
   */
  protected ArrayList asks = new ArrayList();


  static Logger logger = Logger.getLogger(SupplyAndDemandStats.class);


  /**
   * Constructor.
   *
   * @param auction       The auction to compute supply and demand stats for.
   * @param supplyStats   The DataWriter to write the supply curve to.
   * @param demandStats   The DataWriter to write the demand curve to.
   */
  public SupplyAndDemandStats( RoundRobinAuction auction,
                                DataWriter supplyStats,
                                DataWriter demandStats) {
    super(auction);
    this.supplyStats = supplyStats;
    this.demandStats = demandStats;
  }

  public void generateReport() {
    writeSupplyStats();
    writeDemandStats();    
  }

  public void calculate() {
    super.calculate();
  }

  public void writeSupplyStats() {
    writeStats(supplyStats, asks, new AscendingShoutComparator());
  }

  public void writeDemandStats() {
    writeStats(demandStats, bids, new DescendingShoutComparator());
  }

  public void writeStats( DataWriter stats, List shouts,
                           Comparator comparator ) {
    int qty = 0, qty1 = 0;
    if ( shouts.isEmpty() ) {
      return;
    }
    Collections.sort(shouts, comparator);
    Shout shout = (Shout) shouts.get(0);
    Iterator i = shouts.iterator();
    while ( i.hasNext() ) {
      shout = (Shout) i.next();
      qty1 = qty + shout.getQuantity();
      stats.newData(qty);
      stats.newData(shout.getPrice());
      stats.newData(qty1);
      stats.newData(shout.getPrice());
      qty = qty1;
    }
  }

  protected void enumerateTruthfulShout( Shout shout ) {
    Shout.MutableShout copyOfShout = new Shout.MutableShout();
    copyOfShout.copyFrom(shout);
    if ( shout.isBid() ) {
      bids.add(copyOfShout);
    } else {
      asks.add(copyOfShout);
    }
    super.enumerateTruthfulShout(shout);
  }

  public void initialise() {
    super.initialise();
    asks.clear();
    bids.clear();
  }


}
