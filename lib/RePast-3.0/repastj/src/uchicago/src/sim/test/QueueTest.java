/*$$
 * Copyright (c) 2004, Repast Organization for Architecture and Design (ROAD)
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
 * Neither the name of the ROAD nor the names of its
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
package uchicago.src.sim.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.engine.ActionQueue;
import uchicago.src.sim.engine.BasicAction;

/**
 * Tests ActionQueue.
 */
public class QueueTest extends TestCase {

  private ActionQueue queue;

  class TestQueueAction extends BasicAction {

    public void execute() {}
  }

  public QueueTest(String name) {
    super(name);
  }

  public void setUp() {
    queue = new ActionQueue(10);
    BasicAction action = new TestQueueAction();
    action.setNextTime(3.2);
    queue.insert(action);

    action = new TestQueueAction();
    action.setNextTime(1.3);
    queue.insert(action);

    action = new TestQueueAction();
    action.setNextTime(100);
    queue.insert(action);

    action = new TestQueueAction();
    action.setNextTime(.2343);
    queue.insert(action);

    action = new TestQueueAction();
    action.setNextTime(10.3);
    queue.insert(action);
  }

  public void testSize() {
    assertTrue(queue.size() == 5);
    ActionQueue anotherQueue = new ActionQueue();
    assertTrue(anotherQueue.size() == 0);
  }

  public void testIsEmpty() {
    assertTrue(!queue.isEmpty());
    ActionQueue anotherQueue = new ActionQueue();
    assertTrue(anotherQueue.isEmpty());
  }

  public void testGetMin() {
    BasicAction a = queue.popMin();
    assertEquals(.2343, a.getNextTime(), 0);

    a = queue.popMin();
    assertEquals(1.3, a.getNextTime(), 0);

    a = queue.popMin();
    assertEquals(3.2, a.getNextTime(), 0);

    a = queue.popMin();
    assertEquals(10.3, a.getNextTime(), 0);

    a = queue.popMin();
    assertEquals(100, a.getNextTime(), 0);

    assertEquals(0, queue.size());
    assertTrue(queue.isEmpty());
  }

  public void testPeekMin() {
    BasicAction a = queue.peekMin();
    assertEquals(.2343, a.getNextTime(), 0);

    a = queue.popMin();
    assertEquals(.2343, a.getNextTime(), 0);
  }

  public void testToss() {
    BasicAction action = new TestQueueAction();
    action.setNextTime(-32);
    queue.toss(action);

    BasicAction a = queue.popMin();
    assertEquals(-32, a.getNextTime(), 0);

    a = queue.popMin();
    assertEquals(.2343, a.getNextTime(), 0);

    a = queue.popMin();
    assertEquals(1.3, a.getNextTime(), 0);

    action = new TestQueueAction();
    action.setNextTime(-32);
    queue.toss(action);

    action = new TestQueueAction();
    action.setNextTime(40);
    queue.insert(action);

    a = queue.popMin();
    assertEquals(-32, a.getNextTime(), 0);

    a = queue.popMin();
    assertEquals(3.2, a.getNextTime(), 0);

    a = queue.popMin();
    assertEquals(10.3, a.getNextTime(), 0);

    a = queue.popMin();
    assertEquals(40, a.getNextTime(), 0);

    a = queue.popMin();
    assertEquals(100, a.getNextTime(), 0);

    assertEquals(0, queue.size());
    assertTrue(queue.isEmpty());
  }

  public void testClear() {
    queue.clear();
    BasicAction action = new TestQueueAction();
    action.setNextTime(3.2);
    queue.insert(action);

    action = new TestQueueAction();
    action.setNextTime(1.3);
    queue.insert(action);

    assertEquals(2, queue.size());

    BasicAction a = queue.popMin();
    assertEquals(1.3, a.getNextTime(), 0);

    a = queue.popMin();
    assertEquals(3.2, a.getNextTime(), 0);
  }

  /*
  class EVAction extends EveryTickAction {

    String message;

    public EVAction(String message) {
      this.message = message;
    }

    public void execute() {
      System.out.println(message);
    }
  }

  class AtAction extends SingleTickAction {

    String message;
    Schedule schedule;

    public AtAction(String message, Schedule schedule) {
      this.message = message;
      this.schedule = schedule;
    }

    public void execute() {
      System.out.println(message);
      schedule.scheduleActionAt(schedule.getCurrentTick() + .3, this);
    }
  }

  class IAction extends IntervalAction {

    String message;

    public IAction(double interval, String message) {
      super(interval);
      this.message = message;
    }

    public void execute() {
      System.out.println(message);
    }
  }

  public void testScheduleEveryTick() {
    Schedule schedule = new Schedule(.2);
    schedule.scheduleActionEveryTick(1, new EVAction("1 start"));
    schedule.scheduleActionEveryTick(.4, new EVAction(".4 start"));
    schedule.scheduleActionAtInterval(.5, new IAction(.6, "i action - .5 start"));
    schedule.scheduleActionAt(.3, new AtAction("At Action", schedule));

    for (int i = 0; i < 10; i++) {
      schedule.execute();
      System.out.println("^^^^^^ tick: " + schedule.getCurrentTick() + " ^^^^^^");

    }

  }
  */


  public static junit.framework.Test suite() {
    return new TestSuite(uchicago.src.sim.test.QueueTest.class);
  }
}


