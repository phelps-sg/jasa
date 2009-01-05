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
import java.util.List;
import java.util.ListIterator;

import uchicago.src.sim.space.IMulti2DGrid;


/**
 * Displays IMulti2DGrid-s and the objects contained within them. Implements
 * probeable so that the objects within the IMulti2DGrid can be probed. All
 * the objects within the space are expected to have implemented Drawable.
 * Objects are displayed on a grid, an IMulti2DGrid.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.sim.space.IMulti2DGrid
 */
public class MultiObject2DDisplay extends Object2DDisplay {

  /**
   * Creates a MultiObject2DDisplay for displaying the specified
   * IMulti2DGrid.
   *
   * @param grid the space to display
   */
  public MultiObject2DDisplay(IMulti2DGrid grid) {
    super(grid);
  }



  // Probeable interface
  /**
   * Gets the object at the specified screen coordinates for probing.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @return the list of objects at x,y
   */
  public ArrayList getObjectsAt(int x, int y) {

    if (y != 0)
      y /= SimGraphics.getInstance().getCellHeightScale(); //DisplayConstants.CELL_HEIGHT;
    if (x != 0)
      x /= SimGraphics.getInstance().getCellWidthScale(); //DisplayConstants.CELL_WIDTH;

    if (x > grid.getSizeX() || x < 0 || y > grid.getSizeY() || y < 0) {
      return new ArrayList();
    }

    List list = ((IMulti2DGrid) grid).getObjectsAt(x, y);
    if (list != null)
      return new ArrayList(list);
    else
      return new ArrayList();
  }

  // Displayable interface
  /**
   * Draws the contained space, either by iterating over the entire space
   * and calling draw(SimGraphics g) on the Drawables contained therein, or
   * by iterating through a list of Drawables and calling draw(SimGraphics g)
   * on them. This method should never by called directly by a user.<p>
   *
   * When drawing IMulti2DGrids and drawing each cell only the "top"
   * Drawable in each cell is drawn. For ordered grids this will be
   * the last object added to that cell. For unordered grids the "top"
   * object is undetermined.
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
    if (objsToDraw == null) {
      IMulti2DGrid mGrid = (IMulti2DGrid) grid;
      synchronized (mGrid) {
        int xSize = mGrid.getSizeX();
        int ySize = mGrid.getSizeY();
        for (int i = 0; i < xSize; i++) {
          for (int j = 0; j < ySize; j++) {
            if (mGrid.getCellSizeAt(i, j) > 0) {
              List l = mGrid.getObjectsAt(i, j);
              d = (Drawable) l.get(l.size() - 1);
              g.setDrawingCoordinates(i * xTrans, j * yTrans, 0);
              d.draw(g);
            }
          }
        }
      }
    } else {
      ArrayList t;
      synchronized (objsToDraw) {
        t = new ArrayList(objsToDraw);
      }
      ListIterator li = t.listIterator();
      while (li.hasNext()) {
        d = (Drawable) li.next();
        g.setDrawingCoordinates(d.getX() * xTrans, d.getY() * yTrans, 0);
        d.draw(g);
      }
    }
  }
}
