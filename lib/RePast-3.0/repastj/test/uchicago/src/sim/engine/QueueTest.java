package uchicago.src.sim.engine;

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
    return new TestSuite(uchicago.src.sim.engine.QueueTest.class);
  }
}


