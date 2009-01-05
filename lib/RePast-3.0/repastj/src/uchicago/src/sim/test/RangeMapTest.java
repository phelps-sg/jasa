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
    return new TestSuite(uchicago.src.sim.test.RangeMapTest.class);
  }
}
