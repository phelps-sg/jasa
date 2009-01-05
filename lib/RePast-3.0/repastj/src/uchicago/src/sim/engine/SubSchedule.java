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

//import java.lang.reflect.InvocationTargetException;

/**
 * Executes within a parent <code>Schedule</code> and manages the
 * execution of <code>BasicActions</code> according to an internal clock.
 * A SubSchedule allows a user to further divide the ticks of a parent
 * Schedule by some specified number. Certain aspects of the model can
 * then occur "faster" than other elements.<p>
 *
 * A SubSchedule is typically scheduled against a Schedule to execute at some
 * tick t. When the SubSchedule exectutes at t, it then iterates over its
 * own scheduled actions a specified number of times. So, for example,
 * a SubSchedule may execute all its actions three times for every tick
 * of its parent Schedule.<p>
 *
 * The actions scheduled on a SubSchedule will iterate with a simulated
 * concurrency (i.e. in random order). If the actions should be executed
 * in some specified order, the actions should be added to an ActionGroup
 * set for sequential execution. This ActionGroup can then be added to
 * the SubSchedule for execution. Specifying the order in the
 * scheduleActionAt and scheduleActionAtInterval methods can be used to
 * insure that certain actions occur after other actions.
 *
 * @author Nick Collier (Modified by Michael J. North)
 * @version $Revision$ $Date$
 * @deprecated
 *
 * @see Schedule
 * @see BasicAction
 * @see ActionGroup
 */

public class SubSchedule extends ScheduleBase {

  private long iterations = 1;

  /**
   * Constructs a SubSchedule with a default interval of 1, and with the
   * specified number of times to execute its scheduled actions per tick
   * of the parent Schedule
   *
   * @param iterations the number of times to execute scheduled actions per
   * tick of the parent schedule
   */
  public SubSchedule(long iterations) {
    this(1, iterations);
  }

  /**
   * Constructs a SubSchedule with the specifed interval, and with the
   * specified number of times to execute its scheduled actions per tick
   * of the parent Schedule
   *
   * @param interval the execution interval
   * @param iterations the number of times to execute scheduled actions per
   * tick of the parent schedule
   */
  public SubSchedule(double interval, long iterations) {
    super(interval);
    this.iterations = iterations;
  }

  /**
   * Sets the number of iterations of this SubSchedule's action per
   * tick of the parent Schedule.
   *
   * @param iterations the number of times to execute my scheduled actions per
   * tick of the parent schedule
   */
  public void setIterations(long iterations) {
    this.iterations = iterations;
  }

  /**
   * Gets the number of times this SubSchedule's actions will execute per
   * parent Schedule tick.
   */
  public long getIterations() {
    return iterations;
  }

  /**
   * Executes this SubSchedules actions the specified number of times.
   */
  public void execute() {}
  /*
    for (int i = 0; i < iterations; i++) {
      incrementClock();
      Double index = new Double(ticks);
      buildQueue(index);
      buildLastQueue(index);
      topGroup.execute();
    }
  }
  */
}
