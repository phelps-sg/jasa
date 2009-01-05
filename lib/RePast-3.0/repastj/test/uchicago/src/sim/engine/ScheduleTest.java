package uchicago.src.sim.engine;

import java.util.ArrayList;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import cern.colt.list.DoubleArrayList;

/**
 * Tests Schedule methods. This works by executing actions at ticks
 * and checking that those actions occur when they should.
 */
public class ScheduleTest extends TestCase {

  Schedule sch;
  Action action;
  static DoubleArrayList results;
  static ArrayList actionList;

  public ScheduleTest(String name) {
    super(name);
  }

  public void setUp() {
    results = new DoubleArrayList();
    actionList = new ArrayList();
    sch = new Schedule();
    action = new Action(sch);
  }

  /**
   * Tests simple actionAt with a double.
   */
  public void testAt() {
    sch.scheduleActionAt(3.45, action);
    sch.execute();
    assertEquals(3.45, action.at, 0.0);
    assertEquals(3.45, results.get(0), 0.0);
  }


  /*
   * Tests simple actionBeginning at 1.
   */
  public void testBeginning() {
    sch.scheduleActionBeginning(1, action);
    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    for (int i = 0; i < 10; i++) {
      assertEquals(i + 1, results.get(i), 0.0);
    }
  }

  public void testDuration() {
    sch.scheduleActionBeginning(1, action);

    sch.scheduleActionAt(1, new BasicAction() {
      public void execute() {
        try {
          for (int i = 0; i < 100000L; i++) {

            if (Thread.currentThread().isInterrupted()) break;
            //Thread.yield();
            //Thread.sleep(100);
            if (i % 100 == 0) {
              //System.out.println(i + ": " + sch.getCurrentTimeDouble());
            }
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }, 4);

    for (int i = 0; i < 20; i++) {
      sch.execute();
    }
  }

  /*
  public void testDurationInterval() {
    sch.scheduleActionBeginning(1, action);
    sch.scheduleActionAtInterval(4, new BasicAction() {
      public void execute() {
        try {
          for (int i = 0; i < 100000L; i++) {

            if (Thread.currentThread().isInterrupted()) break;
            //Thread.yield();
            //Thread.sleep(100);
            if (i % 100 == 0) {
              System.out.println(i + ": " + sch.getCurrentTimeDouble());
            }
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }, 3);

    for (int i = 0; i < 20; i++) {
      sch.execute();
    }
  }
  */

  /*
   * Tests beginning but with an interval of .4. So, actions scheduled
   * to execute every interval will execute every .4.
   */
  public void testBeginningStepDouble() {
    //System.out.println("\nstepDouble");
    sch = new Schedule(.4);
    action = new Action(sch);
    sch.scheduleActionBeginning(1, action);
    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    assertEquals(1.0, results.get(0), 0.0);
    //System.out.println("interval of .4");

    for (int i = 1; i < 10; i++) {
      assertEquals(1 + (i * .4), results.get(i), 0.01);
      //System.out.println("result " + i + ": " + results.get(i));

    }
  }

  /**
   * Tests beginning at 3 with a schedule interval of 2. First action
   * at 3 then every 2 after.
   */
  public void testBeginningStepInt() {
    //System.out.println("\nStepInt");
    sch = new Schedule(2);
    action = new Action(sch);
    sch.scheduleActionBeginning(3, action);
    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    for (int i = 0; i < 10; i++) {
      assertEquals(3 + (i * 2), results.get(i), 0.0);
    }
  }

  /**
   * Tests actionAtInterval of .4. Executes every .4. This calls
   * schedule.execute() first. As nothing is scheduled for execution
   * then time is not incremented. Calling it makes sure that this is
   * the case.
   */
  public void testInterval() {
    //System.out.println("\ninterval pre execute");
    sch.execute();
    sch.scheduleActionAtInterval(.4, action);
    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    for (int i = 0; i < 10; i++) {
      assertEquals(.4 * (i + 1), results.get(i), 0.01);
    }
  }

  /**
   * Tests actionAt with CONCURRENT.
   */
  public void testAtCon() {
    //System.out.println("atCon");
    Action otherAction = new Action(sch);
    otherAction.tag = 999;
    otherAction.setName("999");
    sch.scheduleActionBeginning(1, action);
    sch.scheduleActionAt(2, otherAction);

    for (int j = 0; j < 25; j++) {
      for (int i = 0; i < 10; i++) {
        sch.execute();
      }

      assertEquals(1, results.get(0), 0.0);
      double res = results.get(1);
      assertTrue(res == 999 || res == 2.0);

      res = results.get(2);
      assertTrue(res == 999 || res == 2.0);

      //System.out.println("results = " + results);
      for (int i = 3; i < 11; i++) {
        assertEquals(i, results.get(i), 0.0);
      }


      setUp();
      otherAction = new Action(sch);
      otherAction.tag = 999;
      otherAction.setName("999");
      sch.scheduleActionBeginning(1, action);
      sch.scheduleActionAt(2, otherAction);
    }
  }

  /*
   * Tests beginning but beginning with at double.
   * With a default step size of 1.0 this will execute at .3 and then
   * 1.3, 2.3, etc.
   */
  public void testBeginningD() {
    sch.scheduleActionBeginning(.3, action);
    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    for (int i = 0; i < 10; i++) {
      assertEquals(.3 + i, results.get(i), 0.0);
    }
  }

  /**
   * Tests beginning in conjunction with at.
   */
  public void testBeginningAt() {
    Action a = new Action(sch);
    sch.scheduleActionBeginning(1, action);
    sch.scheduleActionAt(5.43, a);
    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    for (int i = 0; i < 5; i++) {
      assertEquals(i + 1, results.get(i), 0.0);
    }

    assertEquals(5.43, results.get(5), 0.0);

    for (int i = 6; i < results.size(); i++) {
      assertEquals(i, results.get(i), 0.0);
    }
  }

  /**
   * Test beginning in conjunction with interval.
   */
  public void testBegInterval() {
    //System.out.println("\nbeg and interval");
    action.setName("beg action");
    Action a = new Action(sch);
    a.setName("a - interval 2");
    Action b = new Action(sch);
    b.setName("b - interval 4");

    sch.scheduleActionAtInterval(2, a);
    sch.scheduleActionAtInterval(4, b);
    sch.scheduleActionBeginning(1, action);

    for (int i = 0; i < 12; i++) {
      sch.execute();
    }

    assertEquals(actionList.get(0), action);
    Object o = actionList.get(1);
    assertTrue(o.equals(action) || o.equals(a));
    o = actionList.get(2);
    assertTrue(o.equals(action) || o.equals(a));

    o = actionList.get(3);
    assertTrue(o.equals(action));

    o = actionList.get(4);
    assertTrue(o.equals(action) || o.equals(a) || o.equals(b));
    o = actionList.get(5);
    assertTrue(o.equals(action) || o.equals(a) || o.equals(b));
    o = actionList.get(6);
    assertTrue(o.equals(action) || o.equals(a) || o.equals(b));

    actionList.get(8);
    assertTrue(o.equals(action) || o.equals(a));
    o = actionList.get(9);
    assertTrue(o.equals(action) || o.equals(a));

    o = actionList.get(10);
    assertTrue(o.equals(action));

    o = actionList.get(11);
    assertTrue(o.equals(action) || o.equals(a) || o.equals(b));
    o = actionList.get(12);
    assertTrue(o.equals(action) || o.equals(a) || o.equals(b));
    o = actionList.get(13);
    assertTrue(o.equals(action) || o.equals(a) || o.equals(b));

    o = actionList.get(14);
    assertTrue(o.equals(action));

    actionList.get(15);
    assertTrue(o.equals(action) || o.equals(a));
    o = actionList.get(16);
    assertTrue(o.equals(action) || o.equals(a));

    o = actionList.get(17);
    assertTrue(o.equals(action));

    o = actionList.get(18);
    assertTrue(o.equals(action) || o.equals(a) || o.equals(b));
    o = actionList.get(19);
    assertTrue(o.equals(action) || o.equals(a) || o.equals(b));
    o = actionList.get(20);
    assertTrue(o.equals(action) || o.equals(a) || o.equals(b));
  }

  /**
   * Tests interval with a generated action.
   */
  public void testIntervalWithGenAction() {
    sch.scheduleActionAtInterval(3.2, this, "actionMethod");
    sch.execute();
    sch.execute();

    assertEquals(3.2, results.get(0), 0.01);
    assertEquals(6.4, results.get(1), 0.01);
  }

  /**
   * Tests simple actionAt with a double.
   */
  public void testAtGen() {
    sch.scheduleActionAt(3.45, this, "actionMethod");
    sch.execute();
    assertEquals(3.45, results.get(0), 0.0);
  }

  /**
   * Tests simple actionBeginning at 1.
   */
  public void testBeginningGen() {
    sch.scheduleActionBeginning(1, this, "actionMethod");
    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    for (int i = 0; i < 10; i++) {
      assertEquals(i + 1, results.get(i), 0.0);
    }
  }

  /*
   * Tests beginning but with an interval of .4. So, actions scheduled
   * to execute every interval will execute every .4.
   */
  public void testBeginningStepDoubleGen() {
    sch = new Schedule(.4);
    sch.scheduleActionBeginning(1, this, "actionMethod");
    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    assertEquals(1.0, results.get(0), 0.0);
    //System.out.println("interval of .4");

    for (int i = 1; i < 10; i++) {
      assertEquals(1 + (i * .4), results.get(i), 0.01);
      //System.out.println("result " + i + ": " + results.get(i));

    }
  }

  /**
   * Tests beginning at 3 with a schedule interval of 2. First action
   * at 3 then every 2 after.
   */
  public void testBeginningStepIntGen() {
    sch = new Schedule(2);
    sch.scheduleActionBeginning(3, this, "actionMethod");
    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    for (int i = 0; i < 10; i++) {
      assertEquals(3 + (i * 2), results.get(i), 0.0);
    }
  }

  /**
   * Tests actionAtInterval of .4. Executes every .4. This calls
   * schedule.execute() first. As nothing is scheduled for execution
   * then time is not incremented. Calling it makes sure that this is
   * the case.
   */
  public void testIntervalGen() {
    sch.execute();
    sch.scheduleActionAtInterval(.4, this, "actionMethod");
    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    for (int i = 0; i < 10; i++) {
      assertEquals(.4 * (i + 1), results.get(i), 0.01);
    }
  }

  /**
   * Tests actionAt with LAST parameter.
   */
  public void testAtLast() {
    Action otherAction = new Action(sch);
    otherAction.tag = 999;

    Action action1 = new Action(sch);
    action1.tag = 222;

    Action action2 = new Action(sch);
    action2.tag = 444;

    sch.scheduleActionBeginning(1, action);
    sch.scheduleActionAt(2, otherAction, Schedule.LAST);
    sch.scheduleActionAt(2, action1, Schedule.LAST);
    sch.scheduleActionAt(2, action2, Schedule.LAST);

    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    for (int i = 0; i < 2; i++) {
      assertEquals(i + 1, results.get(i), 0.0);
    }

    assertEquals(999, results.get(2), 0.0);
    assertEquals(222, results.get(3), 0.0);
    assertEquals(444, results.get(4), 0.0);

    for (int i = 5; i < 13; i++) {
      assertEquals(i - 2, results.get(i), 0.0);
    }
  }

  /**
   * Tests actionAtInterval with LAST parameter.
   */
  public void testAtIntervalLast() {
    Action otherAction = new Action(sch);
    otherAction.tag = 999;

    Action action1 = new Action(sch);
    action1.tag = 222;

    Action action2 = new Action(sch);
    action2.tag = 444;

    sch.scheduleActionBeginning(1, action);
    sch.scheduleActionAtInterval(4, otherAction, Schedule.LAST);
    sch.scheduleActionAtInterval(4, action1, Schedule.LAST);
    sch.scheduleActionAtInterval(4, action2, Schedule.LAST);

    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    //System.out.println("results = " + results);

    for (int i = 0; i < 4; i++) {
      assertEquals(i + 1, results.get(i), 0.0);
    }

    assertEquals(999, results.get(4), 0.0);
    assertEquals(222, results.get(5), 0.0);
    assertEquals(444, results.get(6), 0.0);

    for (int i = 7; i < 11; i++) {
      assertEquals(i - 2, results.get(i), 0.0);
    }

    assertEquals(999, results.get(11), 0.0);
    assertEquals(222, results.get(12), 0.0);
    assertEquals(444, results.get(13), 0.0);

    assertEquals(9, results.get(14), 0);
    assertEquals(10, results.get(15), 0);
  }

  /**
   * Tests actionAt with LAST parameter.
   */
  public void testAtLastGen() {
    sch.scheduleActionBeginning(1, this, "actionMethod");
    sch.scheduleActionAt(2, this, "actionMethodTag", Schedule.LAST);

    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    for (int i = 0; i < 2; i++) {
      assertEquals(i + 1, results.get(i), 0.0);
    }

    assertEquals(999, results.get(2), 0.0);

    for (int i = 3; i < 11; i++) {
      assertEquals(i, results.get(i), 0.0);
    }
  }

  /**
   * Tests actionAt with CONCURRENT.
   */
  public void testAtConGen() {
    sch.scheduleActionBeginning(1, this, "actionMethod");
    sch.scheduleActionAt(2, this, "actionMethodTag");

    for (int j = 0; j < 25; j++) {
      for (int i = 0; i < 10; i++) {
        sch.execute();
      }

      assertEquals(1, results.get(0), 0.0);
      double res = results.get(1);
      assertTrue(res == 999 || res == 2.0);

      res = results.get(2);
      assertTrue(res == 999 || res == 2.0);


      for (int i = 3; i < 11; i++) {
        assertEquals(i, results.get(i), 0.0);
      }

      setUp();
      sch.scheduleActionBeginning(1, this, "actionMethod");
      sch.scheduleActionAt(2, this, "actionMethodTag");
    }
  }

  /*
   * Tests beginning but beginning with at double.
   * With a default step size of 1.0 this will execute at .3 and then
   * 1.3, 2.3, etc.
   */
  public void testBeginningDGen() {
    sch.scheduleActionBeginning(.3, this, "actionMethod");
    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    for (int i = 0; i < 10; i++) {
      assertEquals(.3 + i, results.get(i), 0.0);
    }
  }

  /**
   * Tests beginning in conjunction with at.
   */
  public void testBeginningAtGen() {
    sch.scheduleActionBeginning(1, this, "actionMethod");
    sch.scheduleActionAt(5.43, this, "actionMethod");
    for (int i = 0; i < 10; i++) {
      sch.execute();
    }

    for (int i = 0; i < 5; i++) {
      assertEquals(i + 1, results.get(i), 0.0);
    }

    assertEquals(5.43, results.get(5), 0.0);

    for (int i = 6; i < results.size(); i++) {
      assertEquals(i, results.get(i), 0.0);
    }
  }


  /**
   * Tests removing and adding an action. Time should not be incremented
   * while the schedule has no scheduled actions.
   */
  public void testRemoveAction() {
    BasicAction a = sch.scheduleActionBeginning(1, this, "actionMethod");
    sch.execute();
    assertEquals(1, results.size());
    sch.removeAction(a);
    sch.execute();
    assertEquals(1, results.size());


    sch = new Schedule();
    a = sch.scheduleActionBeginning(1, this, "actionMethod");
    results.clear();

    for (int i = 0; i < 15; i++) {
      sch.execute();
      if (i == 6) {
        sch.removeAction(a);
      }
      if (i == 10) {
        sch.scheduleActionBeginning(sch.getCurrentTime() + 1, a);
      }
    }

    assertEquals(11, results.size());

    for (int i = 0; i < 11; i++) {
      assertEquals(i + 1, results.get(i), 0.0);
    }
  }

  /*
  public void testSem() {
    System.out.println();

    for (int i = 0; i < 10; i++) {
      sch.execute();
      System.out.println("time_1: " + sch.getCurrentTimeDouble());
    }

    sch.scheduleActionAt(3.54, action);
    sch.execute();
    System.out.println("action at: " + action.at);

    System.out.println("time_1: " + sch.getCurrentTimeDouble());

    sch = new Schedule(.1);
    for (int i = 0; i < 10; i++) {
      sch.execute();
      System.out.println("time_2: " + sch.getCurrentTimeDouble());
    }

    sch = new Schedule();
    sch.scheduleActionAt(3.54, action);
    sch.execute();
    System.out.println("time_3: " + sch.getCurrentTimeDouble());
  }
  */

  public void actionMethod() {
    results.add(sch.getCurrentTime());
  }

  public void actionMethodTag() {
    results.add(999);
  }


  public static junit.framework.Test suite() {
    return new TestSuite(uchicago.src.sim.engine.ScheduleTest.class);
  }
}

/**
 * When executed this Action records the time it was executed in
 * ScheduleTest.results.
 */
class Action extends BasicAction {

  double at;
  Schedule sch;
  double tag = -1;

  public Action(Schedule s) {
    sch = s;
  }

  public void execute() {
    at = sch.getCurrentTime();
    if (tag == -1)

      ScheduleTest.results.add(at);
    else
      ScheduleTest.results.add(tag);
    //System.out.println("Action " + getName() + " executed at = " + at);
    ScheduleTest.actionList.add(this);
  }
}
