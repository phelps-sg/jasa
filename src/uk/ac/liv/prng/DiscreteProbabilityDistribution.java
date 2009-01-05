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

package uk.ac.liv.prng;

import uk.ac.liv.util.SummaryStats;
import uk.ac.liv.util.Resetable;
import uk.ac.liv.util.Prototypeable;

import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * A class representing a discrete probability distribution which can used to
 * generate random events according to the specified distribution. The output
 * from a uniform PRNG is used to to select from the different possible events.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class DiscreteProbabilityDistribution implements Resetable,
    Serializable, Prototypeable {

	/**
	 * The probability distribution.
	 * 
	 * @uml.property name="p"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	protected double p[];

	/**
	 * The number of possible events for this distribution.
	 * 
	 * @uml.property name="k"
	 */
	protected int k;

	/**
	 * The log4j logger.
	 */
	static Logger logger = Logger
	    .getLogger(DiscreteProbabilityDistribution.class);

	/**
	 * Construct a new distribution with k possible events.
	 * 
	 * @param k
	 *          The number of possible events for this random variable
	 */
	public DiscreteProbabilityDistribution(int k) {
		this.k = k;
		p = new double[k];
		for (int i = 0; i < k; i++) {
			p[i] = 1.0 / k;
		}
	}

	public Object protoClone() {
		DiscreteProbabilityDistribution clonedDistribution;
		try {
			clonedDistribution = (DiscreteProbabilityDistribution) clone();
			clonedDistribution.p = (double[]) this.p.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
		return clonedDistribution;
	}

	/**
	 * Set the probability of the ith event.
	 * 
	 * @param i
	 *          The event
	 * @param probability
	 *          The probability of event i occuring
	 */
	public void setProbability(int i, double probability) {
		p[i] = probability;
	}

	/**
	 * Get the probability of the ith event.
	 * 
	 * @param i
	 *          The event
	 */
	public double getProbability(int i) {
		return p[i];
	}

	/**
	 * Generate a random event according to the probability distribution.
	 * 
	 * @return An integer value representing one of the possible events.
	 */
	public int generateRandomEvent() {
		double rand = GlobalPRNG.getInstance().raw();
		double cummProb = 0;
		for (int i = 0; i < k; i++) {
			cummProb += p[i];
			if (rand < cummProb) {
				return i;
			}
		}
		throw new ProbabilityError(this);
	}

	public void reset() {
		for (int i = 0; i < k; i++) {
			p[i] = 0;
		}
	}

	/**
	 * Compute the expected value of the random variable defined by this
	 * distribution.
	 * 
	 * @return The expected value of the distribution
	 */
	public double computeMean() {
		double total = 0;
		for (int i = 0; i < k; i++) {
			total += i * p[i];
		}
		return total;
	}

	/**
	 * Compute the minimum value of the random variable defined by this
	 * distribution.
	 * 
	 * @return The minimum integer value
	 */
	public int computeMin() {
		for (int i = 0; i < k; i++) {
			if (p[i] > 0) {
				return i;
			}
		}
		throw new ProbabilityError(this);
	}

	/**
	 * Compute the maximum value of the random variable defined by this
	 * distribution.
	 * 
	 * @return The maximum integer value
	 */
	public int computeMax() {
		for (int i = k - 1; i >= 0; i--) {
			if (p[i] > 0) {
				return i;
			}
		}
		throw new ProbabilityError(this);
	}

	public void computeStats(SummaryStats stats) {
	}

	public String toString() {
		StringBuffer s = new StringBuffer("(" + getClass());
		for (int i = 0; i < p.length; i++) {
			s.append(" p[" + i + "]:" + p[i]);
		}
		s.append(")");
		return s.toString();
	}

	public static class ProbabilityError extends Error {

		public ProbabilityError(DiscreteProbabilityDistribution p) {
			super("Probabilities do not sum to 1: " + p);
		}

	}

}
