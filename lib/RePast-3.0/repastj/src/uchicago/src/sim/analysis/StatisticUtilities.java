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

import uchicago.src.sim.util.SimUtilities;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.ListIterator;


/**
 * Statistical utilities. Once the COLT library is finalized this will be
 * a wrapper around its statistics package.
 * These methods are used by the other Statistics classes.
 * A user should not need to call this methods under normal circumstances.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class StatisticUtilities {
	static Class[] args = {  };

	public static double STAT_UTIL_ERRORVAL	= -99999999999d;
	
	/**
	 * Gets an average of the values returned by every member of the specified
	 * list when method m is called on them.
	 *
	 * @param list the list of objects on which to call the method
	 * @param m the method to call on the object
	 * @return the average, 0.0 if the list is empty or STAT_UTIL_ERRORVAL on error
	 */
	public static double getAverage(ArrayList list, Method m) {
		double total = 0.0;
		ArrayList t;

		synchronized (list) {
			t = (ArrayList) list.clone();
		}

		ListIterator li = t.listIterator();

		try {
			Number d;

			while (li.hasNext()) {
				d = (Number) m.invoke(li.next(), args);
				total += d.doubleValue();
			}

			//System.out.println(total/ list.size());
			return total / list.size();
		} catch (Exception ex) {
			SimUtilities.showError("Unable to calculate average in StatisticUtilities.getAverage", ex);
			ex.printStackTrace();
//			System.exit(0);
		}

		return STAT_UTIL_ERRORVAL;
	}
	
	/**
	 * Gets the max of the values returned by every member of the specified
	 * list when method m is called on them.
	 *
	 * @param list the list of objects on which to call the method
	 * @param m the method to call on the object
	 * @return the max, Double.MIN_VALUE if the list is empty or STAT_UTIL_ERRORVAL on error 
	 */
	public static double getMax(ArrayList list, Method m) {
		double max = Double.MIN_VALUE;
		ArrayList t;

		synchronized (list) {
			t = (ArrayList) list.clone();
		}

		ListIterator li = t.listIterator();
		
		try {
			Number d;

			while (li.hasNext()) {
				d = (Number) m.invoke(li.next(), args);
				
				double val = d.doubleValue();
				
				if (val > max)
					max = val;
			}

			return max;
		} catch (Exception ex) {
			SimUtilities.showError("Unable to calculate maximum in StatisticUtilities.getMax", ex);
			ex.printStackTrace();
		}

		return STAT_UTIL_ERRORVAL;
	}
	
	/**
	 * Gets the min of the values returned by every member of the specified
	 * list when method m is called on them.
	 *
	 * @param list the list of objects on which to call the method
	 * @param m the method to call on the object
	 * @return  the max, Double.MAX_VALUE if the list is empty or STAT_UTIL_ERRORVAL on error
	 */
	public static double getMin(ArrayList list, Method m) {
		double min = Double.MAX_VALUE;
		ArrayList t;

		synchronized (list) {
			t = (ArrayList) list.clone();
		}

		ListIterator li = t.listIterator();
		
		try {
			Number d;

			while (li.hasNext()) {
				d = (Number) m.invoke(li.next(), args);
				
				double val = d.doubleValue();
				
				if (val < min)
					min = val;
			}

			return min;
		} catch (Exception ex) {
			SimUtilities.showError("Unable to calculate minimum in StatisticUtilities.getMin", ex);
			ex.printStackTrace();
		}

		return STAT_UTIL_ERRORVAL;
	}

	/**
	 * Gets the double returned when the specified method is called on the
	 * specified object.
	 *
	 * @param o the object on which to call the method
	 * @param m the method to call on the object
	 * 
	 * @return the value returned or STAT_UTIL_ERRORVAL on error
	 */
	public static double getDouble(Object o, Method m) {
		try {
			Number d;
			d = (Number) m.invoke(o, args);

			return d.doubleValue();
		} catch (Exception ex) {
			SimUtilities.showError("Unable to get double value", ex);
			ex.printStackTrace();
//			System.exit(0);
		}

		return STAT_UTIL_ERRORVAL;
	}
}
