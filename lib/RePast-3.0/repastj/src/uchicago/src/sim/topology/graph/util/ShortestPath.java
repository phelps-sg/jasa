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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import uchicago.src.sim.topology.graph.EdgeType;
import uchicago.src.sim.topology.graph.Graph;

/**
 * This class represents a single source shortest path. As such it will yield
 * the shortest paths between the source node and all other nodes in the
 * graph. It is implemented as an iterator.  This uses Dijkstra's algorithm
 * for determining shortest path, so it does not allow for negative wieghts.
 * The performance on this algorithm is O(V^2).
 *
 * @author Tom Howe
 * @version $Revision$
 * @serial 7006545353945094103L
 */
public class ShortestPath implements Iterator, Serializable {

	static final long serialVersionUID = 7006545353945094103L;
	private Graph graph;
	//private Object start;
	private Map distanceMap;
	private TreeSet pq;
	private Object current;
	private EdgeType type;
	//Comparator to compare distances to the source node
	private Comparator distComp = new Comparator() {
		public int compare(Object o1, Object o2) {
				//default distance is infinity
	double o1Dist = Double.MAX_VALUE;
			double o2Dist = Double.MAX_VALUE;
			Double dist = ((Double) distanceMap.get(o1));
			//if it's not null, then use the actual distance.
			if (dist != null) {
				o1Dist = dist.doubleValue();
			}

			dist = ((Double) distanceMap.get(o2));
			//if it's not null, then use the actual distance
			if (dist != null) {
				o2Dist = dist.doubleValue();
			}
			//reverse comparison, because the priority queue searches
			//for the max, but we are looking for the minimum distance.
			if (o1Dist < o2Dist) {
				return 1;
			} else if (o2Dist < o1Dist) {
				return -1;
			} else {
				return 0;
			}
		}
	};


	/**
	 * Constructs an instance of ShortestPath using the graph 
	 * containing the edges and a starting node.
	 *
	 * @param g The graph to search.
	 * @param node1 The source node.
	 * @param t The type of Edge to use.
	 */
	public ShortestPath(Graph g, Object node1, EdgeType t) {
		type = t;
		init(g, node1);
	}

	private void init(Graph g, Object node1) {
		graph = g;
		//start = node1;
		distanceMap = new HashMap();
		pq = new TreeSet(distComp);
		Iterator i = graph.getNodes().iterator();
		while(i.hasNext()){
			Object last = null;
			Object next = i.next();
			if(next.equals(last)){
				System.out.println("equal");
			}
			last = next;
			System.out.println("contains = " + pq.contains(next));
			System.out.println("adding " + next);
			System.out.println(pq.add(next));
		}
		System.out.println("initsize = " + pq.size());
	}

	//standard relax method.  Determine if the distame between
	//start and v is greater than if we made a path from u to v.
	private void relax(Object u, Object v) {
		if (distanceMap.containsKey(v)) {
			double distV = ((Double) distanceMap.get(v)).doubleValue();
			double distU = ((Double) distanceMap.get(u)).doubleValue();

			if (distV > (distU + graph.distance(u, v))) {
				distanceMap.put(v, new Double(distU + graph.distance(u, v)));
			}
		}
	}

	/**
	 * Unsupported Method
	 *
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Determines if there is another node in the path. This will return true
	 * unless we have searched all of the available nodes.
	 *
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		System.out.println("size = " + pq.size());
		if (pq.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the next node in the shortest path.
	 *
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		//pop the max (in this case the min) off the queue
		Object u = pq.last();
		pq.remove(u);
		current = u;
		//get all of the adjacent Nodes
		Iterator i = graph.getAdjacentNodes(u, type).iterator();
		while (i.hasNext()) {
			relax(u, i.next());
		}
		return u;
	}

	/**
	 * Get the distance (in weight) between the start node and the  current node.
	 *
	 * @return The distance from the start node to the current node.
	 */
	public double getDistance() {
		Double ddist = (Double) distanceMap.get(current);
		if (ddist != null) {
			return ddist.doubleValue();
		}
		return Double.MAX_VALUE;
	}
}
