/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

import uk.ac.liv.auction.agent.RoundRobinTrader;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.util.Seedable;
import uk.ac.liv.util.Seeder;

import uk.ac.liv.prng.PRNGFactory;

import edu.cornell.lassp.houle.RngPack.RandomElement;

import java.util.Iterator;

/**
 * <p>
 * An auction in which each trader is given an equal probability of being able
 * to place a shout in the auction on each auction round.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class RandomShoutAuction extends RoundRobinAuction implements Seedable {

  /**
   * The PRNG used to draw the probability of each shout.
   */
  protected RandomElement prng = PRNGFactory.getFactory().create();

  /**
   * The probability of each trader being given a chance to place a shout.
   */
  protected double shoutProbability = 1.0;

  public static final String P_SHOUTPROBABILITY = "shoutprobability";

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
    shoutProbability =
        parameters.getDoubleWithDefault(base.push(P_SHOUTPROBABILITY), null,
                                         shoutProbability);
  }

  public void requestShouts() {
    Iterator i = activeTraders.iterator();
    while (i.hasNext()) {
      RoundRobinTrader trader = (RoundRobinTrader) i.next();
      double probability = prng.raw();
      if (probability <= shoutProbability) {
        trader.requestShout(this);
      }
    }
  }

  public void setSeed( long seed ) {
    prng = PRNGFactory.getFactory().create(seed);
  }

  public void seed( Seeder s ) {
    setSeed(s.nextSeed());
  }

  public void setShoutProbability( double shoutProbability ) {
    this.shoutProbability = shoutProbability;
  }

  public double getShoutProbability() {
    return shoutProbability;
  }

}
