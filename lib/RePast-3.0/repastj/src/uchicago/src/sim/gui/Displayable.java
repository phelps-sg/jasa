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

import java.awt.Dimension;
import java.util.ArrayList;


/**
 * Interface for objects that can be displayed on a DisplaySurface.
 * DisplaySurface calls the drawDisplay method to draw the Displayable object.
 * Consequently, a user should never need to call the drawDisplay method. The
 * Display* classes implement this interface. 
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public interface Displayable {

  public static final int TOGGLE_VIEW = -1;
  public static final int TOGGLE_NODES = 0;
  public static final int TOGGLE_LINKS = 1;
  public static final int TOGGLE_WRAP = 2;
  public static final int TOGGLE_UPDATE_LAYOUT = 3;
  
  /**
   * Draws whatever implements this interface on the SimGraphics object
   */
  public void drawDisplay(SimGraphics g);

  /**
   * Returns an ArrayList of DisplayInfo objects used by a DisplaySurface to
   * create the View menu for that displayable
   *
   * @see DisplayInfo
   */
  public ArrayList getDisplayableInfo();

  /**
   * Invoked when a display event occurs, through the view menu of a
   * display surface for example. Not used to do the actual drawing.
   */
  public void viewEventPerformed(ViewEvent evt);

  /**
   * Gets the size of the display.
   */
  public Dimension getSize();

  /**
   * Resizes the display to this new pixel width and pixel height.
   *
  public void reSize(int width, int height);
   */
}

