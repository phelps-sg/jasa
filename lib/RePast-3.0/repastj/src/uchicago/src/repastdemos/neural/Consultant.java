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
package uchicago.src.repastdemos.neural;

import java.util.Iterator;

import uchicago.src.sim.engine.AutoStepable;
import uchicago.src.sim.util.Random;

/**
 * This is the agent that either praises or scolds {@link Employee}s based on 
 * the actions they performed according to the boss commands they received. 
 * <br/>
 * When an agent is praised they move towards the bosses (the center of 
 * the display), when they are scolded they move away.
 * 
 * @author Jerry Vos
 * @version $Revision$ $Date$
 */
public class Consultant extends Boss implements AutoStepable {
	private static final int CLOSER = 0;

	private static final int FARTHER = 1;

	public Consultant(Office office) {
		super(office);
	}

	public void preStep() {
	}

	public void step() {
	}

	public void postStep() {
		for (Iterator iter = office.getEmployees().iterator(); iter.hasNext();) {
			Employee emp = (Employee) iter.next();

			double actionPerformed = emp.getActionPerformed();
			double[] commands = emp.getCommands();

			int[] simplifiedCommands = new int[] {
					(int) Math.round(commands[0]),
					(int) Math.round(commands[1]) };

//			System.out.println("commands: " + simplifiedCommands[0] + ","
//					+ simplifiedCommands[1]);

			// apply the xor logic
			int correctAction = simplifiedCommands[0] ^ simplifiedCommands[1];
//			System.out.println("correctAction: " + correctAction + ", did: "
//					+ Math.round(actionPerformed));

			if (correctAction == Math.round(actionPerformed)) {
//				System.out.println("praised");
				emp.praise(this);
				moveEmp(emp, CLOSER);
			} else {
//				System.out.println("scolded");
				emp.scold(this);
				moveEmp(emp, FARTHER);
			}
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

		double newX = emp.getX(), newY = emp.getY();
		
		if (xSide == 0) {
			if (ySide == 0) {
				// first quadrant
				newX = (emp.getX() - xMove * mod);
				newY = (emp.getY() + yMove * mod);
			} else {
				// fourth quadrant
				newX = (emp.getX() - xMove * mod);
				newY = (emp.getY() - yMove * mod);
			}
		} else if (xSide == 2) {
			if (ySide == 0) {
				// second quadrant
				newX = (emp.getX() + xMove * mod);
				newY = (emp.getY() + yMove * mod);
			} else {
				// third quadrant
				newX = (emp.getX() + xMove * mod);
				newY = (emp.getY() - yMove * mod);
			}
		} else {
			// on the y axis
			if (ySide == 0)
				newY = (emp.getY() + yMove * mod);
			else if (ySide == 2)
				newY = (emp.getY() - yMove * mod);
			else {
				if (direction == CLOSER) {
					// do nothing, it is already dead center
				} else {
					newX = (emp.getX() - xMove * mod);
					newY = (emp.getY() - yMove * mod);
				}
			}
		}
		
		if (newX < -(emp.getWidth() / 2.0))
			newX = -(emp.getWidth() / 2.0);
		else if (newX > office.getWidth() + emp.getWidth() / 2.0)
			newX = office.getWidth() + emp.getWidth() / 2.0;

		if (newY < -(emp.getHeight() / 2.0))
			newY = -(emp.getHeight() / 2.0);
		else if (newY > office.getHeight() - emp.getHeight() / 2.0)
			newY = office.getHeight() - emp.getHeight() / 2.0;
		
		emp.setX(newX);
		emp.setY(newY);
	}
}