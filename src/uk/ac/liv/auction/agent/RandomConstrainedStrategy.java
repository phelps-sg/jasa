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

package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;

import uk.ac.liv.util.Seedable;

import uk.ac.liv.prng.PRNGFactory;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import edu.cornell.lassp.houle.RngPack.RandomElement;

import java.io.Serializable;

/**
 * <p>
 * A trading strategy that in which we bid a different random markup on our
 * agent's private value in each auction round.
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
                                        implements Seedable, Serializable {

  protected double maxMarkup = 50;

  protected RandomElement randGenerator;

  public static final String P_MAX_MARKUP = "maxmarkup";

  public static final double DEFAULT_MARKUP = 50;

  public RandomConstrainedStrategy() {
    this(null, DEFAULT_MARKUP);
  }

  public RandomConstrainedStrategy( AbstractTraderAgent agent, double maxMarkup ) {
    super(agent);
    randGenerator = PRNGFactory.getFactory().create();
    this.maxMarkup = maxMarkup;
  }

  public void modifyShout( Shout.MutableShout shout ) {

    super.modifyShout(shout);

    double markup = randGenerator.raw() * maxMarkup;
    double price = 0;
    if ( agent.isBuyer() ) {
      price = agent.getPrivateValue(auction) - markup;
    } else {
      price = agent.getPrivateValue(auction) + markup;
    }
    if ( price > 0 ) {
      shout.setPrice(price);
    } else {
      shout.setPrice(0);
    }
    shout.setQuantity(quantity);
  }

  public void endOfRound( Auction auction ) {
    // Do nothing
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
    maxMarkup = parameters.getDoubleWithDefault(base.push(P_MAX_MARKUP), null, 100);
  }

  public void setSeed( long seed ) {
    randGenerator = PRNGFactory.getFactory().create(seed);
  }

  public String toString() {
    return "(" + getClass() + " maxmarkup:" + maxMarkup + " quantity:" +
              quantity + ")";
  }

}
