/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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

import uk.ac.liv.ai.learning.NPTRothErevLearner;
import uk.ac.liv.ai.learning.StimuliResponseLearner;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Debug;
import uk.ac.liv.util.Resetable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

public class StimuliResponseStrategy extends AbstractStrategy {

  StimuliResponseLearner learner;

  static final String P_LEARNER = "learner";

  public StimuliResponseStrategy( AbstractTraderAgent agent ) {
    super(agent);
  }


  public void setup( ParameterDatabase parameters, Parameter base ) {

    learner = (StimuliResponseLearner)
      parameters.getInstanceForParameter(base.push(P_LEARNER),
                                          null, StimuliResponseLearner.class);

    ((Parameterizable) learner).setup(parameters, base);
  }


  public void modifyShout( Shout shout, Auction auction ) {

    super.modifyShout(shout, auction);

    // Reward the learner based on last earnings
    learner.reward(agent.getLastProfit());

    // Generate an action from the learning algorithm
    int action = learner.act();

    Debug.assertTrue("action >= 0", action >= 0);
    // Now turn the action into a price
    double price;
    if ( agent.isSeller() ) {
      price = agent.getPrivateValue() + action;
    } else {
      price = agent.getPrivateValue() - action;
    }
    /* TODO
    if ( price < funds ) {
      price = funds;
    } */
    if ( price < 0 ) {
      price = 0;
    }
    shout.setPrice(price);
  }

  public void reset() {
    super.reset();
    ((Resetable) learner).reset();
  }

  public StimuliResponseLearner getLearner() {
    return learner;
  }

  public void setLearner( StimuliResponseLearner learner ) {
    this.learner = learner;
  }

  public String toString() {
    return "(" + getClass() + " learner:" + learner + ")";
  }

}
