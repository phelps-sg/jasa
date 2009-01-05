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
package uchicago.src.repastdemos.regression;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.AutoStepable;
import uchicago.src.sim.engine.SimpleModel;
import uchicago.src.sim.gui.DefaultGraphLayout;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.RepastException;
import uchicago.src.sim.util.SimUtilities;

/**
 * A model illustrating the use of the openforecast regression library.
 * 
 * @author Prakash Thimmapuram
 * @version $Revision$ $Date$
 */
public class RegressionOfficeModel extends SimpleModel {
	protected Office office;

	private int employeeCount = 1;

	private double polynomimalCoefficient1 = 0.1;

	private double polynomimalCoefficient2 = 0.2;

	private double polynomimalCoefficient3 = 0.3;

	private int lengthOfHistory = 50;

	private DisplaySurface officeDisplaySurface;

	private OpenSequenceGraph officeErrorGraph;

	private OpenSequenceGraph individualGraph;

	private int officeWidth = 500, officeHeight = 500;

	public RegressionOfficeModel() {
		// setup the random generator
		Random.createUniform();
		// and the list of agents that will eventually hold everything
		// that gets "stepped"
		if (super.agentList == null)
			super.agentList = new ArrayList();
	}

	public String[] getInitParam() {
		return new String[] { "EmployeeCount", "OfficeWidth", "OfficeHeight",
				"PolynomimalCoefficient1", "polynomimalCoefficient2",
				"PolynomimalCoefficient3", "LengthOfHistory" };
	}

	public void begin() {
		try {
			super.begin();
			// reset the agent numbers and such
			Employee.resetIndices();

			// hire all the employees
			office.hireEmployees(employeeCount);
			office.hireBoss(this);
			office.hireConsultant(this);

			// add them to the list of
			super.agentList.addAll(office.getEmployees());
			super.agentList.add(office.getBoss());
			super.agentList.add(office.getConsultant());

			// create all the displays
			this.buildDisplay();
			this.buildGraphs();

			// Schedule some pictures of the displays if you'd like
			//			new SnapshotScheduler("display", officeDisplaySurface,
			// "display").scheduleAtInterval(schedule, 200);
			//			new SnapshotScheduler("individual", individualGraph,
			// "individual").scheduleAtInterval(schedule, 400);
			//			new SnapshotScheduler("office", officeErrorGraph,
			// "office").scheduleAtInterval(schedule, 400);
		} catch (RepastException ex) {
			SimUtilities.showError("Error readying the model", ex);
			super.stop();
		}
	}

	/**
	 * This builds the display surface for the office
	 */
	protected void buildDisplay() {
		officeDisplaySurface = new DisplaySurface(new Dimension(officeWidth,
				officeHeight), this, "Office Display");

		// create the graph layout that holds the agents that get displayed
		DefaultGraphLayout layout = new DefaultGraphLayout(officeWidth,
				officeHeight);
		layout.getNodeList().addAll(office.getEmployees());
		layout.getNodeList().add(office.getBoss());

		// tell the display surface to display the layout (after wrapping
		// it in a Network2DDisplay
		Network2DDisplay officeNetDisplay = new Network2DDisplay(layout);
		officeDisplaySurface.addDisplayableProbeable(officeNetDisplay,
				"Office display");
		this.registerDisplaySurface("regression", officeDisplaySurface);
		officeDisplaySurface.setBackground(Color.WHITE);
		officeDisplaySurface.display();
	}

	/**
	 * This builds the error graphs.
	 */
	protected void buildGraphs() {
		/** ** The office statistics graph *** */
		officeErrorGraph = new OpenSequenceGraph("Office error statistics",
				this);
		// Build the error graph
		officeErrorGraph.addSequence("Avg. Error", new Sequence() {
			public double getSValue() {
				double totalErr = 0;
				for (Iterator iter = office.getEmployees().iterator(); iter
						.hasNext();) {
					totalErr += ((Employee) iter.next()).getError();
				}

				return totalErr / office.getEmployees().size();
			}
		});

		officeErrorGraph.addSequence("Max. Error", new Sequence() {
			public double getSValue() {
				double maxErr = Double.MIN_VALUE;
				for (Iterator iter = office.getEmployees().iterator(); iter
						.hasNext();) {
					double err = ((Employee) iter.next()).getError();

					if (err > maxErr)
						maxErr = err;
				}

				return maxErr;
			}
		});

		officeErrorGraph.addSequence("Min. Error", new Sequence() {
			public double getSValue() {
				double minErr = Double.MAX_VALUE;
				for (Iterator iter = office.getEmployees().iterator(); iter
						.hasNext();) {
					double err = ((Employee) iter.next()).getError();

					if (err < minErr)
						minErr = err;
				}

				return minErr;
			}
		});

		officeErrorGraph.setYRange(-.1, 1.1);
		officeErrorGraph.setXRange(0, 5);
		officeErrorGraph.display();

		/** ** the individual statistics graph *** */
		individualGraph = new OpenSequenceGraph("Individual statistics", this);

		for (Iterator iter = office.getEmployees().iterator(); iter.hasNext();) {
			final Employee emp = (Employee) iter.next();
			individualGraph.addSequence(emp.getNodeLabel() + "'s error",
					new Sequence() {
						public double getSValue() {
							return emp.getError();
						}
					}, emp.getColor());

		}

		individualGraph.setYRange(-.1, 1.1);
		individualGraph.setXRange(0, 5);
		individualGraph.display();
	}

	/**
	 * Sets up the model for the next run, clears out all the old employees and
	 * the old displays
	 */
	public void setup() {
		super.setup();

		if (super.agentList == null)
			super.agentList = new ArrayList();
		super.agentList.clear();
		office = new Office(officeWidth, officeHeight);

		if (officeErrorGraph != null)
			officeErrorGraph.dispose();
		if (individualGraph != null)
			individualGraph.dispose();
		if (officeDisplaySurface != null)
			officeDisplaySurface.dispose();

		officeDisplaySurface = null;

		// Add the custom action button that will randomly scatter the
		// agents on the display surface
		getModelManipulator().addButton("Spread out agents",
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Iterator iter = ((ArrayList) office.getEmployees()
								.clone()).iterator();
						for (; iter.hasNext();) {
							Employee emp = (Employee) iter.next();

							emp.setX(Random.uniform.nextIntFromTo(0, office
									.getWidth()));
							emp.setY(Random.uniform.nextIntFromTo(0, office
									.getHeight()));
						}

						officeDisplaySurface.updateDisplayDirect();
					}
				});
	}

	protected void preStep() {
		//		System.out.println();
		//		System.out.println("in PreStep tick: " + getTickCount());
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
		officeDisplaySurface.updateDisplay();
		officeErrorGraph.step();
		individualGraph.step();

	}

	public String getName() {
		return "Regression Model";
	}

	/**
	 * @return Returns the number of employees in the office.
	 */
	public int getEmployeeCount() {
		return employeeCount;
	}

	/**
	 * @param employeeCount
	 *            The number of employees in the office.
	 */
	public void setEmployeeCount(int employeeCount) {
		this.employeeCount = employeeCount;
	}

	/**
	 * @return returns the officeHeight
	 */
	public int getOfficeHeight() {
		return officeHeight;
	}

	/**
	 * @param officeHeight
	 *            the officeHeight
	 */
	public void setOfficeHeight(int officeHeight) {
		this.officeHeight = officeHeight;
	}

	/**
	 * @return returns the officeWidth
	 */
	public int getOfficeWidth() {
		return officeWidth;
	}

	/**
	 * @param officeWidth
	 *            the officeWidth
	 */
	public void setOfficeWidth(int officeWidth) {
		this.officeWidth = officeWidth;
	}

	/**
	 * @param polynomimalCoefficient1
	 *            The polynomimalCoefficient1 to set.
	 */
	public void setPolynomimalCoefficient1(double polynomimalCoefficient1) {
		this.polynomimalCoefficient1 = polynomimalCoefficient1;
	}

	/**
	 * @return Returns the polynomimalCoefficient1.
	 */
	public double getPolynomimalCoefficient1() {
		return polynomimalCoefficient1;
	}

	/**
	 * @param polynomimalCoefficient2
	 *            The polynomimalCoefficient2 to set.
	 */
	public void setPolynomimalCoefficient2(double polynomimalCoefficient2) {
		this.polynomimalCoefficient2 = polynomimalCoefficient2;
	}

	/**
	 * @return Returns the polynomimalCoefficient2.
	 */
	public double getPolynomimalCoefficient2() {
		return polynomimalCoefficient2;
	}

	/**
	 * @param polynomimalCoefficient3
	 *            The polynomimalCoefficient3 to set.
	 */
	public void setPolynomimalCoefficient3(double polynomimalCoefficient3) {
		this.polynomimalCoefficient3 = polynomimalCoefficient3;
	}

	/**
	 * @return Returns the polynomimalCoefficient3.
	 */
	public double getPolynomimalCoefficient3() {
		return polynomimalCoefficient3;
	}

	/**
	 * @param lengthOfHistory
	 *            The lengthOfHistory to set.
	 */
	public void setLengthOfHistory(int lengthOfHistory) {
		this.lengthOfHistory = lengthOfHistory;
	}

	/**
	 * @return Returns the lengthOfHistory.
	 */
	public int getLengthOfHistory() {
		return lengthOfHistory;
	}

	public static void main(String[] args) {
		uchicago.src.sim.engine.SimInit init = new uchicago.src.sim.engine.SimInit();
		RegressionOfficeModel model = new RegressionOfficeModel();
		if (args.length > 0)
			init.loadModel(model, args[0], false);
		else
			init.loadModel(model, null, false);
	}
}