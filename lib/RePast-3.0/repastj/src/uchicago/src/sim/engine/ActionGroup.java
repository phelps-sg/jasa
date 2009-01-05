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

import java.util.ArrayList;
import java.util.Vector;

import uchicago.src.sim.util.SimUtilities;
import cern.jet.random.Uniform;
//import cern.jet.random.engine.MersenneTwister;

/**
 * A collection of BasicActions to be executed by a schedule.
 * An ActionGroup has no notion of time. All the BasicActions added
 * to an ActionGroup execute during the same simulation clock tick.
 * The BasicActions in the ActionGroup can be executed sequentialy in the
 * order they were added or randomly. An ActionGroup is added to a Schedule
 * which can then execute the Group and in doing so execute the BasicActions
 * within the ActionGroup at some specified simulation clock tick.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 *
 * @see Schedule
 * @see BasicAction
 */


public class ActionGroup extends BasicAction {

  /**
   * Sequential type
   */
  public static final int SEQUENTIAL = 0;

  /**
   * Random type
   */
  public static final int RANDOM = 1;

  //private static Class[] paramTypes = {};
  private int actionType;
  private Uniform rng = null;
  Vector actions = new Vector(7);

  /**
   * Constructs an ActionGroup with a default sequential execution.
   */
  public ActionGroup() {
    this(SEQUENTIAL);
  }

  /**
   * Constructs an ActionGroup with execution of the specified type.
   *
   * @param type the type (Sequential or Random) of ActionGroup to construct.
   * Type can be specified by the constants ActionGroup.SEQUENTIAL and
   * ActionGroup.RANDOM.
   */

  public ActionGroup(int type) {
    if (type < SEQUENTIAL)
      throw new IllegalArgumentException("Illegal Action type value");
    if (type > RANDOM)
      throw new IllegalArgumentException("Illegal Action type value");

    actionType = type;
  }

  /**
   * Gets the type (ActionGroup.SEQUENTIAL or ActionGroup.RANDOM) of this
   * ActionGroup.
   */
  public int getType() {
    return actionType;
  }

  /**
   * Sets the random number generator used by this ActionGroup when
   * randomizing the BasicActions to execute. An ActionGroup will use
   * the global rng if this is not set.
   */
  public void setRng(Uniform rng) {
    this.rng = rng;
  }

  /**
   * Executes the BasicActions contained by this ActionGroup.
   */

  public void execute() {
    int size = actions.size();

    if (actionType == ActionGroup.RANDOM && size > 1) {
      if (rng == null) {
        SimUtilities.shuffle(actions);
      } else {
        SimUtilities.shuffle(actions, rng);
      }
    }

    for (int i = 0; i < size; i++) {
      BasicAction ba = (BasicAction)actions.elementAt(i);
      ba.execute();
    }
  }

  /**
   * Creates a BasicAction consisting of the specified method
   * to be called on the specified object, and adds the action to this
   * ActionGroup. The method should take no arguments.
   *
   * @param o the object to the call the method on.
   * @param methodName the name of the method to call. The method should take
   * no arguments
   */
  public void createActionFor(Object o, String methodName)
  {
    BasicAction sa = ActionUtilities.createActionFor(o, methodName);
    actions.add(sa);
  }

  /**
   * Creates a BasicAction consisting of the specified method to be called
   * on every object in the specified list, and adds the action to this
   * ActionGroup. Uses the first object in the list the BasicAction.
   * The method should take no arguments.
   *
   * @param list the list containing the object to call the method on
   * @param methodName the name of the method to call. The method should take
   * no arguments
   * @throws NoSuchMethodException if the first object in the list does not have
   * the specified method.
   */
  public void createActionForEach(ArrayList list, String methodName)
    throws NoSuchMethodException
  {

    BasicAction sla = ActionUtilities.createActionForEach(list, methodName);
    actions.add(sla);
  }

  /**
   * Creates a BasicAction consisting of the specified method to be called
   * on every Object in the specified list, and adds the BasicAction to this
   * ActionGroup. Use this method to create actions when the objects in the
   * list are not all of the same class, but <em>are</em>
   * children of a common super class. The specified method must be a method
   * of this superclass.
   *
   * @param list the list containing the object to call the method on
   * @param superClass the class to use when creating the BasicAction.
   * @param methodName the name of the method to call. The method should take
   * no arguments
   * @throws NoSuchMethodException if the first object in the list does not have
   * the specified method.
   */
  public void createActionForEach(ArrayList list, Class superClass, String methodName)
    throws NoSuchMethodException
  {

    BasicAction sla = ActionUtilities.createActionForEach(list,
                        superClass, methodName);
    actions.add(sla);
  }

  /**
   * Adds a BasicAction to the list of BasicActions to execute.
   *
   * @param action the BasicAction to add
   */
  public void addAction(BasicAction action) {
    actions.add(action);
  }

  /**
   * Removes the specified BasicAction from the group.
   *
   * @param action the BasicAction to remove
   */
   public void removeAction(BasicAction action) {
    actions.remove(action);
   }
}