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

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.learning.Learner;
import net.sourceforge.jabm.learning.MimicryLearner;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.event.AgentPolledEvent;
import net.sourceforge.jasa.event.MarketOpenEvent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;


/**
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class MomentumStrategy extends FixedDirectionStrategy implements
    Serializable {

	protected MimicryLearner learner;

	protected double currentPrice;

	protected Order lastShout;

	/**
	 * A parameter used to scale the randomly drawn price adjustment perturbation
	 * values.
	 */
	protected double scaling = 0.01;

	protected boolean lastShoutAccepted;

	protected double trPrice, trBidPrice, trAskPrice;

	protected AbstractContinousDistribution initialMarginDistribution;

	protected AbstractContinousDistribution relativePerterbationDistribution;

	protected AbstractContinousDistribution absolutePerterbationDistribution;

	protected RandomEngine prng;
	
	static Logger logger = Logger.getLogger(MomentumStrategy.class);

	public MomentumStrategy(AbstractTradingAgent agent, RandomEngine prng) {
		super(agent);
		this.prng = prng;
		initialise();		
	}

	

//	public void setup(ParameterDatabase parameters, Parameter base) {
//
//		super.setup(parameters, base);
//
//		Parameter defBase = new Parameter(P_DEF_BASE);
//
//		scaling = parameters.getDoubleWithDefault(base.push(P_SCALING), defBase
//		    .push(P_SCALING), scaling);
//
//		learner = (MimicryLearner) parameters.getInstanceForParameter(base
//		    .push(P_LEARNER), defBase.push(P_LEARNER), MimicryLearner.class);
//		if (learner instanceof Parameterizable) {
//			((Parameterizable) learner).setup(parameters, base.push(P_LEARNER));
//		}
//
//		initialise();
//
//		report.debug("Initialised with scaling = " + scaling + " and learner = "
//		    + learner);
//
//	}

	public void initialise() {
		super.initialise();
		relativePerterbationDistribution = new Uniform(0, scaling, prng);
		absolutePerterbationDistribution = new Uniform(0, 0.05, prng);
		initialMarginDistribution = new Uniform(0.05, 0.35, prng);
	}

	public boolean modifyShout(Order shout) {
		shout.setPrice(currentPrice);
		return super.modifyShout(shout);
	}
	
	

	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		super.subscribeToEvents(scheduler);
		scheduler.addListener(TransactionExecutedEvent.class, this);
		scheduler.addListener(OrderPlacedEvent.class, this);
		scheduler.addListener(AgentPolledEvent.class, this);
		scheduler.addListener(MarketOpenEvent.class, this);
	}

	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof TransactionExecutedEvent) {
			onTransactionExecuted((TransactionExecutedEvent) event);
		} else if (event instanceof OrderPlacedEvent) {
			onOrderPlaced((OrderPlacedEvent) event);
		} else if (event instanceof AgentPolledEvent) {
			onAgentPolled((AgentPolledEvent) event);
		} else if (event instanceof MarketOpenEvent) {
			onMarketOpen();
		}
	}

	public void onMarketOpen() {
		if (isSell()) {
			setMargin(initialMarginDistribution.nextDouble());
		} else {
			setMargin(-initialMarginDistribution.nextDouble());
		}
		updateCurrentPrice();
	}

	protected void onAgentPolled(AgentPolledEvent event) {
		auction = event.getAuction();
		if (event.getAgent() != agent) {
			adjustMargin();
		}
	}

	protected void onOrderPlaced(OrderPlacedEvent event) {
		lastShout = event.getOrder();
		lastShoutAccepted = false;
	}

	protected void onTransactionExecuted(TransactionExecutedEvent event) {
		lastShoutAccepted = lastShout.isAsk() && event.getAsk().equals(lastShout)
		    || lastShout.isBid() && event.getBid().equals(lastShout);
		if (lastShoutAccepted) {
			trPrice = event.getPrice();
			trBidPrice = event.getBid().getPrice();
			trAskPrice = event.getAsk().getPrice();
		}
	}

	public void onRoundClosed(Market auction) {

	}

	public void setLearner(Learner learner) {
		this.learner = (MimicryLearner) learner;
	}

	public Learner getLearner() {
		return learner;
	}

	public void setMargin(double margin) {
		learner.setOutputLevel(margin);
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public Order getLastShout() {
		return lastShout;
	}

	public boolean isLastShoutAccepted() {
		return lastShoutAccepted;
	}

	public void setScaling(double scaling) {
		assert scaling >= 0 && scaling <= 1;
		this.scaling = scaling;
	}

	public double getScaling() {
		return scaling;
	}

	public double getTrAskPrice() {
		return trAskPrice;
	}

	public double getTrBidPrice() {
		return trBidPrice;
	}

	public double getTrPrice() {
		return trPrice;
	}

	private void updateCurrentPrice() {
		currentPrice = calculatePrice(learner.act());
		assert currentPrice > 0;
	}

	protected double calculatePrice(double margin) {
		if ((isBuy() && margin <= 0.0 && margin > -1.0)
		    || (isSell() && margin >= 0.0)) {
			return getAgent().getValuation(auction) * (1 + margin);
		} else {
			return currentPrice;
		}
	}

	protected double targetMargin(double targetPrice) {
		double privValue = getAgent().getValuation(auction);
		double targetMargin = 0;
		targetMargin = (targetPrice - privValue) / privValue;

		return targetMargin;
	}

	protected void adjustMargin(double targetMargin) {
		learner.train(targetMargin);
		updateCurrentPrice();
	}

	protected double perterb(double price) {
		double relative = relativePerterbationDistribution.nextDouble();
		double absolute = absolutePerterbationDistribution.nextDouble();
		return relative * price + absolute;
	}

	protected abstract void adjustMargin();
}