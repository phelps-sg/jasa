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

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.Shout;

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

  List loggers = null;

  static final String P_NUMLOGGERS = "n";


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

  public void updateQuoteLog(int time, MarketQuote quote) {
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      logger.updateQuoteLog(time, quote);
    }
  }

  public void updateTransPriceLog( int time, Shout ask, double price,
                                    int quantity ) {
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      logger.updateTransPriceLog(time, ask, price, quantity);
    }
  }

  public void updateShoutLog(int time, Shout shout) {
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      logger.updateShoutLog(time, shout);
    }
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

  public void finalReport() {
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      logger.finalReport();
    }
  }

  public void endOfRound() {
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      logger.endOfRound();
    }
  }


  public void endOfDay() {
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      logger.endOfDay();
    }
  }

}