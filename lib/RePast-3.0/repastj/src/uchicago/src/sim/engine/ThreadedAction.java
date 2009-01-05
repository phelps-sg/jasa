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
 * A wrapper that executes BasicActions within a Thread.
 *
 * @version $Revision$ $Date$
 */
public class ThreadedAction extends BasicAction {

  private BasicAction action;
  private boolean done = false;
  private Thread runner;
  private double duration;

  class ThreadedAtUpdater implements Updater {

    private boolean done = false;

    public void update(ActionQueue queue) {
      if (!done) {
        nextTime += duration;
        queue.toss(ThreadedAction.this);
        done = true;
      }
    }
  }

  class ThreadedIntervalUpdater implements Updater {

    private boolean done = false;

    public void update(ActionQueue queue) {
      if (!done) {
        nextTime += duration;
        System.out.println("duration.nextTime = " + nextTime);
        queue.toss(ThreadedAction.this);
        done = true;
      }  else {
        // we want to execute this every interval, but we also want to
        // add the waiting "execute" to the schedule queue at duration which
        // increments nextTime. But we want to calculate the interval from
        // the original nextTime. Subtracting duration allows us to do this.
        nextTime += intervalTime - duration;
        System.out.println("interval.nextTime = " + nextTime);
        queue.toss(ThreadedAction.this);
        done = false;
      }
    }
  }

  static class ActionRunner implements Runnable {

    BasicAction baction;
    ThreadedAction owner;

   public ActionRunner(BasicAction action, ThreadedAction owner) {
     baction = action;
     this.owner = owner;
   }

    public void run() {
      baction.execute();
      owner.done();
    }
  }

  public ThreadedAction(BasicAction action, double duration) {
    super();
    this.action = action;
    this.duration = duration;
  }

  public synchronized void done() {
    done = true;
    notifyAll();
  }

  public synchronized boolean isDone() {
    return done;
  }

  /**
   * Sets the updater for this ThreadedAction. An updater controlls
   * how a BasicAction is readded to a schedule queue. Valid types are:
   * BasicActoin.INTERVAL_UPDATER, and BasicAction.ONE_TIME_UPDATER.
   */
  void setUpdater(int type) {
    if (type == INTERVAL_UPDATER) updater = new ThreadedIntervalUpdater();
    else if (type == ONE_TIME_UPDATER) updater = new ThreadedAtUpdater();
    else throw new IllegalArgumentException("Illegal Updater type");
  }

  public void execute() {
    if (runner == null) {
      runner = new Thread(new ActionRunner(action, this));
      runner.setName("ThreadedAction Thread");
      runner.start();
    } else {
      try {
        synchronized (this) {
          while (!done) wait();
        }

        // set runner to null, so if this gets rescheduled by the updater
        // then it will run the background thread again.
        runner = null;
        done = false;

      } catch (InterruptedException ex) {
        runner.interrupt();
      }
    }
  }
}
