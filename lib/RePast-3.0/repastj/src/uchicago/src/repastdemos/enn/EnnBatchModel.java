/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Chicago nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.repastdemos.enn;

import uchicago.src.sim.analysis.DataRecorder;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.util.SimUtilities;

import java.util.ArrayList;


/**
 * A partial implementation of Randal Picker's Endogenous Neighborhood
 * game as described in his as yet unpublished "Endogenous Neighborhoods and
 * Norms." This simulation extends prior work by Randal Picker. See "Simple
 * Games in a Complex World: A Generative Approach to the Adoption of Norms",
 * 64 University of Chicago Law Review 1225 (1997). Thanks to Randal Picker
 * for allowing us to include this with Repast.
 *
 * Batch version of the EnnModel - virtually the same, but without any
 * display classes, and an arbitrary run stop at 200 ticks
 *
 * @author Nick Collier and Randal Picker
 * @version $Revision$ $Date$
 */
public class EnnBatchModel extends EnnBase {
	protected void initDataRecorder() {
		recorder = new DataRecorder("./EnnData.txt", this);

		// As of Repast 1.3 this is just as fast as creating your own
		// inner classes. (If this means nothing to you, don't worry about it).
		recorder.createNumericDataSource("OAC", this, "computeOAC", -1, 4);
		recorder.createNumericDataSource("Rave", this, "computeRave");
		recorder.createNumericDataSource("OSWA", this, "computeOSWA", -1, 4);
	}

	private void buildSchedule() {
		schedule.scheduleActionBeginning(0,
										 new BasicAction() {
				public void execute() {
					for (int i = 0; i < agentList.size(); i++) {
						EnnAgent agent = (EnnAgent) agentList.get(i);
						agent.playGame();
					}

					if (shuffle) {
						SimUtilities.shuffle(agentList);
					}

					for (int i = 0; i < agentList.size(); i++) {
						EnnAgent agent = (EnnAgent) agentList.get(i);
						agent.makeStrategyDecision();
					}
				}
			});

		schedule.scheduleActionAtEnd(recorder, "record");
		schedule.scheduleActionAtEnd(recorder, "writeToFile");

		// This means stop this run of the simulation after 200 iterations
		// Change the 200 here for longer or shorter runs
		schedule.scheduleActionAt(200, this, "stop");
	}

	public void begin() {
		buildModel();
		buildSchedule();
	}

	public void setup() {
		schedule = new Schedule(1);

		bValue = 1.1f;
		aspiration = 0;
		gridDensity = 50;
		leftPercent = 50;
		happyPlay = EnnAgent.STRATEGY_UPGRADE;
		unhappyPlay = EnnAgent.MOVE_AND_CHANGE;

		agentList = new ArrayList();
		space = null;
		shuffle = false;

		System.gc();
	}

	public void stop() {
		this.fireStopSim();
	}

	public static void main(String[] args) {
		SimInit init = new SimInit();
		EnnBatchModel model = new EnnBatchModel();
		
		// try to find the parameter file
		String parameterFile = SimUtilities.getDataFileName("EnnParams.txt");
		System.out.println(parameterFile);		
		init.loadModel(model, parameterFile, true);
	}
}
