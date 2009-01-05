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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import uchicago.src.sim.topology.graph.EdgeType;
import uchicago.src.sim.topology.graph.Graph;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jul 3, 2003
 * Time: 6:09:35 PM
 * To change this template use Options | File Templates.
 */
public class DepthFirstSearch implements Iterator, Serializable {
	static final long serialVersionUID = -8943919637144475940L;
	private Graph graph;
	private Stack gray;
	private Set white;
	private Map classifications;
	boolean acyclic = true;

	/**
	 * This is the general constructor for when you want to
	 * construct a DepthFirstSearch on an entire graph.
	 * This guarantees to return the nodes in the order that
	 * the links and components were added.
	 * 
	 * @param g The graph to search
	 */
	public DepthFirstSearch(Graph g) {
		Object o = g.getNodes().iterator().next();
		init(g, o);
	}

	/**
	 * This constructor should be used if you want to start
	 * a search from a particular node.  This is useful if
	 * you are interested in traversing a component.
	 * 
	 * @param g The graph to search.
	 * @param o The object to start the search
	 */
	public DepthFirstSearch(Graph g, Object o) {
		init(g, o);
	}

	private void init(Graph g, Object o) {
		graph = g;
		gray = new Stack();
		classifications = new HashMap();
		white = new HashSet(g.getNodes());
		gray.push(o);
		white.remove(o);
		classifications.put(o, EdgeClassification.FORWARD_EDGE);
	}

	/**
	 * This returns the classification of an edge between
	 * nodes (u,v).  This is useful in determining if a graph
	 * is acyclic, for example.
	 *
	 * @param node
	 * @return
	 */
	public EdgeClassification getClassification(Object node) {
		//TODO: make this for an edge, not a node.
		return (EdgeClassification) classifications.get(node);
	}

	/**
	 * Determine if there are any non-explored nodes in the
	 * graph.
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (gray.isEmpty() && !white.isEmpty()) {
			Object next = white.iterator().next();
			gray.push(next);
			white.remove(next);
		}
		return !gray.isEmpty();
	}

	/**
	 * Fully explore and return the next node in the graph.
	 * 
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		Object next = gray.pop();
		List adj = graph.getAdjacentNodes(next, EdgeType.ALL);
		for (int i = adj.size() - 1; i >= 0; i--) {
			Object node = adj.get(i);
			if (gray.contains(node)) {
				classifications.put(node, EdgeClassification.BACK_EDGE);
			} else if (white.contains(node)) {
				gray.push(node);
				white.remove(node);
				classifications.put(node, EdgeClassification.TREE_EDGE);
			} else {
				classifications.put(node, EdgeClassification.FORWARD_EDGE);
			}
		}
		return next;
	}

	/**
	 * Unsupported Operation
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}