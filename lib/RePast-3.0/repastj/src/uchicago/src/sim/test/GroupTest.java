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


