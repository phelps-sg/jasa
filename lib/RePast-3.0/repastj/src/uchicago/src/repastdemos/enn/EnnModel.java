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

import uchicago.src.reflector.ListPropertyDescriptor;

import uchicago.src.sim.analysis.DataRecorder;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Legend;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.util.SimUtilities;

import java.awt.Color;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * A partial implementation of Randal Picker's Endogenous Neighborhood game as
 * described in his as yet unpublished "Endogenous Neighborhoods and Norms."
 * This simulation extends prior work by Randal Picker. See "Simple Games in a
 * Complex World: A Generative Approach to the Adoption of Norms", 64 University
 * of Chicago Law Review 1225 (1997). Thanks to Randal Picker for allowing us to
 * include this with Repast.
 * 
 * Here agent's play a coordination game with their neighbors and receive a
 * payoff. After all agents have have played the game, then each agents
 * determines whether the current location and strategy meet their aspiration,
 * changing strategies and moving if necesary. As described in the paper,
 * satisfied agents can play a strategy of "strategy upgrade" or "pure
 * aspiration". Here only strategy upgrade is implemented. Playing this strategy
 * means that a content agent finds the best average strategy of all its
 * neighbors and plays this the next turn. Unsatisfied agents can have a
 * strategy of either move and change or change first. A strategy of move and
 * changes means that an agent moves a random distance in a random direction and
 * then changes its stratgey to the best average strategy of all its neighbors.
 * Change first means that an agent changes its strategy and then if still
 * unsatisfied, changes to a move and change strategy the next turn.
 * <p/>
 * 
 * These values can be changed as initial parameters before a run <br/>.
 * PURE_ASPIRATION = 0 <br/>
 * STRATEGY_UPGRADE = 1 <br/>
 * CHANGE_FIRST = 2 <br/>
 * MOVE_AND_CHANGE = 3 <br/>
 * 
 * See the playGame method of EnnAgent for the details of the strategies. <br/>
 * See SugarModel.java for more on the general structure of a simulation model
 * file.
 * 
 * @author Nick Collier and Randal Picker
 * @version $Revision$ $Date$
 */
// Like all simulation models EnnModel extends SimModelImpl
public class EnnModel extends EnnBase {
	private DisplaySurface dsurf;

	public EnnModel() {
		Hashtable h1 = new Hashtable();
		h1.put(new Integer(EnnAgent.PURE_ASPIRATION), "PURE ASPIRATION");
		h1.put(new Integer(EnnAgent.STRATEGY_UPGRADE), "STRATEGY UPGRADE");
		h1.put(new Integer(EnnAgent.CHANGE_FIRST), "CHANGE FIRST");
		h1.put(new Integer(EnnAgent.MOVE_AND_CHANGE), "MOVE AND CHANGE");

		ListPropertyDescriptor pd = new ListPropertyDescriptor("HappyPlay", h1);
		descriptors.put("HappyPlay", pd);

		ListPropertyDescriptor pd1 = new ListPropertyDescriptor("UnhappyPlay",
				h1);
		descriptors.put("UnhappyPlay", pd1);
		
		h1.clear();
		h1.put(new Integer(EnnBase.DO_LOAD_GUI), "Yes");
		h1.put(new Integer(EnnBase.DONT_LOAD_GUI), "No");
		ListPropertyDescriptor pdLoadGui = new ListPropertyDescriptor("LoadGui", h1);
		descriptors.put("LoadGui", pdLoadGui);
	}

	// builds the display
	private void buildDisplay() {
		Object2DDisplay agentDisplay = new Object2DDisplay(space);
		agentDisplay.setObjectList(agentList);

		dsurf.addDisplayableProbeable(agentDisplay, "Agents");

		// UNCOMMENT THE TWO LINES BELOW TO TAKE SNAPSHOTS AND MAKE MOVIES
		// dsurf.setSnapshotFileName("./EnnPic");
		// dsurf.setMovieName("./EnnMovie", DisplaySurface.QUICK_TIME);
		dsurf.setBackground(java.awt.Color.white);
		addSimEventListener(dsurf);

		dsurf.createLegend("Enn Legend");
		dsurf.addLegendLabel("Played right then right", Legend.SQUARE,
				Color.red, false);
	}

	protected void initDataRecorder() {
		// recorder uses the compute* methods as sources for its data
		recorder = new DataRecorder("./EnnData.txt", this);

		// As of Repast 1.3 this is just as fast as creating your own
		// inner classes. (If this means nothing to you, don't worry about it).
		recorder.createNumericDataSource("OAC", this, "computeOAC", -1, 4);
		recorder.createNumericDataSource("Rave", this, "computeRave");
		recorder.createNumericDataSource("OSWA", this, "computeOSWA", -1, 4);
	}

	// builds the schedule
	private void buildSchedule() {
		schedule.scheduleActionBeginning(0, new BasicAction() {
			public void execute() {
				// if we're running in batch mode stop at 200.
				if (loadGui == DONT_LOAD_GUI &&
						EnnModel.this.getController().getCurrentTime() == 200) {
					EnnModel.this.stop();
				}

				for (int i = 0; i < agentList.size(); i++) {
					EnnAgent agent = (EnnAgent) agentList.get(i);
					agent.playGame();
				}

				if (dsurf != null)
					dsurf.updateDisplay();
				// UNCOMMENT THIS LINE TO ADD A FRAME TO THE MOVIE EVERY
				// ITERATION
				//dsurf.addMovieFrame();
				if (shuffle) {
					SimUtilities.shuffle(agentList);
				}

				for (int i = 0; i < agentList.size(); i++) {
					EnnAgent agent = (EnnAgent) agentList.get(i);
					agent.makeStrategyDecision();
				}
			}
		});

		// UNCOMMENT LINE BELOW IF MAKING A MOVIE
		//schedule.scheduleActionAtEnd(dsurf, "closeMovie");
		// data is recorded and written to a file at the end
		// i.e. whenever the user clicks the stop button.
		schedule.scheduleActionAtEnd(recorder, "record");
		schedule.scheduleActionAtEnd(recorder, "writeToFile");

		// UNCOMMENT LINE BELOW TO TAKE SNAPSHOT AT END
		//schedule.scheduleActionAtEnd(dsurf, "takeSnapshot");
	}

	public void begin() {
		buildModel();

		if (loadGui == DO_LOAD_GUI) {
			buildDisplay();
		}

		buildSchedule();

		if (dsurf != null && loadGui == DO_LOAD_GUI) {
			dsurf.display();
		}
	}

	public void setup() {
		if (dsurf != null) {
			dsurf.dispose();
		}

		dsurf = null;
		schedule = null;
		System.gc();

		if (loadGui == DO_LOAD_GUI) {
			dsurf = new DisplaySurface(this, "Enn Display");
			registerDisplaySurface("Enn Display", dsurf);
		}
		schedule = new Schedule(1);

		bValue = 1.1f;
		aspiration = 0;
		gridDensity = 50;
		leftPercent = 50;
		happyPlay = EnnAgent.STRATEGY_UPGRADE;
		unhappyPlay = EnnAgent.MOVE_AND_CHANGE;

		agentList = new ArrayList();
		space = null;
		recorder = null;
		shuffle = false;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public String getName() {
		return "Enn";
	}

	public static void main(String[] args) {
		SimInit init = new SimInit();
		EnnModel model = new EnnModel();
		init.loadModel(model, null, false);
	}
}