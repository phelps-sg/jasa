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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uchicago.src.sim.topology.graph.util.DepthFirstSearch;
import uchicago.src.sim.topology.graph.util.EdgeClassification;

/**
 * This represents a DirectedGraph.  
 * 
 * @author Tom Howe
 * @version $Revision$ 
 */
public class DirectedGraph extends AbstractGraph 
	implements Serializable {

	static final long serialVersionUID = -3047402482395522121L;
	private Map inEdgeMap;
	private Map outEdgeMap;
	private Set nodes;
	private String type;
	private final DirectedEdge query = new DirectedEdge();

	/**
	 * Construct a graph using the specified set of vertices.
	 * The type is set to "DirectedGraph"
	 * @param s
	 */
	public DirectedGraph(Set s) {
		this(s, "DirectedGraph");
	}
	
	/**
	 * Construct a graph using the specified set of vertices
	 * and the type.
	 * 
	 * @param s
	 * @param type
	 */
	public DirectedGraph(Set s, String type) {
		this.type = type;
		nodes = s;
		inEdgeMap = new HashMap();
		outEdgeMap = new HashMap();
		nodes = s;
	}

	protected Set getEdgeSet(Object v, EdgeType e) {
		Set edges = null;
		if (e.equals(EdgeType.IN)) {
			edges = (Set) inEdgeMap.get(v);
			if (edges == null) {
				edges = new LinkedHashSet();
				inEdgeMap.put(v, edges);
			}
		} else if (e.equals(EdgeType.OUT)) {
			edges = (Set) outEdgeMap.get(v);
			if (edges == null) {
				edges = new LinkedHashSet();
				outEdgeMap.put(v, edges);
			}
		} else {
			edges = (Set) inEdgeMap.get(v);
			Set s2 = (Set) outEdgeMap.get(v);
			if (edges != null) {
				if (s2 != null) {
					edges.addAll(s2);
				} else {
					outEdgeMap.put(v, new LinkedHashSet());
				}
			} else {
				inEdgeMap.put(v, new LinkedHashSet());
				if (s2 != null) {
					edges = s2;
				} else {
					outEdgeMap.put(v, new LinkedHashSet());
				}
			}
		}
		return edges;
	}

	/**
	 * Insert a new edge into the graph.  This method will create a new edge
	 * based on the class specification from the edgeClass parameter.
	 *
	 * @param e
	 * @param e1
	 * @param directed
	 */
	public void insertEdge(Object e, Object e1, double strength) {
		DirectedEdge edge = new DirectedEdge(e, e1, strength);
		Set edges = getEdgeSet(e, EdgeType.OUT);
		edges.add(edge);
		edges = getEdgeSet(e1, EdgeType.IN);
		edges.add(edge);
	}

	/**
	 * Insert an edge that has already been created.
	 * @param e
	 */
	public void insertEdge(Edge e) {
		DirectedEdge de = (DirectedEdge) e;
		Set edges = getEdgeSet(de.getFrom(), EdgeType.OUT);
		edges.add(de);
		edges = getEdgeSet(de.getTo(), EdgeType.IN);
		edges.add(de);
	}

	/**
	 * Removes an edge from the graph.  This should also remove the edge from the
	 * elements of the edge.  In other words, after this call, there should be no
	 * references to the edge.
	 * @param e
	 */
	public void removeEdge(Edge e) {
		DirectedEdge de = (DirectedEdge) e;
		Object from = de.getFrom();
		Object to = de.getTo();
		((Set) outEdgeMap.get(from)).remove(e);
		((Set) inEdgeMap.get(to)).remove(e);
	}

	/**
	 * Remove the edge between the two given vertices.
	 */
	public void removeEdge(Object e, Object e1) {
		query.setFrom(e);
		query.setTo(e1);
		((Set) outEdgeMap.get(e)).remove(query);
		((Set) inEdgeMap.get(e1)).remove(query);
	}

	/**
	 * Determine if a given edge is directed or undirected.  The semantics of how
	 * directionality is handled is left up to the implementor.
	 * @param e
	 * @return
	 */
	public boolean isUndirected(Edge e) {
		Object from = e.getElementOne();
		Object to = e.getElementTwo();
		query.setFrom(to);
		query.setTo(from);
		if (((Set) outEdgeMap.get(to)).contains(query)) {
			return true;
		}
		return false;
	}

	/**
	 * Reverse the direction of the edge.  If the edge is undirected, this method,
	 * should not alter the directionality.
	 * @param e
	 * @return
	 */
	public boolean reverseDirection(Edge e) {
		Object from = e.getElementOne();
		Object to = e.getElementTwo();
		e.setElementTwo(from);
		e.setElementOne(to);
		return true;
	}

	/**
	 * This should force the directionality of an edge to be from the passed element.
	 * An undirected edge should become directed.
	 * @param e
	 * @param v
	 */
	public void setDirectionFrom(Edge e, Object v) {
		if (!e.getElementOne().equals(v)) {
			Object to = e.getElementOne();
			e.setElementOne(v);
			e.setElementTwo(to);
		}
	}

	/**
	 * Get the Nodes that share an edge with the parameter with the proper directionality.
	 * @param v
	 * @param type
	 * @return
	 */
	public List getAdjacentNodes(Object v, EdgeType type) {
		if (type.equals(EdgeType.IN)) {
			return getInNodes(v);
		} else if (type.equals(EdgeType.OUT)) {
			return getOutNodes(v);
		} else {
			List out = getInNodes(v);
			out.addAll(getOutNodes(v));
			return out;
		}
	}

	/**
	 * Get all of the vertices that the given vertex has an in
	 * edge from.
	 * 
	 * @param v
	 * @return
	 */
	public List getInNodes(Object v) {
		List out = new ArrayList();
		Set s = getEdgeSet(v, EdgeType.IN);
		Iterator i = s.iterator();
		while (i.hasNext()) {
			Edge e = (Edge) i.next();
			out.add(e.getOtherElement(v));
		}
		return out;
	}

	/**
	 * Get all of the vertices that the given vertex has
	 * an out edge to.
	 * 
	 * @param v
	 * @return
	 */
	public List getOutNodes(Object v) {
		List out = new ArrayList();
		Set s = getEdgeSet(v, EdgeType.OUT);
		Iterator i = s.iterator();
		while (i.hasNext()) {
			Edge e = (Edge) i.next();
			out.add(e.getOtherElement(v));
		}
		return out;
	}

	/**
	 * Get a list of the vertices that are attached to the
	 * specified vertex of the given direction and within
	 * the given distance.
	 */
	public List getAdjacentNodes(Object v, double distance, 
			EdgeType type) {
		List out = new ArrayList();
		if (EdgeType.OUT.equals(type)) {
			out.addAll((Set) outEdgeMap.get(v));
		}
		return out;
	}

	/**
	 * Returns whether the two elements share an edge with the 
	 * given directionality.
	 * @param v
	 * @param v1
	 * @param type
	 * @return
	 */
	public boolean areAdjacent(Object v, Object v1, EdgeType type) {
		if (type.equals(EdgeType.OUT)) {
			query.setFrom(v);
			query.setTo(v1);
			if (((Set) outEdgeMap.get(v)).contains(query)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Returns the edge objects of the given directionality for 
	 * the object.
	 * @param v
	 * @param type
	 * @return
	 */
	public List getEdges(Object v, EdgeType type) {
		List out = new ArrayList();
		if (type.equals(EdgeType.OUT) || type.equals(EdgeType.ALL)) {
			out.addAll((Set) outEdgeMap.get(v));
		}
		if (type.equals(EdgeType.IN) || type.equals(EdgeType.ALL)) {
			out.addAll((Set) inEdgeMap.get(v));
		}
		return out;
	}

	/**
	 * Returns the number of edges for the object with the given 
	 * directionality.
	 * @param v
	 * @param type
	 * @return
	 */
	public int degree(Object v, EdgeType type) {
		int degree = 0;
		if (type.equals(EdgeType.OUT) || type.equals(EdgeType.ALL)) {
			degree = + ((Set) outEdgeMap.get(v)).size();
		}
		if (type.equals(EdgeType.IN) || type.equals(EdgeType.ALL)) {
			degree = + ((Set) inEdgeMap.get(v)).size();
		}
		return degree;
	}

	/**
	 * Get an iterator for the graph.
	 * @return
	 */
	public Iterator iterator() {
		return nodes.iterator();
	}

	/**
	 * This returns the distance between two vertices in the graph.
	 * 
	 * @param element1
	 * @param element2
	 * @return
	 */
	public double distance(Object element1, Object element2) {
		if(areAdjacent(element1, element2, EdgeType.ALL)){
			Edge edge= getEdge(element1, element2);
			return edge.getStrength();
		}
		return Double.MAX_VALUE;
	}
	
	private Edge getEdge(Object element1, Object element2){
		List l = getEdges(element1, EdgeType.ALL);
		Iterator i = l.iterator();
		while(i.hasNext()){
			Edge e = (Edge) i.next();
			if(e.getOtherElement(element1).equals(element2)){
				return e;
			}
		}
		return null;
	}

	/**
	 * This makes a directed edge undirected.  For the DirectedGraph,
	 * this method add the transposed edge, i.e., given edge (u,v), 
	 * this will insert (v, u).  The two edges will share any
	 * object references and will start with the same strength.
	 * Note, there are two edges, so if you change primitives in
	 * one edge, they will not change in the second edge.
	 *  
	 * @see uchicago.src.sim.topology.graph.Graph#makeUndirected(uchicago.src.sim.topology.graph.Edge)
	 */
	public void makeUndirected(Edge e) {
		DirectedEdge copy = (DirectedEdge) e.copy();
		Object to = copy.getFrom();
		Object from = copy.getTo();
		copy.setFrom(from);
		copy.setTo(to);
		Set fromSet = getEdgeSet(from, EdgeType.OUT);
		fromSet.add(copy);
		Set toSet = getEdgeSet(to, EdgeType.IN);
		toSet.add(copy);
	}

	/**
	 * Returns the number of vertices in this graph.
	 */
	public int size() {
		return nodes.size();
	}

	/**
	 * Determines if the graph is acyclic.  For an acyclic graph, 
	 * this will take O(V + E) where V is the number of vertices
	 * and E is the number of edges.  For a cyclic graph, this
	 * will return as soon as the back edge is found.
	 * 
	 * @return
	 */
	public boolean isAcyclic() {
		DepthFirstSearch search = new DepthFirstSearch(this);
		while (search.hasNext()) {
			Object next = search.next();
			if (search
				.getClassification(next)
				.equals(EdgeClassification.BACK_EDGE)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Sort the nodes of this graph according to topological
	 * order.  This performs at O(V + E) where V is the number
	 * of vertices and E is the number of edges.  This will 
	 * return false if the graph is not acyclic.
	 *  
	 * @return true if the sort is successful, false otherwise 
	 * 	(usually because the graph is not acyclic).
	 */
	public boolean topologicalSort() {
		DepthFirstSearch search = new DepthFirstSearch(this);
		LinkedHashSet sorted = new LinkedHashSet();
		while (search.hasNext()) {
			Object next = search.next();
			if(search.getClassification(next).equals(
				EdgeClassification.BACK_EDGE)){
					return false;
				}
			sorted.add(next);
			
		}
		nodes = sorted;
		return true;
	}

	public Set getNodes() {
		return this.nodes;
	}

	public int hashCode() {
		final int PRIME = 1000003;
		int result = super.hashCode();
		if (inEdgeMap != null) {
			result = PRIME * result + inEdgeMap.hashCode();
		}
		if (outEdgeMap != null) {
			result = PRIME * result + outEdgeMap.hashCode();
		}
		if (nodes != null) {
			result = PRIME * result + nodes.hashCode();
		}
		if (type != null) {
			result = PRIME * result + type.hashCode();
		}

		return result;
	}

	public boolean equals(Object oth) {
		if (this == oth) {
			return true;
		}

		if (!super.equals(oth)) {
			return false;
		}

		DirectedGraph other = (DirectedGraph) oth;
		if (this.inEdgeMap == null) {
			if (other.inEdgeMap != null) {
				return false;
			}
		} else {
			if (!this.inEdgeMap.equals(other.inEdgeMap)) {
				return false;
			}
		}
		if (this.outEdgeMap == null) {
			if (other.outEdgeMap != null) {
				return false;
			}
		} else {
			if (!this.outEdgeMap.equals(other.outEdgeMap)) {
				return false;
			}
		}
		if (this.nodes == null) {
			if (other.nodes != null) {
				return false;
			}
		} else {
			if (!this.nodes.equals(other.nodes)) {
				return false;
			}
		}
		if (this.type == null) {
			if (other.type != null) {
				return false;
			}
		} else {
			if (!this.type.equals(other.type)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Calculates the clustering coefficient for this graph.
	 * For our purposes the clustering coefficient is defined as
	 * The density of local networks.  From a practical perspective
	 * this is calculated by averaging the results of the following 
	 * procedure over all of the nodes.
	 * 
	 * - Get all adjacent nodes, ignoring self loops (jNodes) 
	 * - for each of those nodes, get all adjacent nodes (kNodes)
	 * - determine how many kNodes are also jNodes 
	 * - divide by the total number possible: 
	 * 		(degree(i) * (degree(i) - 1))
	 * 
	 * @return
	 */
	public double clusterCoeffient(){
		double clust = 0.0;
		Set jNodes = new HashSet();
		Iterator i = nodes.iterator();
		while(i.hasNext()){
			jNodes.clear();
			int iDensity = 0;
			Object iNode = i.next();
			List jAdj = getAdjacentNodes(iNode, EdgeType.OUT);
			int iDegree = jAdj.size();
			Iterator jIter = jAdj.iterator();
			while(jIter.hasNext()){
				jNodes.add(jIter.next());				
			}
			if(jNodes.contains(iNode)){
				jNodes.remove(iNode);
			}
			jIter = jNodes.iterator();
			while(jIter.hasNext()){
				Object jNode = jIter.next();
				Iterator kIter = 
					getAdjacentNodes(jNode, EdgeType.OUT).iterator();
				while(kIter.hasNext()){
					Object kNode = kIter.next();
					if(jNodes.contains(kNode) && !kNode.equals(jNode)){
						iDensity++;
					}
				}
			}
			if(iDensity > 0){
				clust =+ (double)iDensity / (double) (iDegree * (iDegree - 1)); 
			}
		}
		clust = clust / getNodes().size();
		return clust;
	}
}
