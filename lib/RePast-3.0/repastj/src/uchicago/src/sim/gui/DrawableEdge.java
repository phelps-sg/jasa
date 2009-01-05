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

import uchicago.src.sim.network.Edge;

/**
 * Edges that wish to be drawn must implement this interface.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public interface DrawableEdge extends Edge {

  /**
   * Called by a Network*Display instructing the edge to draw itself.
   * The coordinates are screen coordinates and correspond to the center of
   * the nodes. Edges to be drawn by a Network*Display must implement this
   * interface. Typicaly, some line drawing method in SimGraphics is called,
   * passing the remaining arguments to that method. For example<br>
   * <code><pre>
   * public void draw(SimGraphics g, int fromX, int toX, int fromY, int toY) {<br>
   *    g.drawLink(Color.red, fromX, toX, fromY, toY);<br>
   * }<br>
   * </pre></code>
   * The x and y coordinates are calculated by the Display from the position
   * of the nodes to which this is an edge. 
   */
  public void draw(SimGraphics g, int fromX, int toX, int fromY, int toY);

}
