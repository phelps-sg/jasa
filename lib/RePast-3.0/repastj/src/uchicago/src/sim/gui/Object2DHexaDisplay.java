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
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.ListIterator;

import uchicago.src.collection.BaseMatrix;
import uchicago.src.sim.space.Discrete2DSpace;

/**
 * Title:        2D Hexagonal Spaces
 * Description:  2D Hexagonal space library
 * Copyright:    Copyright (c) 2001
 * Company:      Harvard University
 *
 * Displays a hexagonal layout of Discrete2DSpaces and the objects contained
 * within them. Implements probeable so that the objects within the
 * Discrete2DSpace can be probed. All the objects within the space are
 * expected to have implemented Drawable.
 *
 * Objects are displayed on a hexagonal grid.
 *
 * @author Laszlo Gulyas
 * @version 1.0
 */
public class Object2DHexaDisplay extends HexaDisplay2D {

    protected ArrayList objList = null;


    /**
     * Creates an Object2DHexaDisplay for displaying the specified Discrete2DSpace.
     *
     * @param hexagrid the space to display
     */
    public Object2DHexaDisplay(Discrete2DSpace hexagrid) {
        super(hexagrid);
    }

    /**
     * Sets the list of objects to display. If a space is sparsely populated then
     * rather than iterating over the entire space looking for objects to draw,
     * this Object2DDisplay can iterate only over the specified list and draw
     * those objects. This list is expected to contain objects implementing the
     * drawable interface.
     *
     * @param objectList the list of objects to draw
     */
    public void setObjectList(ArrayList objectList) {
        objList = objectList;
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

        Dimension d = getCoordinates(x , y);
        ArrayList list = new ArrayList();

        if (d != null) {
            list.add(grid.getObjectAt(d.width , d.height));
        }

        return list;
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

        // without synchronization get lots of concurrent modification errors.
        Drawable d = null;
        if (objList == null) {
            BaseMatrix matrix = grid.getMatrix();
            synchronized (matrix) {
                for (int i = 0; i < grid.getSizeX(); i++) {   //getSizeX(); i++) {
                    for (int j = 0; j < grid.getSizeY(); j++) {  //getSizeY(); j++ {
                        d = (Drawable)matrix.get(i, j);
                        if (d != null) {
                            // make the translation and set the coordinates
                            drawAt(g, d, i * g.getCellWidthScale(), j * g.getCellHeightScale());
                        }
                    }
                }
            }
        } else {
            ArrayList t;
            synchronized(objList) {
                t = (ArrayList)objList.clone();
            }
            ListIterator li = t.listIterator();
            while (li.hasNext()) {
                d = (Drawable)li.next();
                drawAt(g, d, d.getX(), d.getY());
            }
        }
    }

    private void drawAt(SimGraphics g, Drawable d, int x, int y) {
        y = (x%2 != 0) ? y * yTrans : y * yTrans + yTransHalf;
        x = x * xTrans - x * xTrans1q;

        Polygon q = new Polygon( polyClip.xpoints,
                polyClip.ypoints,
                polyClip.npoints);
        q.translate(x, y);

        Graphics2D g2d = g.getGraphics();

        Shape s = g2d.getClip();

        g2d.setClip(q);
        g.setDrawingCoordinates(x, y, 0);

        d.draw(g);

        g2d.setClip(s);

        if (isFramed) {
            Polygon q1 = new Polygon( polyDraw.xpoints,
                    polyDraw.ypoints,
                    polyDraw.npoints);
            q1.translate(x, y);
            Color c = g2d.getColor();
            g2d.setColor(frameColor);
            g2d.draw(q1);
            g2d.setColor(c);
        }

/*
g.setDrawingCoordinates(x,y,0);
g.setDrawingParameters(xTrans-1,yTrans-1,1);
g.drawHollowRect(Color.white);
*/
/*
g.drawLink(Color.cyan, x+xTrans1q, x, y, y+yTransHalf-1);
g.drawLink(Color.cyan, x, x+xTrans1q, y+yTransHalf, y+yTrans-1);
g.drawLink(Color.cyan, x+xTrans1q, x+xTrans-xTrans1q, y+yTrans-1, y+yTrans-1);
g.drawLink(Color.cyan, x+xTrans-xTrans1q, x+xTrans-1, y+yTrans-1, y+yTransHalf-1);
g.drawLink(Color.cyan, x+xTrans-1, x+xTrans-xTrans1q, y+yTransHalf, y);
g.drawLink(Color.cyan, x+xTrans-xTrans1q, x+xTrans1q, y, y);
*/
/*
g.setDrawingParameters(1,1,0);
g.setDrawingCoordinates(x+xTrans1q, y, 0);
g.drawRect(Color.red);
g.setDrawingCoordinates(x, y+yTransHalf-1, 0);
g.drawRect(Color.blue);
g.setDrawingCoordinates(x, y+yTransHalf, 0);
g.drawRect(Color.blue);
g.setDrawingCoordinates(x+xTrans1q, y+yTrans-1, 0);
g.drawRect(Color.green);
g.setDrawingCoordinates(x+xTrans-xTrans1q-1, y+yTrans-1, 0);
g.drawRect(Color.green);
g.setDrawingCoordinates(x+xTrans-1, y+yTransHalf-1, 0);
g.drawRect(Color.yellow);
g.setDrawingCoordinates(x+xTrans-1, y+yTransHalf, 0);
g.drawRect(Color.yellow);
g.setDrawingCoordinates(x+xTrans-xTrans1q-1, y, 0);
g.drawRect(Color.red);
*/
    }
}
