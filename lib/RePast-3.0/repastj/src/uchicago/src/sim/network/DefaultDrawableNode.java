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
import java.awt.Font;
import java.awt.Point;

import uchicago.src.sim.gui.Drawable2DGridNode;
import uchicago.src.sim.gui.DrawableNonGridNode;
import uchicago.src.sim.gui.Moveable;
import uchicago.src.sim.gui.NetworkDrawable;
import uchicago.src.sim.gui.RectNetworkItem;
import uchicago.src.sim.gui.SimGraphics;

/**
 *
 * @version $Revision$ $Date$
 */

public class DefaultDrawableNode extends DefaultNode implements
  DrawableNonGridNode, Moveable, Drawable2DGridNode
{

  protected NetworkDrawable item;

  /**
   * Creates this DefaultDrawableNode. This DefaultDrawableNode will
   * created with an x and y coordinate of 0,0 and as a Rectangular
   * node.
   */
  public DefaultDrawableNode() {
    item = new RectNetworkItem(0, 0);
  }

  public DefaultDrawableNode(String label, NetworkDrawable nDrawable) {
    super();
    item = nDrawable;
    if (label != null) setNodeLabel(label);
  }

  public DefaultDrawableNode(NetworkDrawable nDrawable) {
    super();
    item = nDrawable;
  }

  /**
   * Sets the drawable for this DefaultNetworkNode. All the properties
   * (color, borderWidth, etc.) of the current NetworkDrawable will be
   * copied to the specified NetworkDrawable.
   */
  public void setDrawable(NetworkDrawable nDrawable) {
    nDrawable.setColor(item.getColor());
    nDrawable.setBorderWidth(item.getBorderWidth());
    if (label.length() > 0) nDrawable.setLabel(getNodeLabel());
    nDrawable.setLabelColor(item.getLabelColor());
    nDrawable.setFont(item.getFont());
    nDrawable.setBorderColor(item.getBorderColor());
    item = nDrawable;
  }

  public void setDrawableNoCopy(NetworkDrawable nDrawable) {
    item = nDrawable;
    if (label.length() > 0) item.setLabel(getNodeLabel());
  }

  /**
   * Calculates the size of this DefaultDrawableNode. This is used to
   * ensure that the DefaultDrawableNode is large enough to accomodate its
   * label. This method is used internally by RePast.
   */
  public void calcSize(SimGraphics g) {
    item.calcSize(g);
  }

  /**
   * Gets the actual network node. The intention here is to get the actual
   * node if a node is being wrapped by an implementor of the
   * DrawableNonGridNodeInterface.
   * @deprecated
   */
  public Node getNode() {
    return this;
  }


  /**
   * Gets the width of this DefaultDrawableNode.
   */
  public int getWidth() {
    return item.getWidth();
  }

  /**
   * Sets the width of this DefaultDrawableNode. By default this will
   * be ignored and the DefaultDrawableNode will be sized to fit its
   * label. Do allowsResizing(false) to set the width and height manually.
   */
  public void setWidth(int width) {
    item.setWidth(width);
  }

  /**
   * Gets the height of this DefaultDrawableNode.
   */
  public int getHeight() {
    return item.getHeight();
  }

  /**
   * Sets the height of this DefaultDrawableNode.By default this will
   * be ignored and the DefaultDrawableNode will be sized to fit its
   * label. Do allowsResizing(false) to set the width and height
   * manually.
   */
  public void setHeight(int height) {
    item.setHeight(height);
  }

  /**
   * Sets whether or not this DefaultDrawableNode will be resized to fit
   * its label. If this is set to true, any setting of the width or height
   * is ignored.
   */
  public void allowResizing(boolean val) {
    item.allowResizing(val);
  }

   /**
   * Sets the label for this DefaultDrawableNode.
   */
  public void setNodeLabel(String nlabel) {
    super.setNodeLabel(nlabel);
    if (item != null) item.setLabel(nlabel);
  }

  /**
   * Sets the border color for this DefaultDrawableNode.
   */
  public void setBorderColor(Color c) {
    item.setBorderColor(c);
  }

  /**
   * Gets the border color for this DefaultDrawableNode.
   */
  public Color getBorderColor() {
    return item.getBorderColor();
  }

  /**
   * Sets the border width for this DefaultDrawableNode.
   */
  public void setBorderWidth(int width) {
    item.setBorderWidth(width);
  }

  /**
   * Gets the border width for this DefaultDrawableNode.
   */
  public int getBorderWidth() {
    return item.getBorderWidth();
  }

  /**
   * Sets the label font for this DefaultDrawableNode.
   */
  public void setFont(Font font) {
    item.setFont(font);
  }

  /**
   * Gets the label font for this DefaultDrawableNode.
   */
  public Font getFont() {
    return item.getFont();
  }

  /**
   * Sets label color for this DefaultDrawableNode.
   */
  public void setLabelColor(Color c) {
    item.setLabelColor(c);
  }

  /**
   * Gets label color for this DefaultDrawableNode.
   */
  public Color getLabelColor() {
    return item.getLabelColor();
  }

  /**
   * Sets the color of this DefaultDrawableNode.
   */
  public void setColor(Color c) {
    item.setColor(c);
  }

  /**
   * Gets the color of this DefaultDrawableNode.
   */
  public Color getColor() {
    return item.getColor();
  }

  /**
   * Gets the x coordinate for this node.
   */
  public double getX() {
    return item.getX();
  }

  /**
   * Gets the y coordinate for this node.
   */
  public double getY() {
    return item.getY();
  }

  /**
   * Sets the x coordinate for this node. Used by GraphLayouts.
   */
  public void setX(double x) {
    item.setX(x);
  }

  /**
   * Sets the y coordinate for this node. Used by GraphLayouts.
   */
  public void setY(double y) {
    item.setY(y);
  }

   /**
   * Does this object contain the point specified by p. This is necessary
   * for probing when returning what is in a cell on a grid is impossible.
   *
   * @param p the point to test for.
   */
  public boolean contains(Point p) {
    return item.contains(p);
  }

  public void draw(SimGraphics g) {
    item.draw(g);
  }

  // moveable interface
  /**
   * Implements the moveable interface, setting the x coordinate to
   * some integer value.
   */
  public void setX(int val) {
    item.setX(val);
  }

  /**
   * Implements the moveable interface, setting the y coordinate to
   * some integer value.
   */
  public void setY(int val) {
    item.setY(val);
  }
}
