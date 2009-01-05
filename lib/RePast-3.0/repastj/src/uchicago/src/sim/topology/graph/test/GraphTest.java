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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.topology.Context;
import uchicago.src.sim.topology.DefaultContext;
import uchicago.src.sim.topology.graph.EdgeType;
import uchicago.src.sim.topology.graph.Graph;
import uchicago.src.sim.topology.graph.UndirectedGraph;

/**
 * @author Tom Howe
 * @version $Revision$
 */
public class GraphTest extends TestCase{
  class TestObject{
    String name;
    public TestObject(String name){
      this.name = name;
    }

    public String toString(){
      return name;
    }
  }

  Context c = new DefaultContext();
  TestObject one = new TestObject("1");
  TestObject two = new TestObject("2");
  TestObject three = new TestObject("3");
  TestObject four = new TestObject("4");
  TestObject five = new TestObject("5");
  TestObject six = new TestObject("6");
  TestObject seven = new TestObject("7");
  TestObject eight = new TestObject("8");
  TestObject nine = new TestObject("9");
  TestObject ten = new TestObject("10");

  public GraphTest(String name){
    super(name);
    setup();
  }

  public void setup(){
    c.add(one);
    c.add(two);
    c.add(three);
    c.add(four);
    c.add(five);
    c.add(six);
    c.add(seven);
    c.add(eight);
    c.add(nine);
    c.add(ten);
    c.addRelationType(new UndirectedGraph(c, "TestRelation"));
    c.addRelation(one, three, "TestRelation", 1);
    c.addRelation(three, four, "TestRelation", 1);
    c.addRelation(one, seven, "TestRelation", 1);
    c.addRelation(seven, eight, "TestRelation", 1);
    c.addRelation(eight, ten, "TestRelation", 1);
    c.addRelation(three, five, "TestRelation", 1);
    c.addRelation(two, nine, "TestRelation", 1);
    c.addRelation(two, five, "TestRelation", 1);
    c.addRelation(six, five, "TestRelation", 1);
  }

  public void testNeighbors(){
    System.out.println("One test");
    List l = c.getRelated(one, "TestRelation", 1);
    assertEquals(2, l.size());
    List tester = new ArrayList();
    tester.add(three);
    tester.add(seven);
    for(int i = 0 ; i < l.size() ; i++){
      assertEquals(l.get(i), tester.get(i));
    }
  }

  public void testExtentNeighbors(){
    System.out.println("Three test");
    List l = c.getRelated(one, "TestRelation", 3);
    assertEquals(8, l.size());
    List tester = new ArrayList();
    tester.add(three);
    tester.add(seven);
    tester.add(four);
    tester.add(five);
    tester.add(eight);
    tester.add(two);
    tester.add(six);
    tester.add(ten);
    for(int i = 0 ; i < l.size() ; i++){
      assertEquals(l.get(i), tester.get(i));
    }
  }

  public void testGraph(){
    Graph g = (Graph) c.getRelationTopology("TestRelation");
    int degree = g.degree(one, EdgeType.ALL);
    assertEquals(2, degree);
    degree = g.degree(six, EdgeType.ALL);
    assertEquals(1, degree);
  }

  public void testAdjacent(){
    assertTrue(c.areRelated(one, three, 1, "TestRelation"));
  }

  public void testRemove(){
    c.removeRelation(two, five, "TestRelation");
    assertTrue(!c.areRelated(one, five, 1, "TestRelation"));
    c.addRelation(one, five, "TestRelation", 1);
  }

  public static Test suite() {
    return new TestSuite(uchicago.src.sim.topology.graph.test.GraphTest.class);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
