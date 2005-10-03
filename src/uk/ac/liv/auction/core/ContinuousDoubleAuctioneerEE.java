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

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionEventListener;
import uk.ac.liv.auction.event.TransactionExecutedEvent;

import uk.ac.liv.auction.stats.ReportVariableBoard;
import uk.ac.liv.util.FixedLengthQueue;

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
 * <b>Parameters </b>
 * </p>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.memorysize</tt><br>
 * <font size=-1>int >=1 </font></td>
 * <td valign=top>(how many recent transaction prices memorized to get the average
 * as the esimated equilibrium)</td>
 * 
 * <td valign=top><i>base </i> <tt>.delta</tt><br>
 * <font size=-1>0 <=double <=1 </font></td>
 * <td valign=top>(relaxing the restriction put by the estimated equilibrium price
 * )</td>
 * <tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class ContinuousDoubleAuctioneerEE extends ContinuousDoubleAuctioneer
    implements Serializable, AuctionEventListener {

  static Logger logger = Logger.getLogger(ContinuousDoubleAuctioneerEE.class);

  /**
   * @uml.property name="expectedHighestAsk"
   */
  private double expectedHighestAsk;

  /**
   * @uml.property name="expectedLowestBid"
   */
  private double expectedLowestBid;

  /**
   * A parameter used to adjust the equilibrium price estimate so as to relax
   * the restriction.
   * 
   * @uml.property name="delta"
   */
  protected double delta = 0;

  public static final String P_DELTA = "delta";

  /**
   * A parameter used to adjust the number of recent transaction prices to be memorized
   * so as to compute the average as the equilibrium price estimate
   * 
   * @uml.property name="memorySize"
   */
  protected int memorySize = 4;

  public static final String P_MEMORYSIZE = "memorysize";

  public static final String EST_EQUILIBRIUM_PRICE = "estimated.equilibrium.price";

  protected FixedLengthQueue memory;

  public ContinuousDoubleAuctioneerEE() {
    this(null);
  }

  public ContinuousDoubleAuctioneerEE( Auction auction ) {
    super(auction);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);

    delta = parameters.getDoubleWithDefault(base.push(P_DELTA), null, delta);
    assert (0 <= delta);

    memorySize = parameters.getIntWithDefault(base.push(P_MEMORYSIZE), null,
        memorySize);
    assert (0 <= memorySize);
    memory = new FixedLengthQueue(memorySize);

  }

  protected void initialise() {
    super.initialise();

    expectedHighestAsk = Double.POSITIVE_INFINITY;
    expectedLowestBid = 0;

    if ( memory != null )
      memory.initialize();
  }

  public void checkImprovement( Shout shout ) throws IllegalShoutException {
    super.checkImprovement(shout);

    if ( shout.isBid() ) {
      if ( shout.getPrice() < expectedLowestBid ) {
        bidNotAnImprovementException();
      }
    } else {
      if ( shout.getPrice() > expectedHighestAsk ) {
        askNotAnImprovementException();
      }
    }
  }

  public void eventOccurred( AuctionEvent event ) {

    if ( event instanceof TransactionExecutedEvent ) {
      memory.newData(((TransactionExecutedEvent)event).getPrice());

      if ( memory.count() >= memorySize ) {
        expectedLowestBid = memory.getMean() - delta;
        expectedHighestAsk = memory.getMean() + delta;

        ReportVariableBoard.getInstance().reportValue(EST_EQUILIBRIUM_PRICE,
            memory.getMean(), event);
      }

    }
  }
}