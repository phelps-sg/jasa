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

/**
 * Interface for a drawable 2 dimensional node that occupies a cell in a grid.
 * Nodes to be displayed in a Network2DGridDisplay should implement this
 * interface.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public interface Drawable2DGridNode {

  /**
   * Returns an ArrayList of DrawableEdges as the outgoing edges from
   * this Drawable2DGridNode.
   */
  public ArrayList getOutEdges();

  /**
   * Returns the x (column) coordinate of the cell in which this node
   * resides. This coordinate will be an int. The method returns a double
   * for compatibility between Grid and non-Grid nodes.
   */
  public double getX();

  /**
   * Returns the y (row) coordinate of the cell in which this node
   * resides. This coordinate will be an int. The method returns a double
   * for compatibility between Grid and non-Grid nodes.
   */
  public double getY();

  /**
   * Draws this Drawable2DGridNode.
   *
   * @param g the SimGraphics object used for drawing
   */
  public void draw(SimGraphics g);
}
