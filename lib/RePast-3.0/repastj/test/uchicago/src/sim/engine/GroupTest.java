package uchicago.src.sim.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.engine.DefaultGroup;
import uchicago.src.sim.engine.Group;

/**
 * Tests ActionQueue.
 */
public class GroupTest extends TestCase {

  public static class GroupItem {
    int val;
    int runCount = 0;

    public GroupItem(int val) {
      this.val = val;
    }

    public void run() {
      runCount++;
    }

    public int getRunCount() {
      return runCount;
    }

    public int getVal() {
      return val;
    }

    public void setVal(int val) {
      this.val = val;
    }
  }

  private Group group;
  private ArrayList list;

  public GroupTest(String name) {
    super(name);
  }

  public void setUp() {
    list = new ArrayList();
    for (int i = 0; i < 10; i++) {
      list.add(new GroupItem(i));
    }

    group = new DefaultGroup(list, "run");
  }

  public void testSize() {
    assertEquals(10, group.size());
  }

  public void testIsEmpty() {
    assertTrue(!group.isEmpty());
    group.clear();
    assertTrue(group.isEmpty());
  }

  public void testAdd() {
    group.add(new GroupItem(11));
    group.add(new GroupItem(12));
    assertEquals(12, group.size());
  }

  public void testStep() {
    for (Iterator iter = group.iterator(); iter.hasNext(); ) {
      GroupItem gi = (GroupItem)iter.next();
      assertEquals(0, gi.getRunCount());
    }

    group.step();

    for (Iterator iter = group.iterator(); iter.hasNext(); ) {
      GroupItem gi = (GroupItem)iter.next();
      assertEquals(1, gi.getRunCount());
    }
  }

  public void testMin() {
    int min = (int)group.min("getVal");
    assertEquals(0, min);

    group.add(new GroupItem(-10));
    min = (int)group.min("getVal");
    assertEquals(-10, min);
  }

  public void testMax() {
    int max = (int)group.max("getVal");
    assertEquals(9, max);

    group.add(new GroupItem(100));
    max = (int)group.max("getVal");
    assertEquals(100, max);
  }

  public void testAvg() {
    double avg = group.avg("getVal");
    assertEquals(45d / 10, avg, 0d);
  }

  public void testMinItem() {
    GroupItem item = (GroupItem)group.iterator().next();
    List mins = group.getItemWithMinValue("getVal");
    assertEquals(1, mins.size());
    assertEquals(item, mins.get(0));

    GroupItem item2 = new GroupItem(0);
    group.add(item2);
    mins = group.getItemWithMinValue("getVal");
    assertEquals(2, mins.size());
    assertTrue(mins.contains(item));
    assertTrue(mins.contains(item2));

    GroupItem item3 = new GroupItem(-1000);
    group.add(item3);
    mins = group.getItemWithMinValue("getVal");
    assertEquals(1, mins.size());
    assertEquals(item3, mins.get(0));
  }

  public void testMaxItem() {
    GroupItem item3 = new GroupItem(1000);
    group.add(item3);
    List maxs = group.getItemWithMaxValue("getVal");
    assertEquals(1, maxs.size());
    assertEquals(item3, maxs.get(0));

    GroupItem item4 = new GroupItem(1000);
    group.add(item4);
    maxs = group.getItemWithMaxValue("getVal");
    assertEquals(2, maxs.size());
    assertTrue(maxs.contains(item3));
    assertTrue(maxs.contains(item4));
  }

  public void testCall() {
    for (Iterator iter = group.iterator(); iter.hasNext(); ) {
      GroupItem gi = (GroupItem)iter.next();
      assertEquals(0, gi.getRunCount());
    }

    group.call("run");

    for (Iterator iter = group.iterator(); iter.hasNext(); ) {
      GroupItem gi = (GroupItem)iter.next();
      assertEquals(1, gi.getRunCount());
    }
  }

  public static junit.framework.Test suite() {
    return new TestSuite(GroupTest.class);
  }
}


