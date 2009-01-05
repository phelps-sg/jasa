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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

/**
 * Abstract class for objects inhabiting non-discrete spaces and that wish
 * to be drawn by a Display. This is particularly useful for nodes. Provides
 * methods for setting the color, label, border size and label, and font
 * of the item. All size variables are in screen coordinates.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see OvalNode
 * @see RectNode
 */

public abstract class DrawableItem implements NonGridDrawable {

  protected String label = null;
  protected Font font = new Font("monospace", Font.BOLD, 9);
  protected Color labelColor = Color.yellow;
  protected Color color = Color.blue;
  protected Color borderColor = Color.white;
  protected int borderWidth = 0;
  protected int width = 8;
  protected int height = 8;
  protected boolean recalc = true;
  protected boolean allowResizing = true;
  protected boolean hollow = false;

  protected BasicStroke stroke = null;

  /**
   * Should this DrawableItem be resized to so that entire label is visible.
   *
   * @param val true if this should allow resizing for label fit, otherwise
   * false
   */

  public void allowResizing(boolean val) {
    if (val != allowResizing) {
      recalc = true;  
      allowResizing = val;
    }
  }

  /**
   * Sets the label.
   *
   * @param l the new label
   */
  public void setLabel(String l) {
    if (l != null) {
      if (!l.equals(label)) {
	label = l;
	recalc = true;
      }
    } else {
      label = null;
    }
  }

  /**
   * Sets the label color.
   *
   * @param c the new label color
   */
  public void setLabelColor(Color c) {
    labelColor = c;
  }

  /**
   * Returns the current label color.
   */
  public Color getLabelColor() {
    return labelColor;
  }


  /**
   * Sets the font for the label
   *
   * @param font the new label font
   */
  public void setFont(Font font) {
    if (!font.equals(this.font)) {
      this.font = font;
      recalc = true;
    }
  }

  /**
   * Returns the current label font.
   */
  public Font getFont() {
    return font;
  }

  /**
   * Sets the color for this DrawableItem.
   *
   * @param c the new color
   */
  public void setColor(Color c) {
    color = c;
  }

  /**
   * Returns the current color.
   */
  public Color getColor() {
    return color;
  }

  /**
   * Sets the border color for this DrawableItem.
   *
   * @param c the new border color
   */
  public void setBorderColor(Color c) {
    borderColor = c;
  }

  /**
   * Returns the current border color.
   */
  public Color getBorderColor() {
    return borderColor;
  }

  /**
   * Sets the border width for this DrawableItem.
   *
   * @param width the new border width
   */
  public void setBorderWidth(int width) {
    if (width < 0)
      width = 0;

    if (borderWidth != width) {
      borderWidth = width;
      stroke = new BasicStroke(borderWidth);
      recalc = true;
    }
  }

  /**
   * Returns the current border width.
   */
  public int getBorderWidth() {
    return borderWidth;
  }

  /**
   * Sets the width of this DrawableItem in screen coordinates. The new width
   * won't be respected if allowsResizing is true (i.e. the width is calculated
   * to allow the entire label to be seen.
   *
   * @param w the new width
   */
  public void setWidth(int w) {
    width = w;
    recalc = true;
  }

  /**
   * Sets the height of this DrawableItem in screen coordinates. The new height
   * won't be respected if allowsResizing is true (i.e. the height is
   * calculated to allow the entire label to be seen.
   *
   * @param h the new height
   */
  public void setHeight(int h) {
    height = h;
    recalc = true;
  }

  /**
   * Sets the size of this DrawableItem in screen coordinates. The new width
   * and height won't be respected if allowsResizing is true (i.e. the width
   * is calculated to allow the entire label to be seen.
   *
   * @param w the new width
   * @param h the new height
   */
  public void setSize(int w, int h) {
    width = w;
    height = h;
    recalc = true;
  }

  /**
   * Sets the size of this DrawableItem in screen coordinates. The new width
   * and height won't be respected if allowsResizing is true (i.e. the width
   * is calculated to allow the entire label to be seen.
   *
   * @param size the new size
   */
  public void setSize(Dimension size) {
    width = size.width;
    height = size.height;
    recalc = true;
  }

  // Non grid drawable interface
  /**
   * Gets the width of this DrawableItem.
   */
  public int getWidth() {
    return width;
  }

  /**
   * Gets the height of this DrawableItem.
   */
  public int getHeight() {
    return height;
  }

  public boolean isHollow () {
    return hollow;
  }

  public void setHollow (boolean hollow) {
    this.hollow = hollow;
  }

  /**
   * Calculates and sets the size of this DrawableItem insuring that the
   * entire label can be seen, if allowResizing is true.
   */
  public void calcSize(SimGraphics g) {
    // insure that the width and height are enough to show label and
    // border
    if (recalc && allowResizing) {
      if (font == null || label == null) {
        width = 8 + borderWidth;
        height = 10 + borderWidth;
      } else {
        Rectangle2D bounds = g.getStringBounds(label, font);
        width = (int)bounds.getWidth() + 6 + borderWidth;
        height = (int)bounds.getHeight() + 8 + borderWidth;
      }

      recalc = false;
    }
  }
}
