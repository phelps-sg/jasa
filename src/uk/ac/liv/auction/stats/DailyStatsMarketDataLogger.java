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

import uk.ac.liv.auction.core.Auction;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.util.Vector;

import uk.ac.liv.util.CummulativeStatCounter;
import uk.ac.liv.util.Parameterizable;

import org.apache.log4j.Logger;

/**
 * A logger that collections individual statistics for each trading day.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class DailyStatsMarketDataLogger extends StatsMarketDataLogger
    implements Parameterizable {

  protected Vector dailyStats;

  static Logger logger = Logger.getLogger(DailyStatsMarketDataLogger.class);

  public DailyStatsMarketDataLogger() {
    super();
    initialise();
  }

  public void setup( ParameterDatabase params, Parameter base ) {
    auction.setDailyStats(this);
  }

  public CummulativeStatCounter getTransPriceStats( int day ) {
    if ( day > dailyStats.size()-1 ) {
      return null;
    }
    return ((CummulativeStatCounter[]) dailyStats.get(day))[TRANS_PRICE];
  }

  public void endOfDay( Auction auction ) {
    // Make a copy of the current stats, reset them and record
    try {
      CummulativeStatCounter[] currentStats =
          new CummulativeStatCounter[stats.length];
      for (int i = 0; i < stats.length; i++) {
        currentStats[i] = (CummulativeStatCounter) stats[i].clone();
        stats[i].reset();
      }
      dailyStats.add(currentStats);
    } catch ( CloneNotSupportedException e ) {
      e.printStackTrace();
      logger.error(e.getMessage());
      throw new Error(e.getMessage());
    }
  }

  public void finalReport() {
    for( int day=0; day<dailyStats.size(); day++ ) {
      CummulativeStatCounter[] todaysStats =
          (CummulativeStatCounter[]) dailyStats.get(day);
      logger.info("Stats for day " + day);
      logger.info("");
      for( int i=0; i<todaysStats.length; i++ ) {
        printStats(todaysStats[i]);
      }
    }
  }

  public void initialise() {
    super.initialise();
    dailyStats = new Vector();
  }

  public void reset() {
    super.reset();
    dailyStats.clear();
  }


}
