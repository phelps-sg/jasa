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

package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;

import uk.ac.liv.prng.GlobalPRNG;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.io.Serializable;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;

/**
 * <p>
 * A trading strategy that in which we bid a different random markup on our
 * agent's private value in each auction round.  This strategy is often
 * referred to as Zero Intelligence Constrained (ZI-C) in the literature.
 * </p>
 *
 * </p><p><b>Parameters</b><br>
 *
 * <table>
 *
 * <tr><td valign=top><i>base</i><tt>.maxmarkup</tt><br>
 * <font size=-1>double &gt;= 0</font></td>
 * <td valign=top>(the maximum markup to bid for)</td></tr>
 *
 * </table>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class RandomConstrainedStrategy extends FixedQuantityStrategyImpl
                                        implements Serializable {

  protected double maxMarkup = DEFAULT_MARKUP;
  
  protected AbstractContinousDistribution markupDistribution;

  public static final String P_MAX_MARKUP = "maxmarkup";

  public static final double DEFAULT_MARKUP = 50;   

  public RandomConstrainedStrategy() {
    this(null, DEFAULT_MARKUP);
  }

  public RandomConstrainedStrategy( AbstractTradingAgent agent,
                                      double maxMarkup ) {
    super(agent);
    this.maxMarkup = maxMarkup;
    initialise();
  }
  
  public void initialise() {
    super.initialise();
    markupDistribution = new Uniform(0, maxMarkup, GlobalPRNG.getInstance());
  }

  public boolean modifyShout( Shout.MutableShout shout ) {
    
    double markup = markupDistribution.nextDouble();
    double price = 0;
    if ( agent.isBuyer() ) {
      price = agent.getValuation(auction) - markup;
    } else {
      price = agent.getValuation(auction) + markup;
    }
    if ( price > 0 ) {
      shout.setPrice(price);
    } else {
      shout.setPrice(0);
    }
    shout.setQuantity(quantity);

    return super.modifyShout(shout);
  }

  public void endOfRound( Auction auction ) {
    // Do nothing
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
    maxMarkup = parameters.getDoubleWithDefault(base.push(P_MAX_MARKUP),
                                                  null, maxMarkup);
    initialise();
  }

  public double getMaxMarkup() {
    return maxMarkup;
  }
  
  public void setMaxMarkup( double maxMarkup ) {
    this.maxMarkup = maxMarkup;
  }
  
  public String toString() {
    return "(" + getClass() + " maxmarkup:" + maxMarkup + " quantity:" +
              quantity + ")";
  }

}
