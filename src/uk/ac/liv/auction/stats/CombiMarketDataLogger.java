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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.event.AuctionEvent;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;

/**
 * <p>
 * An implementation of MarketDataLogger that can be used to log
 * data to a number of different sources.
 * </p>
 *
 * <p><b>Parameters</b><br></p>
 * <table>
 * <tr><td valign=top><i>base</i><tt>.n</tt><br>
 * <font size=-1>int &gt;= 1</font></td>
 * <td valign=top>(the number of different loggers to configure)</td><tr>
 * </table>
 *
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class CombiMarketDataLogger
    implements MarketDataLogger, Parameterizable, Resetable {

  protected List loggers = null;

  protected RoundRobinAuction auction;

  public static final String P_NUMLOGGERS = "n";


  public CombiMarketDataLogger(List loggers) {
    this.loggers = loggers;
  }

  public CombiMarketDataLogger() {
    this.loggers = new LinkedList();
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    int numLoggers = parameters.getInt(base.push(P_NUMLOGGERS), null, 1);

    for( int i=0; i<numLoggers; i++ ) {
      MarketDataLogger logger = (MarketDataLogger)
        parameters.getInstanceForParameter(base.push(i+""), null,
                                            MarketDataLogger.class);
      logger.setAuction(auction);
      if ( logger instanceof Parameterizable ) {
        ((Parameterizable) logger).setup(parameters, base.push(i+""));
      }
      addLogger(logger);
    }
  }

  /**
   * Add a new logger
   */
  public void addLogger( MarketDataLogger logger ) {
    loggers.add(logger);
  }


  public void reset() {
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      if ( logger instanceof Resetable ) {
        ((Resetable) logger).reset();
      }
    }
  }

  public void generateReport() {
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      logger.generateReport();
    }
  }

  public Map getVariables() {
    HashMap variableMap = new HashMap();
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      variableMap.putAll(logger.getVariables());
    }
    return variableMap;
  }
  
  public void eventOccurred( AuctionEvent event ) {
    Iterator i = loggers.iterator();
    while (  i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      logger.eventOccurred(event);
    }
  }


  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      logger.setAuction(auction);
    }
  }

}