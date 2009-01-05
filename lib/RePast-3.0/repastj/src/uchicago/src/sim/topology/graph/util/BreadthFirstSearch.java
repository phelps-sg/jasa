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
package uchicago.src.sim.topology.graph.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uchicago.src.sim.topology.graph.EdgeType;
import uchicago.src.sim.topology.graph.Graph;

/**
 * @author Tom Howe
 * @version $Revision$
 * @serial 979864041242197157L
 */
public class BreadthFirstSearch implements Iterator, Serializable {
	static final long serialVersionUID = 979864041242197157L;

	private Graph graph;
	private Queue gray;

	private Set black;
	private Set white;

	/**
	 * This constructor should be used if you want to start
	 * a search from a particular node.  This is useful if
	 * you are interested in traversing a component.
	 * 
	 * @param g The graph to search.
	 * @param o The object to start the search
	 */
	public BreadthFirstSearch(Graph g, Object o) {
		init(g, o);
	}
	
	/**
		 * This is the general constructor for when you want to
		 * construct a BreadthFirstSearch on an entire graph.
		 * This guarantees to return the nodes in the order that
		 * the links and components were added.
		 * 
		 * @param g The graph to search
		 */
	public BreadthFirstSearch(Graph g){
		init(g, g.getNodes().iterator().next());
	}
	
	private void init(Graph g, Object o){
		graph = g;
		white = new HashSet(g.getNodes());
		gray = new Queue();
		black = new HashSet();
		gray.enqueue(o);
		white.remove(o);
	}

	/**
	 * Determines if there is an unexplored node remaining in 
	 * the graph.
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if(!(gray.size() > 0 ) && !white.isEmpty()){
			Object next = white.iterator().next();
			gray.enqueue(next);
			white.remove(next);
		}		
		return gray.size() > 0;
	}

	/**
	 * Returns the next unexplored node in the graph.
	 * 
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		Object next = gray.dequeue();
		for (Iterator i = graph.getAdjacentNodes(next, EdgeType.ALL).iterator();
			i.hasNext();
			) {
			Object v = i.next();
			if (!gray.contains(v) && !black.contains(v)) {
				gray.enqueue(v);
			}
		}
		return next;
	}

	/**
	 * Unsupported Operation.
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
