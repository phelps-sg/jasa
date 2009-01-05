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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uchicago.src.sim.topology.ModifyableTopology;


/**
 *
 * This represents a more specific RelationTopology, where relationships
 * are represented by edges.  This provides support for directed or non-directed
 * edges.  While this does imply a particular implementation approach for
 * concrete implementations, the primary goal of this was to semantically
 * represent a collection of relationships which adhere to a graph structure, such
 * as a Social Network, not to impose any syntactic implementation requirements.
 *
 * @author Tom Howe
 * @version $Revision$
 * @model
 */
public interface Graph extends ModifyableTopology, Serializable{

  /**
   * Insert a new edge into the graph.  This method will create a new edge
   * based on the class specification from the edgeClass parameter.
   *
   * @param e
   * @param e1
   * @param directed
   * @model
   */
  public void insertEdge(Object e, Object e1, double strength);

  /**
   * Insert an edge that has already been created.
   * @param e
   * @model
   */
  public void insertEdge(Edge e);

  /**
   * Removes an edge from the graph.  This should also remove the edge from the
   * elements of the edge.  In other words, after this call, there should be no
   * references to the edge.
   * @param e
   * @model
   */
  public void removeEdge(Edge e);

/**
 * 
 * @param e
 * @param e1
 * @model
 */

  public void removeEdge(Object e, Object e1);

  /**
   * Get the Nodes that share an edge with the parameter with the proper directionality.
   * @param v
   * @param type
   * @return
   * @model type="Object" containment="true"
   */
  public List getAdjacentNodes(Object v, EdgeType type);

/**
 * @model type="Object" containment="true"
 * @param v
 * @param distance
 * @param type
 * @return
 * 
 */
  public List getAdjacentNodes(Object v, double distance, EdgeType type);

  /**
   * Returns whether the two elements share an edge with the given directionality.
   * @param v
   * @param v1
   * @param type
   * @return
   * @model
   */
  public boolean areAdjacent(Object v, Object v1, EdgeType type);

  /**
   * Returns the edge objects of the given directionality for the object.
   * @param v
   * @param type
   * @return
   * @model type="Edge" containment="true"
   */
  public List getEdges(Object v, EdgeType type);

  /**
   * Returns the number of edges for the object with the given directionality.
   * @param v
   * @param type
   * @return
   * @model
   */
  public int degree(Object v, EdgeType type);

  /**
   * Get an iterator for the graph.
   * @return
   * @model type="Object" containment="true"
   */
  public Iterator iterator();
  
  /**
   * @model
   */
  public int size();
  
  public Set getNodes();
  
}
