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
package uchicago.src.sim.topology.graph.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.topology.graph.util.PriorityQueue;

/**
 * @author Tom Howe
 * @version $Revision$
 */
public class PriorityQueueTest extends TestCase{
  PriorityQueue i;
  public PriorityQueueTest(String s) {
    super(s);

  }

  private void setup(){
    i = new PriorityQueue();
    i.heapInsert(new Integer(1));
    i.heapInsert(new Integer(10));
    i.heapInsert(new Integer(25));
    i.heapInsert(new Integer(3));
    i.heapInsert(new Integer(14));
    i.heapInsert(new Integer(20));
    i.heapInsert(new Integer(4));
    i.heapInsert(new Integer(12));
    i.heapInsert(new Integer(100));
  }
  public void testQueue() {
    setup();
    assertEquals(new Integer(100), i.extractMax());
    assertEquals(new Integer(25), i.extractMax());
    assertEquals(new Integer(20), i.extractMax());
    assertEquals(new Integer(14), i.extractMax());
    assertEquals(new Integer(12), i.extractMax());
    assertEquals(new Integer(10), i.extractMax());
    assertEquals(new Integer(4), i.extractMax());
    assertEquals(new Integer(3), i.extractMax());
    assertEquals(new Integer(1), i.extractMax());
  }

  public static Test suite() {
    return new TestSuite(uchicago.src.sim.topology.graph.test.PriorityQueueTest.class);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
