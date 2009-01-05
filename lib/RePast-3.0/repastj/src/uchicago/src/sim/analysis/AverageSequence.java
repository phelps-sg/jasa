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
package uchicago.src.sim.analysis;

import uchicago.src.sim.engine.ActionUtilities;
import uchicago.src.sim.util.SimUtilities;

import java.lang.reflect.Method;

import java.util.ArrayList;

/**
 * Computes the minimum of a sequence of values, based on a list of 
 * objects and a method to call on them. This class
 * works much the same as the AverageDataSource class.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.sim.analysis.MaxSequence
 * @see uchicago.src.sim.analysis.MinSequence
 */
public class AverageSequence implements Sequence {
	private Method m;
	private ArrayList list;

	/**
	* Constructs this AverageSequence using the specified list and method
	* name. Each object in the list should respond to the method named by
	* method name. This method must return some subclass of Number.
	*
	* @param list the list of objects on which to call the method
	* @param methodName the name of the method to call. This method must return
	* some subclass of java.Number.
	* @see java.lang.Number
	*/
	public AverageSequence(ArrayList list, String methodName) {
		try {
			m = ActionUtilities.getNoArgMethod(list.listIterator().next(),
											   methodName);
			this.list = list;
		} catch (NoSuchMethodException ex) {
			SimUtilities.showError("Unable to find method " + methodName, ex);
			ex.printStackTrace();

			//      System.exit(0);
		}
	}

	/**
	 * Compute and return the average.
	 */
	public double getSValue() {
		return StatisticUtilities.getAverage(list, m);
	}
}
