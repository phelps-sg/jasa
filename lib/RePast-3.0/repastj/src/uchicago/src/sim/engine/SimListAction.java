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
package uchicago.src.sim.engine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.ListIterator;


/**
 * A BasicAction that wraps a list of objects and a Method, and allows for the
 * invocation of that Method on all the Objects in the List.
 *
 * @deprecated No longer needed due to direct bytecode generation of BasicActions.
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class SimListAction extends BasicAction {

  private Method m;
  private ArrayList list;
  private static Object[] args = {};

  /**
   * Constructs a SimListAction from the specified list and specified Method.
   * @param list the list of objects on which to call the Method.
   * @param method the Method to call
   */

  public SimListAction(ArrayList list, Method method) {
    this.list = list;
    m = method;
  }

  /**
   * Executes this SimListAction (invokes a Method on all the Objects in a
   * a List).
   */
  public void execute() {
    try {
      ArrayList t;
      synchronized (list) {
        t = (ArrayList)list.clone();
      }
      ListIterator li = t.listIterator();
      while (li.hasNext()) {
        m.invoke(li.next(), args);
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }
}