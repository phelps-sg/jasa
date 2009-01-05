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

import java.util.ArrayList;

import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.RepastException;

/**
 * The space the agents are contained in. NOT related to the GUI display.
 * 
 * @author Prakash Thimmapuram
 * @version $Revision$ $Date$
 */
public class Office {
	private ArrayList employees = new ArrayList();

	private Boss boss;

	private Consultant consultant;

	private int width;

	private int height;

	public Office() {
		super();
	}

	public Office(int width, int height) {
		super();

		this.width = width;
		this.height = height;
	}

	public int fireAllEmployees() {
		int employeesFired = employees.size();

		employees.clear();

		return employeesFired;
	}

	public void hireEmployees(int numEmployees) throws RepastException {
		for (int i = 0; i < numEmployees; i++) {
			double x = Random.uniform.nextDoubleFromTo(0, width);
			double y = Random.uniform.nextDoubleFromTo(0, height);
			employees.add(new Employee(x, y));
		}
	}

	public void hireEmployee(Employee emp) {
		employees.add(emp);
	}

	public void hireBoss(RegressionOfficeModel officeSpace) throws RepastException {
		if (boss != null)
			return;

		boss = new Boss(officeSpace, this, (double) width / 2, (double) height / 2);
	}

	public void hireConsultant(RegressionOfficeModel officeSpace) throws RepastException {
		if (consultant != null)
			return;

		consultant = new Consultant(officeSpace, this);
	}

	/**
	 * @return Returns the height of the office.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height The height of the office.
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return Returns the width of the office.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width The width of the office.
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return Returns the employees.
	 */
	public ArrayList getEmployees() {
		return employees;
	}

	/**
	 * @return Returns the bosses.
	 */
	public Boss getBoss() {
		return boss;
	}

	/**
	 * @return Returns the consultant.
	 */
	public Consultant getConsultant() {
		return consultant;
	}
}