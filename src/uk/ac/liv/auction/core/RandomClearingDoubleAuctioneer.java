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

import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.prng.GlobalPRNG;

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

public class RandomClearingDoubleAuctioneer extends ContinuousDoubleAuctioneer
    implements Serializable {

  static Logger logger = Logger.getLogger(RandomClearingDoubleAuctioneer.class);
  
	Uniform uniformDistribution;

  /**
   * @uml.property name="threshold"
   */
  private double threshold = 0.5;

  public static final String P_THRESHOLD = "threshold";
  
  public static final String P_DEF_BASE = "rda";

  public RandomClearingDoubleAuctioneer() {
    this(null);
  }

  public RandomClearingDoubleAuctioneer( Auction auction ) {
    super(auction);
  }
  
  protected void initialise() {
    RandomEngine prng = GlobalPRNG.getInstance();
    uniformDistribution = new Uniform(0, 1, prng);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);

    threshold = parameters.getDoubleWithDefault(base.push(P_THRESHOLD), 
    		new Parameter(P_DEF_BASE).push(P_THRESHOLD), threshold);
    assert (0 <= threshold && threshold <= 1);
  }
  
  public void endOfRoundProcessing() {
    super.endOfRoundProcessing();
    generateQuote();
    clear();
  }

  public void newShout( Shout shout ) throws IllegalShoutException {
    checkImprovement(shout);
    super.newShout(shout);
    
    if (uniformDistribution.nextDouble() < threshold) {
    	generateQuote();
    	clear();
    }
  }
}