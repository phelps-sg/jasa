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

package uk.ac.liv.ai.learning;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.io.DataWriter;

import java.io.Serializable;

/**
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class MetaLearner extends AbstractLearner implements
    StimuliResponseLearner, Parameterizable, Serializable {

	/**
	 * @uml.property name="currentLearner"
	 */
	protected int currentLearner;

	/**
	 * @uml.property name="subLearners"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	protected StimuliResponseLearner[] subLearners;

	/**
	 * @uml.property name="masterLearner"
	 * @uml.associationEnd
	 */
	protected StimuliResponseLearner masterLearner;

	static final String P_N = "n";

	static final String P_MASTER = "master";

	public MetaLearner() {
	}

	public MetaLearner(int numLearners) {
		subLearners = new StimuliResponseLearner[numLearners];
	}

	public void setup(ParameterDatabase parameters, Parameter base) {

		masterLearner = (StimuliResponseLearner) parameters
		    .getInstanceForParameter(base.push(P_MASTER), null,
		        StimuliResponseLearner.class);
		if (masterLearner instanceof Parameterizable) {
			((Parameterizable) masterLearner).setup(parameters, base.push(P_MASTER));
		}

		int numLearners = parameters.getInt(base.push(P_N), null, 1);

		subLearners = new StimuliResponseLearner[numLearners];

		for (int i = 0; i < numLearners; i++) {

			StimuliResponseLearner sub = (StimuliResponseLearner) parameters
			    .getInstanceForParameter(base.push(i + ""), null,
			        StimuliResponseLearner.class);

			if (sub instanceof Parameterizable) {
				((Parameterizable) sub).setup(parameters, base.push(i + ""));
			}

			subLearners[i] = sub;
		}
	}

	public int act() {
		currentLearner = masterLearner.act();
		return subLearners[currentLearner].act();
	}

	public void reward(double reward) {
		masterLearner.reward(reward);
		subLearners[currentLearner].reward(reward);
	}

	public double getLearningDelta() {
		return masterLearner.getLearningDelta();
	}

	public int getNumberOfActions() {
		return subLearners.length;
	}

	public void dumpState(DataWriter out) {
		// TODO
	}

}
