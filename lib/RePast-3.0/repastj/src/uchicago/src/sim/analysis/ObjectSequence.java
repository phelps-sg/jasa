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

import java.lang.reflect.Method;

import uchicago.src.sim.engine.ActionUtilities;
import uchicago.src.sim.util.SimUtilities;

/**
 * A source for sequence data from a single object. ObjectSequence is used
 * by SequenceGraph and should not be created by a user under normal
 * circumstances.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.sim.analysis.SequenceGraph
 */
public class ObjectSequence implements Sequence {

  Method m;
  Object o;

  /**
   * Construct this ObjectSequence using the specified object and specified
   * method name. Calling the method named by methodName on the object returns
   * the data for the sequence.
   *
   * @param the object on which to call the method
   * @param the name of the method to call. This method should return a
   * number (int etc.) or a Number (Integer etc.)
   */
  public ObjectSequence(Object o, String methodName) {
    try {
      m = ActionUtilities.getNoArgMethod(o, methodName);
      this.o = o;
    } catch (NoSuchMethodException ex) {
      SimUtilities.showError("Unable to find method " + methodName, ex);
      ex.printStackTrace();
    }
  }

  /**
   * Call the method on the object and return the result as a double.
   */
  public double getSValue() {
    return StatisticUtilities.getDouble(o, m);
  }
}
