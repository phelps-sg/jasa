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

//import java.util.List;
import java.awt.Dimension;
import java.util.ArrayList;


/**
 * Abstract class upon which custom displays can be built.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public abstract class Display implements Displayable {

  protected boolean view = true;
  protected int height, width;

  /**
   * Constructs a Display with the specified width and height.
   *
   * @param width the width of the display
   * @param height the height of the display
   */
  public Display(int width, int height) {
    this.width = width;
    this.height = height;
  }
  
  /**
   * Gets the size of this Display.
   */
  public Dimension getSize() {
    return new Dimension(width, height);
  }

  /**
   * Does the actual drawing using the SimGraphics parameter.
   *
   * @param g the graphics context with which to draw
   */
  public abstract void drawDisplay(SimGraphics g);

  /**
   * Gets a list of the DisplayInfo object associated with this Display.
   */
  public ArrayList getDisplayableInfo() {
    ArrayList list = new ArrayList();
    list.add(new DisplayInfo("", TOGGLE_VIEW, this));
    return list;
  }

  /**
   * Invoked when a viewEvent for this display is fired by the
   * DisplaySurface.
   */
  public void viewEventPerformed(ViewEvent evt) {
    view = evt.showView();
  }

   /**
   * Resizes the display to this new pixel width and pixel height.
   */
  public void reSize(int width, int height) {
    this.width = width;
    this.height = height;
   }
}

