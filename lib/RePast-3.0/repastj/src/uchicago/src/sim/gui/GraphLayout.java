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

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
//import uchicago.src.sim.gui.Network2DDisplay;

/**
 * An Interface for laying out graphs (ie networks of nodes and edges). This
 * is used by Network2DDisplay as the source of nodes and edges to draw.
 * The intention here is that the layout algorithm is separated from the
 * actual drawing code. Concrete implementations should work with lists of
 * DrawableNonGridNodes. These nodes are expected to draw themselves, but
 * have their x and y coordinates set via whatever layout algorithm is
 * used by the implementing class.<p>
 *
 * GraphLayout extends ActionListener. The intent here is that
 * computationally intensive layouts can listen for the Controller's
 * stop, pause, etc. button clicks and interrupt themselves. See the source
 * code to KamadaGraphLayout for an example of this. 
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public interface GraphLayout extends ActionListener {

  /**
   * Gets the height of the area on which to layout the graph.
   */
  public int getHeight();

  /**
   * Gets the width of the area on which to layout the graph.
   */
  public int getWidth();

  /**
   * Sets the list of nodes to be laid out by this GraphLayout.
   */
  public void setList(List listOfNodes);

  /**
   * Appends a list of nodes to the current list of nodes to be laid
   * out by this GraphLayout.
   */
  public void appendToList(List listOfNodes);

  /**
   * Appends the specified nodes to the list of nodes to be laid out by
   * this GraphLayout.
   */
  public void appendToList(DrawableNonGridNode node);

  /**
   * Updates the layout of this graph by setting the x, y coordinate of
   * each DrawableNonGridNode in the current list of nodes.
   */
  public void updateLayout();

  /**
   * Gets the list of nodes.
   */
  public ArrayList getNodeList();

  /**
   * Sets whether the display will update or not when updateLayout is
   * called. This allows for the regular scheduling of updateLayout
   * together with the ability to skip the actual execution of the method.
   *
   * @param doUpdate if true, this GraphLayout will perform the layout when
   * updateLayout is called. If false, then the layout will not be
   * performed.
   */
  public void setUpdate(boolean doUpdate);
}
