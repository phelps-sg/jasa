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


/**
 * An edge between two nodes. A default implementation of
 * Edge.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.sim.network.Node
 */
public class DefaultEdge implements Edge {

  protected Node from;
  protected Node to;
  protected String label = "";
  protected String type = "";
  protected double strength = 1.0d;

  /**
   * Constructs a DefaultEdge without any connecting Nodes.
   * This is primarily used to load this class in a "bean-like"
   * fashion.
   */
  public DefaultEdge() {}



  /**
   * Construct an edge from the specified to node to the specified node with
   * the specified label, and a default strength of 1.0.
   * This edge is not automatically added to the nodes,
   * and so each node must add the edge.
   *
   * @param from the from Node
   * @param to the to Node
   * @param label for this edge
   */
  public DefaultEdge (Node from, Node to, String label) {
    this(from, to, label, 1.0f);
  }

  /**
   * Construct an edge from the specified to node to the specified node with
   * the specified label, and the specified strength.
   * This edge is not automatically added to the nodes, and so each node must
   * add the edge.
   *
   * @param from the from Node
   * @param to the to Node
   * @param label the label for this edge
   * @param strength the strength of this edge
   */
  public DefaultEdge(Node from, Node to, String label, float strength) {
    this.from = from;
    this.to = to;
    this.label = label;
    this.strength = strength;
  }

  /**
   * Construct an edge from the specified to node to the specified node with
   * a blank label. This edge is not automatically added to the nodes,
   * and so each node must add the edge.
   *
   * @param from the from Node
   * @param to the to Node
   */

  public DefaultEdge(Node from, Node to) {
    this(from, to, "");
  }

  /**
   * Gets the from node
   */
  public Node getFrom() {
    return from;
  }

  /**
   * Sets the from node. This will automatically make the appropriate
   * changes in the corresponding to node and also add this edge as an
   * out edge to the specified Node.
   */
  public void setFrom(Node node) {
    if (to != null) {
      to.removeInEdge(this);
    }
    from = node;
    from.addOutEdge(this);
    if (to != null) {
      to.addInEdge(this);
    }
  }

  /**
   * Gets the to node
   */
  public Node getTo() {
    return to;
  }

  /**
   * Sets the to node. This will automatically make the appropriate
   * changes in the corresponding from node and also add this edge as an
   * in edge to the specified Node.
   */
  public void setTo(Node node) {
    if (from != null) {
      from.removeOutEdge(this);
    }

    to = node;
    to.addInEdge(this);
    if (from != null) {
      from.addOutEdge(this);
    }
  }


  /**
   * Sets the label for this Edge
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Gets the label for this Edge
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the strength of this edge
   */
  public void setStrength(double val) {
    strength = val;
  }

  /**
   * Gets the strength of this edge
   */
  public double getStrength() {
    return strength;
  }

  /**
   * Gets the type of this DefaultEdge. The type is intended to
   * contain the type of network this edge represents (i.e.
   * kinship, business etc.).
   */
  public String getType() {
    return type;
  }

   /**
   * Sets the type of this DefaultEdge. The type is intended to
   * contain the type of network this edge represents (i.e.
   * kinship, business etc.).
   *
   * @param type this DefaultEdge's type (i.e. the network type)
   */
  public void setType(String type) {
    this.type = type;
  }
}
