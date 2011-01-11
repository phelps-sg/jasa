/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2009 Steve Phelps
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

package net.sourceforge.jasa.agent.strategy;

import java.io.Serializable;

import net.sourceforge.jabm.learning.Learner;
import net.sourceforge.jabm.learning.MDPLearner;
import net.sourceforge.jabm.util.Parameterizable;
import net.sourceforge.jabm.util.Resetable;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;

/**
 * <p>
 * A trading strategy that uses an MDP learning algorithm, such as the
 * Q-learning algorithm, to adapt its trading behaviour in successive market
 * rounds. The current market-quote is hashed to produce an integer state value.
 * 
 * @author Steve Phelps
 * @version $Revision$
 *          </p>
 */

public class MDPStrategy extends DiscreteLearnerStrategy implements
    Serializable {

	protected MDPLearner learner;

	protected double bidBinStart;

	protected double bidBinWidth;

	protected double askBinStart;

	protected double askBinWidth;

	protected int quoteBins;

	protected boolean firstShout = true;

	public MDPStrategy(AbstractTradingAgent agent, double askBinStart,
	    double askBinWidth, double bidBinStart, double bidBinWidth) {
		super(agent);
		this.askBinStart = askBinStart;
		this.askBinWidth = askBinWidth;
		this.bidBinStart = bidBinStart;
		this.bidBinWidth = bidBinWidth;
	}

	public MDPStrategy() {
		super();
	}

	public int act() {
		return learner.act();
	}

	public void learn(Market auction) {
		learner.newState(agent.getLastPayoff(), auctionState(auction));
	}

	/**
	 * Hash the market quote to produce a state value for the learning algorithm.
	 */
	public int auctionState(Market auction) {
		MarketQuote quote = auction.getQuote();
		double bid = quote.getBid();
		double ask = quote.getAsk();
		int bidBin = 0;
		int askBin = 0;
		if (!Double.isInfinite(bid)) {
			bidBin = ((int) ((bid - bidBinStart) / bidBinWidth)) + 1;
		}
		if (!Double.isInfinite(ask)) {
			askBin = ((int) ((ask - askBinStart) / askBinWidth)) + 1;
		}
		return bidBin * quoteBins + askBin;
	}

	public void reset() {
		super.reset();
		((Resetable) learner).reset();
	}

	public Learner getLearner() {
		return learner;
	}

	public void setLearner(Learner learner) {
		this.learner = (MDPLearner) learner;
	}

	public String toString() {
		return "(" + getClass() + " learner:" + learner + ")";
	}

}
