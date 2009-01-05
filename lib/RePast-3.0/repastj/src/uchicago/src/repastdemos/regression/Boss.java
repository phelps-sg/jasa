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
import java.util.Iterator;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;
import uchicago.src.sim.engine.AutoStepable;
import uchicago.src.sim.gui.RectNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;
import uchicago.src.sim.util.Random;

/**
 * This class represents the agent who outputs a value based on either exponential or 
 * polynomial function for a given input. The employees agents try to guess the boss's
 * output. 
 * 
 * @see Employee
 * @see Consultant
 *  
 * @author Prakash Thimmapuram
 * @version $Revision$ $Date$
 */
public class Boss extends DefaultDrawableNode implements AutoStepable {
	private static final int EXPONENTIAL	= 0;
	private static final int POLYNOMIAL		= 1;
	
	protected Office office;
	protected RegressionOfficeModel officeSpace;
	
	private double polynmicalCoefficient1 = 0.1;
	private double polynmicalCoefficient2 = 0.2;
	private double polynmicalCoefficient3 = 0.3;
	
	private DataSet history = null;	//boss keeps a history of x and y

	private double currentX = 0.0;
	private double currentY = 0.0;
	
	private int historytoKeep = 50;
	private int currentFunction = EXPONENTIAL;
	
	
	public Boss(RegressionOfficeModel officeSpace, Office office, double x, double y) {
		super(new RectNetworkItem(x, y));

		super.setColor(Color.RED);
		this.office = office;
		this.officeSpace = officeSpace;
		this.polynmicalCoefficient1 = this.officeSpace.getPolynomimalCoefficient1();
		this.polynmicalCoefficient2 = this.officeSpace.getPolynomimalCoefficient2();
		this.polynmicalCoefficient3 = this.officeSpace.getPolynomimalCoefficient3();
		this.historytoKeep = this.officeSpace.getLengthOfHistory();
	}

	public Boss(RegressionOfficeModel officeSpace, Office office) {
		this(officeSpace, office, 0, 0 );
	}

	public void preStep() {
		for (Iterator iter = office.getEmployees().iterator(); iter.hasNext();) {
			Employee emp = (Employee) iter.next();
			getNextCommand();
			emp.forecast(currentX, this.history);
		}
	}
	
	public void step() {
	}

	public void postStep() {
		if(this.history == null)
			this.history = new DataSet();
		
		DataPoint dataPoint = new Observation(this.getCurrentY());
		dataPoint.setIndependentValue("X",this.currentX);
		this.history.add(dataPoint);
		
		this.switchFunction();
		this.trimHistory();
	}

	private void getNextCommand() {
		
		this.currentX = Random.uniform.nextDoubleFromTo(0, 1);		
		switch (currentFunction) {
		
			case EXPONENTIAL:
				this.currentY = expFunction(currentX);
				break;
				
			case POLYNOMIAL:
				this.currentY = polynomialFunction(currentX);
				break;
					
		}
	}

	private void trimHistory() {
		// remove elements from the history until we reach the
		// desired amount
		Iterator itr = this.history.iterator();
		while(this.history.size() > this.historytoKeep){
			itr.next();
			itr.remove();
		}
		
	}
	
	private void switchFunction() {
		double time = this.officeSpace.getTickCount();
		if(time % 25  == 0){
			if(this.currentFunction == 0)
				this.currentFunction = 1;
			else
				this.currentFunction = 0;
		}
	}
	
	private double expFunction(double x) {
		return Math.exp(x);
	}
	
	private double polynomialFunction(double x) {
		return (polynmicalCoefficient1 + polynmicalCoefficient2 * x + polynmicalCoefficient3*x*x);
	}
	
	/**
	 * @return Returns the currentY.
	 */
	public double getCurrentY() {
		return currentY;
	}
}