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
import uk.ac.liv.util.CummulativeDistribution;

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
 * <td valign=top><i>base </i> <tt>.k</tt><br>
 * <font size=-1>0 <=double <=1 </font></td>
 * <td valign=top>(determining the equilibrium price estimate in the interval
 * between matched ask and bid)</td>
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

  private double expectedHighestAsk;

  private double expectedLowestBid;

  /**
   * A parameter used to adjust the equilibrium price estimate in the interval
   * between matched bid and ask.
   */
  protected double k = 0.5;
  protected double delta = 0;
  protected int memorySize = 10;

  public static final String P_K = "k";
  public static final String P_DELTA = "delta";
  public static final String P_MEMORYSIZE = "memorysize";
  
  public static final String EST_EQUILIBRIUM_PRICE = "estimated.equilibrium.price";
  public static final String EST_EQUILIBRIUM_PRICE_DEVIATION = "estimated.equilibrium.price.deviation";
  
  FixedLengthQueue memory;

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
    
    delta = parameters.getDoubleWithDefault(base.push(P_DELTA), null, delta);
    assert (0 <= delta);
    
    memorySize = parameters.getIntWithDefault(base.push(P_MEMORYSIZE), null, memorySize);
    assert (0 <= memorySize);
    memory = new FixedLengthQueue(memorySize);
   
  }

  protected void initialise() {
    super.initialise();

    expectedHighestAsk = Double.POSITIVE_INFINITY;
    expectedLowestBid = 0;
    
    if (memory != null) 
      memory.initialize();
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
    
    double estimate;
    
    if (event instanceof TransactionExecutedEvent) {
      Shout ask = ((TransactionExecutedEvent) event).getAsk();
      Shout bid = ((TransactionExecutedEvent) event).getBid();
      estimate = k * bid.getPrice() + (1 - k) * ask.getPrice();
      memory.newData(estimate);
      
      if (memory.count() >= memorySize) {
//        logger.info("Estimate : "+memory.getMean());
        expectedLowestBid = memory.getMean() - delta;
        expectedHighestAsk = memory.getMean() + delta;

        ReportVariableBoard.getInstance().reportValue(EST_EQUILIBRIUM_PRICE,
            memory.getMean(), event);

        double equ = ((TimePeriodValue) ReportVariableBoard.getInstance()
            .getValue(ReportVariableBoardUpdater.EQUIL_PRICE)).getValue()
            .doubleValue();
        ReportVariableBoard.getInstance().reportValue(
            EST_EQUILIBRIUM_PRICE_DEVIATION,
            Math.abs(memory.getMean() - equ) * 100 / equ, event);
      }

    }
  }
  
  class FixedLengthQueue {
    double list[];
    int curIndex;
    double sum;
    int count;
    
    
    public FixedLengthQueue(int length) {
      assert (length >= 0);
      list = new double[length];
    }
    
    public void initialize() {
      for (int i=0; i<list.length; i++) {
        list[i] = 0;
      }
      curIndex = 0;
      sum = 0;
      count = 0;
    }
    
    public void newData(double value) {
      sum -= list[curIndex];
      list[curIndex] = value;
      sum += value;
      
      curIndex++;
      curIndex %= list.length;
      
      count++;
    }
    
    public int count() {
      return (count >= list.length) ? list.length : count;
    }
    
    public double getMean() {
      return sum/count();
    }
  }
}