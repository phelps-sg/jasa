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

import uchicago.src.sim.util.SimUtilities;

/**
 * Utility methods for creating Edges.
 *
 * @version $Revision$ $Date$
 */
public class EdgeFactory {

  /**
   * Links the specified Nodes with the specified Edge.
   *
   * @param from the Node from which the edge proceeds
   * @param to the Node to which the edge proceeds
   * @param edge the edge to link the two Nodes
   * 
   */

  public static Edge linkNodes(Node from, Node to, Edge edge) {
    edge.setFrom(from);
    edge.setTo(to);
    from.addOutEdge(edge);
    to.addInEdge(edge);
    return edge;
  }

  /**
   * Construct an edge from the specified node to the specified node with
   * the specified label, and a default strength of 1.0. This edge is
   * automatically added to the from and to Nodes as an out and in edge
   * respectively.
   *
   * @param from the from Node
   * @param to the to Node
   * @param label for this edge
   */
  public static DefaultEdge createEdge (Node from, Node to, String label) {
    DefaultEdge edge = new DefaultEdge (from, to, label);
    from.addOutEdge (edge);
    to.addInEdge (edge);
    return edge;
  }

  /**
   * Construct an edge from the specified node to the specified node with
   * the specified label, and the specified strength. This edge is
   * automatically added to the from and to Nodes as an out and in edge
   * respectively.
   *
   * @param from the from Node
   * @param to the to Node
   * @param label the label for this edge
   * @param strength the strength of this edge
   */
  public static DefaultEdge createEdge (Node from, Node to, String label,
                                        float strength) {
    DefaultEdge edge = new DefaultEdge (from, to, label, strength);
    from.addOutEdge (edge);
    to.addInEdge (edge);
    return edge;
  }

  /**
   * Constructs an edge from the specified node to the specified node with
   * a blank label. This edge is
   * automatically added to the from and to Nodes as an out and in edge
   * respectively.
   *
   * @param from the from Node
   * @param to the to Node
   */

  public static DefaultEdge createEdge (Node from, Node to) {
    DefaultEdge edge = new DefaultEdge (from, to);
    from.addOutEdge (edge);
    to.addInEdge (edge);
    return edge;
  }

  /**
   * Constructs an edge from the specified node to the specified node with
   * the specified label, and a default strength of 1.0. This edge is
   * automatically added to the from and to Nodes as an out and in edge
   * respectively.
   *
   * @param from the from Node
   * @param to the to Node
   * @param label for this edge
   */
  public static DefaultDrawableEdge createDrawableEdge (Node from, Node to, String label) {
    DefaultDrawableEdge edge = new DefaultDrawableEdge (from, to, label);
    from.addOutEdge (edge);
    to.addInEdge (edge);
    return edge;
  }

  /**
   * Constructs an edge from the specified from node to the specified to node with
   * the specified label, and the specified strength. This edge is
   * automatically added to the from and to Nodes as an out and in edge
   * respectively.
   *
   * @param from the from Node
   * @param to the to Node
   * @param label the label for this edge
   * @param strength the strength of this edge
   */
  public static DefaultDrawableEdge createDrawableEdge (Node from, Node to, String label,
                                                        float strength) {
    DefaultDrawableEdge edge = new DefaultDrawableEdge (from, to, label, strength);
    from.addOutEdge (edge);
    to.addInEdge (edge);
    return edge;
  }

  /**
   * Constructs an edge from the specified to node to the specified node with
   * a blank label. This edge is
   * automatically added to the from and to Nodes as an out and in edge
   * respectively.
   *
   * @param from the from Node
   * @param to the to Node
   */

  public static DefaultDrawableEdge createDrawableEdge (Node from, Node to) {
    DefaultDrawableEdge edge = new DefaultDrawableEdge (from, to);
    from.addOutEdge (edge);
    to.addInEdge (edge);
    return edge;
  }

  /**
   * Construct an Edge from the specified from node to the specified to node with
   * the specified label, and a default strength of 1.0. The Edge itself is
   * created from the specified Class. This Class must implement the Edge
   * interface and have a no argument constructor. Note that this method
   * is slower than creating edges of this Class type by hand. This edge is
   * automatically added to the from and to Nodes as an out and in edge
   * respectively.
   *
   * @param from the from Node
   * @param to the to Node
   * @param label for this edge
   * @param edgeClass the Class to create the Edge from
   */
  public static Edge createCustomEdge (Node from, Node to, String label,
                                       Class edgeClass)
  {
    Edge edge = EdgeFactory.createCustomEdge(from, to, edgeClass);
    edge.setLabel(label);
    return edge;
  }

  /**
   * Construct an edge from the specified from node to the specified to node with
   * the specified label, and the specified strength. The Edge itself is
   * created from the specified Class. This Class must implement the Edge
   * interface and have a no argument constructor. Note that this method
   * is slower than creating edges of this Class type by hand. This edge is
   * automatically added to the from and to Nodes as an out and in edge
   * respectively.
   *
   * @param from the from Node
   * @param to the to Node
   * @param label the label for this edge
   * @param strength the strength of this edge
   * @param edgeClass the Class to create the Edge from
   */
  public static Edge createCustomEdge (Node from, Node to, String label,
                                       float strength, Class edgeClass)
  {
    Edge edge = EdgeFactory.createCustomEdge(from, to, edgeClass);
    edge.setLabel(label);
    edge.setStrength(strength);
    return edge;
  }

  /**
   * Constructs an edge from the specified from node to the specified to node with
   * a blank label. The Edge itself is created from the specified Class.
   * This Class must implement the Edge interface and have a no argument
   * constructor. Note that this method is slower than creating edges of this
   * Class type by hand.This edge is automatically added to the from and to Nodes
   * as an out and in edge respectively.
   *
   * @param from the from Node
   * @param to the to Node
   * @param edgeClass the Class to create the Edge from
   */

  public static Edge createCustomEdge (Node from, Node to, Class edgeClass) {
    if (Edge.class.isAssignableFrom (edgeClass)) {
      try {
        Edge edge = (Edge) edgeClass.newInstance ();
        from.addOutEdge (edge);
        to.addInEdge (edge);
        return edge;
      } catch (InstantiationException ex) {
        SimUtilities.showError ("Error creating custom edge", ex);
        System.exit (0);
      } catch (IllegalAccessException ex) {
        SimUtilities.showError ("Error creating custom edge", ex);
        System.exit (0);
      }
    }

    String message = "Edge class argument to createCustomEdge does not implement Edge interface";
    IllegalArgumentException ex = new IllegalArgumentException (message);
    SimUtilities.showError (message, ex);
    // fail fast
    System.exit (0);
    return null;
  }
}
