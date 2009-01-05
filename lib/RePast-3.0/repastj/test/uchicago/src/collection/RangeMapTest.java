package uchicago.src.collection;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.collection.RangeMap;

/**
 * Tests RangeMap.
 */
public class RangeMapTest extends TestCase {

  private RangeMap map;

  public RangeMapTest(String name) {
    super(name);
  }

  public void setUp() {
    map = new RangeMap();
    map.put(1, new Integer(1));
    map.put(2, new Integer(2));
    map.put(3, new Integer(3));
    map.put(10, new Integer(10));
    map.put(0, new Integer(0));
    map.put(-3.2, new Double(-3.2));
  }

  /*
  public void testPrint() {
    map.print();
  }
  */
  
  
  public void testGet() {
    Integer i = (Integer)map.get(1.5);
    assertEquals(1, i.intValue());

    i = (Integer)map.get(.6);
    assertEquals(0, i.intValue());

    i = (Integer)map.get(7);
    assertEquals(3, i.intValue());

    i = (Integer)map.get(11);
    assertEquals(10, i.intValue());

    i = (Integer)map.get(10);
    assertEquals(10, i.intValue());

    i = (Integer)map.get(-5);
    assertEquals(null, i);

    i = (Integer)map.get(2.25);
    assertEquals(2, i.intValue());

    Double d = (Double)map.get(-1.2);
    assertEquals(-3.2, d.doubleValue(), 0);
  }

  public void testClear() {
    map.clear();
    Integer i = (Integer)map.get(2);
    assertEquals(null, i);
  }

  public void testClearAdd() {
    map.clear();
    setUp();
    testGet();
  }
  
  public void testIsEmpty() {
    assertTrue(!map.isEmpty());
    map.clear();
    assertTrue(map.isEmpty());
  }

  public static junit.framework.Test suite() {
    return new TestSuite(uchicago.src.collection.RangeMapTest.class);
  }
}
