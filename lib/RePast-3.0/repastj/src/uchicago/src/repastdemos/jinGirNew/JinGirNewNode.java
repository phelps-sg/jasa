
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
package uchicago.src.repastdemos.jinGirNew;

import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.gui.RectNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;
import uchicago.src.sim.network.DefaultNode;
import uchicago.src.sim.network.Edge;
import uchicago.src.sim.util.Random;

/**
 * The agent class for the JinGirNew simluation. JinGirNode extends
 * DefaultDrawableNode and so it is both a DefaultNode and a
 * DrawableNonGridNode. All of the important behavoir here as to do
 * with creating new links.
 *
 * @version $Revision$ $Date$
 *
 */

public class JinGirNewNode extends DefaultDrawableNode {
  
  /*
   * No arg constructor so that a JinGirNode can be created from a
   * a file.
   */
  public JinGirNewNode() {}

  public JinGirNewNode(int x, int y) {
    init(x, y);
  }

  /**
   * Initialize this JinGirNewNode, this takes the place of the
   * constructor when a JinGirNewNode is created from a file using its
   * no-arg constructor.
   */
  public void init(int x, int y) {
    RectNetworkItem rect = new RectNetworkItem(x, y);
    setDrawable(rect);
  }

  /**
   * Makes an edge to the specified node and from the specifed node to
   * thisJinGirNewNode if both nodes do not already have edges to each
   * other and if adding the edge keeps their degrees less than
   * maxDegree. The edges is displayed in the specified color.
   */
  public void makeEdgeToFrom(DefaultNode node, int maxDegree, Color color) {
    if ((! hasEdgeTo(node)) && getOutDegree() < maxDegree &&
	node.getOutDegree() < maxDegree) {
      
      Edge edge = new JinGirNewEdge(this, node, color);
      addOutEdge(edge);
      node.addInEdge(edge);
      Edge otherEdge = new JinGirNewEdge(node, this, color);
      node.addOutEdge(otherEdge);
      addInEdge(otherEdge);
    }
  }

  /**
   * Creates a new edge between this JinGirNewNode and a node chosen
   * at random from the specified list. This edge is created via
   * <tt>makeEdgeFromTo</tt> and so those conditions must be met as
   * well.
   */
  public void meetRandom(ArrayList list, int maxDegree) {
    int index = Random.uniform.nextIntFromTo(0, list.size() - 1);
    JinGirNewNode node = (JinGirNewNode)list.get(index);
    while (this.equals(node)) {
      index = Random.uniform.nextIntFromTo(0, list.size() - 1);
      node = (JinGirNewNode)list.get(index);
    }

    makeEdgeToFrom(node, maxDegree, Color.red);
  }

  /**
   * Creates an edge between two nodes that both link to this
   * JinGirNewNode.  This edge is created via <tt>makeEdgeFromTo</tt>
   * and so those conditions must be met as well.
   */
  public void meetNeighbor(int maxDegree) {

    if (getOutDegree() > 1) {
      JinGirNewNode jNode = (JinGirNewNode)getRandomNodeOut();
      JinGirNewNode kNode = (JinGirNewNode)getRandomNodeOut();
    
      while (jNode.equals(kNode)) {
	kNode = (JinGirNewNode)getRandomNodeOut();
      }

      jNode.makeEdgeToFrom(kNode, maxDegree, Color.green);
    }
  }

  /**
   * Removes a link between this JinGirNewNode and one chosen at
   * random from those linked to this JinGirNewNode.
   */
  public void removeFriend() {
    JinGirNewNode jNode = (JinGirNewNode)getRandomNodeOut();

    // will be null if no outEdges.
    if (jNode != null) {
      removeEdgesTo(jNode);
      jNode.removeEdgesFrom(this);
      removeEdgesFrom(jNode);
      jNode.removeEdgesTo(this);
    }
  }
}
