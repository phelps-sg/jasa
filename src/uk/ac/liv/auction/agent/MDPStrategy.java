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

package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.MarketQuote;

import uk.ac.liv.ai.learning.*;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.io.Serializable;

/**
 * <p>
 * A trading strategy that uses an MDP learning algorithm,
 * such as the Q-learning algorithm, to adapt its trading behaviour in
 * successive auction rounds.  The current market-quote is hashed produce
 * an integer state value.
 *
 * @author Steve Phelps
 * @version $Revision$
 * </p>
 */

public class MDPStrategy extends DiscreteLearnerStrategy
                                  implements Serializable {

  protected MDPLearner learner;

  protected double bidBinStart;

  protected double bidBinWidth;

  protected double askBinStart;

  protected double askBinWidth;

  protected int quoteBins;

  protected boolean firstShout = true;

  static final String P_LEARNER = "learner";
  static final String P_QUOTEBINS = "quotebins";
  static final String P_ASKBINSTART = "askbinstart";
  static final String P_ASKBINWIDTH = "askbinwidth";
  static final String P_BIDBINSTART = "bidbinstart";
  static final String P_BIDBINWIDTH = "bidbinwidth";

  public MDPStrategy( AbstractTraderAgent agent,
                        double askBinStart, double askBinWidth,
                        double bidBinStart, double bidBinWidth ) {
    super(agent);
    this.askBinStart = askBinStart;
    this.askBinWidth = askBinWidth;
    this.bidBinStart = bidBinStart;
    this.bidBinWidth = bidBinWidth;
  }

  public MDPStrategy() {
    super();
  }


  public void setup( ParameterDatabase parameters, Parameter base ) {

    super.setup(parameters, base);

    Parameter learnerParameter = base.push(P_LEARNER);
    learner = (MDPLearner)
      parameters.getInstanceForParameter(learnerParameter, null,
                                          MDPLearner.class);
    ((Parameterizable) learner).setup(parameters, learnerParameter);

    askBinStart = parameters.getDouble(base.push(P_ASKBINSTART), null, 0);
    askBinWidth = parameters.getDouble(base.push(P_ASKBINWIDTH), null, 0);
    bidBinStart = parameters.getDouble(base.push(P_BIDBINSTART), null, 0);
    bidBinWidth = parameters.getDouble(base.push(P_BIDBINWIDTH), null, 0);
    quoteBins = parameters.getInt(base.push(P_QUOTEBINS), null, 1);
  }


  public int act() {
    return learner.act();
  }

  public void learn( Auction auction ) {
    learner.newState(agent.getLastProfit(), auctionState(auction));
  }

  public int auctionState(Auction auction) {
    MarketQuote quote = auction.getQuote();
    double bid = quote.getBid();
    double ask = quote.getAsk();
    int bidBin = 0;
    int askBin = 0;
    if ( ! Double.isInfinite(bid) ) {
      bidBin = ((int) ( (bid - bidBinStart) / bidBinWidth)) + 1;
    }
    if ( ! Double.isInfinite(ask) ) {
      askBin = ((int) ( (ask - askBinStart) / askBinWidth)) + 1;
    }
    return bidBin*quoteBins + askBin;
  }

  public void reset() {
    super.reset();
    ((Resetable) learner).reset();
  }

  public Learner getLearner() {
    return learner;
  }

  public void setLearner( Learner learner ) {
    this.learner = (MDPLearner) learner;
  }

  public String toString() {
    return "(" + getClass() + " learner:" + learner + ")";
  }

}
