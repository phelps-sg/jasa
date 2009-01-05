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
package uchicago.src.repastdemos.life;

import java.awt.Dimension;
import java.util.ArrayList;

import uchicago.src.collection.SparseObjectMatrix;
import uchicago.src.sim.gui.DisplayConstants;
import uchicago.src.sim.gui.DisplayInfo;
import uchicago.src.sim.gui.Displayable;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.Moveable;
import uchicago.src.sim.gui.Probeable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.gui.ViewEvent;

/**
 * Display class for displaying InfiniteLifeSpaces. This acts as
 * a window into the sparse matrix that is the InfiniteLifeSpace. The
 * origin and width of the window can be set while the model is running
 * and thus provide a scrolling window into the space.
 */
public class InfiniteSpaceDisplay implements Displayable, Probeable {

  protected boolean view = true;
  private SparseObjectMatrix matrix;
  //private int width, height,
  private int x, y;
  private int max;
  private Dimension size;
  private int rowLimit, colLimit;

  public InfiniteSpaceDisplay(SparseObjectMatrix matrix, int x, int y,
			      int width, int height, int max) {
    this.matrix = matrix;
    //this.width = width;
    //this.height = height;
    this.x = x;
    this.y = y;
    this.max = max;
    rowLimit = y + height;
    colLimit = x + width;

    size = new Dimension(width * DisplayConstants.CELL_WIDTH,
			 height * DisplayConstants.CELL_HEIGHT);
  }

  /**
   * Sets the origin and width of the window into the "infinite" space.
   */
  public void setViewWindow(int x, int y, int width, int height) {
    //this.width = width;
    //this.height = height;
    this.x = x;
    this.y = y;

    rowLimit = y + height;
    colLimit = x + width;

  }

  /**
   * Gets the x origin of the view window.
   */
  public int getViewX() {
    return x;
  }

  /**
   * Gets the y origin of the view window.
   */
  public int getViewY() {
    return y;
  }

  /**
   * Gets a list of objects at the specified screen (pixel) coordinates.
   */
  public ArrayList getObjectsAt(int mx, int my) {

    if (my != 0)
      my /= SimGraphics.getInstance().getCellHeightScale();
    if (mx != 0)
      mx /= SimGraphics.getInstance().getCellWidthScale();
    mx = mx + x;
    my = my + y;
    ArrayList list = new ArrayList();
    if (mx > max || mx < 0 || my > max || my < 0) {
      return list;
    } else {
      list.add(matrix.get(mx, my));
    }

    return list;
  }

  /**
   * Sets the new coordinates for specified moveable. This goes through
   * probeable as some translation between screen pixel coordinates and
   * the simulation coordinates may be necessary.
   *
   * @param moveable the moveable whose coordinates are changed
   * @param x the x coordinate in pixels
   * @param y the y coordinate in pixels
   */
  public void setMoveableXY(Moveable moveable, int x, int y ) {
    moveable.setX(x / SimGraphics.getInstance().getCellWidthScale());
    moveable.setY(y / SimGraphics.getInstance().getCellHeightScale());
  }

  // Displayable interface
  /**
   * Draws the objects (Drawables) contained by the view window.
   *
   * @param g the graphics context on which to draw
   */
  public void drawDisplay(SimGraphics g) {
    if (!view) {
      return;
    }
    int xTrans = g.getCellWidthScale();
    int yTrans = g.getCellHeightScale();
    // without synchronization get lots of concurrent modification errors.
    Drawable d = null;
    synchronized (matrix) {
      int ypos = 0;
      int xpos = 0;
      for (int j = y; j < rowLimit; j++) {
	for (int i = x; i < colLimit; i++) {
	  d = (Drawable)matrix.get(i, j);
	  if (d != null) {
	    //System.out.println(i + ", " + j);
	    // make the translation and set the coordinates
	    g.setDrawingCoordinates(xpos * xTrans, ypos * yTrans, 0);
	    //g.setDrawingCoordinates(0, 0, 0);
	    d.draw(g);
	  }

	  xpos++;
	}
	xpos = 0;
	ypos++;
      }
    }
  }


  public ArrayList getDisplayableInfo() {
    ArrayList list = new ArrayList();
    list.add(new DisplayInfo("", TOGGLE_VIEW, this));
    return list;
  }

  public void viewEventPerformed(ViewEvent evt) {
    view = evt.showView();
  }

  public Dimension getSize() {
    return size;
  }

  public void reSize (int width, int height) {
    //this.width = width;
    //this.height = height;
  }
}
