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

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.RoundRobinAuction;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;

import org.apache.log4j.Logger;

/**
 * <p>
 * An implementation of MarketStats that can be used configure
 * a combination of different reports
 * </p>
 *
 * <p><b>Parameters</b><br></p>
 * <table>
 * <tr><td valign=top><i>base</i><tt>.n</tt><br>
 * <font size=-1>int &gt;= 1</font></td>
 * <td valign=top>(the number of different reports to configure)</td><tr>
 * </table>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class CombiMarketStats
    implements MarketStats, Parameterizable, Resetable {

  protected List stats = null;

  public static final String P_NUMMARKETSTATS = "n";

  static Logger logger = Logger.getLogger(CombiMarketStats.class);

  protected RoundRobinAuction auction;

  public CombiMarketStats( List stats ) {
    this.stats = stats;
  }

  public CombiMarketStats() {
    this.stats = new LinkedList();
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    int numStats = parameters.getInt(base.push(P_NUMMARKETSTATS), null, 1);

    for( int i=0; i<numStats; i++ ) {
      MarketStats s = (MarketStats)
        parameters.getInstanceForParameter(base.push(i+""), null,
                                            MarketStats.class);
      if ( s instanceof Parameterizable ) {
        ((Parameterizable) s).setup(parameters, base.push(i+""));
      }
      s.setAuction(auction);
      addStats(s);
    }
  }

  public void addStats( MarketStats s ) {
    stats.add(s);
  }

  public void setAuction( RoundRobinAuction auction ) {
    logger.debug("Setting auction to " + auction);
    this.auction = auction;
  }

  public void calculate() {
    Iterator i = stats.iterator();
    while ( i.hasNext() ) {
      MarketStats s = (MarketStats) i.next();
      s.calculate();
    }
  }

  public void generateReport() {
    Iterator i = stats.iterator();
    while (i.hasNext()) {
      MarketStats s = (MarketStats) i.next();
      s.generateReport();
    }
  }

  public void reset() {
    Iterator i = stats.iterator();
    while (i.hasNext()) {
      MarketStats s = (MarketStats) i.next();
      ((Resetable) s).reset();
    }
  }

}
