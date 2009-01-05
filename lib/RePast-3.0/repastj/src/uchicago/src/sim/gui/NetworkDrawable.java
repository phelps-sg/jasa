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

import java.awt.Color;
import java.awt.Font;

/**
 *
 * @version $Revision$ $Date$
 */
public interface NetworkDrawable extends NonGridDrawable {

  // note that NonGridDrawable provides the getX and getY methods.

  /**
   * Sets the X coordinate for the NetworkDrawable.
   */
  public void setX(double val);
  
  /**
   * Sets the Y coordinate for the NetworkDrawable.
   */
  public void setY(double val);

  /**
   * Calculates the size of this  NetworkDrawable. This is used to
   * ensure that the NetworkDrawable is large enough to accomodate its
   * label. This method is used internally by RePast.
   */
  public void calcSize(SimGraphics g);

  /**
   * Sets the label for this NetworkDrawable.
   */
  public void setLabel(String label);

  /**
   * Sets the border color for this NetworkDrawable.
   */
  public void setBorderColor(Color c);

  /**
   * Gets the border color for this NetworkDrawable.
   */
  public Color getBorderColor();

  /**
   * Sets the border width for this NetworkDrawable.
   */
  public void setBorderWidth(int width);

  /**
   * Gets the border width for this NetworkDrawable.
   */
  public int getBorderWidth();

  /**
   * Sets the label font for this NetworkDrawable.
   */
  public void setFont(Font font);

  /**
   * Gets the label font for this NetworkDrawable.
   */
  public Font getFont();

  /**
   * Sets label color for this NetworkDrawable.
   */
  public void setLabelColor(Color c);

  /**
   * Gets label color for this NetworkDrawable.
   */
  public Color getLabelColor();

  /**
   * Sets color of this NetworkDrawable.
   */
  public void setColor(Color c);

  /**
   * Gets the color of this NetworkDrawable.
   */
  public Color getColor();

  /**
   * Gets the width of this NetworkDrawable.
   */
  public int getWidth();

  /**
   * Sets the width of this NetworkDrawable.
   */
  public void setWidth(int width);

  /**
   * Gets the height of this NetworkDrawable.
   */
  public int getHeight();

  /**
   * Sets the height of this NetworkDrawable.
   */
  public void setHeight(int height);

  /**
   * Sets whether or not this NetworkDrawable will be resized to fit
   * its label. If this is set to true, any setting of the width or height
   * is ignored.
   */
  public void allowResizing(boolean allow);
}
