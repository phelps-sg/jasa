/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2013 Steve Phelps
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

import net.sourceforge.jasa.agent.AbstractTradingAgent;

import org.apache.log4j.Logger;

import cern.jet.random.engine.RandomEngine;

/**
 * <p>
 * An implementation of the Zero-Intelligence-Plus (ZIP) strategy. See:
 * </p>
 * 
 * <p>
 * "Minimal Intelligence Agents for Bargaining Behaviours in Market-based
 * Environments" Dave Cliff 1997.
 * </p>
 * 
 * @author Steve Phelps
 */
public class ZIPStrategy extends MomentumStrategy {

	static Logger logger = Logger.getLogger(ZIPStrategy.class);
	
	public ZIPStrategy() {
		super();
	}

	public ZIPStrategy(AbstractTradingAgent agent, RandomEngine prng) {
		super(agent, prng);
	}

	public ZIPStrategy(RandomEngine prng) {
		this(null, prng);
	}

//	public Object protoClone() {
//		ZIPStrategy clone = new ZIPStrategy(prng);
//		clone.scaling = this.scaling;
//		clone.learner = (MimicryLearner) ((Prototypeable) this.learner)
//		    .protoClone();
//		clone.reset();
//		return clone;
//	}

	protected void adjustMargin() {
		if (isSell()) {
			sellerStrategy();
		} else {
			buyerStrategy();
		}
	}

	protected void sellerStrategy() {

		if (lastShout == null) {
			return;
		}

		if (lastShoutAccepted) {
			if (currentPrice <= trPrice) {
				adjustMargin(targetMargin(trPrice + perterb(trPrice)));
			} else if (getAgent().active()) {
				adjustMargin(targetMargin(trPrice - perterb(trPrice)));
			}
		} else {
			if (getAgent().active()) {
				adjustMargin(targetMargin(lastShout.getPrice()
				    - perterb(lastShout.getPrice())));
			}
		}
	}

	protected void buyerStrategy() {

		if (lastShout == null) {
			return;
		}

		if (lastShoutAccepted) {
			if (currentPrice >= trPrice) {
				adjustMargin(targetMargin(trPrice - perterb(trPrice)));
			} else if (getAgent().active()) {
				adjustMargin(targetMargin(trPrice + perterb(trPrice)));
			}
		} else {
			if (getAgent().active()) {
				adjustMargin(targetMargin(lastShout.getPrice()
				    + perterb(lastShout.getPrice())));
			}
		}
	}
	
}