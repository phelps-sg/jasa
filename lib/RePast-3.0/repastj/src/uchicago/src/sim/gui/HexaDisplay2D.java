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
import java.awt.Dimension;
import java.awt.Polygon;
import java.util.ArrayList;

import uchicago.src.sim.space.Discrete2DSpace;

/**
 * Base class for two dimensional displays of hexagonal spaces. This
 * class takes care of calculating the hexagon sizes, but itself does
 * not do any drawing.
 */
public abstract class HexaDisplay2D extends Display2D implements Probeable {

  protected boolean view = true;

  protected boolean isFramed = true;
  protected Color frameColor = Color.yellow;

  protected static int xTrans;
  protected static int xTrans1q;
  protected static int yTrans;
  protected static int yTransHalf;
  protected static Polygon polyClip;
  protected Polygon polyDraw;
  
  /**
   * Creates a HexaDisplay2D for the specifed Discrete2DSpace
   */
  public HexaDisplay2D(Discrete2DSpace hexagrid) {
    super(hexagrid);
    setHexagons();
  }

  protected void setHexagons() {
      if(SimGraphics.getInstance() != null){
          xTrans1q = SimGraphics.getInstance().getCellWidthScale() / 4;
      }else{
        xTrans1q   = DisplayConstants.CELL_WIDTH / 4;
      }
    if (xTrans1q < 1)
      xTrans1q = 1;
    xTrans     = xTrans1q * 4;
      int yTrans1q;
      if(SimGraphics.getInstance() != null){
          yTrans1q = SimGraphics.getInstance().getCellHeightScale() / 4;
      }else{
        yTrans1q = DisplayConstants.CELL_HEIGHT / 4;
      }
    if (yTrans1q < 1)
      yTrans1q = 1;
    yTransHalf = yTrans1q * 2;
    yTrans     = yTransHalf * 2;

    int[] xpoints = new int[]{ xTrans1q, 1, 1, xTrans1q,
                               xTrans-xTrans1q, xTrans, xTrans,
			       xTrans-xTrans1q };
    int[] ypoints = new int[]{ 1, yTransHalf, yTransHalf+1, yTrans,
                               yTrans, yTransHalf, yTransHalf+1, 1 };
    polyClip = new Polygon(xpoints, ypoints, 8);

    int[] xpoints1 = new int[]{ xTrans1q, 0, 0, xTrans1q,
                                xTrans-xTrans1q, xTrans-1, xTrans-1,
				xTrans-xTrans1q };
    int[] ypoints1 = new int[]{ 0, yTransHalf-1, yTransHalf, yTrans-1,
                                yTrans-1, yTransHalf-1, yTransHalf, 0 };
    polyDraw = new Polygon(xpoints1, ypoints1, 8);
  }

  /**
   * Gets a list of the DisplayInfo objects associated with this
   * HexDisplay.
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
  
  protected Dimension selectPolygon(int x, int y, int x1, int y1, int x2,
				    int y2) {
    Polygon p1 = null;
    if ((x1>=0) && (y1>=0)) {
      p1 = new Polygon( polyDraw.xpoints,
			polyDraw.ypoints,
			polyDraw.npoints);
      int yy = (x1%2 != 0) ? y1 * yTrans : y1 * yTrans + yTransHalf;
      int xx = x1 * xTrans - x1 * xTrans1q;
      p1.translate(xx, yy);
    }
    Polygon p2 = null;
    if ((x2<grid.getSizeX()) && (y2<grid.getSizeY())) {
      p2 = new Polygon( polyDraw.xpoints,
			polyDraw.ypoints,
			polyDraw.npoints);
      int yy = (x2%2 != 0) ? y2 * yTrans : y2 * yTrans + yTransHalf;
      int xx = x2 * xTrans - x2 * xTrans1q;
      p2.translate(xx, yy);
    }

    if (p1 != null) {
      if (p1.contains(x,y))
	return new Dimension(x1, y1);
    }

    if (p2 != null) {
      if (p2.contains(x, y))
	return new Dimension(x2, y2);
    }

    return null;
  }

  protected Dimension getCoordinates(int x, int y) {
    int x1, y1, x2, y2;
    int xx = x / xTrans1q;
    int modX = xx % 6;
    int yy = y / yTransHalf;
    switch (modX) {
    case 0:
      x1 = xx / 3 - 1;
      y1 = y / yTrans;
      x2 = x1 + 1;

      y2 = (yy % 2 == 0) ? y1+1 : y1;
      /*
	if (yy % 2 == 0) {
	y2 = y1 + 1;
	} else {
	y2 = y1;
	}
      */

      // Check whether x1>0, y1>0, x2<worldSize, y2<worldSize
      // Check in which polygon it is...
      // Return (x1, y1) or (x2, y2)
      return selectPolygon(x, y, x1, y1, x2, y2);
    case 1:
    case 2: // Easy 1
      yy =  y - yTransHalf;
      if (yy < 0)
	return null;
      else
	return new Dimension(xx/3, yy / yTrans);
    case 3:
      x1 = xx / 3;
      y1 = y / yTrans;
      x2 = x1 - 1;

      y2 = (yy % 2 == 0) ? y1 - 1 : y1;
      /*
	if (yy % 2 == 0) {
	y2 = y1 - 1;
	} else {
	y2 = y1;
	}
      */

      // Check whether x1>0, y1>0, x2<worldSize, y2<worldSize
      // Check in which polygon it is...
      // Return (x1, y1) or (x2, y2)
      return selectPolygon(x, y, x1, y1, x2, y2);
    case 4:
    case 5: // Easy 2
      return new Dimension(xx/3, y / yTrans);
    default:
      return null;
    }
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
    Dimension d = getCoordinates(x, y);
    if (d != null) {
      moveable.setX(d.width);
      moveable.setY(d.height);
    }
  }

  public Color getFrameColor() {
    return frameColor;
  }

  public void setFrameColor(Color c) {
    frameColor = c;
  }

  public boolean isFramed() {
    return isFramed;
  }

  public void setFramed(boolean b) {
    isFramed = b;
  }

  public Dimension getSize() {
    return new Dimension(grid.getSizeX() * (xTrans - xTrans1q) + xTrans1q,
			 grid.getSizeY() * yTrans + yTransHalf) ;
  }
}




