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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.ListIterator;

import uchicago.src.sim.util.SimUtilities;

/**
 * Utility methods for return data. Used by those classes that realize
 * the DataSource interface.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class DataSourceUtilities {

  static Class[] args = {};

  /**
   * Calls the specified method on the specified object, returning the
   * result as an Object
   */
  public static Object getObject(Object o, Method m) {
    try {
      return m.invoke(o, args);
    } catch (InvocationTargetException ex) {
      SimUtilities.showError("Unable to execute method " + m.getName(), ex);
      ex.printStackTrace();
      System.exit(0);
    } catch (IllegalAccessException ex) {
      SimUtilities.showError("Unable to execute method " + m.getName(), ex);
      ex.printStackTrace();
      System.exit(0);
    }
    return null;
  }

  /**
   * Calls the specified method on every object in the specified list. The
   * resulting values are averaged and this average is returned as a Double.
   * Average is computed as the (sum of the all the elements in the list) /
   * (the size of the list).
   *
   * @return the average as a Double (to be cast by caller).
   */
  public static Object getAverage(ArrayList list, Method m) {
    double total = 0.0;
    ArrayList t;
    synchronized (list) {
      t = (ArrayList)list.clone();
    }
    ListIterator li = t.listIterator();
    try {
      Number d;
      while (li.hasNext()) {
        d = (Number)m.invoke(li.next(), args);
        total += d.doubleValue();
      }
      //System.out.println(total/ list.size());
      return new Double(total / list.size());
    } catch (InvocationTargetException ex) {
      SimUtilities.showError("Unable to execute method " + m.getName(), ex);
      ex.printStackTrace();
      System.exit(0);
    } catch (IllegalAccessException ex) {
      SimUtilities.showError("Unable to execute method " + m.getName(), ex);
      ex.printStackTrace();
      System.exit(0);
    }
    return null;
  }
}
