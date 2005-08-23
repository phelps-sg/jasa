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
 * A report that combines several different reports.
 * </p>
 * 
 * <p>
 * <b>Parameters</b><br>
 * </p>
 * <table>
 * <tr>
 * <td valign=top><i>base</i><tt>.n</tt><br>
 * <font size=-1>int &gt;= 1</font></td>
 * <td valign=top>(the number of different loggers to configure)</td>
 * <tr> </table>
 * 
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class CombiAuctionReport implements AuctionReport, Parameterizable,
    Resetable {

  /**
   * @uml.property name="reports"
   * @uml.associationEnd multiplicity="(0 -1)"
   *                     elementType="uk.ac.liv.auction.stats.AuctionReport"
   */
  protected List reports = null;

  /**
   * @uml.property name="auction"
   * @uml.associationEnd
   */
  protected RoundRobinAuction auction;

  public static final String P_NUMLOGGERS = "n";

  public CombiAuctionReport( List reports ) {
    this.reports = reports;
  }

  public CombiAuctionReport() {
    this.reports = new LinkedList();
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    int numLoggers = parameters.getInt(base.push(P_NUMLOGGERS), null, 1);

    for ( int i = 0; i < numLoggers; i++ ) {
      AuctionReport report = (AuctionReport) parameters
          .getInstanceForParameter(base.push(i + ""), null, AuctionReport.class);
      report.setAuction(auction);
      if ( report instanceof Parameterizable ) {
        ((Parameterizable) report).setup(parameters, base.push(i + ""));
      }
      addReport(report);
    }
  }

  /**
   * Add a new logger
   */
  public void addReport( AuctionReport report ) {
    reports.add(report);
  }

  public void reset() {
    Iterator i = reports.iterator();
    while ( i.hasNext() ) {
      AuctionReport logger = (AuctionReport) i.next();
      if ( logger instanceof Resetable ) {
        ((Resetable) logger).reset();
      }
    }
  }

  public void produceUserOutput() {
    Iterator i = reports.iterator();
    while ( i.hasNext() ) {
      AuctionReport logger = (AuctionReport) i.next();
      logger.produceUserOutput();
    }
  }

  public Iterator reportIterator() {
    return reports.iterator();
  }

  public Map getVariables() {
    HashMap variableMap = new HashMap();
    Iterator i = reports.iterator();
    while ( i.hasNext() ) {
      AuctionReport logger = (AuctionReport) i.next();
      variableMap.putAll(logger.getVariables());
    }
    return variableMap;
  }

  public void eventOccurred( AuctionEvent event ) {
    Iterator i = reports.iterator();
    while ( i.hasNext() ) {
      AuctionReport logger = (AuctionReport) i.next();
      logger.eventOccurred(event);
    }
  }

  /**
   * @uml.property name="auction"
   */
  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
    Iterator i = reports.iterator();
    while ( i.hasNext() ) {
      AuctionReport logger = (AuctionReport) i.next();
      logger.setAuction(auction);
    }
  }

}