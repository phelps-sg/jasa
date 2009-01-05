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
import java.util.List;

import uchicago.src.codegen.GeneratorException;
import uchicago.src.sim.util.ByteCodeBuilder;
import uchicago.src.sim.util.SimUtilities;

/**
 * A collection of utility methods for the creation of SimActions and
 * SimListActions. Used by ActionGroup, and Schedule in the creation of
 * BasicActions. Under normal circumstances these methods should not be
 * called by a user.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see ActionGroup
 * @see Schedule
 */
public class ActionUtilities {

   /**
   * Creates a BasicAction whose execute method calls the specified method
   * on the specified object. The method should take no arguments.
   *
   * @param o the object to the call the method on.
   * @param methodName the name of the method to call.
   * @return the created BasicAction
   */
  static public BasicAction createActionFor(Object o, String methodName)
  {
    BasicAction ba = null;
    try {
      ba = ByteCodeBuilder.generateBasicAction(o, methodName);
    } catch (GeneratorException ex) {
      SimUtilities.showError("Error creating action: " + methodName +
            " on " + o.getClass().getName(), ex);
      System.exit(0);
    }

    return ba;
  }

  /**
   * Creates a BasicAction whose execute method calls the specified method
   * on every object in the specified list. Assumes that all the objects in the
   * list are of the same class. The method should take no arguments.
   *
   * @param list the list containing the object to call the method on
   * @param methodName the name of the method to call
   * @return the created BasicAction
   */
  static public BasicAction createActionForEach(List list, String methodName)
  {

    BasicAction ba = null;
    try {
      ba = ByteCodeBuilder.generateBasicActionForList(list, methodName);
    } catch (GeneratorException ex) {
      SimUtilities.showError("Error creating action for list: " + methodName,
			     ex);
      System.exit(0);
    }

    return ba;
  }
  
  /**
   * Creates a BasicAction whose execute method calls the specified method
   * on every object in the specified list. Assumes that all the objects in the
   * list are of the same class. The method should take no arguments. The
   * list is randomized (shuffled) with SimUtilties.shuffle before iterating
   * through it.
   *
   * @param list the list containing the object to call the method on
   * @param methodName the name of the method to call
   * @return the created BasicAction
   * @see uchicago.src.sim.util.SimUtilties
   */
  static public BasicAction createActionForEachRnd(List list,
						   String methodName) {
    BasicAction ba = null;
    try {
      ba = ByteCodeBuilder.generateBasicActionForListRnd(list, methodName);
    } catch (GeneratorException ex) {
      SimUtilities.showError("Error creating action for list: " + methodName,
			     ex);
      System.exit(0);
    }

    return ba;
  }

  /**
   * Creates a BasicAction whose execute method calls the specified method
   * on every Object of the specified Class in the specified list. This is used
   * when the objects in the list are not all of the same class, but
   * <em>are</em> children of a common super class.
   *
   * @param list the list containing the objects to call the method on
   * @param superClass the class to use when creating the BasicAction.
   * @param methodName the name of the method to call
   * @return the created BasicAction
   * @see uchicago.src.sim.util.SimUtilties
   */
  static public BasicAction createActionForEach(List list, Class superClass,
          String methodName)

  {
    BasicAction ba = null;
    try {
      ba = ByteCodeBuilder.generateBasicActionForList(list,
						      methodName, superClass,
						      false);
    } catch (GeneratorException ex) {
      SimUtilities.showError("Error creating action for list: " + methodName,
			     ex);
      System.exit(0);
    }

    return ba;

  }

  /**
   * Creates a BasicAction whose execute method calls the specified method
   * on every Object of the specified Class in the specified list. This is used
   * when the objects in the list are not all of the same class, but
   * <em>are</em> children of a common super class.  The list is randomized
   * (shuffled) with SimUtilties.shuffle before iterating through it.
   *
   * @param list the list containing the objects to call the method on
   * @param superClass the class to use when creating the BasicAction.
   * @param methodName the name of the method to call
   * @return the created BasicAction
   */
  static public BasicAction createActionForEachRnd(List list, Class superClass,
          String methodName)

  {
    BasicAction ba = null;
    try {
      ba = ByteCodeBuilder.generateBasicActionForList(list,
						      methodName,
						      superClass, true);
    } catch (GeneratorException ex) {
      SimUtilities.showError("Error creating action for list: " + methodName,
			     ex);
      System.exit(0);
    }

    return ba;

  }

  /**
   * Creates a java.lang.reflect.Method from the specified object and the
   * the specified methodName.
   *
   * @param o the object from which to create the Method
   * @param name the name of the method to create
   * @return the created Method
   * @throws NoSuchMethodException if the first object in the list does not
   * have the specified method.
   */
  static public Method getNoArgMethod(Object o, String name) throws
    NoSuchMethodException
  {
    Class[] paramTypes = {};
    Class c = o.getClass();
    return c.getMethod(name, paramTypes);
  }

  /**
   * Creates a java.lang.reflect.Method from the specified Class and the
   * the specified methodName.
   *
   * @param c the Class from which to create the Method
   * @param name the name of the method to create
   * @return the created Method
   * @throws NoSuchMethodException if the first object in the list does not have
   * the specified method.
   */
  static public Method getNoArgMethod(Class c, String name) throws NoSuchMethodException {
    Class[] paramTypes = {};
    return c.getMethod(name, paramTypes);
  }
}
