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

import java.awt.Color;
import java.util.Iterator;

import uchicago.src.sim.engine.AutoStepable;
import uchicago.src.sim.gui.RectNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;
import uchicago.src.sim.util.Random;

/**
 * This class represents the agent who gives orders to the employees.  These 
 * agents don't actually praise or punish the agents according to their actions.
 * The {@link Consultant} does that.
 * 
 * @see Consultant
 *  
 * @author Jerry Vos
 * @version $Revision$ $Date$
 */
public class Boss extends DefaultDrawableNode implements AutoStepable {
	protected Office office;

	private int bossID;

	public Boss(Office office, double x, double y) {
		super(new RectNetworkItem(x, y));

		super.setColor(Color.RED);
		this.office = office;
	}

	public Boss(Office office) {
		this(office, 0, 0);
	}

	public void preStep() {
		for (Iterator iter = office.getEmployees().iterator(); iter.hasNext();) {
			Employee emp = (Employee) iter.next();

			emp.receiveCommand(bossID, getNextCommand());
		}
	}

	public void step() {
	}

	public void postStep() {
	}

	private int getNextCommand() {
		// return getIteratedCommand();
		return getRandomCommand();
	}

	private int getRandomCommand() {
		double randCommand = Random.uniform.nextDoubleFromTo(0, 1);
		if (randCommand >= .5)
			return Employee.DO_NOTHING;
		else
			return Employee.DO_SOMETHING;
	}

	
//	static int baseI = 0;
//	int i = baseI++;
//	int z = 0;
//	int[][] commands = new int[][] {{ 0, 1, 0, 1 }, { 0, 0, 1, 1 }};
//	private int getIteratedCommand() {
//		if (z > commands[0].length)
//			z = 0;
//		return commands[i][z++];
//	}
	
	/**
	 * @return Returns the bossID.
	 */
	public int getBossID() {
		return bossID;
	}

	/**
	 * @param bossID The bossID to set.
	 */
	public void setBossID(int bossID) {
		this.bossID = bossID;
	}
}