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

package uk.ac.liv.auction.stats;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.ac.liv.auction.agent.AbstractTradingAgent;

import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;

import uk.ac.liv.util.SummaryStats;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;

import uk.ac.liv.util.io.DataWriter;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class ComplexityReport extends AbstractAuctionReport implements
    Resetable, Serializable, Parameterizable {

	protected int numStates = 20;

	protected SummaryStats globalMargin = new SummaryStats();

	protected HashMap independentHistograms;

	protected HashMap jointHistograms;

	protected DataWriter complexitySequence;

	protected ArrayList agents;

	public static final String P_DEF_BASE = "complexityreport";

	public static final String P_WRITER = "writer";

	public static final String P_NUMSTATES = "numstates";

	public ComplexityReport() {
		initialise();
	}

	public void reset() {
		globalMargin.reset();
		initialise();
	}

	public void initialise() {
		independentHistograms = new HashMap();
		jointHistograms = new HashMap();
		agents = null;
	}

	public void eventOccurred(AuctionEvent event) {
		if (event instanceof ShoutPlacedEvent) {
			updateShoutLog((ShoutPlacedEvent) event);
		} else if (event instanceof RoundClosedEvent) {
			roundClosed(event);
		}
	}

	public void updateShoutLog(ShoutPlacedEvent event) {
		Shout shout = event.getShout();
		AbstractTradingAgent agent = (AbstractTradingAgent) shout.getAgent();
		double markup = markup(agent);
		globalMargin.newData(markup);
	}

	public void setup(ParameterDatabase parameters, Parameter base) {

		Parameter defBase = new Parameter(P_DEF_BASE);

		complexitySequence = (DataWriter) parameters.getInstanceForParameter(base
		    .push(P_WRITER), defBase.push(P_WRITER), DataWriter.class);

		if (complexitySequence instanceof Parameterizable) {
			((Parameterizable) complexitySequence).setup(parameters, base
			    .push(P_WRITER));
		}

		numStates = parameters.getIntWithDefault(base.push(P_NUMSTATES), defBase
		    .push(P_NUMSTATES), numStates);

	}

	public void produceUserOutput() {
	}

	public Map getVariables() {
		return new HashMap();
	}

	public void roundClosed(AuctionEvent event) {
		buildAgentList();
		updateIndependentHistograms();
		updateJointHistograms();
		complexitySequence.newData(calculateTotalIndependenceDistance());
	}

	public double calculateTotalIndependenceDistance() {
		double distance = 0;
		for (int i = 0; i < agents.size(); i++) {
			for (int j = 0; j < i; j++) {
				AbstractTradingAgent agent1 = (AbstractTradingAgent) agents.get(i);
				AbstractTradingAgent agent2 = (AbstractTradingAgent) agents.get(j);
				distance += independence(agent1, agent2);
			}
		}
		return distance / ((agents.size() * agents.size()) / 2);
	}

	protected void updateJointHistograms() {
		for (int i = 0; i < agents.size(); i++) {
			for (int j = 0; j < i; j++) {
				if (i != j) {
					AbstractTradingAgent agent1 = (AbstractTradingAgent) agents.get(i);
					AbstractTradingAgent agent2 = (AbstractTradingAgent) agents.get(j);
					int si = state(agent1);
					int sj = state(agent2);
					AgentPair pair = new AgentPair(agent1, agent2);
					AgentStateHistogram histogram = (AgentStateHistogram) jointHistograms
					    .get(pair);
					if (histogram == null) {
						histogram = new AgentStateHistogram();
						jointHistograms.put(pair, histogram);
					}
					if (si == sj) {
						histogram.newState(si);
					} else {
						histogram.nullState();
					}
				}
			}
		}
	}

	protected void updateIndependentHistograms() {
		for (int i = 0; i < agents.size(); i++) {
			AbstractTradingAgent agent = (AbstractTradingAgent) agents.get(i);
			AgentStateHistogram histogram = (AgentStateHistogram) independentHistograms
			    .get(agent);
			if (histogram == null) {
				histogram = new AgentStateHistogram();
				independentHistograms.put(agent, histogram);
			}
			histogram.newState(state(agent));
		}
	}

	protected void buildAgentList() {
		if (agents == null) {
			agents = new ArrayList();
			Iterator i = ((RandomRobinAuction) auction).getTraderIterator();
			while (i.hasNext()) {
				AbstractTradingAgent agent = (AbstractTradingAgent) i.next();
				agents.add(agent);
			}
		}
	}

	protected double independence(AbstractTradingAgent agent1,
	    AbstractTradingAgent agent2) {

		AgentStateHistogram h1 = (AgentStateHistogram) independentHistograms
		    .get(agent1);
		AgentStateHistogram h2 = (AgentStateHistogram) independentHistograms
		    .get(agent2);

		AgentStateHistogram jointHistogram = (AgentStateHistogram) jointHistograms
		    .get(new AgentPair(agent1, agent2));

		if (jointHistogram == null) {
			jointHistogram = new AgentStateHistogram();
		}

		double independence = 0;
		for (int s = 0; s < numStates; s++) {

			double jointProbability = jointHistogram.getProbability(s);
			double independentProbability = h1.getProbability(s)
			    * h2.getProbability(s);

			independence += Math.abs(jointProbability - independentProbability);

		}

		return independence;
	}

	protected int state(AbstractTradingAgent agent) {
		return state(markup(agent), agent);
	}

	protected int state(double markup, AbstractTradingAgent agent) {
		double markupBin = globalMargin.getMax() / numStates;
		int bin = (int) (markup / markupBin);
		if (bin >= numStates) {
			bin = numStates - 1;
		}
		return (int) bin;
	}

	protected double markup(AbstractTradingAgent agent) {

		double markup = Math.abs(agent.getValuation(auction)
		    - agent.getCurrentShout().getPrice());

		return markup;
	}

	class AgentStateHistogram {

		int[] frequency;

		int total = 0;

		public AgentStateHistogram() {
			frequency = new int[numStates];
		}

		public void newState(int state) {
			frequency[state]++;
			total++;
		}

		public void nullState() {
			total++;
		}

		public double getProbability(int state) {
			if (total == 0) {
				return 1 / numStates;
			} else {
				return (double) frequency[state] / (double) total;
			}
		}

	}

}

class AgentPair {

	protected AbstractTradingAgent agent1;

	protected AbstractTradingAgent agent2;

	public AgentPair(AbstractTradingAgent agent1, AbstractTradingAgent agent2) {
		this.agent1 = agent1;
		this.agent2 = agent2;
	}

	public boolean equals(Object other) {
		AgentPair otherPair = (AgentPair) other;
		return (this.agent1.equals(otherPair.agent1) && this.agent2
		    .equals(otherPair.agent2))
		    || (this.agent2.equals(otherPair.agent1) && this.agent1
		        .equals(otherPair.agent2));
	}

	public int hashCode() {
		int hash = 0;
		if (agent1 != null) {
			hash += agent1.hashCode();
		}
		if (agent2 != null) {
			hash += agent2.hashCode();
		}
		return hash;
	}

}
