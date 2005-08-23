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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimePeriodValue;

import uk.ac.liv.auction.event.AuctionClosedEvent;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionOpenEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * A class updates values of major ReportVariables on ReportVariableBoard.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class ReportVariableBoardUpdater extends AbstractAuctionReport {

  public static String TRANS_PRICE = "transaction.price";

  public static String EQUIL_PRICE = "equilibrium.price";

  /**
   * @uml.property name="equilPrice"
   */
  protected double equilPrice;

  public void produceUserOutput() {
  }

  public Map getVariables() {
    return new HashMap();
  }

  public void eventOccurred( AuctionEvent event ) {
    if ( event instanceof TransactionExecutedEvent ) {
      ReportVariableBoard.getInstance().reportValue(
          TRANS_PRICE,
          new TimePeriodValue(
              new Millisecond(new Date(event.getPhysicalTime())),
              ((TransactionExecutedEvent) event).getPrice()));
    } else if ( event instanceof AuctionOpenEvent ) {
      EquilibriumReport eqmReport = new EquilibriumReport(getAuction());
      eqmReport.calculate();
      equilPrice = eqmReport.calculateMidEquilibriumPrice();
      ReportVariableBoard.getInstance().reportValue(
          EQUIL_PRICE,
          new TimePeriodValue(
              new Millisecond(new Date(event.getPhysicalTime())), equilPrice));
    } else if ( event instanceof AuctionClosedEvent ) {
      ReportVariableBoard.getInstance().reportValue(
          EQUIL_PRICE,
          new TimePeriodValue(
              new Millisecond(new Date(event.getPhysicalTime())), equilPrice));
    }
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
  }

}