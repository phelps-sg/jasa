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
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import uchicago.src.sim.space.VectorSpace;


/**
 * Used to display VectorSpaces - non-discrete spaces.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class VectorDisplay implements Displayable, Probeable {

  protected boolean view = true;
  private int height, width;
  protected VectorSpace space;

  /**
   * Constructs a VectorDisplay to display the specified space with the
   * specified with and height. Width and height should be in pixels. The
   * space should contain NonGridDrawables.
   *
   * @param space the space to display
   * @param width the width of the display
   * @param height the height of the display
   * @see NonGridDrawable
   */
  public VectorDisplay(VectorSpace space, int width, int height) {
    this.space = space;
    this.width = width;
    this.height = height;
  }

  /**
   * Constructs a VectorDisplay to display the specified list of objects with the
   * specified with and height. Width and height should be in pixels. This list
   * should contain NonGridDrawables.
   *
   * @param space the space to display
   * @param width the width of the display
   * @param height the height of the display
   * @see NonGridDrawable
   */
  public VectorDisplay(List list, int width, int height) {
    // there's really no reason to create a vector space from the list.
    // but in the future VectorSpaces may have more to do.
    this(new VectorSpace(list), width, height);
  }

  /**
   * Gets the size of this VectorDisplay.
   */
  public Dimension getSize() {
    return new Dimension(width, height);
  }

  /**
   * Draws this VectorDisplay.
   *
   * @param g the graphics context with which to draw
   */
  public void drawDisplay(SimGraphics g) {
    ArrayList list = space.getMembers();

    ArrayList members;
    synchronized(list) {
       members = (ArrayList)list.clone();
    }

    for (int i = 0; i < members.size(); i++) {
      NonGridDrawable item = (NonGridDrawable)members.get(i);
      g.setDrawingParameters(item.getWidth(), item.getHeight(), 0);
      g.setDrawingCoordinates((int)item.getX(), (int)item.getY(), 0);
      item.draw(g);
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

  // Probeable interface

  /**
   * Gets a list of the objects that contain the specified screen coordinate.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public ArrayList getObjectsAt(int x, int y) {
    ArrayList list = space.getMembers();
    Point p = new Point(x, y);

    ArrayList retList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      Object o = list.get(i);
      if (o instanceof DrawableItem) {
        DrawableItem item = (DrawableItem) o;
        if (item.contains(p)) {
          retList.add(o);
        }
      }
    }
    return retList;
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
    moveable.setX(x);
    moveable.setY(y);
  }

  public void reSize (int width, int height) {
    this.width = width;
    this.height = height;
  }
}
