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
 * A data source that returns objects. ObjectDataSource is used by DataRecorder
 * and should not be created by users under normal circumstances.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.sim.analysis.DataRecorder
 */
public class ObjectDataSource implements DataSource {

  Method m;
  Object object;
  String name;

  /**
   * Constructs this ObjectDataSource using the specified object and method
   * name. The method whose method name is specified by methodName is called
   * on the object in ObjectDataSource.execute(). The result is the data
   * returned by this DataSource.
   *
   * @param name the name of this DataSource
   * @param o the object on which to call the method
   * @param methodName the name of the method to call on the object. This
   * method must return an Object.
   */
  public ObjectDataSource(String name, Object o, String methodName) {
    this.name = name;
    try {
      m = ActionUtilities.getNoArgMethod(o, methodName);
      object = o;
    } catch (NoSuchMethodException ex) {
      SimUtilities.showError("Unable to find method " + methodName, ex);
      ex.printStackTrace();
      System.exit(0);
    }
  }

  /**
   * Constructs an ObjectDataSource using the specified name, Object and
   * method.
   *
   * @param name the name of this DataSource
   * @param o the object on which to call the method that returns the data
   * @param m the method to call on the object. This method returns the data
   */
  public ObjectDataSource(String name, Object o, Method m) {
    this.name = name;
    object = o;
    this.m = m;
  }

  /**
   * Call the method on the object and return the resulting data as an object
   */
  public Object execute() {
    return DataSourceUtilities.getObject(object, m);
  }

  /**
   * Gets the name of this datasource
   */
  public String getName() {
    return name;
  }
}
