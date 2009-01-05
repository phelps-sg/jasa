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
package uchicago.src.sim.gui;

import java.util.ArrayList;

import uchicago.src.sim.network.DefaultNode;
import uchicago.src.sim.network.Edge;
import uchicago.src.sim.network.Node;


/**
 * An oval shaped node. This can be used as a nodes or nodes in
 * Network2DDisplays. An OvalNode is intended to be used a drawable
 * wrapper over a Node object. OvalNode by default wraps a DefaultNode,
 * but can wrap any object implementing the Node interface. Use setNode()
 * to set the Node to be wrapped.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see OvalItem
 * @see DrawableItem
 * @deprecated As of repast 2.0, OvalNodes and RectNodes will no longer work.
 * To visualize nodes, use DefaultDrawableNode as the base class and set
 * the appropriate NetworkDrawable. See the Network how to
 * (repast/docs/how_to/network.html) for more information.
 *
 */

public class OvalNode extends OvalItem implements DrawableNonGridNode, Node,
  Moveable
{

  protected Node node = new DefaultNode();
  //private boolean isWrapper = false;
  protected double x, y;

  /**
   * Constructs an OvalNode with the specified x and y coordinates.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public OvalNode(double x, double y) {
    System.out.println ("OvalNode will not work!!!!");
    this.x = x;
    this.y = y;
  }

  /**
   * Gets the x coordinate.
   */
  public double getX() {
    return x;
  }

  /**
   * Sets the x coordinate.
   *
   * @param x the new x coordindate
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Gets the y coordinate.
   */
  public double getY() {
    return y;
  }

  /**
   * Sets the y coordinate.
   *
   * @param y the new y coordinate
   */
  public void setY(double y) {
    this.y = y;
  }

  public void setLabel(String label) {
    node.setNodeLabel(label);
    super.setLabel(label);
  }

  // Node interface

  /**
   *  Gets the node's label
   */

  public String getNodeLabel() {
    return node.getNodeLabel();
  }

  public void setNodeLabel(String label) {
    node.setNodeLabel(label);
    super.setLabel(label);
  }

  /**
   * Gets the node id.
   */
  public Object getId() {
    return node.getId();
  }

  /**
   * Gets the ArrayList of edges into this OvalNode.
   */
  public ArrayList getInEdges() {
    return node.getInEdges();
  }

  /**
   * Gets the ArrayList of Edge out of this OvalNode.
   */
  public ArrayList getOutEdges() {
    return node.getOutEdges();
  }

  /**
   * Adds an in Edge to this OvalNode
   *
   * @param edge the "in" edge to add
   */
  public void addInEdge(Edge edge) {
    node.addInEdge(edge);
  }

  /**
   * Adds an out Edge to this OvalNode
   *
   * @param edge the "out" edge to add
   */
  public void addOutEdge(Edge edge) {
   node.addOutEdge(edge);
  }

  /**
   * Removes the specified edge from the list of "in" edges.
   *
   * @param edge the edge to remove
   */
  public void removeInEdge(Edge edge) {
    node.removeInEdge(edge);
  }

  /**
   * Removes the specified edge from the list of "out" edges.
   *
   * @param edge the edge to remove
   */
  public void removeOutEdge(Edge edge) {
    node.removeOutEdge(edge);
  }

  /**
   * Clears (removes) all the in edges.
   */
  public void clearInEdges() {
    node.clearInEdges();
  }

  /**
   * Clears (removes) all the out edges.
   */
  public void clearOutEdges() {
    node.clearOutEdges();
  }

  // moveable interface
  /**
   * Implements the moveable interface, setting the x coordinate to
   * some integer value.
   */
  public void setX(int x) {
    this.x = x;
  }

  /**
   * Implements the moveable interface, setting the y coordinate to
   * some integer value.
   */
  public void setY(int y) {
    this.y = y;
  }

  public boolean hasEdgeTo (Node node) {
    return node.hasEdgeTo(node);
  }

  public boolean hasEdgeFrom (Node node) {
    return node.hasEdgeFrom(node);
  }
}
