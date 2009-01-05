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

/**
 * Abstract base class for Schedule and SubSchedule. Provides methods to
 * schedule BasicActions for execution.
 *
 * @author Nick Collier (Modified by Michael J. North)
 * @version $Revision$ $Date$
 *
 * @see BasicAction
 * @see ActionGroup
 * @see Schedule
 */
public abstract class ScheduleBase extends BasicAction implements TickCounter {

  public static class Order {
    private int val;

    private Order (int val) {
      this.val = val;
    }
    
    public int getVal(){
    	return val;
    }
  }

  // Declare the execution order flags.
  public static final Order LAST = new Order(0);
  public static final Order CONCURRENT = new Order(-1);

  // Declare the default step size.
  protected double repeatInterval = 1.0;

  // Declare the simulation clock.
  protected volatile double ticks = 0.0;

  // Declare the list of all regular actions.
  protected ActionQueue actionQueue = new ActionQueue();

  // Declare the list of actions to execute after the each tick.
  protected ActionQueue lastQueue = new ActionQueue();

  // Declare the group of actions to execute on the current tick.  Simply
  // contains randGroup and lastGroup.
  protected ScheduleGroup topGroup;

  // Declare the group of regular actions to execute on the current tick.
  protected RandomScheduleGroup randGroup;

  protected ScheduleGroup lastGroup;

  protected ScheduleGroup groupToExecute;

  // whether are not the preExecute() method has been executed during
  // the current execution cycle.
  protected boolean preExecuted = false;

  // BasicActions added LAST are assigned and index value in the order
  // they are added. This keeps track of the current index value.
  protected long indexCount = 0;

  /**
   * Constructs a schedule that with a default execution interval of 1.
   *
   * @see Schedule(double)
   */
  public ScheduleBase() {
    this(1);
  }

  /**
   * Constructs a schedule that executes at the specified interval. (i.e
   * a schedule with an interval of 2 executes all its BasicActions every other
   * clock tick. The master Schedule built in a model and used to execute
   * all the actions in the simulation will typicaly have an interval of 1.
   *
   * @param executionInterval the execution interval.
   */
  public ScheduleBase(double executionInterval) {
    this.repeatInterval = executionInterval;
    topGroup  = new ScheduleGroup(actionQueue);
    lastGroup = new ScheduleGroup(lastQueue);
    randGroup = new RandomScheduleGroup(actionQueue);

    topGroup.addBasicAction(randGroup);
    topGroup.addBasicAction(lastGroup);
  }

  /**
   * Schedules the specified BasicAction to occur at the specified clock tick
   * in the specified order. The BasicAction executes
   * only once.
   *
   * @param at the clock tick to execute the action.
   * @param action the action to execute.
   */

  public BasicAction scheduleActionAt(double at, BasicAction action) {
    return scheduleActionAt(at, action, ScheduleBase.CONCURRENT, 0.0);
  }

  /**
   * Schedules the specified BasicAction to occur at the specified clock tick
   * in the specified order. Order refers to the order of this action with respect
   * to other actions executing at the same tick. The BasicAction executes
   * only once.
   *
   * @param at the clock tick to execute the action.
   * @param action the action to execute.
   * @param order the mode of the action - Schedule.LAST or
   * Schedule.CONCURRENT.
   */
  public BasicAction scheduleActionAt(double at, BasicAction action, Order order) {
    return scheduleActionAt(at, action, order, 0.0);
  }


  /**
   * Schedules the specified BasicAction to occur at the specified clock tick
   * in the specified order. Order refers to the order of this action with respect
   * to other actions executing at the same tick. The BasicAction executes
   * only once.
   *
   * @param at the clock tick to execute the action.
   * @param action the action to execute.
   * @param order the mode of the action - Schedule.LAST or
   * Schedule.CONCURRENT.
   * @param duration the length of the action.
   */
  protected BasicAction scheduleActionAt(double at, BasicAction action, Order order,
    double duration)
  {
    if (at == 0) at = repeatInterval;
    if (at <= ticks) return action;

    if (duration > 0) action = new ThreadedAction(action, duration);
    action.setUpdater(BasicAction.ONE_TIME_UPDATER);

    action.nextTime = at;
    if (order == ScheduleBase.CONCURRENT) actionQueue.insert(action);
    else {
      action.index = ++indexCount;
      lastQueue.insert(action);
    }
    return action;
  }

   /**
   * Schedules the specified BasicAction to occur at the specified clock tick
   * in the specified order. The BasicAction executes
   * only once.
   *
   * @param at the clock tick to execute the action.
   * @param action the action to execute.
   * @param duration the length of the action.
   */
  public BasicAction scheduleActionAt(double at, BasicAction action, double duration) {
    return scheduleActionAt(at, action, ScheduleBase.CONCURRENT, duration);
  }

  /**
   * Schedule the specified BasicAction to execute at the specified interval,
   * (e.g. every 3 clock ticks).
   *
   * @param interval the interval at which to execute
   * @param action the BasicAction to execute
   */
  public BasicAction scheduleActionAtInterval(double interval, BasicAction action) {
    return scheduleActionAtInterval(interval, action, ScheduleBase.CONCURRENT,
                                    0.0);
  }

  /**
   * Schedule the specified BasicAction to execute at the specified interval,
   * (e.g. every 3 clock ticks).
   *
   * @param interval the interval at which to execute
   * @param action the BasicAction to execute
   * @param duration the length of the action.
   */
  public BasicAction scheduleActionAtInterval(double interval, BasicAction action,
    double duration) {
    return scheduleActionAtInterval(interval, action, CONCURRENT, duration);
  }

  /**
   * Schedule the specified BasicAction to execute at the specified interval,
   * (e.g. every 3 clock ticks) in the specified order. Order is relative
   * to other actions scheduled for this tick.
   *
   * @param interval the interval at which to execute
   * @param action the BasicAction to execute
   * @param order the order to execute this action relative to others
   * scheduled for this tick. order is one of Schedule.LAST, or
   * Schedule.CONCURRENT
   */
  public BasicAction scheduleActionAtInterval(double interval, BasicAction action,
                                              Order order)
  {
    return scheduleActionAtInterval(interval, action, order, 0.0);
  }

  /**
   * Schedule the specified BasicAction to execute at the specified interval,
   * (e.g. every 3 clock ticks) in the specified order. Order is relative
   * to other actions scheduled for this tick.
   *
   * @param interval the interval at which to execute
   * @param action the BasicAction to execute
   * @param order the order to execute this action relative to others
   * scheduled for this tick. order is one of Schedule.LAST, or
   * Schedule.CONCURRENT
   * @param duration the length of the action.
   */
  protected BasicAction scheduleActionAtInterval(double interval, BasicAction action,
    Order order, double duration)
  {
    if (interval <= 0)
      throw new IllegalArgumentException("Interval must be greater than 0");
    if (interval < duration)
      throw new IllegalArgumentException("Duration must be less than interval");

    if (duration > 0) action = new ThreadedAction(action, duration);
    action.nextTime = ticks + interval;

    action.intervalTime = interval;
    action.setUpdater(BasicAction.INTERVAL_UPDATER);
    if (order == Schedule.CONCURRENT) actionQueue.insert(action);
    else {
      action.index = ++indexCount;
      lastQueue.insert(action);
    }

    return action;
  }

  /**
   * Schedules the specified BasicAction to execute starting at the specified
   * clock tick and every tick thereafter.
   *
   * @param beginning the clock tick to begin executing
   * @param action the BasicAction to execute
   */
  public BasicAction scheduleActionBeginning(double beginning, BasicAction action) {
    return scheduleActionBeginning(beginning, action, 0.0);
  }

  /**
   * Schedules the specified BasicAction to execute starting at the specified
   * clock tick and every tick thereafter.
   *
   * @param beginning the clock tick to begin executing
   * @param action the BasicAction to execute
   * @param duration the length of the action.
   */
  public BasicAction scheduleActionBeginning(double beginning, BasicAction action, double duration) {
    if (beginning == 0) beginning = repeatInterval;
    if (ticks < beginning) {
      action.nextTime = beginning;
      action.intervalTime = repeatInterval;
      action.setUpdater(BasicAction.INTERVAL_UPDATER);
      actionQueue.insert(action);
    }

    return action;
  }

  /**
   * Schedules the execution of the specified method on the specified object
   * to occur at the specified clock tick. This executes only once.
   *
   * @param at the clock tick to execute the action.
   * @param o the object on which the method will be called
   * @param methodName the name of the method to call on the object.
   */
  public BasicAction scheduleActionAt(double at, Object o, String methodName) {
    return scheduleActionAt(at, o, methodName, 0.0);
  }

  /**
   * Schedules the execution of the specified method on the specified object
   * to occur at the specified clock tick. This executes only once.
   *
   * @param at the clock tick to execute the action.
   * @param o the object on which the method will be called
   * @param methodName the name of the method to call on the object.
   * @param duration the length of the action.
   */
  public BasicAction scheduleActionAt(double at, Object o, String methodName, double duration)
  {
    BasicAction ba = ActionUtilities.createActionFor(o, methodName);
    return this.scheduleActionAt(at, ba, duration);
  }

  /**
   * Schedules the execution of the specified method on the specified object
   * to occur at the specified clock tick in the specified order. Order is
   * relative to the other actions scheduled for this tick where actions
   * schedule for Schedule.LAST will execute after all non-LAST actions.
   * This executes only once.
   *
   * @param at the clock tick to execute the action.
   * @param o the object on which the method will be called
   * @param methodName the name of the method to call on the object.
   * @param order the order to execute this action relative to others in this
   * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
   */
  public BasicAction scheduleActionAt(double at, Object o, String methodName,
    Order order) {
    return scheduleActionAt(at, o, methodName, order, 0.0);
  }

  /**
   * Schedules the execution of the specified method on the specified object
   * to occur at the specified clock tick in the specified order. Order is
   * relative to the other actions scheduled for this tick where actions
   * schedule for Schedule.LAST will execute after all non-LAST actions.
   * This executes only once.
   *
   * @param at the clock tick to execute the action.
   * @param o the object on which the method will be called
   * @param methodName the name of the method to call on the object.
   * @param order the order to execute this action relative to others in this
   * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
   * @param duration the length of the action.
   */
  protected BasicAction scheduleActionAt(double at, Object o, String methodName,
    Order order, double duration)
  {
    BasicAction ba = ActionUtilities.createActionFor(o, methodName);
    return this.scheduleActionAt(at, ba, order, duration);
  }

  /**
   * Schedules the execution of the specified method on the specified object
   * at the specified interval, (e.g. every 3 clock ticks).
   *
   * @param interval the interval at which to execute
   * @param o the object on which the method will be called
   * @param methodName the name of the method to call
   */
  public BasicAction scheduleActionAtInterval(double interval, Object o,
        String methodName)
  {
    return scheduleActionAtInterval(interval, o, methodName, 0.0);
  }

  /**
   * Schedules the execution of the specified method on the specified object
   * at the specified interval, (e.g. every 3 clock ticks) in the specified
   * order. Order is relative to the other actions scheduled for this tick
   * where actions schedule for Schedule.LAST will execute after all non-LAST
   * actions.
   *
   * @param interval the interval at which to execute
   * @param o the object on which the method will be called
   * @param methodName the name of the method to call
   * @param order the order to execute this action relative to others in this
   * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
   */
  public BasicAction scheduleActionAtInterval(double interval, Object o,
        String methodName, Order order)
  {
   return scheduleActionAtInterval(interval, o, methodName, order, 0.0);
  }

  /**
   * Schedules the execution of the specified method on the specified object
   * at the specified interval, (e.g. every 3 clock ticks).
   *
   * @param interval the interval at which to execute
   * @param o the object on which the method will be called
   * @param methodName the name of the method to call
   * @param duration the length of the action.
   */
  public BasicAction scheduleActionAtInterval(double interval, Object o,
        String methodName, double duration)
  {
    BasicAction ba = ActionUtilities.createActionFor(o, methodName);
    return this.scheduleActionAtInterval(interval, ba, duration);
  }

  /**
   * Schedules the execution of the specified method on the specified object
   * at the specified interval, (e.g. every 3 clock ticks) in the specified
   * order. Order is relative to the other actions scheduled for this tick
   * where actions schedule for Schedule.LAST will execute after all non-LAST
   * actions.
   *
   * @param interval the interval at which to execute
   * @param o the object on which the method will be called
   * @param methodName the name of the method to call
   * @param order the order to execute this action relative to others in this
   * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
   * @param duration the length of the action.
   */
  protected BasicAction scheduleActionAtInterval(double interval, Object o,
        String methodName, Order order, double duration)
  {
    BasicAction ba = ActionUtilities.createActionFor(o, methodName);
    return this.scheduleActionAtInterval(interval, ba, order, duration);
  }

  /**
   * Schedules the execution of the specified method on the specified object
   * to start at the specified clock tick and continue every tick thereafter.
   *
   * @param beginning the clock tick to begin executing
   * @param o the object on which the method will be called
   * @param methodName the name of the method to call
   */
  public BasicAction scheduleActionBeginning(double beginning, Object o,
        String methodName)
  {
    return scheduleActionBeginning(beginning, o, methodName, 0.0);
  }

  /**
   * Schedules the execution of the specified method on the specified object
   * to start at the specified clock tick and continue every tick thereafter.
   *
   * @param beginning the clock tick to begin executing
   * @param o the object on which the method will be called
   * @param methodName the name of the method to call
   * @param duration the length of the action.
   */
  public BasicAction scheduleActionBeginning(double beginning, Object o,
        String methodName, double duration)
  {
    BasicAction sa = ActionUtilities.createActionFor(o, methodName);
    return scheduleActionBeginning(beginning, sa, duration);
  }

  /* more schedule methods */
  /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick.
     * This executes only once. Assumes all objects in the list are of the
     * same class.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param methodName the name of the method to call
     * @return the scheduled BasicAction
     */
    public BasicAction scheduleActionAt(double at, List list, String methodName) {
      return scheduleActionAt(at, list, methodName, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick.
     * This executes only once. Assumes all objects in the list are of the
     * same class.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param methodName the name of the method to call
     * @param duration the length of the action.
     * @return the scheduled BasicAction
     */
    public BasicAction scheduleActionAt(double at, List list, String methodName,
      double duration) {

      BasicAction sa = ActionUtilities.createActionForEach(list, methodName);
      return this.scheduleActionAt(at, sa, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick.
     * This executes only once. Assumes all objects in the list are of the
     * same class. The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param methodName the name of the method to call
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionAtRnd(double at, List list, String methodName)
    {
      return scheduleActionAtRnd(at, list, methodName, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick.
     * This executes only once. Assumes all objects in the list are of the
     * same class. The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param methodName the name of the method to call
     * @param duration the length of the action.
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionAtRnd(double at, List list,
      String methodName, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEachRnd(list, methodName);
      return this.scheduleActionAt(at, sa, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick in the specified
     * order. Order is relative to the other actions scheduled for this tick
     * where actions schedule for Schedule.LAST will execute after all non-LAST
     * actions.<p>
     *
     * This action executes only once, and ssumes all objects in the list
     * are of the same class.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @return the scheduled BasicAction
     */
    public BasicAction scheduleActionAt(double at, List list, String methodName,
      Order order)
    {
      return scheduleActionAt(at, list, methodName, order, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick in the specified
     * order. Order is relative to the other actions scheduled for this tick
     * where actions schedule for Schedule.LAST will execute after all non-LAST
     * actions.<p>
     *
     * This action executes only once, and ssumes all objects in the list
     * are of the same class.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @param duration the length of the action.
     * @return the scheduled BasicAction
     */
    protected BasicAction scheduleActionAt(double at, List list, String methodName,
      Order order, double duration)
    {

      BasicAction sa = ActionUtilities.createActionForEach(list, methodName);
      return this.scheduleActionAt(at, sa, order, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick in the specified
     * order. Order is relative to the other actions scheduled for this tick
     * where actions schedule for Schedule.LAST will execute after all non-LAST
     * actions. The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.<p>
     *
     * This action executes only once, and ssumes all objects in the list
     * are of the same class.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionAtRnd(double at, List list, String methodName,
      Order order)
    {
      return scheduleActionAtRnd(at, list, methodName, order, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick in the specified
     * order. Order is relative to the other actions scheduled for this tick
     * where actions schedule for Schedule.LAST will execute after all non-LAST
     * actions. The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.<p>
     *
     * This action executes only once, and ssumes all objects in the list
     * are of the same class.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @param duration the length of the action.
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    protected BasicAction scheduleActionAtRnd(double at, List list, String methodName,
      Order order, double duration)
    {

      BasicAction sa = ActionUtilities.createActionForEachRnd(list, methodName);
      return this.scheduleActionAt(at, sa, order, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval. (e.g. every three
     * clock ticks) in the specified order. Order is relative to the other
     * actions scheduled for this tick where actions schedule for Schedule.LAST
     * will execute after all non-LAST actions.<p>
     *
     * Assumes all objects in the list are of the same class.
     *
     * @param interval the interval at which to execute the method
     * @param list the list containing the objects on which the method will be
     * called.
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @return the scheduled BasicAction
     */

    public BasicAction scheduleActionAtInterval(double interval, List list,
      String methodName, Order order)
    {
      return scheduleActionAtInterval(interval, list, methodName, order, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval. (e.g. every three
     * clock ticks) in the specified order. Order is relative to the other
     * actions scheduled for this tick where actions schedule for Schedule.LAST
     * will execute after all non-LAST actions.<p>
     *
     * Assumes all objects in the list are of the same class.
     *
     * @param interval the interval at which to execute the method
     * @param list the list containing the objects on which the method will be
     * called.
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @param duration the length of the action.
     * @return the scheduled BasicAction
     */
    protected BasicAction scheduleActionAtInterval(double interval, List list,
      String methodName, Order order, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEach(list, methodName);
      return this.scheduleActionAtInterval(interval, sa, order, duration);
    }

     /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval. (e.g. every three
     * clock ticks) in the specified order. Order is relative to the other
     * actions scheduled for this tick where actions schedule for Schedule.LAST
     * will execute after all non-LAST actions. The list will be randomized
     * with SimUtilites.shuffle before the the method is called on the objects.
     * <p>
     *
     * Assumes all objects in the list are of the same class.
     *
     * @param interval the interval at which to execute the method
     * @param list the list containing the objects on which the method will be
     * called.
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionAtIntervalRnd(double interval, List list,
      String methodName, Order order)
    {
      return scheduleActionAtIntervalRnd(interval, list, methodName, order, 0.0);
    }

     /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval. (e.g. every three
     * clock ticks) in the specified order. Order is relative to the other
     * actions scheduled for this tick where actions schedule for Schedule.LAST
     * will execute after all non-LAST actions. The list will be randomized
     * with SimUtilites.shuffle before the the method is called on the objects.
     * <p>
     *
     * Assumes all objects in the list are of the same class.
     *
     * @param interval the interval at which to execute the method
     * @param list the list containing the objects on which the method will be
     * called.
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    protected BasicAction scheduleActionAtIntervalRnd(double interval, List list,
      String methodName, Order order, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEachRnd(list, methodName);
      return this.scheduleActionAtInterval(interval, sa, order, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval. (e.g. every three
     * clock ticks). Assumes all objects in the list are of the same class.
     *
     * @param interval the interval at which to execute the method
     * @param list the list containing the objects on which the method will be
     * called.
     * @param methodName the name of the method to call
     */
    public BasicAction scheduleActionAtInterval(double interval, List list,
      String methodName)
    {
      return scheduleActionAtInterval(interval, list, methodName, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval. (e.g. every three
     * clock ticks). Assumes all objects in the list are of the same class.
     *
     * @param interval the interval at which to execute the method
     * @param list the list containing the objects on which the method will be
     * called.
     * @param methodName the name of the method to call
     * @param duration the length of the action.
     */
    public BasicAction scheduleActionAtInterval(double interval, List list,
      String methodName, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEach(list, methodName);
      return this.scheduleActionAtInterval(interval, sa, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval. (e.g. every three
     * clock ticks). Assumes all objects in the list are of the same class.
     * The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.
     *
     * @param interval the interval at which to execute the method
     * @param list the list containing the objects on which the method will be
     * called.
     * @param methodName the name of the method to call
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionAtIntervalRnd(double interval, List list,
      String methodName)
    {
      return scheduleActionAtIntervalRnd(interval, list, methodName, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval. (e.g. every three
     * clock ticks). Assumes all objects in the list are of the same class.
     * The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.
     *
     * @param interval the interval at which to execute the method
     * @param list the list containing the objects on which the method will be
     * called.
     * @param methodName the name of the method to call
     * @param duration the length of the action.
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionAtIntervalRnd(double interval, List list,
      String methodName, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEachRnd(list, methodName);
      return this.scheduleActionAtInterval(interval, sa, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur beginning at the specified clock tick and
     * every tick thereafter. Assumes all objects in the list are of the
     * same class.
     *
     * @param beginning the clock tick at which to begin execution
     * @param list the List containing the objects on which the method will be
     * called.
     * @param methodName the name of the method to call
     * @return the scheduled BasicAction
     */
    public BasicAction scheduleActionBeginning(double beginning, List list,
            String methodName)
    {
      return scheduleActionBeginning(beginning, list, methodName, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur beginning at the specified clock tick and
     * every tick thereafter. Assumes all objects in the list are of the
     * same class.
     *
     * @param beginning the clock tick at which to begin execution
     * @param list the List containing the objects on which the method will be
     * called.
     * @param methodName the name of the method to call
     * @param duration the length of the action.
     * @return the scheduled BasicAction
     */
    public BasicAction scheduleActionBeginning(double beginning, List list,
            String methodName, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEach(list, methodName);
      return this.scheduleActionBeginning(beginning, sa, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur beginning at the specified clock tick and
     * every tick thereafter. Assumes all objects in the list are of the
     * same class. The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.
     *
     * @param beginning the clock tick at which to begin execution
     * @param list the List containing the objects on which the method will be
     * called.
     * @param methodName the name of the method to call
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionBeginningRnd(double beginning, List list,
            String methodName)
    {
      return scheduleActionBeginningRnd(beginning, list, methodName, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur beginning at the specified clock tick and
     * every tick thereafter. Assumes all objects in the list are of the
     * same class. The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.
     *
     * @param beginning the clock tick at which to begin execution
     * @param list the List containing the objects on which the method will be
     * called.
     * @param methodName the name of the method to call
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionBeginningRnd(double beginning, List list,
            String methodName, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEachRnd(list, methodName);
      return this.scheduleActionBeginning(beginning, sa, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick. This executes only
     * once, and assumes all objects in the list are of the specified class.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @return the scheduled BasicAction
     */
    public BasicAction scheduleActionAt(double at, List list, Class superClass,
      String methodName)
    {
      return scheduleActionAt(at, list, superClass, methodName, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick. This executes only
     * once, and assumes all objects in the list are of the specified class.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @param duration the length of the action.
     * @return the scheduled BasicAction
     */
    public BasicAction scheduleActionAt(double at, List list, Class superClass,
      String methodName, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEach(list, superClass,
                                                          methodName);
      return this.scheduleActionAt(at, sa, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick. This executes only
     * once, and assumes all objects in the list are of the specified class.
     * The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionAtRnd(double at, List list, Class superClass,
      String methodName)
    {
      return scheduleActionAtRnd(at, list, superClass, methodName, 0.0);
    }
    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick. This executes only
     * once, and assumes all objects in the list are of the specified class.
     * The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @param duration the length of the action.
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionAtRnd(double at, List list, Class superClass,
      String methodName, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEachRnd(list, superClass,
                                                          methodName);
      return this.scheduleActionAt(at, sa, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick in the specified
     * order. Order is relative to the other actions scheduled for this tick
     * where actions schedule for Schedule.LAST will execute after all non-LAST
     * actions. This executes only once, and assumes all objects in the list are
     * of the specified class.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @return the scheduled BasicAction
     */
    public BasicAction scheduleActionAt(double at, List list, Class superClass,
      String methodName, Order order)
    {
      return scheduleActionAt(at, list, superClass, methodName, order, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick in the specified
     * order. Order is relative to the other actions scheduled for this tick
     * where actions schedule for Schedule.LAST will execute after all non-LAST
     * actions. This executes only once, and assumes all objects in the list are
     * of the specified class.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @param duration the length of the action.
     * @return the scheduled BasicAction
     */
    protected BasicAction scheduleActionAt(double at, List list, Class superClass,
      String methodName, Order order, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEach(list, superClass,
                                                          methodName);
      return this.scheduleActionAt(at, sa, order, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick in the specified
     * order. Order is relative to the other actions scheduled for this tick
     * where actions schedule for Schedule.LAST will execute after all non-LAST
     * actions. This executes only once, and assumes all objects in the list are
     * of the specified class. The list will be randomized with
     * SimUtilites.shuffle before the the method is called on the objects.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionAtRnd(double at, List list, Class superClass,
      String methodName, Order order)
    {
      return scheduleActionAtRnd(at, list, superClass, methodName, order, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified clock tick in the specified
     * order. Order is relative to the other actions scheduled for this tick
     * where actions schedule for Schedule.LAST will execute after all non-LAST
     * actions. This executes only once, and assumes all objects in the list are
     * of the specified class. The list will be randomized with
     * SimUtilites.shuffle before the the method is called on the objects.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param at the clock tick to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionAtRnd(double at, List list, Class superClass,
      String methodName, Order order, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEachRnd(list, superClass,
                                                          methodName);
      return this.scheduleActionAt(at, sa, order, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval (e.g. every three clock
     * ticks). This assumes all objects in the list are of the specified class.
     *  The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param interval the tick interval at which to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionAtIntervalRnd(double interval, List list,
      Class superClass, String methodName)
    {
      return scheduleActionAtIntervalRnd(interval, list, superClass, methodName, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval (e.g. every three clock
     * ticks). This assumes all objects in the list are of the specified class.
     *  The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param interval the tick interval at which to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionAtIntervalRnd(double interval, List list,
      Class superClass, String methodName, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEachRnd(list, superClass,
                              methodName);
      return this.scheduleActionAtInterval(interval, sa, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval (e.g. every three clock
     * ticks) in the specified order. Order is relative to the other actions
     * scheduled for this tick where actions schedule for Schedule.LAST will
     * execute after all non-LAST actions. This assumes all objects in the list
     * are of the specified class.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param interval the tick interval at which to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @return the scheduled BasicAction
     */
    public BasicAction scheduleActionAtInterval(double interval, List list,
      Class superClass, String methodName, Order order)
    {
      return scheduleActionAtInterval(interval, list, superClass, methodName, order, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval (e.g. every three clock
     * ticks) in the specified order. Order is relative to the other actions
     * scheduled for this tick where actions schedule for Schedule.LAST will
     * execute after all non-LAST actions. This assumes all objects in the list
     * are of the specified class.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param interval the tick interval at which to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @param duration the length of the action.
     * @return the scheduled BasicAction
     */
    protected BasicAction scheduleActionAtInterval(double interval, List list,
      Class superClass, String methodName, Order order, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEach(list, superClass,
                              methodName);
      return this.scheduleActionAtInterval(interval, sa, order, duration);
    }

     /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval (e.g. every three clock
     * ticks) in the specified order. Order is relative to the other actions
     * scheduled for this tick where actions schedule for Schedule.LAST will
     * execute after all non-LAST actions. This assumes all objects in the list
     * are of the specified class. The list will be randomized with
     * SimUtilites.shuffle before the the method is called on the objects.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param interval the tick interval at which to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionAtIntervalRnd(double interval, List list,
      Class superClass, String methodName, Order order)
    {
      return scheduleActionAtIntervalRnd(interval, list, superClass, methodName, order, 0.0);
    }

     /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur at the specified interval (e.g. every three clock
     * ticks) in the specified order. Order is relative to the other actions
     * scheduled for this tick where actions schedule for Schedule.LAST will
     * execute after all non-LAST actions. This assumes all objects in the list
     * are of the specified class. The list will be randomized with
     * SimUtilites.shuffle before the the method is called on the objects.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param interval the tick interval at which to execute the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @param order the order to execute this action relative to others in this
     * tick. order is one of Schedule.LAST, or Schedule.CONCURRENT.
     * @return the scheduled BasicAction
     * @param duration the length of the action.
     * @see uchicago.src.sim.util.SimUtilities
     */
    protected BasicAction scheduleActionAtIntervalRnd(double interval, List list,
      Class superClass, String methodName, Order order, double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEachRnd(list, superClass,
                              methodName);
      return this.scheduleActionAtInterval(interval, sa, order, duration);
    }


    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur beginning at the specified clock tick and every
     * tick thereafter. This assumes all objects in the list are of the specified
     * class.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param beginning the tick at which to start the repeated execution of
     *  the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @return the scheduled BasicAction
     */
    public BasicAction scheduleActionBeginning(double beginning, List list,
                           Class superClass,
                           String methodName)
    {
      return scheduleActionBeginning(beginning, list, superClass, methodName, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur beginning at the specified clock tick and every
     * tick thereafter. This assumes all objects in the list are of the specified
     * class.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param  beginning the tick at which to start the repeated execution of
     *  the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @param duration the length of the action.
     * @return the scheduled BasicAction
     */
    public BasicAction scheduleActionBeginning(double beginning, List list,
                           Class superClass,
                           String methodName,
                                     double duration)
    {
      BasicAction sa = ActionUtilities.createActionForEach(list, superClass,
                               methodName);
      return this.scheduleActionBeginning(beginning, sa, duration);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur beginning at the specified clock tick and every
     * tick thereafter. This assumes all objects in the list are of the specified
     * class. The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param  beginning the tick at which to start the repeated execution of
     *  the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionBeginningRnd(double beginning, List list,
                           Class superClass,
                           String methodName)
    {
      return scheduleActionBeginningRnd(beginning, list, superClass, methodName, 0.0);
    }

    /**
     * Schedules the execution of the specified method on every object in the
     * specified List to occur beginning at the specified clock tick and every
     * tick thereafter. This assumes all objects in the list are of the specified
     * class. The list will be randomized with SimUtilites.shuffle
     * before the the method is called on the objects.<p>
     *
     * The intention here is that type of the objects in the list may different,
     * but that they all share a common super-class or interface. The specified
     * method must be a method of this super-class or interface.
     *
     * @param  beginning the tick at which to start the repeated execution of
     *  the action
     * @param list the List containing the objects on which the method will be
     * called
     * @param superClass the common super-class or interface
     * @param methodName the name of the method to call
     * @return the scheduled BasicAction
     * @see uchicago.src.sim.util.SimUtilities
     */
    public BasicAction scheduleActionBeginningRnd(double beginning, List list,
                                                  Class superClass,
                                                  String methodName,
                                                  double duration) {
      BasicAction sa = ActionUtilities.createActionForEachRnd(list, superClass,
                                                              methodName);
      return this.scheduleActionBeginning(beginning, sa, duration);
    }


  /* end of other methods */

  /**
   * Gets the current clock tick.
   * 
   * @return the current clock tick
   */

  public synchronized double getCurrentTime() {
    return ticks;
  }

  /**
   * Gets the current clock tick as a double precision number while
   * maintaining compatibility with previous RePast releases. 
   * @see #getCurrentTime()
   * @deprecated
   * @return the current clock tick
   */

  public synchronized double getCurrentTimeDouble() {
    return ticks;
  }

  private synchronized void setTicks(double newTick) {
    ticks = newTick;
  }

  public void preExecute() {
    double queueMin = Double.POSITIVE_INFINITY;
    double lastMin = Double.POSITIVE_INFINITY;

    if (actionQueue.size() > 0) queueMin = actionQueue.peekMin().nextTime;
    if (lastQueue.size() > 0)  lastMin = lastQueue.peekMin().nextTime;

    //System.out.println ("queueMin = " + queueMin);
    //System.out.println ("lastMin = " + lastMin);

    if (queueMin == Double.POSITIVE_INFINITY &&
        lastMin == Double.POSITIVE_INFINITY) {

      //  the proper semantics, I think, are to just return here
      //throw new IllegalStateException("Action queues are empty");
      // we provide an empty group of BasicActions to execute.
      randGroup.clear();
      groupToExecute = randGroup;
      preExecuted = true;
      return;
    }

    if (queueMin < lastMin) {
      // only execute actions in the actionQueue
      fillGroup(queueMin, randGroup, actionQueue);
      groupToExecute = randGroup;
    } else if (lastMin < queueMin) {
      // only execute last actions
      fillGroup(lastMin, lastGroup, lastQueue);
      lastGroup.indexSort();
      groupToExecute = lastGroup;
    } else {
      // execute both sets
      fillGroup(queueMin, randGroup, actionQueue);
      fillGroup(lastMin, lastGroup, lastQueue);
      lastGroup.indexSort();
      groupToExecute = topGroup;
    }

    preExecuted = true;
  }

  // queueMin is the current minimum tick value in actionQueue. We want
  // to add that minimum BasicAction to group and perhaps more if
  // they have the same nextTime value.
  private void fillGroup(double queueMin, ScheduleGroup group, ActionQueue queue) {
    double newTick = queueMin;
    group.clear();
    queue.popMin().addToGroup(group);

    if (queue.size() > 0) {
      // only try and keep adding if there are BasicActions left in the
      // queue.
      queueMin = queue.peekMin().nextTime;
      while (queueMin == newTick) {
        // newTick is our original minimum nextTime we don't want to
        // add any BasicActions whose nextTime is not equal to this.
        queue.popMin().addToGroup(group);
        if (queue.size() == 0) break;
        queueMin = queue.peekMin().nextTime;
      }
    }

    // there may have been empty removed actions in the queue. These don't
    // add themselves to group on addGroup so we only update the true tick count
    // if there are actual BasicActions to execute.
    if (group.size() > 0) setTicks(newTick);
  }

  /**
   * Removes the specified action from this Schedule. Note that this
   * is not recursive and will only a remove an action explicity added
   * to this Schedule. It will not remove any actions contained by child
   * containers of this Schedule.
   *
   * @param action the action to remove
   * @return null if the specified BasicAction is not found in this Schedule
   * or the BasicAction itself.
   */
  public BasicAction removeAction(BasicAction action) {
    randGroup.removeAction(action);
    lastGroup.removeAction(action);
    if (actionQueue.voidAction(action)) return action;
    return null;
  }

  /**
   * Removes the specified action from this Schedule at the specified tick.
   * The actual call to remove the action is made as part of the Schedule.LAST.
   * Consequently, if the action is scheduled with Schedule.CONCURRENT order
   * (the default order for scheduling actions), the action will execute during
   * the specified tick and then be removed.
   *
   * @param at the tick at which to remove the action
   * @param action the action to remove
   */
  public void removeActionAt(double at, final BasicAction action) {
    this.scheduleActionAt(at, new BasicAction() {
      public void execute() {
        removeAction(action);
      }
    }, Schedule.LAST, 0);
  }
}
