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


package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.agent.AbstractTraderAgent;
import uk.ac.liv.auction.core.*;

import uk.ac.liv.util.Debug;
import uk.ac.liv.util.Resetable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import huyd.poolit.*;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.io.DataWriter;

import java.util.*;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * @author Steve Phelps
 */

public class SupplyAndDemandStats extends DirectRevelationStats
    implements MarketStats {

  static Logger logger = Logger.getLogger(DirectRevelationStats.class);

  protected DataWriter supplyStats;

  protected DataWriter demandStats;


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
    releaseShouts();
  }

  public void calculate() {
    super.calculate();
  }

  public void writeSupplyStats() {
    writeStats(supplyStats, asks, new DescendingShoutComparator());
  }


  public void writeDemandStats() {
    writeStats(demandStats, bids, new AscendingShoutComparator());
  }

  public void writeStats( DataWriter stats, List shouts,
                           Comparator comparator ) {
    if ( shouts.isEmpty() ) {
      return;
    }
    Collections.sort(shouts, comparator);
    Iterator i = shouts.iterator();
    Shout shout = (Shout) i.next();
    int qty = 0;
    while ( i.hasNext() ) {
      qty += shout.getQuantity();
      stats.newData(qty);
      stats.newData(shout.getPrice());
      shout = (Shout) i.next();
    }
  }

}
