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

import org.apache.log4j.Logger;

import uk.ac.liv.ai.learning.MimicryLearner;
import uk.ac.liv.ai.learning.SelfKnowledgable;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;
import uk.ac.liv.auction.stats.ReportVariableBoard;
import uk.ac.liv.util.Parameterizable;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * implements the shout-accepting rule under which a shout must be more
 * competitive than an estimated equilibrium.
 * 
 * The equilibrium is estimated through some learning algorithm, e.g.
 * sliding-window-average learning and widrowhoff learning, by training with
 * transaction prices.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class EquilibriumBeatingAcceptingPolicy extends
    QuoteBeatingAcceptingPolicy {

	static Logger logger = Logger
	    .getLogger(EquilibriumBeatingAcceptingPolicy.class);

	/**
	 * Reusable exceptions for performance
	 */
	protected static IllegalShoutException bidException = null;

	protected static IllegalShoutException askException = null;

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

	protected MimicryLearner learner;

	public static final String P_LEARNER = "learner";

	public static final String P_DEF_BASE = "equilibriumbeatingaccepting";

	public static final String EST_EQUILIBRIUM_PRICE = "estimated.equilibrium.price";

	public void setup(ParameterDatabase parameters, Parameter base) {
		super.setup(parameters, base);

		Parameter defBase = new Parameter(P_DEF_BASE);

		delta = parameters.getDoubleWithDefault(base.push(P_DELTA), defBase
		    .push(P_DELTA), delta);
		assert (0 <= delta);

		learner = (MimicryLearner) parameters.getInstanceForParameter(base
		    .push(P_LEARNER), defBase.push(P_LEARNER), MimicryLearner.class);
		if (learner instanceof Parameterizable) {
			((Parameterizable) learner).setup(parameters, base.push(P_LEARNER));
		}
	}

	public void initialise() {
		expectedHighestAsk = Double.MAX_VALUE;
		expectedLowestBid = 0;
	}

	public void reset() {
		initialise();
	}

	/**
	 * checks whether
	 * <p>
	 * shout
	 * </p>
	 * can beat the estimated equilibrium.
	 */
	public void check(Shout shout) throws IllegalShoutException {
		super.check(shout);

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

	protected void bidNotAnImprovementException() throws IllegalShoutException {
		if (bidException == null) {
			// Only construct a new exception the once (for improved
			// performance)
			bidException = new IllegalShoutException(
			    "Bid cannot beat the estimated equilibrium!");
		}
		throw bidException;
	}

	protected void askNotAnImprovementException() throws IllegalShoutException {
		if (askException == null) {
			// Only construct a new exception the once (for improved
			// performance)
			askException = new IllegalShoutException(
			    "Ask cannot beat the estimated equilibrium!");
		}
		throw askException;
	}

	public void eventOccurred(AuctionEvent event) {
		super.eventOccurred(event);

		if (event instanceof TransactionExecutedEvent) {
			learner.train(((TransactionExecutedEvent) event).getPrice());

			if (learner instanceof SelfKnowledgable
			    && ((SelfKnowledgable) learner).goodEnough()) {

				expectedLowestBid = learner.act() - delta;
				expectedHighestAsk = learner.act() + delta;

				ReportVariableBoard.getInstance().reportValue(EST_EQUILIBRIUM_PRICE,
				    learner.act(), event);
			}
		}
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

	public double getDelta() {
		return delta;
	}

	public MimicryLearner getLearner() {
		return learner;
	}

	public void setLearner(MimicryLearner learner) {
		this.learner = learner;
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + " delta:" + delta + " " + learner
		    + ")";
	}
}
