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

/**
 * Abstract base class for any action in a simulation that can be executed
 * by a <code>Schedule</code>.
 *
 * @author Nick Collier (Modified by Michael J. North)
 * @version $Revision$ $Date$
 * @see Schedule
 */
public abstract class BasicAction {

  //public static final int BEG_UPDATER = 0;
  public static final int INTERVAL_UPDATER = 1;
  public static final int ONE_TIME_UPDATER = 2;

  private String name = "";

  // Declare the next execution time scheduling value.
  double nextTime = Double.NaN;

  // an index tracking the order this BasicAction was added, if added
  // with an order of LAST
  long index = 0;

  // the interval at which to execute this basic action.
  // This may be the repeat interval of a schedule or it may be
  // the user-scheduled interval.
  double intervalTime = 0;

  interface Updater {
    public void update(ActionQueue queue);
  }

  class IntervalUpdater implements Updater {
    public void update(ActionQueue queue) {
      nextTime += intervalTime;
      queue.toss(BasicAction.this);
    }
  }

  class OneTimeUpdater implements Updater {
    public void update(ActionQueue queue) {}
  }

  protected Updater updater = new IntervalUpdater();


  /**
   * Sets the next time (tick) this BasicAction will be executed.
   */
  public void setNextTime(double nextTime) {
    this.nextTime = nextTime;
  }

  public double getNextTime() {
    return nextTime;
  }

  public double getIntervalTime() {
    return intervalTime;
  }

  public void setIntervalTime(double intervalTime) {
    this.intervalTime = intervalTime;
  }

  /**
   * Sets the name of this BasicAction.
   *
   * @param aName the name for this BasicAction
   */
  public void setName(String aName) {
    name = aName;
  }

  /**
   * Gets the name of this BasicAction.
   * @return the name of this BasicAction
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the updater for this BasicAction. An updater controlls
   * how a BasicAction is readded to a schedule queue. Valid types are:
   * BasicActoin.INTERVAL_UPDATER, and BasicAction.ONE_TIME_UPDATER.
   */
  void setUpdater(int type) {
    //if (type == BEG_UPDATER) updater = new BegUpdater();
    if (type == INTERVAL_UPDATER) updater = new IntervalUpdater();
    else if (type == ONE_TIME_UPDATER) updater = new OneTimeUpdater();
    else throw new IllegalArgumentException("Illegal Updater type");
  }

  /**
   * Executes this BasicAction. Typically invokes a Method on some
   * Object.
   */
  public abstract void execute();

  /**
   * Reschedule this BasicAction if necessary.
   */
  public void reSchedule(ActionQueue queue) {
    updater.update(queue);
  }

  public void addToGroup(ScheduleGroup group) {
    group.addBasicAction(this);
  }
}