package uchicago.src.sim.engine;

import java.util.ArrayList;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.util.Random;

/**
 * Tests randomization of list in generated basic actions.
 */
public class ScheduleListTest extends TestCase {
  
  private ArrayList list = new ArrayList();
  private Schedule schedule;
  private String order;
  private String newOrder;

  public ScheduleListTest(String name) {
    super(name);
  }

  public void setUp() {
    Random.createUniform();
    schedule = new Schedule();
    
    list = new ArrayList();
    
    for (int i = 0; i < 20; i++) {
      list.add(new ScheduleTestAgent(i, schedule));
    }

    order = new String();
    for (int i = 0; i < 20; i++) {
      ScheduleTestAgent a = (ScheduleTestAgent)list.get(i);
      order += a.getId() + " ";
    }

    //System.out.println("order: " + order);
    
  }

  private String getTicks() {
    return ((ScheduleTestAgent)list.get(0)).getTicks();
  }

  private void orderComp() {
    orderComp(10);
  }

  private void orderComp(int runTime) {
    runSchedule(runTime);
    newOrder = "";
    for (int i = 0; i < 20; i++) {
      ScheduleTestAgent a = (ScheduleTestAgent)list.get(i);
      newOrder += a.getId() + " ";
    }
  }

  public void testAtRnd() {
    schedule.scheduleActionAtRnd(3, list, "printId");
    orderComp();
    assertTrue("shuffled list == unshuffled list", !(newOrder.equals(order)));
    //System.out.println("execution ticks: " + getTicks());
    assertTrue("3.0 ".equals(getTicks()));
  }

  public void testAtRndLast() {
    schedule.scheduleActionAtRnd(4, list, "printId", Schedule.LAST);
    orderComp();
    assertTrue("shuffled list == unshuffled list", !(newOrder.equals(order)));
    //System.out.println("execution ticks: " + getTicks());
    assertTrue("4.0 ".equals(getTicks()));
  }

  public void testIntervalRnd() {
    schedule.scheduleActionAtIntervalRnd(3, list, "printId");
    orderComp(3);
    assertTrue("shuffled list == unshuffled list", !(newOrder.equals(order)));
    //System.out.println("execution ticks: " + getTicks());
    assertTrue("3.0 6.0 9.0 ".equals(getTicks()));
  }

  public void testIntervalLastRnd() {
    schedule.scheduleActionAtIntervalRnd(3, list, "printId", Schedule.LAST);
    orderComp(3);
    assertTrue("shuffled list == unshuffled list", !(newOrder.equals(order)));
    //System.out.println("execution ticks: " + getTicks());
    assertTrue("3.0 6.0 9.0 ".equals(getTicks()));
  }

   public void testBeginningRnd() {
    schedule.scheduleActionBeginningRnd(1, list, "printId");
    orderComp();
    assertTrue("shuffled list == unshuffled list", !(newOrder.equals(order)));
    //System.out.println("execution ticks: " + getTicks());
    assertTrue("1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 10.0 ".equals(getTicks()));
  }
    

  public void testOrder() {
    String newOrder = "";
    for (int i = 0; i < 20; i++) {
      ScheduleTestAgent a = (ScheduleTestAgent)list.get(i);
      newOrder += a.getId() + " ";
    }
    assertTrue("unshuffled != unshuffled", order.equals(newOrder));
  }

  public void runSchedule(int runTime) {
    //System.out.print("new order: ");
    for (int i = 0; i < runTime; i++) {
      schedule.execute();
    }
  }

  public void runSchedule() {
    runSchedule(10);
  }

  public static junit.framework.Test suite() {
    return new TestSuite(uchicago.src.sim.engine.ScheduleListTest.class);
  }
}
