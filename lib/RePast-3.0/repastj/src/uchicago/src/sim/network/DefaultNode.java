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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uchicago.src.sim.util.Random;


/**
 * A simple graph node that is a default implementation of
 * Node.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.sim.network.Node
 * @see uchicago.src.sim.network.Edge
 */
public class DefaultNode implements Node {

  /**
   * HashMap that keeps an ordered list of its keys.
   */
  protected static class OrderedHashMap extends HashMap {

    LinkedList keys = new LinkedList();

    public OrderedHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
    }

    public OrderedHashMap(int initialCapacity) {
      super(initialCapacity);
    }

    public OrderedHashMap() {
    }

    public OrderedHashMap(Map m) {
      super(m);
    }

    public Object put(Object key, Object value) {
      if (!containsKey(key)) keys.add(key);
      return super.put(key, value);
    }

    public Object remove(Object key) {
      if (containsKey(key)) keys.remove(key);
      return super.remove(key);
    }

    public void clear() {
      super.clear();
      keys.clear();
    }
  }

  /**
   * A list of the edges into this Node.
   */
  protected ArrayList inEdges = new ArrayList(3);

  /**
   * A Map of nodes whose edges are into this Node. The Node is the key
   * and a HashSet of edges is the value.
   */

  // ToDo when we go to 1.4.1 or > then we can use a LinkedHashMap
  protected OrderedHashMap inMap = new OrderedHashMap();

  /**
   * A list of the edges out of this Node.
   */
  protected ArrayList outEdges = new ArrayList(3);

  /**
   * A Map of nodes whose edges are out of this Node. The Node is the key
   * and a Hashset of edges is the value.
   */

// ToDo when we go to 1.4.1 or > then we can use a LinkedHashMap
  protected OrderedHashMap outMap = new OrderedHashMap();
  protected String label;

  /**
   * Creates a DefaultNode with a blank label.
   */
  public DefaultNode() {
    this("");
  }

  /**
   * Creates a DefaultNode with the specified string as its label.
   *
   * @param label the label for this node
   */
  public DefaultNode(String label) {
    this.label = label;
  }

  /**
   * Gets a unique id for this node. This returns the hashCode as an Integer.
   */
  public Object getId() {
    return new Integer(hashCode());
  }

  /**
   *  Gets the label for this node.
   */
  public String getNodeLabel() {
    return label;
  }

  /**
   *  Sets the label for this node.
   *
   */
  public void setNodeLabel(String label) {
    this.label = label;
  }

  /**
   * Gets the ArrayList of edges into this node. The order of the list
   * is the order in which the Edges where added.
   */
  public ArrayList getInEdges() {
    return inEdges;
  }

  /**
   * Gets the ArrayList of Edge out of this node. The order of the list
   * is the order in which the Edges where added.
   */
  public ArrayList getOutEdges() {
    return outEdges;
  }


  /**
   * Gets all of the Nodes that this DefaultNode has an edge
   * from. This is a list of all the nodes on the end of this node's
   * in edges.
   */
  public ArrayList getFromNodes() {
    return getInNodes();
  }

  /**
   * Gets all of the Nodes that this DefaultNode has an edge to. This
   * is a list of all the nodes on the end of this node's out edges.
   */
  public ArrayList getToNodes() {
    return getOutNodes();
  }

  /**
   * Gets all of the Nodes that this DefaultNode has an edge to. This
   * is a list of all the nodes on the end of this node's out edges.
   * The iteration order of these Nodes will be the order in which the
   * corresponding edges were first added to this DefaultNode.
   */
  public ArrayList getOutNodes() {
    return new ArrayList(outMap.keys);
  }

  /**
   * Gets all of the Nodes that this DefaultNode has an edge
   * from. This is a list of all the nodes on the end of this node's
   * in edges. The iteration order of these Nodes will be the order in which the
   * corresponding edges were first added to this DefaultNode.
   */
  public ArrayList getInNodes() {
    return new ArrayList(inMap.keys);
  }

  /**
   * Does this Node have an edge to or from the specified node.
   *
   * @param node the node to check if this DefaultNode contains an edge to.
   */
  public boolean hasEdgeToOrFrom(Node node) {
    return inMap.containsKey(node) || outMap.containsKey(node);
  }

  /**
   * Adds an in Edge to this DefaultNode.
   *
   * @param edge the "in" edge to add
   */
  public void addInEdge(Edge edge) {
    Node from = edge.getFrom();
    HashSet s = (HashSet)inMap.get(from);
    if (s == null) {
      s = new HashSet();
      inMap.put(from, s);
      s.add(edge);
      inEdges.add(edge);

    } else if (!s.contains(edge)) {
      s.add(edge);
      inEdges.add(edge);
    }

  }

  /**
   * Adds an out Edge to this DefaultNode
   *
   * @param edge the "out" edge to add
   */
  public void addOutEdge(Edge edge) {
    Node to = edge.getTo();
    HashSet s = (HashSet)outMap.get(to);
    if (s == null) {
      s = new HashSet();
      outMap.put(to, s);
      s.add(edge);
      outEdges.add(edge);
    } else if (!s.contains(edge)) {
      s.add(edge);
      outEdges.add(edge);
    }
  }

  /**
   * Adds out edges.
   */
  public void addOutEdges(Collection edges) {
    Iterator i = edges.iterator();
    while (i.hasNext()) {
      Edge edge = (Edge)i.next();
      addOutEdge(edge);
    }
  }

  /**
   * Adds in edges.
   */
  public void addInEdges(Collection edges) {
    Iterator i = edges.iterator();
    while (i.hasNext()) {
      Edge edge = (Edge)i.next();
      addInEdge(edge);
    }
  }

  /**
   * Clears (removes) all the in edges.
   */
  public void clearInEdges() {
    inMap.clear();
    inEdges.clear();
  }

  /**
   * Clears (removes) all the out edges. This does not <b>NOT</b> remove
   * these cleared out edges as in edges from the Nodes on the other side
   * of these edges.
   */
  public void clearOutEdges() {
    outMap.clear();
    outEdges.clear();
  }

  /**
   * Removes the specified edge from the list of "in" edges. This does
   * <b>NOT</b> remove the edge as an out edge from the Node on the other
   * side of this Edge. This does not <b>NOT</b> remove
   * these cleared in edges as out edges from the Nodes on the other side
   * of these edges.
   *
   * @param edge the edge to remove
   */
  public void removeInEdge(Edge edge) {
    Node node = edge.getFrom();
    inMap.remove(node);
    inEdges.remove(edge);
  }

  /**
   * Removes the specified edge from the list of "out" edges. This does
   * <b>NOT</b> remove the edge as an in edge from the Node on the other
   * side of this Edge.
   *
   * @param edge the edge to remove
   */
  public void removeOutEdge(Edge edge) {
    Node node = edge.getTo();
    outMap.remove(node);
    outEdges.remove(edge);
  }

  /**
   * Returns the actual Node associated with this Node. This is for those
   * Nodes that wrap other Nodes, such as OvalNode and RectNode which
   * are primiarily drawable wrappers around Nodes. All network operations,
   * adding edges etc., should be done to the Node returned by this
   * method. DefaultNode merely returns itself.
   *
   * @deprecated No longer part of the Node interface so not necessary
   */
  public Node getNode() {
    return this;
  }

  /**
   * Gets a node at random from the list of out edges. This will return null
   * if there are no out edges.
   */
  public Node getRandomNodeOut() {
    if (outEdges.size() > 0) {
      int index = Random.uniform.nextIntFromTo(0, outEdges.size() - 1);
      Edge e = (Edge)outEdges.get(index);
      return e.getTo();
    }

    return null;
  }

  /**
   * Gets a node at random from the list of in edges. This will return null
   * if there are no in edges.
   */
  public Node getRandomNodeIn() {
    if (inEdges.size() > 0) {
      int index = Random.uniform.nextIntFromTo(0, inEdges.size() - 1);
      Edge e = (Edge)inEdges.get(index);
      return e.getFrom();
    }

    return null;
  }

  /**
   * Gets a node at random from the list of nodes that this node has an
   * edge from. This is identical to getRandomNodeIn().
   */
  public Node getRandomFromNode() {
    return getRandomNodeIn();
  }

  /**
   * Gets a node at random from the list of nodes that this node has an
   * edge to. This is identical to getRandomNodeOut().
   */
  public Node getRandomToNode() {
    return getRandomNodeOut();
  }

  /**
   * Removes all the edges that link from this Node to the specified node.
   * This does <b>NOT</b> remove these edges as from edges from the
   * specified node.
   */
  public void removeEdgesTo(Node node) {
    HashSet s = (HashSet)outMap.remove(node);
    if (s != null) outEdges.removeAll(s);
  }

  /**
   * Removes all the edges that link to this Node from the specified node.
   * This does <b>NOT</b> remove these edges as to edges from the
   * specified node.
   */
  public void removeEdgesFrom(Node node) {
    HashSet s = (HashSet)inMap.remove(node);
    if (s != null) inEdges.removeAll(s);
  }


  /**
   * Creates an out edge from this node to a randomly chosen node in
   * the specified list using the specified edge. If allowSelfLoops is
   * true, then the created Edge may return a self loop, assuming that
   * this Node is an element in the list. If not, then the returned
   * Edge will always be to some other Node. Note that this method
   * adds the edge as an in edge to the random node, and as an out
   * edge to this DefaultNode.
   *
   * @param list the list of nodes to create the Edge to.
   * @param edge the edge to use as the link
   * @param allowSelfLoops if true then self loops are allowed. If not the
   * self loops are disallowed.
   */
  public Edge makeRandomOutEdge(List list, Edge edge, boolean allowSelfLoops) {
    if (list.size() == 0) throw new IllegalArgumentException("list size must be greater that 0");

    int limit = list.size() - 1;
    Node to = null;
    if (allowSelfLoops) {
      int index = Random.uniform.nextIntFromTo(0, limit);
      to = (Node)list.get(index);
    } else {
      int index = Random.uniform.nextIntFromTo(0, limit);
      to = (Node)list.get(index);
      while (to == this) {
	index = Random.uniform.nextIntFromTo(0, limit);
	to = (Node)list.get(index);
      }
    }

    edge.setTo(to);
    edge.setFrom(this);
    to.addInEdge(edge);
    this.addOutEdge(edge);
    return edge;
  }

  /**
   * Creates an in edge to this node from a randomly chosen node in
   * the specified list using the specified edge. If allowSelfLoops is
   * true, then the created Edge may return a self loop, assuming that
   * this Node is an element in the list. If not, then the returned
   * Edge will always be to some other Node. Note that this method
   * adds the edge as an out edge to the random node, and as an in
   * edge to this DefaultNode.
   *
   * @param list the list of nodes to create the Edge from.
   * @param edge the edge to use as the link
   * @param allowSelfLoops if true then self loops are allowed. If not the
   * self loops are disallowed.
   */
  public Edge makeRandomInEdge(List list, Edge edge, boolean allowSelfLoops) {
    if (list.size() == 0) throw new IllegalArgumentException("list size must be greater that 0");

    Node from = null;
    int limit = list.size() - 1;
    if (allowSelfLoops) {
      int index = Random.uniform.nextIntFromTo(0, limit);
      from = (Node)list.get(index);
    } else {
      int index = Random.uniform.nextIntFromTo(0, limit);
      from= (Node)list.get(index);
      while (from == this) {
	index = Random.uniform.nextIntFromTo(0, limit);
	from = (Node)list.get(index);
      }
    }

    edge.setTo(this);
    edge.setFrom(from);
    from.addOutEdge(edge);
    this.addInEdge(edge);
    return edge;
  }

  /**
   * Returns the number of out edges contained by the Node.
   */
  public int getNumOutEdges() {
    return outEdges.size();
  }

  /**
   * Returns the number of in edges contained by the Node.
   */
  public int getNumInEdges() {
    return inEdges.size();
  }

  /**
   * Returns true if this DefaultNode has an Edge to the specified Node,
   * otherwise false.
   */
  public boolean hasEdgeTo(Node node) {
    return getEdgesTo(node) != null;
  }

  /**
   * Returns true if this DefaultNode has an Edge from the specified
   * Node, otherwise false.
   */
  public boolean hasEdgeFrom(Node node) {
    return getEdgesFrom(node) != null;
  }

  /**
   * Gets the out degree of this DefaultNode. Same as
   * getNumOutEdges().
   */
  public int getOutDegree() {
    return outEdges.size();
  }


  /**
   * Gets the in degree of this DefaultNode. Same as getNumInEdges().
   */
  public int getInDegree() {
    return inEdges.size();
  }

  /**
   * Returns the Edges from this Node to the specified Node.  This
   * will return null if no such Edges exist.
   */
  public HashSet getEdgesTo(Node node) {
    return (HashSet)outMap.get(node);
  }

  /**
   * Returns the Edges from the specified Node to this Node. This will
   * return null if no such Edges exits.
   */
  public HashSet getEdgesFrom(Node node) {
    return (HashSet)inMap.get(node);
  }
}
