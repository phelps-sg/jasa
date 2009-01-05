/*$$
 * Copyright (c) 2004, Repast Organization for Architecture and Design (ROAD)
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
 * Neither the name of the ROAD nor the names of its
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
package uchicago.src.repastdemos.genetic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import uchicago.src.sim.engine.AutoStepable;
import uchicago.src.sim.engine.SimpleModel;
import uchicago.src.sim.gui.DefaultGraphLayout;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.RepastException;
import uchicago.src.sim.util.SimUtilities;

/**
 * This demo illustrates the use of the genetic algorithm package in an agent. The
 * premise of this model is that there is a simple competition where the competitors
 * must make a particular amount of change with the least amount of coins possible.
 * Each step a judge will judge the competitors based on the number of coins they
 * used and the total change they have.  For example, if the goal amount is
 * $0.79, an agent should have 3 quarters and 4 pennies.<p/>
 * 
 * The number of coins the competitor has is determined by a genetic algorithm.  
 * This is a simple example, but shows the way in which the GA can be incorporated
 * into an agent, and the procedures neccessary to have the GA work.  Even with a
 * small initial genetic pool the GA finds the correct solution usually in the
 * first few ticks.<p/>
 * 
 * When a competitor performs the correct action she moves towards the center of
 * the display.
 * 
 * @author Jerry Vos
 * @version $Revision$ $Date$
 */
public class GeneticChangeModel extends SimpleModel {
	protected CompetitionSpace space;
	
	private int competitorCount = 1;

	private double goalAmountInDollars = .79;
	
	private DisplaySurface spaceDisplaySurface;

	private int spaceWidth = 500, spaceHeight = 500;

	public GeneticChangeModel() {
		// setup the random generator
		Random.createUniform();
		// and the list of agents that will eventually hold everything
		// that gets "stepped"
		if (super.agentList == null)
			super.agentList = new ArrayList();
	}

	
	public String[] getInitParam() {
		return new String[] { 
				"CompetitorCount", 
				"SpaceWidth", 
				"SpaceHeight",
				"GoalAmountInDollars"
			};
	}

	public void begin() {
		try {
			super.begin();
			
			// reset the agent numbers and such
			Competitor.resetIndices();

			if (super.agentList == null)
				super.agentList = new ArrayList();
			super.agentList.clear();
			space = new CompetitionSpace(spaceWidth, spaceHeight);

			// hire all the employees
			space.recruitJudge();

			space.getJudge().setDesiredAmount(goalAmountInDollars);
			
			space.recruitCompetitors(competitorCount);

			// add them to the list of 
			super.agentList.addAll(space.getCompetitors());
			super.agentList.add(space.getJudge());

			// create all the displays
			this.buildDisplay();
//			this.buildGraphs();

			// Schedule some pictures of the displays if you'd like
//			new SnapshotScheduler("display", officeDisplaySurface, "display").scheduleAtInterval(schedule, 200);
//			new SnapshotScheduler("individual", individualGraph, "individual").scheduleAtInterval(schedule, 400);
//			new SnapshotScheduler("office", officeErrorGraph, "office").scheduleAtInterval(schedule, 400);
		} catch (RepastException ex) {
			SimUtilities.showError("Error readying the model", ex);
			super.stop();
		}
	}

	/**
	 * This builds the display surface for the competition
	 */
	protected void buildDisplay() {
		spaceDisplaySurface = 
			new DisplaySurface(
					new Dimension(spaceWidth, spaceHeight), 
					this, 
					"Competition Display");

		// create the graph layout that holds the agents that get displayed
		DefaultGraphLayout layout = new DefaultGraphLayout(spaceWidth,
				spaceHeight);
		layout.getNodeList().addAll(space.getCompetitors());
		layout.getNodeList().add(space.getJudge());

		// tell the display surface to display the layout (after wrapping
		// it in a Network2DDisplay
		Network2DDisplay spaceNetDisplay = new Network2DDisplay(layout);
		spaceDisplaySurface.addDisplayableProbeable(spaceNetDisplay,
				"Competition display");
		
		spaceDisplaySurface.setBackground(Color.WHITE);
		
		spaceDisplaySurface.display();
	}
	
	/**
	 * Sets up the model for the next run, clears out all the old employees
	 * and the old displays
	 */
	public void setup() {
		super.setup();

		if (spaceDisplaySurface != null)
			spaceDisplaySurface.dispose();

		spaceDisplaySurface	= null;
		
		// Add the custom action button that will randomly scatter the 
		// agents on the display surface
		getModelManipulator().addButton("Spread out agents", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Iterator iter = ((ArrayList) space.getCompetitors().clone()).iterator();
				for (; iter.hasNext(); ) {
					Competitor comp = (Competitor) iter.next();
					
					comp.setX(Random.uniform.nextIntFromTo(0, space.getWidth()));
					comp.setY(Random.uniform.nextIntFromTo(0, space.getHeight()));
				}
				
				spaceDisplaySurface.updateDisplayDirect();
			}
		});
	}

	
	protected void preStep() {
		if (getTickCount() == 10000)
			super.stop();
//		System.out.println();
//		System.out.println("tick: " + getTickCount());
		try {
			for (Iterator iter = super.agentList.iterator(); iter.hasNext();) {
				((AutoStepable) iter.next()).preStep();
			}
		} catch (Exception ex) {
			SimUtilities.showError("Error preStepping the simulation", ex);
			super.stop();
		}
	}

	
	protected void step() {
		try {
			for (Iterator iter = super.agentList.iterator(); iter.hasNext();) {
				Object o = iter.next();
				((AutoStepable) o).step();
			}
		} catch (Exception ex) {
			SimUtilities.showError("Error stepping the simulation", ex);
			super.stop();
		}
	}

	protected void postStep() {
		try {
			for (Iterator iter = super.agentList.iterator(); iter.hasNext();) {
				((AutoStepable) iter.next()).postStep();
			}
		} catch (Exception ex) {
			SimUtilities.showError("Error postStepping the simulation", ex);
			super.stop();
		}
		spaceDisplaySurface.updateDisplay();
	}

	
	public String getName() {
		return "Genetic Change Competition Model";
	}

	/**
	 * @return Returns the number of employees in the office.
	 */
	public int getCompetitorCount() {
		return competitorCount;
	}

	/**
	 * @param employeeCount The number of employees in the office.
	 */
	public void setCompetitorCount(int employeeCount) {
		this.competitorCount = employeeCount;
	}

	/**
	 * @return returns the officeHeight
	 */
	public int getSpaceHeight() {
		return spaceHeight;
	}

	/**
	 * @param officeHeight the officeHeight
	 */
	public void setSpaceHeight(int officeHeight) {
		this.spaceHeight = officeHeight;
	}

	/**
	 * @return returns the officeWidth
	 */
	public int getSpaceWidth() {
		return spaceWidth;
	}

	/**
	 * @param officeWidth the officeWidth
	 */
	public void setSpaceWidth(int officeWidth) {
		this.spaceWidth = officeWidth;
	}
	
	/**
	 * @return Returns the goalAmountInDollars.
	 */
	public double getGoalAmountInDollars() {
		return goalAmountInDollars;
	}
	
	/**
	 * @param goalAmountInDollars The amount the agents are going for.
	 */
	public void setGoalAmountInDollars(double goalAmountInDollars) {
		if (goalAmountInDollars > .99) {
			SimUtilities.showMessage("Goal amount must be between 0.00 and 0.99.");
			goalAmountInDollars = .99;
		}
		
		this.goalAmountInDollars = goalAmountInDollars;
	}
	
	
	public static void main(String[] args) {
		uchicago.src.sim.engine.SimInit init = new uchicago.src.sim.engine.SimInit();
		GeneticChangeModel model = new GeneticChangeModel();
		if (args.length > 0)
			init.loadModel(model, args[0], false);
		else
			init.loadModel(model, null, false);
	}
}