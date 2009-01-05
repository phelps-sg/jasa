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
package uchicago.src.sim.network;


import java.util.ArrayList;


/**
 * A simple interface for a graph node in a networked space.
 *
 * @author Nick Collier
 * @version $Revision$
 */
public interface Node {

  /**
   * Gets the id associated with this node.
   */
  public Object getId ();

  /**
   *  Gets the label of this node.
   */
  public String getNodeLabel ();

  public void setNodeLabel (String node);

  /**
   * Gets the edges coming into this node.
   */
  public ArrayList getInEdges ();

  /**
   * Gets the edges going out from this node.
   */
  public ArrayList getOutEdges ();

  /**
   * Add an in edge to this node.
   */
  public void addInEdge (Edge edge);

  /**
   * Add an out edge to this node.
   */
  public void addOutEdge (Edge edge);

  /**
   * Removes the specified edge from the list of "in" edges.
   *
   * @param edge the edge to remove
   */
  public void removeInEdge (Edge edge);

  /**
   * Removes the specified edge from the list of "out" edges.
   *
   * @param edge the edge to remove
   */
  public void removeOutEdge (Edge edge);

  /**
   * Clears (removes) all the in edges.
   */
  public void clearInEdges ();

  /**
   * Clears (removes) all the out edges.
   */
  public void clearOutEdges ();

  /**
   * Returns true if this DefaultNode has an Edge to the specified Node,
   * otherwise false.
   */
  public boolean hasEdgeTo (Node node);

  /**
   * Returns true if this DefaultNode has an Edge from the specified
   * Node, otherwise false.
   */
  public boolean hasEdgeFrom (Node node);

}

