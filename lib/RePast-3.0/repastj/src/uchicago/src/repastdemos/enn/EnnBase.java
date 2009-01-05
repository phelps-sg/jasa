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
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.games.PayoffPair;
import uchicago.src.sim.space.Object2DTorus;
import uchicago.src.sim.util.Random;

import java.util.ArrayList;


/**
 * Base class for EnnModels. The gui model (EnnModel.java) and the batch
 * model share common behavoir and state. This class encapsulates
 * these shared qualities.
 *
 * A partial implementation of Randal Picker's Endogenous Neighborhood
 * game as described in his as yet unpublished "Endogenous Neighborhoods and
 * Norms." This simulation extends prior work by Randal Picker. See "Simple
 * Games in a Complex World: A Generative Approach to the Adoption of Norms",
 * 64 University of Chicago Law Review 1225 (1997). Thanks to Randal Picker
 * for allowing us to include this with Repast.
 *
 * Here agent's
 * play a coordination game with their neighbors and receive a payoff. After
 * all agents have have played the game, then each agents determines whether
 * the current location and strategy meet their aspiration, changing strategies
 * and moving if necesary. As described in the paper, satisfied agents
 * can play a strategy of "strategy upgrade" or "pure aspiration". Here only
 * strategy upgrade is implemented. Playing this strategy means that a content
 * agent finds the best average strategy of all its neighbors and plays this
 * the next turn. Unsatisfied agents can have a strategy of either move and
 * change
 * or change first. A strategy of move and changes means that an agent moves
 * a random distance in a random direction and then changes its stratgey to
 * the best average strategy of all its neighbors. Change first means that
 * an agent changes its strategy and then if still unsatisfied, changes
 * to a move and change strategy the next turn.<p>
 *
 * These values can be changed as initial parameters before a run<br>.
 * PURE_ASPIRATION = 0<br>
 * STRATEGY_UPGRADE = 1<br>
 * CHANGE_FIRST = 2<br>
 * MOVE_AND_CHANGE = 3<br>
 *
 * See the playGame method of EnnAgent for the details of the strategies.<br>
 * See SugarModel.java for more on the general structure of a simulation model
 * file.
 *
 * @author Nick Collier and Randal Picker
 * @version $Revision$ $Date$
 */

// Like all simulation models EnnModel extends SimModelImpl
public abstract class EnnBase extends SimModelImpl {
	protected static final int WORLD_X_SIZE = 101;
	protected static final int WORLD_Y_SIZE = 101;
	protected int aspiration = 0;
	protected int gridDensity = 50;
	protected float bValue = 1.1f;
	protected int leftPercent = 50;
	protected int unhappyPlay = EnnAgent.MOVE_AND_CHANGE;
	protected int happyPlay = EnnAgent.STRATEGY_UPGRADE;
	protected boolean shuffle = false;
	protected Schedule schedule;
	protected ArrayList agentList = new ArrayList();

	public static final int DO_LOAD_GUI		= 1;
	public static final int DONT_LOAD_GUI	= 0;
	
	/**
	 * if the model should load gui elements or not, used to run the nonbatch
	 * model in batch mode.
	 */
	protected int loadGui = DO_LOAD_GUI;
	
	// The agents operate in a Torus
	protected Object2DTorus space;

	// use a DataRecorder object to record any relevant data
	protected DataRecorder recorder;

	public EnnBase() {
	}

	// the get/set methods allowing for graphical and batch manipulation of the
	// initial parameters. See SugarModel.java for more information on get/set
	// methods in a model.
	public int getAspiration() {
		return aspiration;
	}

	public void setAspiration(int newAspiration) {
		aspiration = newAspiration;
	}

	public int getGridDensity() {
		return gridDensity;
	}

	public void setGridDensity(int newDensity) {
		gridDensity = newDensity;
	}

	public void setBValue(float newBValue) {
		bValue = newBValue;
	}

	public float getBValue() {
		return bValue;
	}

	public int getPercentLeft() {
		return leftPercent;
	}

	public void setPercentLeft(int newPercentLeft) {
		leftPercent = newPercentLeft;
	}

	public void setHappyPlay(int play) {
		happyPlay = play;
	}

	public int getHappyPlay() {
		return happyPlay;
	}

	public void setUnhappyPlay(int play) {
		unhappyPlay = play;
	}

	public int getUnhappyPlay() {
		return unhappyPlay;
	}

	public void setDoShuffle(int i) {
		if (i > 0) {
			shuffle = true;
		} else {
			shuffle = false;
		}
	}

	public int getDoShuffle() {
		return shuffle ? 1 : 0;
	}

	// The results of three compute*() methods are collected by the
	// DataRecorder object and written to a file.
	// computes overall average connectedness
	public double computeOAC() {
		int totalNeighbors = 0;

		for (int i = 0; i < agentList.size(); i++) {
			EnnAgent agent = (EnnAgent) agentList.get(i);
			int numNeighbors = space.getMooreNeighbors(agent.getX(),
													   agent.getY(), 2, 2, false)
									.size() + 1;
			totalNeighbors += numNeighbors;
		}

		return (double) totalNeighbors / (double) agentList.size();
	}

	// computes percentage in final round the play superior strategy
	public double computeRave() {
		int redCount = 0;
		int curStrat;
		int prevStrat;

		for (int i = 0; i < agentList.size(); i++) {
			EnnAgent agent = (EnnAgent) agentList.get(i);
			curStrat = agent.getCurStrategy();
			prevStrat = agent.getPrevStrategy();

			if ((curStrat == EnnAgent.RIGHT) && (prevStrat == EnnAgent.RIGHT)) {
				redCount++;
			}
		}

		return (double) redCount / (double) agentList.size();
	}

	// computes the overall social welfare average
	public double computeOSWA() {
		int totalPayoff = 0;

		for (int i = 0; i < agentList.size(); i++) {
			EnnAgent agent = (EnnAgent) agentList.get(i);
			totalPayoff += agent.getPayoff();
		}

		return (double) totalPayoff / (double) agentList.size();
	}

	protected abstract void initDataRecorder();

	// builds the model
	// this is a good example of how initial parameters are used in the creation
	// of agents and their distribution in the "agent space".
	protected void buildModel() {
		Random.createUniform();
		space = new Object2DTorus(WORLD_X_SIZE, WORLD_Y_SIZE);

		// See EnnGame.java for more
		EnnGame game = new EnnGame();
		game.setPayoffs(new PayoffPair(1.0f, 1.0f), new PayoffPair(0, 0),
						new PayoffPair(0, 0), new PayoffPair(bValue, bValue));

		int numAgents = (int) ((WORLD_X_SIZE * WORLD_Y_SIZE) * (gridDensity * .01));

		//float leftPer = (float)(leftPercent * .01);
		//int numLeft = (int)(numAgents * leftPer);
		//int leftCount = 0;
		for (int i = 0; i < numAgents; i++) {
			int x;
			int y;
			int strategy;

			do {
				x = Random.uniform.nextIntFromTo(0, space.getSizeX() - 1);
				y = Random.uniform.nextIntFromTo(0, space.getSizeY() - 1);
			} while (space.getObjectAt(x, y) != null);

			int leftIndex = Random.uniform.nextIntFromTo(0, 100);

			if (leftIndex <= leftPercent) {
				strategy = EnnAgent.LEFT;

				//leftCount++;
			} else {
				strategy = EnnAgent.RIGHT;
			}

			EnnAgent agent = new EnnAgent(x, y, aspiration, strategy, game,
										  space, happyPlay, unhappyPlay);
			agentList.add(agent);
			space.putObjectAt(x, y, agent);
		}

		//System.out.println("Num Agents: " + numAgents);
		//System.out.println("Left count: " + leftCount);
		initDataRecorder();
	}

	// The required SimModel interface methods
	public String[] getInitParam() {
		String[] params = {
							  "aspiration", "bValue", "happyPlay", "unhappyPlay",
							  "gridDensity", "percentLeft", "doShuffle", "loadGui"
						  };

		return params;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public String getName() {
		return "Enn";
	}

	public int getLoadGui() {
		return loadGui;
	}

	public void setLoadGui(int loadGui) {
		this.loadGui = loadGui;
	}

}
