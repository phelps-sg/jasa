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

import uk.ac.liv.auction.core.Auction;

import uk.ac.liv.ai.learning.*;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Debug;
import uk.ac.liv.util.Resetable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

/**
 * <p>
 * A trading strategy that uses a stimuli-response learning algorithm,
 * such as the Roth-Erev algorithm, to adapt its trading behaviour in
 * successive auction rounds.
 * </p>
 *
 * </p><p><b>Parameters</b><br>
 *
 * <table>
 *
 * <tr><td valign=top><i>base</i><tt>.learner</tt><br>
 * <font size=-1>classname, inherits StimuliResponseLearner</font></td>
 * <td valign=top>(the learning algorithm to use)</td></tr>
 *
 * </table>
 *
 * @author Steve Phelps 
 */

public class StimuliResponseStrategy extends AdaptiveStrategy {

  /**
   * The learning algorithm to use.
   */
  StimuliResponseLearner learner;

  static final String P_LEARNER = "learner";

  public StimuliResponseStrategy( AbstractTraderAgent agent ) {
    super(agent);
  }

  public StimuliResponseStrategy() {
    super();
  }


  public void setup( ParameterDatabase parameters, Parameter base ) {

    super.setup(parameters, base);

    Parameter learnerParameter = base.push(P_LEARNER);
    learner = (StimuliResponseLearner)
      parameters.getInstanceForParameter(learnerParameter, null,
                                          StimuliResponseLearner.class);

    ((Parameterizable) learner).setup(parameters, learnerParameter);
  }

  public int act() {
    return learner.act();
  }

  public void calculateReward( Auction auction ) {
    learner.reward(agent.getLastProfit());
  }

  public void reset() {
    super.reset();
    ((Resetable) learner).reset();
  }

  public Learner getLearner() {
    return learner;
  }

  public void setLearner( StimuliResponseLearner learner ) {
    this.learner = learner;
  }

  public String toString() {
    return "(" + getClass() + " markupscale:" + markupScale + " learner:" +
              learner + "quantity:" + quantity + ")";      
  }

}
