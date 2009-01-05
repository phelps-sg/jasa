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

import uchicago.src.collection.BaseMatrix;
import uchicago.src.collection.DoubleMatrix;
import uchicago.src.sim.space.Discrete2DSpace;

/**
 * Displays 2d arrays of values (Integers or ints). As a Displayable this class
 * iterates through a Discrete2DSpace, turns the integers into
 * colors according to a {@link uchicago.src.sim.gui.ColorMap ColorMap}, and
 * draws these colors in the appropriate cells.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class Value2DDisplay extends Display2D implements Probeable {

  private ColorMap colorMap;

  private int mapping = 1;
  private int mappingColor = 0;
  //private boolean colorMapping = false;
  private boolean zeroTrans = false;

  private boolean view = true;
  private Drawer drawer = new Drawer();

  private class Drawer {
    public void draw(SimGraphics g, int i, int j, int cv) {


      g.setDrawingCoordinates(i * g.getCellWidthScale(),
                              j * g.getCellHeightScale(), 0);
      g.drawFastRect(colorMap.getColor(cv));
    }
  }

  private class TransDrawer extends Drawer {
    public void draw(SimGraphics g, int i, int j, int cv) {
      if (cv != 0) {
        g.setDrawingCoordinates(i * g.getCellWidthScale(),
                                j * g.getCellHeightScale(), 0);
        g.drawFastRect(colorMap.getColor(cv));
      }
    }
  }


  /**
   * Creates a Value2DDisplay to display the specified Discrete2DSpace
   * using the specified ColorMap.
   */
  public Value2DDisplay(Discrete2DSpace grid, ColorMap map) {
    super(grid);
    colorMap = map;
  }

  /**
   * Linear transform of states (doubles, floats, integers etc.) to
   * colors for drawing. color = state / m + c
   */
  public void setDisplayMapping(int m, int c) {
    mapping = m;
    mappingColor = c;
    //colorMapping = true;
  }

  /**
   * Sets whether or not a zero value in space drawn by this grid is
   * transparent or drawn according to the color map. Default is to
   * draw according to the color map.
   *
   * @param val if true, zero value will be drawn as transparent, otherwise
   * the value in the color map is drawn.
   */
  public void setZeroTransparent(boolean val) {
    zeroTrans = val;
    if (val)
      drawer = new TransDrawer();
    else
      drawer = new Drawer();
  }

  /**
   * Returns whether a zero value in space drawn by this grid is
   * transparent or drawn according to the color map.
   */
  public boolean isZeroTransparent() {
    return zeroTrans;
  }


  /**
   * Draws the Discrete2DSpace converting the Numbers contained therein to
   * Colors according to the ColorMap specified in the constructor
   */
  public void drawDisplay(SimGraphics g) {
    if (!view)
      return;

    
    BaseMatrix matrix = grid.getMatrix();
    if (matrix instanceof DoubleMatrix) {
      DoubleMatrix dm = (DoubleMatrix) matrix;
      double value;
      for (int i = 0; i < grid.getSizeX(); i++) {
        for (int j = 0; j < grid.getSizeY(); j++) {
          value = dm.getDoubleAt(i, j);
          int colorValue = (int) (value / mapping + mappingColor);
          drawer.draw(g, i, j, colorValue);
        }
      }
    } else {
      Number value;
      for (int i = 0; i < grid.getSizeX(); i++) {
        for (int j = 0; j < grid.getSizeY(); j++) {
          value = (Number) matrix.get(i, j);
          int colorValue = (int) (value.doubleValue() / mapping + mappingColor);
          drawer.draw(g, i, j, colorValue);
        }
      }
    }
  }

  /**
   * Gets a list of the DisplayInfo objects associated with this Object2DDisplay.
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
   * Gets an ArrayList of Objects at an x, y screen (pixel) coordinate.
   * Implements the probeable interface.
   */
  public ArrayList getObjectsAt(int x, int y) {
    if (y != 0)
      y /= SimGraphics.getInstance().getCellHeightScale();
    if (x != 0)
      x /= SimGraphics.getInstance().getCellWidthScale();

    ArrayList list = new ArrayList();
    Object o = grid.getObjectAt(x, y);
    if (o != null) {
      list.add(new ProbeableNumber(x, y, grid, o));
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
  public void setMoveableXY(Moveable moveable, int x, int y) {
    moveable.setX(x / SimGraphics.getInstance().getCellWidthScale());
    moveable.setY(y / SimGraphics.getInstance().getCellHeightScale());
  }
}




