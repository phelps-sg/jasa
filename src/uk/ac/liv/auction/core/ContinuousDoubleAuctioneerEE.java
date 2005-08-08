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

package uk.ac.liv.auction.core;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.jfree.data.time.TimePeriodValue;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionEventListener;
import uk.ac.liv.auction.event.TransactionExecutedEvent;
import uk.ac.liv.auction.stats.ReportVariableBoard;
import uk.ac.liv.auction.stats.ReportVariableBoardUpdater;

/**
 * <p>
 * An auctioneer for a double auction with continuous clearing and equlibrium
 * price estimation.
 * </p>
 * 
 * <p>
 * The clearing operation is performed every time a shout arrives. Shouts must
 * beat the current quote and be at the right side of the estimated equilibrium
 * price in order to be accepted.
 * </p>
 * 
 * <p>
 * <b>Parameters </b> <br>
 * </p>
 * <table>
 * <tr>
 * <td valign=top><i>base </i> <tt>.k</tt><br>
 * <font size=-1>0 <=double <=1 </font></td>
 * <td valign=top>(determining the equilibrium price estimate in the interval
 * between matched ask and bid)</td>
 * <tr></table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class ContinuousDoubleAuctioneerEE extends ContinuousDoubleAuctioneer
    implements Serializable, AuctionEventListener {

  static Logger logger = Logger.getLogger(ContinuousDoubleAuctioneerEE.class);

  private double expectedHighestAsk;

  private double expectedLowestBid;

  /**
   * A parameter used to adjust the equilibrium price estimate in the interval
   * between matched bid and ask.
   */
  protected double k = 0.5;

  public static final String P_K = "k";
  
  public static final String EST_EQUILIBRIUM_PRICE = "estimated.equilibrium.price";
  public static final String EST_EQUILIBRIUM_PRICE_DEVIATION = "estimated.equilibrium.price.deviation";

  public ContinuousDoubleAuctioneerEE() {
    this(null);
  }

  public ContinuousDoubleAuctioneerEE(Auction auction) {
    super(auction);
  }

  public void setup(ParameterDatabase parameters, Parameter base) {
    super.setup(parameters, base);

    k = parameters.getDoubleWithDefault(base.push(P_K), null, k);
    assert (0 <= k && k <= 1);
  }

  protected void initialise() {
    super.initialise();

    expectedHighestAsk = Double.POSITIVE_INFINITY;
    expectedLowestBid = 0;
  }
  
  public void checkImprovement(Shout shout) throws IllegalShoutException {
    super.checkImprovement(shout);

    if (shout.isBid()) {
      if (shout.getPrice() < expectedLowestBid) {
        bidNotAnImprovementException();
      }
    } else {
      if (shout.getPrice() > expectedHighestAsk) {
        askNotAnImprovementException();
      }
    }
  }
  
  public void eventOccurred(AuctionEvent event) {
    
    if (event instanceof TransactionExecutedEvent) {
      Shout ask = ((TransactionExecutedEvent) event).getAsk();
      Shout bid = ((TransactionExecutedEvent) event).getBid();
      expectedLowestBid = expectedHighestAsk = k * bid.getPrice() + (1 - k)
          * ask.getPrice();

      ReportVariableBoard.getInstance().reportValue(EST_EQUILIBRIUM_PRICE,
          expectedLowestBid, event);

      double equ = ((TimePeriodValue) ReportVariableBoard.getInstance()
          .getValue(ReportVariableBoardUpdater.EQUIL_PRICE)).getValue()
          .doubleValue();
      ReportVariableBoard.getInstance().reportValue(
          EST_EQUILIBRIUM_PRICE_DEVIATION,
          Math.abs(expectedLowestBid - equ) * 100 / equ, event);
    }
  }
}