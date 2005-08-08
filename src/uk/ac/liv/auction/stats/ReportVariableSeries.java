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

import org.apache.log4j.Logger;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.time.TimeSeries;

import ec.util.ParamClassLoadException;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;

/**
 * Defined a data series that is fed with values of some <code>ReportVariable
 * </code> when some auction event occurs.
 * 
 * <p><b>Parameters</b><br>
 *
 * <table>
 *
 * <tr><td valign=top><i>base</i><tt>.var</tt><br>
 * <font size=-1> string </font></td>
 * <td valign=top>(the full name of the <code>ReportVariable</code>)</td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>.event</tt><br>
 * <font size=-1> event class inheriting <code>uk.ac.liv.auction.event.AuctionEvent</code>
 * </font></td>
 * <td valign=top>(the type of events that trigger sampling of the value of the 
 * <code>ReportVariable</code>)</td></tr>
 * 
 * </table>
 *
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class ReportVariableSeries extends FreeChartSeries {

  static Logger logger = Logger.getLogger(ReportVariableSeries.class);

  public static final String P_VAR = "var";
  public static final String P_EVENT = "event";
  
  protected String varName;
  protected Class eventClass;

  public ReportVariableSeries() {
  }

  public void setup(ParameterDatabase parameters, Parameter base) {
    super.setup(parameters, base);
    
    varName = parameters.getString(base.push(P_VAR));
    if (varName == null || varName.length() == 0)
      logger.error("Name of a ReportVariable must be specified for ReportVariableSeries!");
    
    if (getName() == null || getName().length() == 0)
      setName(varName);
    
    if (getSeries() == null) {
      series = new TimeSeries(getName(), Millisecond.class);
    }
    
    if (getDataset() == null) {
      dataset = createDataset(series);
    }
    
    try {
      eventClass = (Class) parameters.getClassForParameter(base.push(P_EVENT), null, AuctionEvent.class);
    } catch (ParamClassLoadException e) {
      eventClass = null;
    }
  }

  /**
   * @param event
   */
  public void eventOccurred(AuctionEvent event) {
    if (eventClass == null || eventClass.isInstance(event)) {
      TimePeriodValue tpValue = (TimePeriodValue) ReportVariableBoard
          .getInstance().getValue(varName);
      if (tpValue != null) {
        if (getSeries() instanceof TimeSeries) {
          ((TimeSeries)getSeries()).addOrUpdate((RegularTimePeriod) tpValue.getPeriod(), tpValue
              .getValue());
        }
      }
    }
  }

  /**
   * @param event
   */
  public void shoutPlaced(ShoutPlacedEvent event) {
  }

  /**
   * @param event
   */
  public void transactionExecuted(TransactionExecutedEvent event) {
  }

  /**
   * @param event
   */
  public void roundClosed(RoundClosedEvent event) {
  }

}