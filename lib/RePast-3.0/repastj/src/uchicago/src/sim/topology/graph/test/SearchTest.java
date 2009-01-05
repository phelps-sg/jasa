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

import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.topology.graph.DirectedGraph;
import uchicago.src.sim.topology.graph.EdgeType;
import uchicago.src.sim.topology.graph.Graph;
import uchicago.src.sim.topology.graph.UndirectedGraph;
import uchicago.src.sim.topology.graph.util.ShortestPath;

/**
 * @author Tom Howe
 * @version $Revision$
 */
public class SearchTest extends TestCase {
	private Graph g;
	private DirectedGraph dg;
	Integer one = new Integer(1);
	Integer two = new Integer(2);
	Integer three = new Integer(3);
	Integer four = new Integer(4);
	Integer five = new Integer(5);
	Integer six = new Integer(6);
	Integer seven = new Integer(7);
	Integer eight = new Integer(8);
	Integer nine = new Integer(9);
	Integer ten = new Integer(10);
	Set s = new LinkedHashSet();

	public SearchTest(String name) {
		super(name);
		s.add(one);
		s.add(two);
		s.add(three);
		s.add(four);
		s.add(five);
		s.add(six);
		s.add(seven);
		s.add(eight);
		s.add(nine);
		s.add(ten);
	}

	public void setup() {
		g = new UndirectedGraph(s, "MyGraph");
		g.addRelation(two, four, 1);
		g.addRelation(three, five, 1);
		g.addRelation(two, five, 1);
		g.addRelation(nine, six, 1);
		g.addRelation(eight, nine, 1);
		g.addRelation(one, nine, 1);
		g.addRelation(one, ten, 1);
	}

	/*public void testBFS() {
		System.out.println("bfs");
		setup();
		BreadthFirstSearch search = new BreadthFirstSearch(g, one);
		assertEquals(search.next(), one);
		assertEquals(search.next(), nine);
		assertEquals(search.next(), ten);
		assertEquals(search.next(), six);
		assertEquals(search.next(), eight);
	}

	public void testDFS() {
		System.out.println("dfs");
		setup();
		DepthFirstSearch search = new DepthFirstSearch(g, one);
		if(search.hasNext()){
			//System.out.println("expected one");
			assertEquals(one, search.next());
		}
		//System.out.println(search.hasNext());
		if(search.hasNext()){
			//System.out.println("expected nine");
			assertEquals(nine, search.next());
		}
		if(search.hasNext()){
			//System.out.println("expected six");
			assertEquals(six, search.next());
		}
		if(search.hasNext()){
			//System.out.println("expected eight");
			assertEquals(eight, search.next());			
		}
		if(search.hasNext()){
			//System.out.println("expected ten");
			assertEquals(ten, search.next());
		}
	}

	public void testAcyclic() {
		System.out.println("acyclic");
		dg = new DirectedGraph(s, "DirectedGraph");
		dg.addRelation(one, three, 1);
		dg.addRelation(three, five, 1);
		dg.addRelation(five, one, 1);
		assertTrue(!dg.isAcyclic());
	}
	*/
	public void testShortestPath(){
		System.out.println("Shortest Path");
		dg = new DirectedGraph(s, "shortestPath");
		dg.addRelation(one,three, 1);
		dg.addRelation(three, two, 1);
		dg.addRelation(two, five, 1);
		dg.addRelation(one, four, 1);
		dg.addRelation(four, six, 1);
		dg.addRelation(six, two, 1);
		ShortestPath path = new ShortestPath(dg, one, EdgeType.OUT);
		while(path.hasNext()){
			System.out.println("next = " + path.next());
		}
	}

	/*public void testTopologicalSort(){
		System.out.println("topological sort");
		dg = new DirectedGraph(s, "DirectedGraph");
		dg.addRelation(one, two, 1);
		dg.addRelation(two, five, 1);
		dg.addRelation(five, three, 1);
		dg.addRelation(three, seven, 1);
		dg.addRelation(seven, ten, 1);
		dg.addRelation(ten, nine, 1);
		dg.addRelation(nine, eight, 1);
		dg.addRelation(eight, four,1);
		dg.addRelation(four, six,1);
		LinkedHashSet expectedList = new LinkedHashSet();
		expectedList.add(one);
		expectedList.add(two);
		expectedList.add(five);
		expectedList.add(three);
		expectedList.add(seven);
		expectedList.add(ten);
		expectedList.add(nine);
		expectedList.add(eight);
		expectedList.add(four);
		expectedList.add(six);
		//System.out.println("topological sort");
		assertTrue(dg.topologicalSort());		
		//System.out.println("dg = " + dg.size());
		assertTrue(dg.size() == expectedList.size());
		
		Iterator i1 = dg.iterator();
		Iterator i2 = expectedList.iterator();
		while(i1.hasNext() && i2.hasNext()){
			Object o1 = i1.next();
			Object o2 = i2.next();
			assertEquals(o1, o2);
		}
	}
	*/
	public static Test suite() {
		return new TestSuite(uchicago.src.sim.topology.graph.test.SearchTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
}
