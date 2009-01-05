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
package uchicago.src.sim.topology.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents an undirected graph.  As such each
 * edge has a strength of 1, so distance is equivelent to number of links
 * separating two objects.  If you don't need directional edges, you should use
 * this class as it has less overhead in terms of memory.
 *
 * @author Tom Howe
 * @version $Revision$
 */
public class UndirectedGraph extends AbstractGraph {
	private HashMap relations;
	private Set con;
	//private Edge query;

	/**
	 * Create a new DefaultGraph for a particular context with the given
	 * label.
	 *
	 * @param con
	 * @param type
	 */
	public UndirectedGraph(Set con, String type) {
		this.con = con;
		this.type = type;
		relations = new HashMap();
	}
	
	public UndirectedGraph(Set s){
		this(s, "");
	}

	/**
	 * Determine if the two objects are connected by an edge.  Since
	 * all edges in this graphtype have a strength of 1, this  is
	 * equivlent to a range query with distance = 1.
	 *
	 * @param element1
	 * @param element2
	 * @return
	 */

	public boolean areAdjacent(Object element1, Object element2) {
		if (getEdgeSet(element1).contains(getQuery(element1, element2))) {
			return true;
		}
		return false;
	}

	/**
	 * Insert a new edge into the graph.  This method will create a new edge
	 * based on the class specification from the edgeClass parameter.
	 * The directed flagged will be ignored in this implementation as
	 * all edges are assumed to be undirected.
	 *
	 * @param e The first element.
	 * @param e1 The second element.
	 * @param strength Ignored, as all edges are unweighted.
	 * @param directed Ignored, as all edges are undirected.
	 */
	public void insertEdge(Object e, Object e1, double strength){
		Edge edge1 = new DefaultEdge(e, e1);
		Edge edge2 = new DefaultEdge(e1, e);
		getEdgeSet(e).add(edge1);
		getEdgeSet(e1).add(edge2);
	}

	/**
	 * Insert a new edge into the graph.  This method will create a new
	 * undirected edge between element1 and element2.
	 *
	 * @param e The first element.
	 * @param e1 The second element.
	 */
	public void insertEdge(Object e, Object e1) {
		insertEdge(e, e1, 1);
	}

	protected Set getEdgeSet(Object e) {
		Set edges = (Set) relations.get(e);
		if (edges == null) {
			edges = new LinkedHashSet();
			relations.put(e, edges);
		}
		return edges;
	}

	/**
	 * Insert an edge that has already been created. This assumes that
	 * the edge should be undirected and makes sure both elements
	 * have the edge.
	 *
	 * @param e
	 */
	public void insertEdge(Edge e) {
		//    Edge e1 = e.copy();
		getEdgeSet(e.getElementOne()).add(e);
		getEdgeSet(e.getElementTwo()).add(e);
	}

	/**
	 * Removes an edge from the graph.  This should also remove the edge from the
	 * elements of the edge.  In other words, after this call, there should be no
	 * references to the edge.
	 * @param e
	 */
	public void removeEdge(Edge e) {
		Set fromSet = getEdgeSet(e.getElementOne());
		Set toSet = getEdgeSet(e.getElementTwo());
		//Edge e1 = e.getInverse();
		fromSet.remove(e);
		toSet.remove(e);
	}

	/**
	 * Removes the edge between element1 and element2.
	 *
	 * @param e
	 * @param e1
	 */
	public void removeEdge(Object e, Object e1) {
		Edge edge = getQuery(e, e1);
		Set fromSet = getEdgeSet(e);
		Set toSet = getEdgeSet(e1);
		fromSet.remove(edge);
		edge = getQuery(e1, e);
		toSet.remove(edge);
	}

	/**
	 * This method is not implemented in DefaultGraph.
	 * @param e
	 * @throws UnsupportedOperationException
	 */
	public void makeUndirected(Edge e) {
		throw new UnsupportedOperationException(
			"All edges in DefaultGraph must be" + "Undirected");
	}

	/**
	 * Determine if a given edge is directed or undirected.  The semantics of how
	 * directionality is handled is left up to the implementor. This method always
	 * returns true for the DefaultGraph
	 *
	 * @param e
	 * @return True.
	 */
	public boolean isUndirected(Edge e) {
		return true;
	}

	/**
	 * Reverse the direction of the edge.  If the edge is undirected, this method,
	 * should not alter the directionality.
	 * @param e
	 * @return
	 */
	public boolean reverseDirection(Edge e) {
		throw new UnsupportedOperationException("All edges in an DefaultGraph are Undirected");
	}

	/**
	 * This should force the directionality of an edge to be from the passed element.
	 * An undirected edge should become directed.
	 * @param e
	 * @param v
	 */
	public void setDirectionFrom(Edge e, Object v) {
		throw new UnsupportedOperationException(
			"You can't set the directionality in " + "an undirected Graph");
	}

	/**
	 * Get the Nodes that share an edge with the parameter with the proper directionality.
	 * @param v
	 * @param type
	 * @return
	 */
	public List getAdjacentNodes(Object v, EdgeType type) {
		List nodes = new ArrayList();
		Set edges = getEdgeSet(v);
		Iterator i = edges.iterator();
		while (i.hasNext()) {
			Edge e = (Edge) i.next();
			nodes.add(e.getElementTwo());
		}
		return nodes;
	}

	/**
	 * Returns whether the two elements share an edge with the given directionality.
	 * @param v
	 * @param v1
	 * @param type
	 * @return
	 */
	public boolean areAdjacent(Object v, Object v1, EdgeType type) {
		return areAdjacent(v, v1);
	}

	/**
	 * Returns the edge objects of the given directionality for the object.
	 * @param v
	 * @param type
	 * @return
	 */
	public List getEdges(Object v, EdgeType type) {
		return new ArrayList(getEdgeSet(v));
	}

	public Edge getEdge(Object element1, Object element2) {
		Iterator i = getEdgeSet(element1).iterator();
		while (i.hasNext()) {
			Edge e = (Edge) i.next();
			if (e.getElementTwo().equals(element2)) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Returns the number of edges for the object with the given directionality.
	 * @param v
	 * @param type
	 * @return
	 */
	public int degree(Object v, EdgeType type) {
		return getEdgeSet(v).size();
	}

	/**
	 * Get an iterator for the graph.
	 * @return
	 */
	public Iterator iterator() {
		return con.iterator();
	}

	public double distance(Object element1, Object element2) {
		if (areAdjacent(element1, element2)) {
			return getEdge(element1, element2).getStrength();
		}
		return Double.MAX_VALUE;
	}

	public int size(){
		return this.con.size();
	}
	
	/**
	 * Two DefaultGraphs are equal if they have the same relationship table
	 * and the same type.
	 *
	 * @param o
	 * @return
	 */
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof UndirectedGraph))
			return false;

		final UndirectedGraph defaultGraph = (UndirectedGraph) o;

		if (relations != null
			? !relations.equals(defaultGraph.relations)
			: defaultGraph.relations != null)
			return false;
		if (type != null
			? !type.equals(defaultGraph.type)
			: defaultGraph.type != null)
			return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (relations != null ? relations.hashCode() : 0);
		result = 29 * result + (type != null ? type.hashCode() : 0);
		return result;
	}

	public String toString() {
		return this.getClass().getName()
			+ " of type: "
			+ type
			+ " with degree "
			+ degree()
			+ ".";
	}

	public int degree() {
		Iterator i = relations.values().iterator();
		int count = 0;
		while (i.hasNext()) {
			count = ((Set) i.next()).size();
		}
		return count / 2;
	}

	private Edge getQuery(Object o1, Object o2) {
		return null;
	}

	/* (non-Javadoc)
	 * @see uchicago.src.sim.topology.graph.Graph#getAdjacentNodes(java.lang.Object, double, uchicago.src.sim.topology.graph.EdgeType)
	 */
	public List getAdjacentNodes(Object v, double distance, EdgeType type) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see uchicago.src.sim.topology.graph.Graph#getNodes()
	 */
	public Set getNodes() {
		return this.con;
	}
}
