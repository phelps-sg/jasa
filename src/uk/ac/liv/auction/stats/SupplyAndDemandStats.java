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

  protected ArrayList bids = new ArrayList();

  protected ArrayList asks = new ArrayList();


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

  protected void enumerateShout( Shout shout ) {
    Shout copyOfShout = new Shout();
    copyOfShout.copyFrom(shout);
    if ( shout.isBid() ) {
      bids.add(copyOfShout);
    } else {
      asks.add(copyOfShout);
    }
  }

  protected void releaseShouts() {
    super.releaseShouts();
    releaseShouts(bids.iterator());
    releaseShouts(asks.iterator());
  }

  public void initialise() {
    super.initialise();
    asks.clear();
    bids.clear();
  }


}
