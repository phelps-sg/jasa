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

import java.awt.Color;

import uchicago.src.sim.gui.DrawableEdge;
import uchicago.src.sim.gui.SimGraphics;

/**
 * A drawable edge between two nodes. A DefaultEdge implementing
 * DrawableEdge.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.sim.network.Node
 */

public class DefaultDrawableEdge extends DefaultEdge implements DrawableEdge {

  private boolean isDirected = true;
  private Color color = Color.red;


  /**
   * Constructs a DefaultDrawableEdge without any connecting Nodes.
   * This is primarily used to load this class in a "bean-like"
   * fashion.
   */
  public DefaultDrawableEdge() {
    super();
  }

  /**
   * Construct an edge from the specified to node to the specified node with
   * the specified label, and a default strength of 1.0.
   * This edge is not automatically added to the nodes,
   * and so each node must add the edge. The edge will be drawn as directed
   * and in red.
   *
   * @param from the from Node
   * @param to the to Node
   * @param the label for this edge
   */
  public DefaultDrawableEdge (Node from, Node to, String label) {
    super(from, to, label, 1.0f);
  }

  /**
   * Construct an edge from the specified to node to the specified node with
   * the specified label, and the specified strength.
   * This edge is not automatically added to the nodes, and so each node must
   * add the edge. The actual end Nodes for this edge are those returned by
   * the Node.getNode() calls. The edge will be drawn as directed
   * and in red.
   *
   * @param from the from Node
   * @param to the to Node
   * @param label the label for this edge
   * @param strength the strength of this edge
   */
  public DefaultDrawableEdge(Node from, Node to, String label, float strength) {
    super(from, to, label, strength);
  }

  /**
   * Construct an edge from the specified to node to the specified node with
   * a blank label. This edge is not automatically added to the nodes,
   * and so each node must add the edge. The edge will be drawn as directed
   * and in red.
   *
   * @param from the from Node
   * @param to the to Node
   */

  public DefaultDrawableEdge(Node from, Node to) {
    super(from, to, "");
  }

  /**
   * Sets the color of this DefaultDrawableEdge.
   */
  public void setColor(Color val) {
    color = val;
  }

  /**
   * Sets whether this DefaultDrawableEdge is drawn as directed or not.
   */
  public void setDrawDirected(boolean val) {
    isDirected = val;
  }


  /**
   * Draws this DefaultDrawableEdge.
   */
  public void draw(SimGraphics g, int fromX, int toX, int fromY, int toY) {
    if (isDirected) {
      g.drawDirectedLink(color, fromX, toX, fromY, toY);
    } else {
      g.drawLink(color, fromX, toX, fromY, toY);
    }
  }
}
