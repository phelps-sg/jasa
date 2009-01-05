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

import java.util.Iterator;

import uchicago.src.sim.engine.AutoStepable;
import uchicago.src.sim.util.Random;

/**
 * This is the agent that moves agents according to how correct
 * they were to the boss's command.
 * 
 * @author Prakash Thimmapuram
 * @version $Revision$ $Date$
 */
public class Consultant extends Boss implements AutoStepable {
	private static final int CLOSER = 0;

	public Consultant(RegressionOfficeModel officeSpace, Office office) {
		super(officeSpace, office);
	}

	public void preStep() {
	}

	public void distributeTasks() {
	}

	public void postStep() {
		Boss boss = office.getBoss();
		
		for (Iterator iter = office.getEmployees().iterator(); iter.hasNext();) {
			Employee emp = (Employee) iter.next();

			int move = (int)(emp.getForecastValue() - boss.getCurrentY());
			emp.setError(Math.abs(emp.getForecastValue() - boss.getCurrentY()));
			moveEmp(emp, move);

		}
	}

	/**
	 * this handles the moving of the agents
	 * 
	 * @param emp		the employee to move
	 * @param direction	whether to move the employee towards the bosses 
	 * 					(the center of the display) or away
	 */
	private void moveEmp(Employee emp, int direction) {
		int xSide, ySide;

		// this algorithm says quadrants are like so (xSide, ySide)
		/*
		 * 			|
		 *	(2,0) (1,0)	(0,0)
		 * 			|
		 * -------(1,1)---------
		 * 			|
		 * 	(2,2) (1,2)	(0,2)
		 * 			|
		 */	

		// this should really be taking into account the fact that this is
		// two doubles being compared, but for now it doesn't
		if (emp.getX() < (double) office.getWidth() / 2) {
			xSide = 2;
		} else if (emp.getX() == (double) office.getWidth() / 2) {
			xSide = 1;
		} else {
			xSide = 0;
		}

		if (emp.getY() < (double) office.getHeight() / 2) {
			ySide = 0;
		} else if (emp.getY() == (double) office.getHeight() / 2) {
			ySide = 1;
		} else {
			ySide = 2;
		}

		double xMove = Random.uniform.nextDoubleFromTo(0, 20);
		double yMove = Random.uniform.nextDoubleFromTo(0, 20);

		int mod = (direction == CLOSER) ? 1 : -1;

		if (xSide == 0) {
			if (ySide == 0) {
				// first quadrant
				emp.setX(emp.getX() - xMove * mod);
				emp.setY(emp.getY() + yMove * mod);
			} else {
				// fourth quadrant
				emp.setX(emp.getX() - xMove * mod);
				emp.setY(emp.getY() - yMove * mod);
			}
		} else if (xSide == 2) {
			if (ySide == 0) {
				// second quadrant
				emp.setX(emp.getX() + xMove * mod);
				emp.setY(emp.getY() + yMove * mod);
			} else {
				// third quadrant
				emp.setX(emp.getX() + xMove * mod);
				emp.setY(emp.getY() - yMove * mod);
			}
		} else {
			// on the y axis
			if (ySide == 0)
				emp.setY(emp.getY() + yMove * mod);
			else if (ySide == 2)
				emp.setY(emp.getY() - yMove * mod);
			else {
				if (direction == CLOSER) {
					// do nothing, it is already dead center
				} else {
					emp.setX(emp.getX() - xMove * mod);
					emp.setY(emp.getY() - yMove * mod);
				}
			}
		}
	}
}