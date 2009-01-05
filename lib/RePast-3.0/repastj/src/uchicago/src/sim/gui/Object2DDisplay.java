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
import java.util.Collection;
import java.util.Iterator;

import uchicago.src.collection.BaseMatrix;
import uchicago.src.sim.space.Discrete2DSpace;
//import cern.colt.matrix.impl.DenseObjectMatrix2D;

/**
 * Displays Discrete2DSpaces and the objects contained within them. Implements
 * probeable so that the objects within the Discrete2DSpace can be probed. All
 * the objects within the space are expected to have implemented Drawable.
 * Objects are displayed on a grid, a Discrete2DSpace.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class Object2DDisplay extends Display2D implements Probeable {

  protected Collection objsToDraw = null;
  protected boolean view = true;

  /**
   * Creates an Object2DDisplay for displaying the specified Discrete2DSpace.
   *
   * @param grid the space to display
   */
  public Object2DDisplay(Discrete2DSpace grid) {
    super(grid);
  }

  /**
   * Sets the list of objects to display. If a space is sparsely populated then
   * rather than iterating over the entire space looking for objects to draw,
   * this Object2DDisplay can iterate only over the specified list and draw
   * those objects. This list is expected to contain objects implementing the
   * drawable interface.
   *
   * @param objects the collection of objects to draw
   */
  public void setObjectList(Collection objects) {
    objsToDraw = objects;
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

    ArrayList list = new ArrayList();
    if (x > grid.getSizeX() || x < 0 || y > grid.getSizeY() || y < 0) {
      return list;
    } else {
      list.add(grid.getObjectAt(x, y));
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
    moveable.setX(x / SimGraphics.getInstance().getCellWidthScale()); //DisplayConstants.CELL_WIDTH);
    moveable.setY(y / SimGraphics.getInstance().getCellHeightScale()); //DisplayConstants.CELL_HEIGHT);
  }

  // Displayable interface
  /**
   * Draws the contained space, either by iterating over the entire space
   * and calling draw(SimGraphics g) on the Drawables contained therein, or
   * by iterating through a list of Drawables and calling draw(SimGraphics g)
   * on them. This method should never by called directly by a user.
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
      BaseMatrix matrix = grid.getMatrix();
      synchronized (matrix) {
        for (int i = 0, n = grid.getSizeX(); i < n; i++) {   //getSizeX(); i++) {
          for (int j = 0, m = grid.getSizeY(); j < m; j++) {  //getSizeY(); j++ {
            d = (Drawable)matrix.get(i, j);
            if (d != null) {
              // make the translation and set the coordinates
              g.setDrawingCoordinates(i * xTrans, j * yTrans, 0);
              d.draw(g);
            }
          }
        }
      }
    } else {
      //ArrayList t;
      //synchronized(objsToDraw) {
      //  t = new ArrayList(objsToDraw);
      //}
      Iterator li = objsToDraw.iterator();
      while (li.hasNext()) {
        d = (Drawable)li.next();
        g.setDrawingCoordinates(d.getX() * xTrans, d.getY() * yTrans, 0);
        d.draw(g);
      }
    }
  }

  /**
   * Gets a list of the DisplayInfo object associated with this Object2DDisplay.
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
}
