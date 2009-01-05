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

import uchicago.src.sim.space.Discrete2DSpace;

/**
 * Base class for all discrete 2D displays. Encapsulates a
 * Discrete2DSpace and allows that space to be displayed on a
 * DisplaySurface. The typical user should not use this class
 * directly. The objects in the Discrete2DSpace are displayed in cells
 * whose dimensions can be programmaticly and graphicaly defined. The
 * size (that is, the size in pixels) of a Display2D is the size of
 * the Discrete2DSpace * the cell size.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see DisplaySurface
 * @see uchicago.src.sim.space.Discrete2DSpace
 */
public abstract class Display2D implements Displayable {

  protected Discrete2DSpace grid = null;
  protected Dimension size;

  /**
   * Constructs a Display2D with specified Discrete2DSpace to display
   *
   * @param grid the space to display
   */
  public Display2D(Discrete2DSpace grid) {
    this.grid = grid;
    size = new Dimension(grid.getSizeX() * DisplayConstants.CELL_WIDTH,
                         grid.getSizeY() * DisplayConstants.CELL_HEIGHT);
  }

  /**
   * Gets the Discrete2DSpace that is being displayed
   */
  public Discrete2DSpace getGrid() {
    return grid;
  }

  /**
   * Gets the size of the display. The size of the display is the size of the
   * contained Discrete2DSpace * DisplayConstants.CELL_WIDTH/HEIGHT
   */
  public Dimension getSize() {
    return size;
  }

  /**
   * Resizes the display to the appropriate width and height adjusting
   * the CELL SIZE as appropriate.
   */
  public void reSize(int width, int height) {
    int wDiff = (width - size.width) / grid.getSizeX();
    int hDiff = (height - size.height) / grid.getSizeY();

    DisplayConstants.CELL_WIDTH = DisplayConstants.CELL_WIDTH + wDiff;
    DisplayConstants.CELL_HEIGHT = DisplayConstants.CELL_HEIGHT + hDiff;

    System.out.println ("old size = " + size);
    System.out.println ("DisplayConstants.CELL_WIDTH = " + DisplayConstants.CELL_WIDTH);
    System.out.println ("DisplayConstants.CELL_HEIGHT = " + DisplayConstants.CELL_HEIGHT);

    size = new Dimension(grid.getSizeX() * DisplayConstants.CELL_WIDTH,
                         grid.getSizeY() * DisplayConstants.CELL_HEIGHT);

    System.out.println ("DisplayConstants.CELL_WIDTH = " + DisplayConstants.CELL_WIDTH);
    System.out.println ("DisplayConstants.CELL_HEIGHT = " + DisplayConstants.CELL_HEIGHT);
    System.out.println ("new size = " + size);
  }

  // Displayable interface
  public abstract void drawDisplay(SimGraphics g);
}
