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

import java.util.List;
import java.util.Vector;
//import java.lang.reflect.*;

//import uchicago.src.sim.util.SimUtilities;

/**
 * Manages the execution of BasicActions (ActionGroups,
 * SimActions, and SimListActions) according to a simulation clock. The clock
 * is incremeneted at the completion of the execution of all the BasicActions
 * scheduled for execution at that clock tick. A Schedule is itself a
 * BasicAction and so Schedules can be added to other Schedules.<p>
 *
 * The actions scheduled on a Schedule will iterate with a simulated
 * concurrency (i.e. in random order). If the actions should be executed
 * in some specified order, the action should be added to an ActionGroup
 * set for sequential execution. This ActionGroup can then be added to
 * the Schedule for execution. Specifying the order in the
 * scheduleActionAt and scheduleActionAtInterval methods can be used to
 * insure that certain actions occur after other actions.
 *
 * @author Nick Collier (Modified by Michael J. North)
 * @version $Revision$ $Date$
 *
 * @see BasicAction
 * @see ActionGroup
 * @see SubSchedule
 */
public class Schedule extends ScheduleBase {

  private Vector endActions = new Vector();
  private Vector pauseActions = new Vector();

  /**
   * Constructs a schedule that with a default execution interval of 1.
   *
   * @see Schedule(double)
   */
  public Schedule() {
    super();
  }

  /**
   * Constructs a schedule that executes at the specified interval. (i.e
   * a schedule with an interval of 2 executes all its BasicActions every other
   * clock tick. The master Schedule built in a model and used to execute
   * all the actions in the simulation will typicaly have an interval of 1.
   * Any sub schedules added to this "master" schedule might have other
   * intervals.
   *
   * @param executionInterval the execution interval.
   */
  public Schedule(double executionInterval) {
    super(executionInterval);
  }


  /**
   * Schedules the execution of the specified action for the end of
   * the simulation run. Excutes only once. Useful for writing data to a file
   * at the end of the simulation.
   *
   * @param action the BasicAction to execute at the end of a run.
   */
  public BasicAction scheduleActionAtEnd(BasicAction action) {
    endActions.add(action);
    return action;
  }

  /**
   * Schedules the execution of the specified action for a pause in
   * the simulation run. Executes once for each pause event. Useful, for example,
   * for updating the display on a pause if the display does not update
   * every turn.
   *
   * @param action the BasicAction to execute when the simulation run is
   * paused
   */
  public BasicAction scheduleActionAtPause(BasicAction action) {
    pauseActions.add(action);
    return action;
  }

  /**
   * Schedules the execution of the specified method on the
   * specified object for the end of the simulation run. Excutes only once each
   * simulation run. Useful for writing data to a file
   * at the end of the simulation run.
   *
   * @param o the object on which the method will be called
   * @param methodName the name of method to call on the object.
   */
  public BasicAction scheduleActionAtEnd(Object o, String methodName)
  {
    BasicAction sa = ActionUtilities.createActionFor(o, methodName);
    endActions.add(sa);
    return sa;
  }

  /**
   * Schedules the execution of the specified method on the
   * specified object for a pause in the simulation run. Executes once for
   * each pause event. Useful for updating the display on a pause if the
   * display does not update every turn.
   *
   * @param o the object on which the method will be called
   * @param methodName the name of method to call on the object.
   */
  public BasicAction scheduleActionAtPause(Object o, String methodName)
  {
    BasicAction sa = ActionUtilities.createActionFor(o, methodName);
    pauseActions.add(sa);
    return sa;
  }

  /**
   * Schedules the execution of the specified method on every object in the
   * specified List for the end of the simulation. Excutes only once each
   * simulation run. Useful for writing data to a file
   * at the end of the simulation.
   *
   * @param list the List of objects on which the method will be called
   * @param methodName the name of method to call on the object.
   */
  public BasicAction scheduleActionAtEnd(List list, String methodName)
  {
    BasicAction sa = ActionUtilities.createActionForEach(list, methodName);
    endActions.add(sa);
    return sa;
  }

   /**
   * Schedules the execution of the specified method on every object in the
   * specified List for the end of the simulation. Excutes only once each
   * simulation run. Useful for writing data to a file
   * at the end of the simulation. The list will be randomized with
   * SimUtilites.shuffle before the the method is called on the objects.
   *
   * @param list the List of objects on which the method will be called
   * @param methodName the name of method to call on the object.
   * @see uchicago.src.sim.util.SimUtilities
   */
  public BasicAction scheduleActionAtEndRnd(List list, String methodName)
  {
    BasicAction sa = ActionUtilities.createActionForEachRnd(list, methodName);
    endActions.add(sa);
    return sa;
  }

  /**
   * Schedules the execution of the specified method on every object in the
   * specified List for a pause in the simulation. Executes once for
   * each pause event. Useful for updating the display on a pause if the
   * display does not update every turn.
   *
   * @param list the List of objects on which the method will be called
   * @param methodName the name of method to call on the object.
   */
  public BasicAction scheduleActionAtPause(List list, String methodName)
  {
    BasicAction sa = ActionUtilities.createActionForEach(list, methodName);
    pauseActions.add(sa);
    return sa;
  }

  /**
   * Schedules the execution of the specified method on every object in the
   * specified List for a pause in the simulation. Executes once for
   * each pause event. Useful for updating the display on a pause if the
   * display does not update every turn. The list will be randomized with
   * SimUtilites.shuffle before the the method is called on the objects.
   *
   * @param list the List of objects on which the method will be called
   * @param methodName the name of method to call on the object.
   * @see uchicago.src.sim.util.SimUtilities
   */
  public BasicAction scheduleActionAtPauseRnd(List list, String methodName)
  {
    BasicAction sa = ActionUtilities.createActionForEachRnd(list, methodName);
    pauseActions.add(sa);
    return sa;
  }

  /**
   * Schedules the execution of the specified method on every object in the
   * specified List for the end of the simulation. Excutes only once each
   * simulation run. Useful for writing data to a file
   * at the end of the simulation. Assumes all objects in the list
   * are instances of the specified class.
   *
   * @param list the List of objects on which the method will be called
   * @param superClass the superclass of all the objects in the list.
   * @param methodName the name of method to call on the object.
   */
  public BasicAction scheduleActionAtEnd(List list, Class superClass,
					 String methodName)
  {
    BasicAction sa = ActionUtilities.createActionForEach(list, superClass,
							 methodName);
    endActions.add(sa);
    return sa;
  }

  /**
   * Schedules the execution of the specified method on every object in the
   * specified List for the end of the simulation. Excutes only once each
   * simulation run. Useful for writing data to a file
   * at the end of the simulation. Assumes all objects in the list
   * are instances of the specified class. The list will be randomized
   * with SimUtilites.shuffle before the the method is called on the objects
   *
   * @param list the List of objects on which the method will be called
   * @param superClass the superclass of all the objects in the list.
   * @param methodName the name of method to call on the object.
   * @see uchicago.src.sim.util.SimUtilities
   */
  public BasicAction scheduleActionAtEndRnd(List list, Class superClass,
					 String methodName)
  {
    BasicAction sa = ActionUtilities.createActionForEachRnd(list, superClass,
							 methodName);
    endActions.add(sa);
    return sa;
  }

  /**
   * Schedules the execution of the specified method on every object in the
   * specified List for a pause in the simulation. Executes once for
   * each pause event. Useful for updating the display on a pause if the
   * display does not update every turn. Assumes all objects in the list
   * are instances of the specified class.
   *
   * @param list the AList of objects on which the method will be called
   * @param superClass the superclass of all the objects in the list.
   * @param methodName the name of method to call on the object.
   */
  public BasicAction scheduleActionAtPause(List list, Class superClass,
					   String methodName)
  {
    BasicAction sa = ActionUtilities.createActionForEach(list, superClass,
							 methodName);
    pauseActions.add(sa);
    return sa;
  }

  /**
   * Schedules the execution of the specified method on every object in the
   * specified List for a pause in the simulation. Executes once for
   * each pause event. Useful for updating the display on a pause if the
   * display does not update every turn. Assumes all objects in the list
   * are instances of the specified class. The list will be randomized
   * with SimUtilites.shuffle before the the method is called on the objects
   *
   * @param list the AList of objects on which the method will be called
   * @param superClass the superclass of all the objects in the list.
   * @param methodName the name of method to call on the object.
   * @see uchicago.src.sim.util.SimUtilities
   */
  public BasicAction scheduleActionAtPauseRnd(List list, Class superClass,
					   String methodName)
  {
    BasicAction sa = ActionUtilities.createActionForEachRnd(list, superClass,
							 methodName);
    pauseActions.add(sa);
    return sa;
  }

  /**
   * Removes the specified action from the list of actions to be executed
   * at the end of a simulation.
   *
   * @param action the action to remove
   */
  public void removeEndAction(BasicAction action) {
    endActions.remove(action);
  }

  /**
   * Removes the specified action from the list of actions to be executed
   * at the pause in a simulation.
   *
   * @param action the action to remove
   */
  public void removePauseAction(BasicAction action) {
    pauseActions.remove(action);
  }

  /**
   * Gets the Vector of BasicActions to execute at the end of the simulation
   * run.
   *
   * @return the vector of BasicActions to execute at the end of the simuation
   * run
   */
  public Vector getEndActions() {
    return endActions;
  }

  /**
   * Gets the Vector of BasicActions to execute during a pause in the
   * simulation run.
   *
   * @return the vector of BasicActions to execute during a pause in the
   * simulation run.
   */
  public Vector getPauseActions() {
    return pauseActions;
  }

  /**
   * Executes all the actions scheduled for the current clock tick.
   */

  public void execute() {
    if (! preExecuted) preExecute();
    groupToExecute.execute();
    groupToExecute.reSchedule(null);
    preExecuted = false;
  }

  /**
   * Executes all the actions scheduled to execute at simulation end.
   */
  public void executeEndActions() {
    for (int i = 0; i < endActions.size(); i++) {
      BasicAction ba = (BasicAction)endActions.elementAt(i);
      ba.execute();
    }
  }

  /**
   * Executes all the actions scheduled to execute at a simulation pause.
   */
  public void executePauseActions() {
    for (int i = 0; i < pauseActions.size(); i++) {
      ((BasicAction)pauseActions.get(i)).execute();
    }
  }
}
